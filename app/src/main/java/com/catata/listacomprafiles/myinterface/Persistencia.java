package com.catata.listacomprafiles.myinterface;

import android.widget.ProgressBar;

import com.catata.listacomprafiles.models.Producto;

import java.util.List;

public  interface Persistencia {
    void addProducto(Producto p);
    List<Producto> getProductos();
    Producto getProductoByName(String n_producto);
    void updateProducto(Producto p);
    void deleteProducto(Producto p);
}
