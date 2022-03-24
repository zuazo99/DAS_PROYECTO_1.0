package com.example.proyecto_das.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.proyecto_das.R;
import com.example.proyecto_das.adapters.CategoryAdapter;
import com.example.proyecto_das.models.Categoria;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmChangeListener;
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


//        crearCategoria("Freeride", String.valueOf(R.drawable.freeride), "Para los amantes del puro esqui");
        recyclerView = findViewById(R.id.recyclerViewCategoria);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        fab = findViewById(R.id.categoriaFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Cuando clicke en el boton pasaramos a la actividad de editar o crear una categoria nueva
                Intent intent = new Intent(CategoryActivity.this, AddEditCategoryActivity.class);
                startActivity(intent);
            }
        });
        setHideShowFAB();

        adapter = new CategoryAdapter(categorias, R.layout.card_view_category, new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Categoria categoria, int position) {
                //Si pulsa el card view --> activity de editar o crear una nueva categoria
                Intent intent = new Intent(CategoryActivity.this, EsquisActivity.class);
                startActivity(intent);
            }
        }, new CategoryAdapter.OnButtonClickListener() {
            @Override
            public void onButtonDeleteClick(Categoria categoria, int position) {
                // Boton delete borramos la categoria --> le avisamos mediante un dialog
                dialogBorrarCategoria("Borrar Categoria", "Estas seguro que quieres borrar" + categoria.getNombre() + "?", position);
            }

            @Override
            public void onButtonEditClick(Categoria categoria, int position) {
                Intent intent = new Intent(CategoryActivity.this, AddEditCategoryActivity.class);
                intent.putExtra("id", categoria.getId());
                startActivity(intent);
            }

        });

        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        categorias.addChangeListener(new RealmChangeListener<RealmResults<Categoria>>() {
            @Override
            public void onChange(RealmResults<Categoria> categorias) {
                adapter.notifyDataSetChanged();
            }
        });


        searchView = findViewById(R.id.searchView);

        //searchView.setQueryHint("Search view");
        //searchView.setIconifiedByDefault(true);
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

    private void borrarCategoria(int position){
        realm.beginTransaction();
        categorias.get(position).deleteFromRealm();
        realm.commitTransaction();
    }

    // ** DIALOGS ** //
    private void dialogBorrarCategoria(String title, String message, final int position){
        /*
        Creamos el metodo para que nos apare
         */
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        borrarCategoria(position);
                        Toast.makeText(CategoryActivity.this, "Se ha borrado con exito", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null).show();


    }
}