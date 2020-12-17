package com.catata.listacomprafiles.persistencia;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.catata.listacomprafiles.models.Producto;
import com.catata.listacomprafiles.myinterface.Persistencia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ProductoFichero implements Persistencia {
    String FICHERO_DATOS;

    Context ctx;

    public ProductoFichero(String FICHERO_DATOS, Context ctx) {
        this.FICHERO_DATOS = FICHERO_DATOS;
        this.ctx = ctx;
    }

    @Override
    public void addProducto(Producto p) {
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(ctx.openFileOutput(FICHERO_DATOS, ctx.MODE_PRIVATE | ctx.MODE_APPEND));
            archivo.write(p.toString());
            archivo.flush();
            archivo.close();
        } catch (IOException e) {
            Toast.makeText(ctx, "Error guardando producto", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(ctx, "Producto Guardado Fichero", Toast.LENGTH_SHORT).show();

    }

    @Override
    public List<Producto> getProductos() {
        List<Producto> productos = new ArrayList<Producto>();
        File f = new File(ctx.getFilesDir(),FICHERO_DATOS);
        if (f.exists()) {
            try {
                InputStreamReader archivo = new InputStreamReader(ctx.openFileInput(FICHERO_DATOS));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                while ( linea != null) {
                    Producto p = stringToProducto(linea);
                    if(p!=null)
                        productos.add(p);
                    linea = br.readLine();

                }
                br.close();
                archivo.close();
            } catch (IOException e) {
                Toast.makeText(ctx, "Error cargando productos", Toast.LENGTH_SHORT).show();
            }
        }
        return productos;
    }

    @Override
    public Producto getProductoByName(String n_producto) {
        Producto producto = null;
        File f = new File(ctx.getFilesDir(),FICHERO_DATOS);
        if (f.exists()) {
            try {
                InputStreamReader archivo = new InputStreamReader(ctx.openFileInput(FICHERO_DATOS));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                while ( linea != null) {
                    Producto p = stringToProducto(linea);
                    if(p!=null){
                        if(p.getNombre().toLowerCase().compareTo(n_producto.toLowerCase())==0){
                            producto = p;
                            break;
                        }
                    }

                    linea = br.readLine();

                }
                br.close();
                archivo.close();
            } catch (IOException e) {
                Toast.makeText(ctx, "Error cargando productos", Toast.LENGTH_SHORT).show();
            }
        }
        return producto;
    }


    @Override
    public void updateProducto(Producto p) {

        File f = new File(ctx.getFilesDir(),FICHERO_DATOS);
        File temp = getTempFile(ctx,"temp01");
        if (f.exists()) {
            try {

                FileOutputStream outputStream;
                outputStream = new FileOutputStream(temp, true);

                InputStreamReader archivo = new InputStreamReader(ctx.openFileInput(FICHERO_DATOS));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                while ( linea != null) {
                    Producto prod = stringToProducto(linea);
                    if(p!=null){
                        if(p.getNombre().toLowerCase().compareTo(prod.getNombre().toLowerCase())!=0){ //Si no es el producto lo escribimos en caché
                            outputStream.write((linea+"\n").getBytes());
                        }else{
                            outputStream.write(p.toString().getBytes());
                        }
                    }

                    linea = br.readLine();

                }
                outputStream.flush();
                outputStream.close();
                br.close();
                archivo.close();

                OutputStreamWriter rewrite = new OutputStreamWriter(ctx.openFileOutput(FICHERO_DATOS, ctx.MODE_PRIVATE ));
                BufferedReader buffered_temp = new BufferedReader(new FileReader(temp));

                String l = buffered_temp.readLine();
                while (l != null){
                    rewrite.write(l+"\n");
                    l = buffered_temp.readLine();
                }

                rewrite.flush();
                rewrite.close();
                buffered_temp.close();

                temp.delete();


            } catch (IOException e) {
                Toast.makeText(ctx, "Error cargando productos", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void deleteProducto(Producto p) {
        File f = new File(ctx.getFilesDir(),FICHERO_DATOS);
        File temp = getTempFile(ctx,"temp01");
        if (f.exists()) {
            try {

                FileOutputStream outputStream;
                outputStream = new FileOutputStream(temp, true);

                InputStreamReader archivo = new InputStreamReader(ctx.openFileInput(FICHERO_DATOS));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                while ( linea != null) {
                    Producto prod = stringToProducto(linea);
                    if(p!=null){
                        if(p.getNombre().toLowerCase().compareTo(prod.getNombre().toLowerCase())!=0){ //Si no es el producto lo escribimos en caché
                            outputStream.write((linea+"\n").getBytes());
                        }
                    }

                    linea = br.readLine();

                }
                outputStream.flush();
                outputStream.close();
                br.close();
                archivo.close();

                OutputStreamWriter rewrite = new OutputStreamWriter(ctx.openFileOutput(FICHERO_DATOS, ctx.MODE_PRIVATE ));
                BufferedReader buffered_temp = new BufferedReader(new FileReader(temp));

                String l = buffered_temp.readLine();
                while (l != null){
                    rewrite.write(l+"\n");
                    l = buffered_temp.readLine();
                }

                rewrite.flush();
                rewrite.close();
                buffered_temp.close();

                temp.delete();

            } catch (IOException e) {
                Toast.makeText(ctx, "Error cargando productos", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private Producto stringToProducto(String s){
        Producto p = null;
        String[] trozos = s.split(";");
        if(trozos.length==3){
            String nombre = trozos[0];
            try{
                int cantidad = Integer.parseInt(trozos[1]);
                Double precio_u = Double.parseDouble(trozos[2]);
                p = new Producto(nombre,cantidad,precio_u);
            }catch (Error e){
                Log.e("ERROR", e.getLocalizedMessage());
            }

        }
        return p;
    }


    private File getTempFile(Context context, String url) {
        // For a more secure solution, use EncryptedFile from the Security library
        // instead of File.
        File file=null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
            // Error while creating file
            Toast.makeText(ctx, "Error Entrada/Salida",Toast.LENGTH_SHORT).show();
        }
        return file;
    }



}
