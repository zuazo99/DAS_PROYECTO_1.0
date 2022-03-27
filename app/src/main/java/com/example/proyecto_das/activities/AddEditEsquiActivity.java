package com.example.proyecto_das.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.proyecto_das.R;
import com.example.proyecto_das.models.Categoria;
import com.example.proyecto_das.models.Esqui;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmList;

public class AddEditEsquiActivity extends AppCompatActivity {

    private Realm realm;
    private int esquiId;
    private Categoria categoria;
    private int categoriaID;
    private boolean isCreation; //Para saber si editamos o creamos
    private boolean fromGalery = false; // Para saber si la foto es de galeria o no
    private RealmList<Esqui> esquis;


    private Esqui esqui;
    
    private TextView textViewTitle;
    private ImageView esquiImage;
    private EditText editTextMarcaEsqui;
    private EditText editTextModeloEsqui;
    private EditText editTextLinkEsqui;
    private EditText editTextPrecioEsqui;
    private Button btnLink;
    private EditText editTextEsquiDescripcion;
    private Button btnGaleria;
    private RatingBar ratingBarEsqui;
    private FloatingActionButton fab;
// Atributos que nos ayudan para subir una foto desde la galeria
    private Uri path;
    private String direktorio;



    private static final int PHOTO_SELECTED = 10; // El codigo del intent para luego poder recuperar la imagen



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_esqui);

        realm = Realm.getDefaultInstance();
        viewReferencesSki();


        // Comprobar si va a ser una accion para editar o para crear
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            // Para saber a la categoria que pertenece el esqui
            categoriaID = getIntent().getExtras().getInt("id_categoria");
            if (bundle.getInt("id_Esqui") != 0) {
                esquiId = getIntent().getExtras().getInt("id_Esqui");
                isCreation = false;
            } else {
                isCreation = true;
            }
        }

            categoriaID = getIntent().getExtras().getInt("id_categoria");

            categoria = realm.where(Categoria.class).equalTo("id", categoriaID).findFirst();
            setActivityTitle();

            if(!isCreation){
                esqui = getEsquiById(esquiId);
                fromGalery = esqui.getFromGalery();
                setDatosEsqui();
            }

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isCreation){
                        updateEsqui();
                    }else{
                        addNewEsqui();
                    }
                }
            });

            btnLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fromGalery = false;
                    String link = editTextLinkEsqui.getText().toString();
                    if (link.length() > 0){
                        loadImageLink(link);
                    }
                }
            });

            btnGaleria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Intent implicito, nos hace falta ver la version de Android etc...
                    //comprobar version actual de android que estamos runeando
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Mayor que la version 6.0
                        //Comprobar si ha aceptado, no ha aceptado, o nunca se le ha preguntado
                        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            // ha aceptado
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Seleccione la aplicacion"), PHOTO_SELECTED);
                        }else{
                            // o  ha denegado o es la primera vez que se le pregunta
                            if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                                //No se le ha preguntado aun
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PHOTO_SELECTED);

                            }else {
                                // Ha denegado
                                Toast.makeText(AddEditEsquiActivity.this, "Porfavor, acepta los permisos", Toast.LENGTH_SHORT).show();
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


// Metodo de respuesta de startActivityForResult
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
                        Picasso.get().load(new File(getRealPathFromUri(path))).placeholder(R.drawable.placeholder).fit().into(esquiImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(AddEditEsquiActivity.this, "Error!!", Toast.LENGTH_LONG).show();
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

    private void loadImageLink(String link) {
            Picasso.get().load(link).fit().into(esquiImage);
    }

    private void setDatosEsqui() {
        //Metodo para settear  los datos en la categoria y hacerlo mas realista para que
        // aparezcan los datos que ya estan  de la categoria
        editTextMarcaEsqui.setText(esqui.getNombreMarca());
        editTextModeloEsqui.setText(esqui.getNombreProd());
        ratingBarEsqui.setRating(esqui.getStars());
        editTextEsquiDescripcion.setText(esqui.getDescripcion());
        editTextPrecioEsqui.setText(String.valueOf(esqui.getPrecio()));
        if (fromGalery){
            Picasso.get().load(new File(esqui.getImagen())).placeholder(R.drawable.placeholder).fit().into(esquiImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(AddEditEsquiActivity.this, "Error en el setDatosESqui ", Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Picasso.get().load(esqui.getImagen()).placeholder(R.drawable.placeholder).fit().into(esquiImage);
            editTextLinkEsqui.setText(esqui.getImagen());
        }
    }

    private Esqui getEsquiById(int esquiId) {
        //Para conseguir el esqui a traves de su id
            return  realm.where(Esqui.class).equalTo("id", esquiId).findFirst();
    }

    private void setActivityTitle(){
        // Para cambiar el titulo de la actividad dependiendo de si es para editar/crear
        String title = getString(R.string.titulo_Editar_Esqui);
        if (isCreation) title = getString(R.string.titulo_Add_Esqui);
        textViewTitle.setText(title);
    }
    private void viewReferencesSki(){
        //Metodo para hacer referencia a las views
        textViewTitle = findViewById(R.id.textViewTitleEsqui);
        esquiImage = findViewById(R.id.imageViewPreviewEsqui);
        editTextMarcaEsqui = findViewById(R.id.editTextMarcaEsqui);
        editTextModeloEsqui = findViewById(R.id.editTextModeloEsqui);
        editTextLinkEsqui = findViewById(R.id.editTextEsquiLink);
        editTextPrecioEsqui = findViewById(R.id.editTextEsquiPrecio);
        btnLink = findViewById(R.id.buttonPreviewEsqui);
        editTextEsquiDescripcion = findViewById(R.id.editTextEsquiDescription);
        btnGaleria = findViewById(R.id.btnGaleriaEsqui);
        ratingBarEsqui = findViewById(R.id.ratingBarEsqui);
        fab = findViewById(R.id.FABSaveEsqui);
    }

    private boolean validarDatosEsquisNuevos(){
        // Validamos en este metodo que los datos esten correctos y la informacion completa, que no haya ningun editTExt vacio
        boolean correcto = false;
        int lengthMarca = editTextMarcaEsqui.getText().toString().length();
        int lengthModelo = editTextModeloEsqui.getText().toString().length();
        int lengthLink = editTextLinkEsqui.getText().toString().length();
        int lengthPrecio = editTextPrecioEsqui.getText().toString().length();

        if(!fromGalery && lengthMarca> 0 && lengthModelo > 0 && lengthLink > 0 && lengthPrecio > 0){
            correcto = true;
        }else if (fromGalery && lengthMarca > 0 && lengthModelo > 0 && lengthPrecio > 0){
            correcto = true;
        }

        return correcto;
    }



    // ** CRUD ** //
    private void addNewEsqui(){
        String marca = null;
        String modeloEsqui = null;
        String link = null;
        String descripcion = null;
        float stars;
        double precio;
        if(validarDatosEsquisNuevos()){
            marca = editTextMarcaEsqui.getText().toString();
            modeloEsqui = editTextModeloEsqui.getText().toString();
            stars = ratingBarEsqui.getRating();
            link = editTextLinkEsqui.getText().toString();
            descripcion = editTextEsquiDescripcion.getText().toString();
            precio = Double.valueOf(editTextPrecioEsqui.getText().toString());
            boolean galeria = fromGalery;
            if (galeria) link = direktorio;
            Esqui esqui = new Esqui(marca, link, modeloEsqui, descripcion, precio, stars);
            esqui.setFromGalery(galeria);
            if (!isCreation) esqui.setId(esquiId);

            realm.executeTransaction(r->{
                r.copyToRealmOrUpdate(esqui);
                categoria.getEsquis().add(esqui);
            });
            goToEsquiActivity();
        }else{
            Toast.makeText(this, getString(R.string.addNewCategoryToast), Toast.LENGTH_SHORT).show();

        }
    }


    private void updateEsqui(){
        String marca = null;
        String modeloEsqui = null;
        String link = null;
        String descripcion = null;
        float stars;
        double precio;
        if(validarDatosEsquisNuevos()){
            marca = editTextMarcaEsqui.getText().toString();
            modeloEsqui = editTextModeloEsqui.getText().toString();
            stars = ratingBarEsqui.getRating();
            link = editTextLinkEsqui.getText().toString();
            descripcion = editTextEsquiDescripcion.getText().toString();
            precio = Double.valueOf(editTextPrecioEsqui.getText().toString());
            boolean galeria = fromGalery;
            if (galeria) link = direktorio;
            Esqui esqui = new Esqui(marca, link, modeloEsqui, descripcion, precio, stars);


            realm.executeTransaction(r->{
                esqui.setFromGalery(galeria);
                esqui.setId(esquiId);
                r.copyToRealmOrUpdate(esqui);
            });
            goToEsquiActivity();
        }else{
            Toast.makeText(this, getString(R.string.addNewCategoryToast), Toast.LENGTH_SHORT).show();

        }
    }

    private void goToEsquiActivity() {
            // Un intent para despues de añadir o actualizar un esqui vayamos
        // directamente a la lista de esquis.
        Intent intent = new Intent(AddEditEsquiActivity.this, EsquisActivity.class);
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
