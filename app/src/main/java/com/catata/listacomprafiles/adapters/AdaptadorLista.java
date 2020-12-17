package com.catata.listacomprafiles.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.catata.listacomprafiles.R;
import com.catata.listacomprafiles.models.Producto;
import com.catata.listacomprafiles.myinterface.Persistencia;
import com.catata.listacomprafiles.utils.MyFormater;

import java.util.List;


public class AdaptadorLista extends RecyclerView.Adapter<AdaptadorLista.MyViewHolder> {

    List<Producto> productos;
    Context context;
    MyCallBack myCallBack;
    TextView tvResultado;
    String simbolo_moneda="€";

    Persistencia persistencia;

    public AdaptadorLista(List<Producto> productos, Context context, MyCallBack mcb, TextView resultado, Persistencia persistencia, String simbolo_moneda) {
        this.productos = productos;
        this.context = context;
        this.myCallBack = mcb;
        this.tvResultado = resultado;
        this.persistencia = persistencia;
        this.simbolo_moneda= simbolo_moneda;

        if(this.productos.size()==0){
            this.productos = this.persistencia.getProductos();
            tvResultado.setText(MyFormater.DoubleToString2Digits(updateTotal()) + " " + simbolo_moneda);
        }
    }

    public void setPersistencia(Persistencia p){
        this.persistencia = p;
        this.productos = this.persistencia.getProductos();
        tvResultado.setText(MyFormater.DoubleToString2Digits(updateTotal()) + " " + simbolo_moneda);
        notifyDataSetChanged();
    }

    public void setSimbolo_moneda(String moneda){
        this.simbolo_moneda = moneda;
        notifyDataSetChanged();
        tvResultado.setText(MyFormater.DoubleToString2Digits(updateTotal()) + " " + simbolo_moneda);
    }


    public void addDato(Producto p){
        //Los añadimos arriba, no es necesario
        productos.add(0,p);
        //myCallBack.actualizarTotal(updateTotal());
        tvResultado.setText(MyFormater.DoubleToString2Digits(updateTotal()) + " " + simbolo_moneda);
        notifyDataSetChanged();

        //Lo guardamos en el fichero o SQLite
        persistencia.addProducto(p);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.tvCantidad.setText(String.valueOf(productos.get(position).getCantidad()));
        holder.tvNombreProducto.setText(productos.get(position).getNombre());
        holder.tvPrecio_U.setText(""+MyFormater.DoubleToString2Digits(productos.get(position).getPrecio_u())+" "+simbolo_moneda);
        holder.tvPrecioTotal.setText("" + MyFormater.DoubleToString2Digits(productos.get(position).getCantidad()*productos.get(position).getPrecio_u())+ " " + simbolo_moneda);

        holder.ibEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productos.get(position).setCantidad(productos.get(position).getCantidad() - 1);
                if(productos.get(position).getCantidad()==0){ //Ya no quedan, eliminamos el registro
                    persistencia.deleteProducto(productos.get(position));
                    productos.remove(position);

                }else{
                    persistencia.updateProducto(productos.get(position));
                }

                //Notificamos cambios
                notifyDataSetChanged();
                //myCallBack.actualizarTotal(updateTotal());
                tvResultado.setText(MyFormater.DoubleToString2Digits(updateTotal()) + " " + simbolo_moneda);


            }
        });

    }

    double updateTotal(){
        double total=0;
        for(Producto p: productos){
            total += p.getCantidad()*p.getPrecio_u();
        }

        return total;
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreProducto, tvPrecio_U, tvCantidad, tvPrecioTotal;
        ImageButton ibEliminar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreProducto = (TextView)itemView.findViewById(R.id.tvProductoName);
            tvPrecio_U = (TextView)itemView.findViewById(R.id.tvPrecio);
            tvCantidad = (TextView)itemView.findViewById(R.id.tvCantidad);
            tvPrecioTotal = (TextView)itemView.findViewById(R.id.tvSubtotal);

            ibEliminar = (ImageButton)itemView.findViewById(R.id.btnEliminar);

        }
    }

    public interface MyCallBack{
        void actualizarTotal(double total);
    }
}
