package com.example.proyecto_das.models;

import com.example.proyecto_das.app.MyApplication;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Categoria extends RealmObject {
    @PrimaryKey
    private int id;
    private String nombre;
    private String imagen;
    private String descripcion;
    private boolean fromGalery;
    private RealmList<Esqui> esquis;

    public Categoria(){

    }

    public Categoria(String nombre, String imagen, String descripcion){
        this.id = MyApplication.CategoriaID.incrementAndGet();
        this.nombre = nombre;
        this.imagen = imagen;
        this.descripcion = descripcion;
        //this.fromGalery = false;
        esquis = new RealmList<Esqui>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setFromGalery(boolean fromGalery){
        this.fromGalery = fromGalery;
    }
    public boolean getFromGalery(){ return fromGalery;}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public RealmList<Esqui> getEsquis() {
        return esquis;
    }

}
