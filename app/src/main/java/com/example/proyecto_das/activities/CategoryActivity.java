package com.example.proyecto_das.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.proyecto_das.R;
import com.example.proyecto_das.adapters.CategoryAdapter;
import com.example.proyecto_das.models.Categoria;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmResults;

public class CategoryActivity extends AppCompatActivity {

    private Realm realm;
    private FloatingActionButton fab;
    private SearchView searchView;
    private RealmResults<Categoria> categorias;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();
        categorias = realm.where(Categoria.class).findAll();

        recyclerView = findViewById(R.id.recyclerViewCategoria);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        fab = findViewById(R.id.categoriaFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // TODO
                Toast.makeText(CategoryActivity.this, "FAB", Toast.LENGTH_SHORT).show();
            }
        });
        setHideShowFAB();

        adapter = new CategoryAdapter(categorias, R.layout.card_view_category, new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Categoria categoria, int position) {

            }
        });
        recyclerView.setAdapter(adapter);
    }


    private void setHideShowFAB() { // Este metodo nos permite saber la posicion del scroll.
        //Logra que cuando vayas para abajo el "fab" desaparezca y al volver para arriba vuelva aparecer
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });
    }

    // ** CRUD **//
    private void crearCategoria(String nombre, String imagen, String descripcion){
        realm.beginTransaction();
        Categoria categoria = new Categoria(nombre, imagen, descripcion);
        realm.copyToRealm(categoria);
        realm.commitTransaction();
    }

    private void editarCategoria(String imagen, String descripcion, Categoria categoria){
        realm.executeTransaction(r->{
            categoria.setImagen(imagen); categoria.setDescripcion(descripcion);
            realm.copyToRealmOrUpdate(categoria);
        });
    }

    private void borrarCategoria(Categoria categoria){
        realm.beginTransaction();
        categoria.deleteFromRealm();
        realm.commitTransaction();
    }

    // ** DIALOGS ** //
    private void dialogCrearCategoria(String titulo, String mensaje){
        /*
        Creamos el metodo para que nos apare
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


    }
}