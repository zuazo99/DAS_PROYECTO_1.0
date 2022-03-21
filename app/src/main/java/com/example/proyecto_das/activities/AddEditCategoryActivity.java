package com.example.proyecto_das.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.proyecto_das.R;
import com.example.proyecto_das.models.Categoria;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

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



    }

    private void viewReferences(){
        //Metodo para hacer referencia a las views
        categoryImage = findViewById(R.id.imageViewPreview);
        editTextCategoryName = findViewById(R.id.editTextCategoryName);
        editTextCategoryDescription = findViewById(R.id.editTextCategoryDescription);
        editTextCategoryLink = findViewById(R.id.editTextCategoryLink);
        fab = findViewById(R.id.FABSaveCategory);
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
                r.copyToRealmOrUpdate(categoria);
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

}