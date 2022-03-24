package com.example.proyecto_das.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_das.R;
import com.example.proyecto_das.adapters.EsquiAdapter;
import com.example.proyecto_das.models.Categoria;
import com.example.proyecto_das.models.Esqui;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;

public class EsquisActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab;

    private Realm realm;
    private RealmList<Esqui> esquis;

    private EsquiAdapter adapter;

    private Categoria categoria;
    private int categoriaID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqui);

        realm = Realm.getDefaultInstance();
        if (getIntent().getExtras() != null){
            categoriaID = getIntent().getExtras().getInt("id");
        }

        categoria = realm.where(Categoria.class).equalTo("id", categoriaID).findFirst();
        categoria.addChangeListener(new RealmChangeListener<Categoria>() {
            @Override
            public void onChange(Categoria categoria) {
                adapter.notifyDataSetChanged();
            }
        });
        setHideShowFAB();
        esquis = categoria.getEsquis();
        setTitle(categoria.getNombre());
        fab = findViewById(R.id.FABSaveEsqui);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EsquisActivity.this, AddEditEsquiActivity.class);
                startActivity(intent);
            }
        });
        recyclerView = findViewById(R.id.recyclerViewEsqui);
        adapter = new EsquiAdapter(esquis, R.layout.card_view_esqui, new EsquiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Esqui esqui, int position) {

            }
        }, new EsquiAdapter.OnButtonClickListener() {
            @Override
            public void onButtonDeleteClick(Esqui esqui, int position) {
                // Boton delete borramos el modelo del esqui --> le avisamos mediante un dialog
                dialogBorrarEsqui("Borrar Modelo de Esqui", "Estas seguro que quieres borrar" + esqui.getNombreProd() + "?", esqui);
            }

            @Override
            public void onButtonEditClick(Esqui esqui, int position) {
                Intent intent = new Intent(EsquisActivity.this, AddEditEsquiActivity.class);
                intent.putExtra("id", esqui.getId());
                startActivity(intent);
            }
        });
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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


    // ** CRUD ** //

    private void borrarCategoria(Esqui esqui){
        realm.beginTransaction();
        esqui.deleteFromRealm();
        realm.commitTransaction();
    }


    // ** DIALOGS ** //
    private void dialogBorrarEsqui(String title, String message, final Esqui esqui ){
        /*
        Creamos el metodo para que nos apare
         */
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_borrar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        borrarCategoria(esqui);
                        Toast.makeText(EsquisActivity.this, "Se ha borrado con exito", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null).show();


    }
}
