package com.example.proyecto_das.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_das.R;
import com.example.proyecto_das.models.Esqui;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;

public class AddEditEsquiActivity extends AppCompatActivity {

    private Realm realm;
    private int esquiId;
    private boolean isCreation; //Para saber si editamos o creamos
    private boolean fromGalery = false; // Para saber si la foto es de galeria o no



    private Esqui esqui;
    
    private TextView textViewTitle;
    private ImageView esquiImage;
    private EditText editTextMarcaEsqui;
    private EditText editTextModeloEsqui;
    private EditText editTextLinkEsqui;
    private Button btnLink;
    private EditText editTextEsquiDescripcion;
    private Button btnGaleria;
    private RatingBar ratingBarEsqui;
    private FloatingActionButton fab;
    
    
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_esqui);
        
        Realm realm = Realm.getDefaultInstance();
        viewReferencesSki();

            // Comprobar si va a ser una accion para editar o para crear
            if(getIntent().getExtras() != null){
                esquiId = getIntent().getExtras().getInt("id");
                isCreation = false;
            }else{
                isCreation = true;
            }
            setActivityTitle();

            if(!isCreation){
                esqui = getEsquiById(esquiId);
                fromGalery = esqui.getFromGalery();
                setDatosEsqui();
            }
    }

    private void setDatosEsqui() {
    }

    private Esqui getEsquiById(int esquiId) { 
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
        btnLink = findViewById(R.id.buttonPreviewEsqui);
        editTextEsquiDescripcion = findViewById(R.id.editTextEsquiDescription);
        btnGaleria = findViewById(R.id.btnGaleriaEsqui);
        ratingBarEsqui = findViewById(R.id.ratingBarEsqui);
        fab = findViewById(R.id.FABSaveEsqui);
    }
}
