package com.example.proyecto_das.models;

import com.example.proyecto_das.app.MyApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Esqui extends RealmObject {

    @PrimaryKey
    private int id;
    private double precio;
    @Required
    private String nombreMarca;
    @Required
    private String nombreProd;
    private String descripcion;

    private String imagen;
    private boolean fromGalery;
    private float stars;


    public Esqui(){

    }

    public Esqui(String nombreMarca, String imagen, String nombreProd, String descripcion, double precio, float stars) {
        this.id = MyApplication.EsquiID.incrementAndGet();
        this.precio = precio;
        this.nombreMarca = nombreMarca;
        this.imagen = imagen;
        this.nombreProd = nombreProd;
        this.descripcion = descripcion;
        this.stars = stars;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getNombreMarca() {
        return nombreMarca;
    }

    public void setNombreMarca(String nombreMarca) {
        this.nombreMarca = nombreMarca;
    }

    public String getNombreProd() {
        return nombreProd;
    }

    public void setNombreProd(String nombreProd) {
        this.nombreProd = nombreProd;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public boolean getFromGalery() {
        return fromGalery;
    }

    public void setFromGalery(boolean fromGalery) {
        this.fromGalery = fromGalery;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }
}
