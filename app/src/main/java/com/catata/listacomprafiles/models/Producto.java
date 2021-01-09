package com.catata.listacomprafiles.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Producto {
    String nombre;
    int cantidad;
    double precio_u;

    public Producto(){}

    public Producto(String nombre, int cantidad, double precio_u) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio_u = precio_u;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio_u() {
        return precio_u;
    }

    public void setPrecio_u(double precio_u) {
        this.precio_u = precio_u;
    }

    @NonNull
    @Override
    public String toString() {
        return this.nombre+";"+this.cantidad+";"+this.precio_u+"\n";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof Producto) && ((Producto)obj).getNombre().toUpperCase().compareTo(this.nombre.toUpperCase())==0;
    }
}

