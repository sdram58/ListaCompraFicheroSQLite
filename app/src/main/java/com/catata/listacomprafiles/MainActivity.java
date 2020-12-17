package com.catata.listacomprafiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText etProducto, etCantidad, etPrecioUnitario;
    TextView tvTotalCompra;
    ImageButton ibAdd;

    RecyclerView lista;
    AdaptadorLista adaptadorLista;

    final String DEFAULT_STORAGE="SQLite";

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



        String moneda = sharedPreferences.getString(SettingsActivity.KEY_MONEDA, "€");
        String almacenamiento = sharedPreferences.getString(SettingsActivity.KEY_ALMACENAMIENTO, DEFAULT_STORAGE);

        etPrecioUnitario.setHint(moneda);


        tvTotalCompra = (TextView) findViewById(R.id.txtTotalCompra);

        lista = (RecyclerView)findViewById(R.id.mi_lista);

        lista.setLayoutManager(new LinearLayoutManager(this));

        adaptadorLista = new AdaptadorLista(new ArrayList<Producto>(), this, new AdaptadorLista.MyCallBack() {
            @Override
            public void actualizarTotal(double total) {
                tvTotalCompra.setText(MyFormater.DoubleToString2Digits(total) + " €");
            }
        }, tvTotalCompra, almacenamiento.compareTo(DEFAULT_STORAGE)==0?new SQLManager(this) :new ProductoFichero("Producto.dat", this),moneda);

        lista.setAdapter(adaptadorLista);

        ibAdd = (ImageButton)findViewById(R.id.btnAdd);



        ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: Falta comprobar los campos
                addItem();
            }
        });

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if(key.compareTo(SettingsActivity.KEY_ALMACENAMIENTO)==0){
                    Persistencia p;
                    if(prefs.getString(key,"€").compareTo(DEFAULT_STORAGE)==0){
                        p = new SQLManager(getApplicationContext());
                    }else{
                        p=new ProductoFichero("Producto.dat", getApplicationContext());
                    }
                    adaptadorLista.setPersistencia(p);

                }else if(key.compareTo(SettingsActivity.KEY_MONEDA)==0){
                    etPrecioUnitario.setHint(prefs.getString(key,"€"));
                    adaptadorLista.setSimbolo_moneda(prefs.getString(key,"€"));
                }
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mi_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    void addItem(){
        String nombreProducto = etProducto.getText().toString();
        int cantidad = Integer.parseInt(etCantidad.getText().toString());
        double precioUnitario = Double.parseDouble(etPrecioUnitario.getText().toString());
        Producto p = new Producto(nombreProducto, cantidad, precioUnitario);
        adaptadorLista.addDato(p);

    }
}