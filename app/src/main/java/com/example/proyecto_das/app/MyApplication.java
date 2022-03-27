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

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("MundoGlaciar.realm")
                .initialData(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Categoria categoria = new Categoria("FreeStyle",
                                "https://cdn-s-www.lalsace.fr/images/1582E271-6990-443F-BE24-7DC102E3EE22/NW_raw/dara-howell-premiere-championne-olympique-de-ski-slopestyle-photo-afp-j-sorriano-1596542410.jpg",
                                "Para los amantes");

                        Categoria categoria1 = new Categoria("FreeRide", "https://www.skischule-fiss-ladis.at/uploads/tx_bh/sfl_skikurse_freeride_9.jpg?mod=1407166384", "Para los amantes del puro esqui");
                        Esqui esqui = new Esqui("ATOMIC", "https://www.snowcountry.eu/media/catalog/product/cache/31b8a3b5dbd0c4cbc4ada78b51a1a9c9/a/t/atomic-backland-85-ul.jpg", "Atomic BackLand", "Esquis perfectos para practicar", 400, 3);

                        realm.insertOrUpdate(categoria);
                        realm.insertOrUpdate(categoria1);
                        realm.insertOrUpdate(esqui);
                        categoria.getEsquis().add(esqui);
                    }
                })
                .deleteRealmIfMigrationNeeded()
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
