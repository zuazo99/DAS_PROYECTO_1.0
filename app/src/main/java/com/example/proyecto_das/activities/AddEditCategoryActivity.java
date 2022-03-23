package com.example.proyecto_das.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_das.R;
import com.example.proyecto_das.models.Categoria;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import io.realm.Realm;

public class AddEditCategoryActivity extends AppCompatActivity {

    private int categoriaId;
    private boolean isCreation; //Para saber si editamos o creamos

    private Categoria categoria;
    private Realm realm;

    private TextView textViewTitle;
    private ImageView categoryImage;
    private EditText editTextCategoryName;
    private EditText editTextCategoryDescription;
    private EditText editTextCategoryLink;
    private FloatingActionButton fab;
    private Button btnPreview;
    private Button btnGallery;
    private boolean fromGalery = false;
    private Uri path;
    private String direktorio;


    private static final int PHOTO_SELECTED = 1; // El codigo del intent para luego poder recuperar la imagen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_category);

        realm = Realm.getDefaultInstance();
        viewReferences();

        // Comprobar si va a ser una accion para editar o para crear
        if(getIntent().getExtras() != null){
            categoriaId = getIntent().getExtras().getInt("id");
            isCreation = false;
        }else{
            isCreation = true;
        }
        setActivityTitle();

        if(!isCreation){
            categoria = getCategoriaById(categoriaId);
            fromGalery = categoria.getFromGalery();
            setDatosCategoria();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewCategory();
            }
        });

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromGalery = false;
                String link = editTextCategoryLink.getText().toString();
                if (link.length()>0){
                    loadImageLink(link);
                }
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent implicito, nos hace falta ver la version de Android etc...
                //comprobar version actual de android que estamos runeando
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ // Mayor que la version 6.0
                    //Comprobar si ha aceptado, no ha aceptado, o nunca se le ha preguntado
                    if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
                        // ha aceptado
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Seleccione la aplicacion"), PHOTO_SELECTED);
                    }else{
                        // o  ha denegado o es la primera vez que se le pregunta
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                            //No se le ha preguntado aun
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PHOTO_SELECTED);

                        }else{
                            // Ha denegado
                            Toast.makeText(AddEditCategoryActivity.this, "Porfavor, acepta los permisos", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            i.setData(Uri.parse("package:" + getPackageName()));
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); // excluir del flujo
                            startActivity(i);

                        }
                    }
                }else{
                    olderVersion();
                }

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case PHOTO_SELECTED:
                if (resultCode == Activity.RESULT_OK) {
                    //Cargar imagen desde la galeria
                        path = data.getData();
                        direktorio = getRealPathFromUri(path);
                        Toast.makeText(this, path.getPath(), Toast.LENGTH_LONG).show();
                        if (direktorio != null) {
                            fromGalery = true;
                            Picasso.get().load(new File(getRealPathFromUri(path))).placeholder(R.drawable.placeholder).fit().into(categoryImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Toast.makeText(AddEditCategoryActivity.this, "Error!!", Toast.LENGTH_LONG).show();
                                }
                            });

                        }


                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
//
        }
    }



    // ** Obtener la uri de la imagen ** //

    private String getRealPathFromUri(Uri contentUri){
        String result = null;

        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
        }
            return result;
    }





    private void viewReferences(){
        //Metodo para hacer referencia a las views
        textViewTitle = findViewById(R.id.textViewTitle);
        categoryImage = findViewById(R.id.imageViewPreview);
        editTextCategoryName = findViewById(R.id.editTextCategoryName);
        editTextCategoryDescription = findViewById(R.id.editTextCategoryDescription);
        editTextCategoryLink = findViewById(R.id.editTextCategoryLink);
        fab = findViewById(R.id.FABSaveCategory);
        btnPreview = findViewById(R.id.buttonPreview);
        btnGallery = findViewById(R.id.btnGaleria);
    }

    private void setActivityTitle(){
        String title = getString(R.string.titulo_Editar_Actividad);
        if (isCreation) title = getString(R.string.titulo_Add_Actividad);
        textViewTitle.setText(title);
    }

    private void setDatosCategoria(){
        //Metodo para settear  los datos en la categoria y hacerlo mas realista para que
        // aparezcan los datos que ya estan  de la categoria
        editTextCategoryName.setText(categoria.getNombre());
        editTextCategoryDescription.setText(categoria.getDescripcion());


        if (fromGalery){
            Picasso.get().load(new File(categoria.getImagen())).placeholder(R.drawable.placeholder).fit().into(categoryImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(AddEditCategoryActivity.this, "Error en el setDatosCategori", Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Picasso.get().load(categoria.getImagen()).placeholder(R.drawable.placeholder).fit().into(categoryImage);
            editTextCategoryLink.setText(categoria.getImagen());
        }
    }

    private void loadImageLink(String link){
        // Libreria para el manejo de imagenes, pasandole una url carga una imagen
        Picasso.get().load(link).fit().into(categoryImage);
    }

    private Categoria getCategoriaById(int categoryId){
        // COnseguir la categoria con el id
        return realm.where(Categoria.class).equalTo("id", categoriaId).findFirst();
    }

    private void editarCategoria(String imagen, String descripcion, Categoria categoria){
        realm.executeTransaction(r->{
            categoria.setImagen(imagen); categoria.setDescripcion(descripcion);
            realm.copyToRealmOrUpdate(categoria);
        });
    }

    private boolean validarDatosCategoriaNueva() {
        // Validamos en este metodo que los datos esten correctos y la informacion completa, que no haya ningun editTExt vacio
        boolean correcto = false;
        if (!fromGalery && editTextCategoryName.getText().toString().length() > 0 &&
                editTextCategoryDescription.getText().toString().length() > 0 &&
                editTextCategoryLink.getText().toString().length() > 0) {
            correcto = true;

        } else if(fromGalery && editTextCategoryName.getText().toString().length() > 0 &&
                editTextCategoryDescription.getText().toString().length() > 0) {
            correcto = true;
        }

        return correcto;
    }

    private void addNewCategory() {
        String name = null;
        String description = null;
        String link = null;
        if (validarDatosCategoriaNueva()) {
            name = editTextCategoryName.getText().toString();
            description = editTextCategoryDescription.getText().toString();
            link = editTextCategoryLink.getText().toString();
            boolean galeria = fromGalery;
            if (galeria) link = direktorio;
            Categoria categoria = new Categoria(name, link, description);
            categoria.setFromGalery(galeria);
            if (!isCreation) categoria.setId(categoriaId);

            realm.executeTransaction(r -> {
                r.copyToRealmOrUpdate(categoria); // Esto lo que hace es crear o actualiza una categoria
            });
            goToMainActivity();
        } else {
            Toast.makeText(this, getString(R.string.addNewCategoryToast), Toast.LENGTH_SHORT).show();
        }
    }


    private void goToMainActivity() {
        Intent intent = new Intent(AddEditCategoryActivity.this, CategoryActivity.class);
        startActivity(intent);
    }



    // ** COMPROBAR PERMISOS ** //


    private void olderVersion() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Seleccione la aplicacion"), PHOTO_SELECTED);
        }else{
            Toast.makeText(this, "No tienes los permisos", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkForPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            // El permiso no esta concedido, pedirlo
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PHOTO_SELECTED);
        }
    }

    private boolean hasPermission(String permissionToCheck) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, permissionToCheck);
        return (permissionCheck == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PHOTO_SELECTED:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Seleccione la aplicacion"), PHOTO_SELECTED);
                }else{
                    // No concedido el permiso
                    Toast.makeText(this, "No tienes los permisos", Toast.LENGTH_SHORT).show();

                }
                break;
            default:
                break;
        }
    }
}