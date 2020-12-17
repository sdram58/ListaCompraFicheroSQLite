package com.catata.listacomprafiles.sqlite;

import android.provider.BaseColumns;

/*Clase que indicamos el nombre de la tabla así como sus campos*/
public class ProductoContract {
    //Para evitar que nadie la pueda instanciar hacemos el constructor privado
    private ProductoContract(){}

    //Creamos una clase interna estática con la información
    public static class ProductoInfo implements BaseColumns{
        public static final String TABLE_NAME = "producto";
        public static final String COLUMN_NAME_NOMBRE = "nombew";
        public static final String COLUMN_NAME_CANTIDAD = "cantidad";
        public static final String COLUMN_NAME_PRECIO_U = "precio_u";

        public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ProductoInfo.TABLE_NAME + " (" +
                    ProductoInfo._ID + " INTEGER PRIMARY KEY," +
                    ProductoInfo.COLUMN_NAME_NOMBRE + " TEXT," +
                    ProductoInfo.COLUMN_NAME_CANTIDAD + " NUMBER," +
                    ProductoInfo.COLUMN_NAME_PRECIO_U + " NUMBER)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ProductoInfo.TABLE_NAME;
}
}
