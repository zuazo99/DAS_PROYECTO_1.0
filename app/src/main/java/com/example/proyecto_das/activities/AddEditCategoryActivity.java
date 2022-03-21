package com.example.proyecto_das.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.proyecto_das.R;
import com.example.proyecto_das.models.Categoria;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

import io.realm.Realm;

public class AddEditCategoryActivity extends AppCompatActivity {

    private int categoriaId;
    private boolean isCreation; //Para saber si editamos o creamos

    private Categoria categoria;
    private Realm realm;

    private ImageView categoryImage;
    private EditText editTextCategoryName;
    private EditText editTextCategoryDescription;
    private EditText editTextCategoryLink;
    private FloatingActionButton fab;
    private Button btnPreview;
    private Button btnGallery;

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
                String link = editTextCategoryLink.getText().toString();
                if (link.length()>0){
                    loadImageLink(link);
                }
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarImagenes();
            }
        });


    }

    private void cargarImagenes(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccione la aplicacion"), PHOTO_SELECTED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent imageReturnedIntent) {
        Uri selectedImageUri = null;
        Uri selectedImage;
        String filePath = null;
        switch (requestCode){
            case PHOTO_SELECTED:
                if (requestCode == Activity.RESULT_OK){
                    selectedImage = imageReturnedIntent.getData();
                    String selectedPath = selectedImage.getPath();
                    if (requestCode == PHOTO_SELECTED){
                        if(selectedPath != null){
                            InputStream imageStream = null;
                            try{
                                imageStream = getContentResolver().openInputStream(selectedImage);
                            }catch (FileNotFoundException e){
                                e.printStackTrace();
                            }

                            // Transformamos la URI de la imagen a inputStream y este en un Bitmap
                            Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                            // Ponemos nuestro bitmap en un ImageView que tenagmos en la vista

                            categoryImage.setImageBitmap(bmp);
                        }
                    }
                }

                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);

        }


    }

    private void viewReferences(){
        //Metodo para hacer referencia a las views
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
        setTitle(title);
    }

    private void setDatosCategoria(){
        //Metodo para settear  los datos en la categoria y hacerlo mas realista para que
        // aparezcan los datos que ya estan  de la categoria
        editTextCategoryName.setText(categoria.getNombre());
        editTextCategoryDescription.setText(categoria.getDescripcion());
        editTextCategoryLink.setText(categoria.getImagen());
        Picasso.get().load(categoria.getImagen()).placeholder(R.drawable.placeholder).into(categoryImage);
    }

    private void loadImageLink(String link){
        // Libreria para el manejo de imagenes, pasandole una url carga una imagen

        Picasso.get().load(link).fit().into(categoryImage);
    }

    private Categoria getCategoriaById(int categoryId){
        // COnseguir la categoria con el id
        return realm.where(Categoria.class).equalTo("id", categoriaId).findFirst();
    }

    private boolean validarDatosCategoriaNueva(){
        // Validamos en este metodo que los datos esten correctos y la informacion completa, que no haya ningun editTExt vacio
        if (editTextCategoryName.getText().toString().length() > 0 &&
                editTextCategoryDescription.getText().toString().length() > 0 &&
                editTextCategoryLink.getText().toString().length() > 0){
            return true;
        }else {
            return false;
        }
    }

    private void addNewCategory(){
        if (validarDatosCategoriaNueva()){
            String name = editTextCategoryName.getText().toString();
            String description = editTextCategoryDescription.getText().toString();
            String link = editTextCategoryLink.getText().toString();
            Categoria categoria = new Categoria(name, link, description);

            if (!isCreation) categoria.setId(categoriaId);

            realm.executeTransaction(r->{
                r.copyToRealmOrUpdate(categoria); // Esto lo que hace es crear o actualiza una categoria
            });
            goToMainActivity();
        }else{
            Toast.makeText(this, getString(R.string.addNewCategoryToast) , Toast.LENGTH_SHORT).show();
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(AddEditCategoryActivity.this, CategoryActivity.class);
        startActivity(intent);
    }



    // ** COMPROBAR PERMISOS ** //

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
    }
}