package com.catata.listacomprafiles.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.widget.Toast;

import com.catata.listacomprafiles.myinterface.Persistencia;
import com.catata.listacomprafiles.models.Producto;

import java.util.ArrayList;
import java.util.List;

public class SQLManager implements Persistencia {
    public static ProductoDBHelper productoDBHelper;

    Context c;

    public SQLManager(Context c) {
        this.c = c;
    }

    public ProductoDBHelper getInstance(){
        if(productoDBHelper == null){
            productoDBHelper =  new ProductoDBHelper(c);
        }
        return productoDBHelper;
    }


    public void cerrar(){
        ProductoDBHelper dbHelper = getInstance();
        dbHelper.close();
    }

    @Override
    public void addProducto(Producto p) {
        ProductoDBHelper dbHelper = getInstance();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ProductoContract.ProductoInfo.COLUMN_NAME_NOMBRE, p.getNombre());
        values.put(ProductoContract.ProductoInfo.COLUMN_NAME_CANTIDAD, p.getCantidad());
        values.put(ProductoContract.ProductoInfo.COLUMN_NAME_PRECIO_U, p.getPrecio_u());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(ProductoContract.ProductoInfo.TABLE_NAME, null, values);
        Toast.makeText(c, "Producto Guardado en SQLite", Toast.LENGTH_SHORT).show();

    }

    @Override
    public List<Producto> getProductos() {
        ProductoDBHelper dbHelper = getInstance();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Seleccionamos los campos que queremos recupearr
        String[] projection = {
                BaseColumns._ID,
                ProductoContract.ProductoInfo.COLUMN_NAME_NOMBRE,
                ProductoContract.ProductoInfo.COLUMN_NAME_CANTIDAD,
                ProductoContract.ProductoInfo.COLUMN_NAME_PRECIO_U
        };

        // Elegimos el orden, en este caso por apellidos ascendente
        String sortOrder =
                ProductoContract.ProductoInfo.COLUMN_NAME_NOMBRE + " ASC";

        //Lanzamos la consulta
        Cursor cursor = db.query(
                ProductoContract.ProductoInfo.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        //Leemos el cursor
        List<Producto> productos = new ArrayList<Producto>();
        while(cursor.moveToNext()) {
            //Buscamos todas por el _ID
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(ProductoContract.ProductoInfo._ID));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ProductoContract.ProductoInfo.COLUMN_NAME_NOMBRE));
            int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow(ProductoContract.ProductoInfo.COLUMN_NAME_CANTIDAD));
            Double precio_u = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductoContract.ProductoInfo.COLUMN_NAME_PRECIO_U));
            productos.add(new Producto(nombre,cantidad, precio_u));
        }
        cursor.close();

        return productos;
    }

    @Override
    public Producto getProductoByName(String n_producto) {
        ProductoDBHelper dbHelper = getInstance();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Seleccionamos los campos que queremos recupearr
        String[] projection = {
                BaseColumns._ID,
                ProductoContract.ProductoInfo.COLUMN_NAME_NOMBRE,
                ProductoContract.ProductoInfo.COLUMN_NAME_CANTIDAD,
                ProductoContract.ProductoInfo.COLUMN_NAME_PRECIO_U
        };

        // Filtramos los resultados donde el nombre = name (parámetro)
        String selection = ProductoContract.ProductoInfo.COLUMN_NAME_NOMBRE + " = ?";
        String[] selectionArgs = { n_producto };

        // Elegimos el orden, en este caso por apellidos ascendente
        String sortOrder =
                ProductoContract.ProductoInfo.COLUMN_NAME_NOMBRE + " ASC";

        //Lanzamos la consulta
        Cursor cursor = db.query(
                ProductoContract.ProductoInfo.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        //Leemos el cursor
        List<Producto> productos = new ArrayList<Producto>();
        while(cursor.moveToNext()) {
            //Buscamos todas por el _ID
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(ProductoContract.ProductoInfo._ID));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ProductoContract.ProductoInfo.COLUMN_NAME_NOMBRE));
            int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow(ProductoContract.ProductoInfo.COLUMN_NAME_CANTIDAD));
            Double precio_u = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductoContract.ProductoInfo.COLUMN_NAME_PRECIO_U));
            productos.add(new Producto(nombre,cantidad, precio_u));
        }
        cursor.close();

        return productos.size()>=1?productos.get(0):null;
    }

    @Override
    public void updateProducto(Producto p) {
        ProductoDBHelper dbHelper = getInstance();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Elegimos el nuevo valor para la columna
        ContentValues values = new ContentValues();
        values.put(ProductoContract.ProductoInfo.COLUMN_NAME_NOMBRE, p.getNombre());
        values.put(ProductoContract.ProductoInfo.COLUMN_NAME_CANTIDAD, p.getCantidad());
        values.put(ProductoContract.ProductoInfo.COLUMN_NAME_PRECIO_U, p.getPrecio_u());

        // Qué fila actualizamos. La que coincida con el ID
        String selection = ProductoContract.ProductoInfo.COLUMN_NAME_NOMBRE + " = ?";
        String[] selectionArgs = { String.valueOf(p.getNombre()) };

        db.update(
                ProductoContract.ProductoInfo.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        //Devuelve la cantidad de filas afectadas, debería ser 0 o 1 si existe ya que filtramos por el ID.
    }

    @Override
    public void deleteProducto(Producto p) {
        ProductoDBHelper dbHelper = getInstance();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Definimos el Where de la consulta
        String selection = ProductoContract.ProductoInfo.COLUMN_NAME_NOMBRE + " = ?";
        // Vinculamos el valor al que corresponda, como la consulta solo tiene un ?, solo habrá un argunmento.
        String[] selectionArgs = { String.valueOf(p.getNombre()) };
        // ejecutamos la orden que nos devuelve las filas eliminadas
        db.delete(ProductoContract.ProductoInfo.TABLE_NAME, selection, selectionArgs);

    }
}
