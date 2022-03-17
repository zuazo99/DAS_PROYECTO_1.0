package com.example.proyecto_das.models;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Categoria extends RealmObject {

    private int id;
    private String nombre;
    // private imagen

    private RealmList<Esqui> esquis;

    public Categoria(){

    }

    public Categoria(String nombre){

    }
}
