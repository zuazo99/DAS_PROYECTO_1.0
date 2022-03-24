package com.example.proyecto_das.app;

import android.app.Application;
import android.util.Log;

import com.example.proyecto_das.models.Categoria;
import com.example.proyecto_das.models.Esqui;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class MyApplication extends Application {

    public static AtomicInteger CategoriaID = new AtomicInteger();
    public static AtomicInteger EsquiID = new AtomicInteger();

    //Esto se ejecuta antes del main activity


    @Override
    public void onCreate() {
        super.onCreate();
        setUpRealmConfig();
        Realm realm = Realm.getDefaultInstance();
        Log.i("Realm", realm.getPath());
        CategoriaID = getIdByTable(realm, Categoria.class);
        EsquiID = getIdByTable(realm, Esqui.class);
        realm.close();
    }


    private void setUpRealmConfig(){
        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder().name("MundoGlaciar.realm")
                .allowWritesOnUiThread(true)
                .build();
        Realm.setDefaultConfiguration(config);
    }

    //Trabajamos con clase generica, extendera de la clase RealmObject
    //Le pasamos la base de datos REalm y la clase que queremos ejecutar
    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass){
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max("id").intValue()): new AtomicInteger();
    }
}
