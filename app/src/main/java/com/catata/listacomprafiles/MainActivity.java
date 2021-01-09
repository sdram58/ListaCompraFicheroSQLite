package com.catata.listacomprafiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.PluralRules;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.catata.listacomprafiles.adapters.AdaptadorLista;
import com.catata.listacomprafiles.models.Producto;
import com.catata.listacomprafiles.myinterface.Persistencia;
import com.catata.listacomprafiles.persistencia.ProductoFichero;
import com.catata.listacomprafiles.sqlite.SQLManager;
import com.catata.listacomprafiles.utils.MyFormater;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText etProducto, etCantidad, etPrecioUnitario;
    TextView tvTotalCompra;
    ImageButton ibAdd;

    RecyclerView lista;
    AdaptadorLista adaptadorLista;

    final String DEFAULT_STORAGE="SQLite";
    final String NOMBRE_FICHERO="Producto.dat";

    String textoAnterior = "";

    String[] monedas;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etProducto = (EditText)findViewById(R.id.etProducto);
        etCantidad = (EditText)findViewById(R.id.etCantidad);
        etPrecioUnitario = (EditText)findViewById(R.id.etPrecioUnitario);


        etPrecioUnitario.addTextChangedListener(new MyFormater(etPrecioUnitario,2));


        //Le añadimos manejador al teclado cuando estamos en precio unitario, que es el último
        //para que cuando pulsemos ok, añada el elemento sin necesidad de pulsar el botón +
        etPrecioUnitario.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addItem();
                    handled = true;
                }
                return handled;
            }
        });


        /*Obtenemos el valor guardado de las preferencias*/
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);


        //Cogemos las preferencias de la moneda y el tipo de almacenamiento que tenemos al arrancar
        String moneda = sharedPreferences.getString(SettingsActivity.KEY_MONEDA, "€");
        String almacenamiento = sharedPreferences.getString(SettingsActivity.KEY_ALMACENAMIENTO, DEFAULT_STORAGE);

        //En precio le ponemos el tipo de moneda como hint (placeholder en html)
        etPrecioUnitario.setHint(moneda);


        tvTotalCompra = (TextView) findViewById(R.id.txtTotalCompra);

        lista = (RecyclerView)findViewById(R.id.mi_lista);

        lista.setLayoutManager(new LinearLayoutManager(this));

        /*Creamos el adaptador, le pasamos:
        * Una lista vacía, que no haría falta
        * el contexto, que es la actividad
        * El callback, que en este caso no lo usamos
        * El textView del total para que se actualice dentro del adaptador
        * El tipo de persistencia en función del alamacenamiento guardado
        * El tipo de moneda
        * */
        adaptadorLista = new AdaptadorLista(new ArrayList<Producto>(), this, new AdaptadorLista.MyCallBack() {
            @Override
            public void actualizarTotal(double total) {
                tvTotalCompra.setText(MyFormater.DoubleToString2Digits(total) + " €");
            }
        }, tvTotalCompra, almacenamiento.compareTo(DEFAULT_STORAGE)==0?new SQLManager(this) :new ProductoFichero(NOMBRE_FICHERO, this),moneda);

        /*Añadimos el manejador al RecyclerView*/
        lista.setAdapter(adaptadorLista);

        ibAdd = (ImageButton)findViewById(R.id.btnAdd);

        //Manejador deñ botón + (añadir)
        ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: Falta comprobar los campos que estén rellenos
                addItem();
            }
        });

        /*Manejador de las preferencias, cuando se cambien salta este método*/
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                //Sí la clave de preferencia es la de almacenamiento
                if(key.compareTo(SettingsActivity.KEY_ALMACENAMIENTO)==0){
                    //Si la clave es DEFAULT_STORAGE, que es SQLite creamos una Persistencia de ese tipo
                    //Sino la creamos de tipo Fichero
                    Persistencia p;
                    if(prefs.getString(key,"").compareTo(DEFAULT_STORAGE)==0){
                        p = new SQLManager(getApplicationContext());
                    }else{
                        p=new ProductoFichero(NOMBRE_FICHERO, getApplicationContext());
                    }

                    //Enviamos la nueva persistencia al adaptador
                    adaptadorLista.setPersistencia(p);
                //Si la clave es la de Moneda
                }else if(key.compareTo(SettingsActivity.KEY_MONEDA)==0){
                    //Obtenemos el valor, lo añadimos al Hint de precio unitario y
                    //Se lo enviamos al adaptador
                    etPrecioUnitario.setHint(prefs.getString(key,"€"));
                    adaptadorLista.setSimbolo_moneda(prefs.getString(key,"€"));
                }
            }
        };

        //Registramos el manejador anterior
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    // Inflamos el menú para que esté presente en la barra
        getMenuInflater().inflate(R.menu.mi_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Manejador del menú
        //Nos quedamos con el ID de la opción elegida y en función de ésta ejecutamos una acción u otra
        switch (item.getItemId()){
            case R.id.action_settings:{
                //Mostramos la pantalla de preferencias
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            }
            case R.id.action_sincronizar: {
                //Sincronizamos Persistencias
                sincronizar();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sincroniza ambos sistemas de persistencia, Ficheros y SQLite, para que contengan la misma info.
     */
    private void sincronizar() {
        /*Obtenemos una referencia a cada una de los modos de persistencia*/
        Persistencia pSQLite = new SQLManager(getApplicationContext());
        Persistencia pFichero=new ProductoFichero(NOMBRE_FICHERO, getApplicationContext());

        //Obtenemos todos los productos de cada una de las fuentes de datos
        List<Producto> listProductoFichero = pFichero.getProductos();
        List<Producto> listProductoSQLite = pSQLite.getProductos();

        //Recorremos todos los productos de fichero
        for(Producto p:listProductoFichero){
            //Comprobamos si está en SQLite
            Producto pSql = pSQLite.getProductoByName(p.getNombre());
            if(pSql != null){ //Si está en SQLite actualizamos el producto en ambas persistencias
                pSql = mezclarProductos(p,pSql);
                pSQLite.updateProducto(pSql);
                pFichero.updateProducto(pSql);
            }else{//Si no está lo añadimos en SQLite
                pSQLite.addProducto(p);
            }
        }

        //Recorremos todos los productos de SQLite
        for(Producto p:listProductoSQLite){
            //Si el producto de SQLite no está en el array de fichero
            //Ojo se ha sobreescrito el método equals.
            if(listProductoFichero.indexOf(p)<0){
                pFichero.addProducto(p);         //Se inserta.
            }
        }
        //Recuperamos de nuevo todos los productos
        listProductoSQLite = pSQLite.getProductos();
        adaptadorLista.resetDatos(listProductoSQLite);

    }

    /*
    * Método que busca un producto en una lista no ordenada sin sobreescribir equals
    * */
    private Producto buscarProductoEnArray(List<Producto> lista, Producto p){
        Producto producto = null;
        for(Producto prod: lista){
            if(prod.getNombre().toUpperCase().compareTo(p.getNombre().toUpperCase())==0){
                producto = prod;
                break;
            }
        }
        return producto;
    }

    /*mezclarProductos
    * @param a
    * @param b
    * Dados dos productos devuelve un nuevo producto con la suma de las cantidades y la media
    * del precio unitario con el nombre del 1º */
    private Producto mezclarProductos(Producto a, Producto b){
        Producto c = new Producto();
        c.setCantidad(a.getCantidad()+b.getCantidad());
        c.setPrecio_u((a.getPrecio_u()+b.getPrecio_u())/2);
        //TODO Habría que comprobar que ambos tienen el mismo nombre
        c.setNombre(a.getNombre());
        return c;
    }


    void addItem(){
        String nombreProducto = etProducto.getText().toString();
        int cantidad = Integer.parseInt(etCantidad.getText().toString());
        double precioUnitario = Double.parseDouble(etPrecioUnitario.getText().toString());
        Producto p = new Producto(nombreProducto, cantidad, precioUnitario);
        adaptadorLista.addDato(p);
        etPrecioUnitario.setText("");
        etCantidad.setText("");
        etProducto.setText("");
        etPrecioUnitario.clearFocus();

        /** Ocultamos el teclado **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Obtenemos la vista que tiene el foco.
        View view = getCurrentFocus();
        //Si no hay ninguna creamos una vista para poder cerrar el teclado
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


    }
}