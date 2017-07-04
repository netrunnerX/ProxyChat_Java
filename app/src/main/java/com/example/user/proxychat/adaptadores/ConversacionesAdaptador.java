package com.example.user.proxychat.adaptadores;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.proxychat.interfaces.OnItemClickListener;
import com.example.user.proxychat.interfaces.OnItemLongClickListener;
import com.example.user.proxychat.R;
import com.example.user.proxychat.modelos.Conversacion;
import com.example.user.proxychat.modelos.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Saul Castillo Forte on 22/04/17.
 */

/**
 * ConversacionesAdaaptador: clase encargada de cargar en el RecyclerView de ConversacionesFragment
 * los items que muestran datos de las conversaciones
 */
public class ConversacionesAdaptador extends RecyclerView.Adapter<ConversacionesAdaptador.ConversacionesViewHolder> {

    //Los atributos estaticos clickListener y longClickListener nos permitira manejar eventos
    //de click en cada item desde la clase que crea la instancia del adaptador.
    //La clase que instancia al adaptador debera implementar las interfaces OnItemClickListener
    //y OnItemLongClickListener, y establecerse a si misma como escuchador para poder gestionar
    //los eventos de click en cada item del RecyclerView
    private static OnItemClickListener clickListener;
    private static OnItemLongClickListener longClickListener;
    private List<Conversacion> conversaciones;
    private Context context;

    /**
     * Constructor parametrizado
     * @param context contexto
     * @param conversaciones lista de objetos Conversacion
     */
    public ConversacionesAdaptador(Context context, List<Conversacion> conversaciones) {
        this.context = context;
        this.conversaciones = conversaciones;
    }

    /**
     * onCreateViewHolder: este metodo se ejecuta a la hora de crear un nuevo ViewHolder.
     * Un ViewHolder es un contenedor para un item del RecyclerView
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ConversacionesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Crea un View usando el layout del item
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_conversaciones, parent, false);
        //Crea objeto ConversacionesViewHolder, pasandole la vista como parametro
        return new ConversacionesViewHolder(v);
    }

    /**
     * onBindViewHolder: este metodo lo llama el RecyclerView a la hora de mostrar el item
     * en una posicion determinada
     * @param holder ViewHolder con los datos a mostrar
     * @param position posicion en el RecyclerView donde mostrara el item
     */
    @Override
    public void onBindViewHolder(final ConversacionesViewHolder holder, int position) {

        //Configura el TextView del nombre de la conversacion con el nombre del contacto
        holder.tvNombre.setText(conversaciones.get(position).getContacto());
        //Configura el TextView del mensaje con el ultimo mensaje de la conversacion
        holder.tvMensaje.setText(conversaciones.get(position).getUltimoMensaje());

        //Obtiene una referencia a la base de datos
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        //Realiza una consulta a la base de datos para obtener la URL de la imagen del contacto
        //de la conversacion
        databaseReference.child("usuarios").child(conversaciones.get(position).getIdContacto())
                .addValueEventListener(new ValueEventListener() {
                    /**
                     * onDataChange: este metodo es llamado cuando cambian los datos en la base de datos,
                     * ademas de para obtener un resultado inicial
                     * @param dataSnapshot
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Obtiene un objeto Usuario con los datos del Contacto
                        Usuario contacto = dataSnapshot.getValue(Usuario.class);
                        //Instancia un objeto Uri con la URL de la imagen del contacto
                        Uri fotoUri = Uri.parse(contacto.getImagenUrl());

                        //Descarga la imagen y la añade al ImageView de la imagen del contacto
                        //utilizando la libreria Glide
                        Glide.with(context.getApplicationContext())
                                .load(fotoUri)
                                .apply(new RequestOptions().placeholder(R.drawable.iconouser).centerCrop())
                                .into(holder.ivFotoContacto);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    /**
     * getItemCount: este metodo devuelve el total de items en el RecyclerView
     * @return
     */
    @Override
    public int getItemCount() {
        //Como es equivalente al tamaño de la lista de conversaciones, se devuelve este
        return conversaciones.size();
    }

    /**
     * ConversacionesViewHolder: clase que define un ViewHolder personalizado de conversaciones
     */
    static class ConversacionesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        CardView cardView;
        TextView tvNombre;
        TextView tvMensaje;
        ImageView ivFotoContacto;

        /**
         * Constructor parametrizado
         * @param v vista del item
         */
        ConversacionesViewHolder(View v) {
            super(v);

            //Instancia el CardView
            cardView = (CardView) v.findViewById(R.id.cardViewConversaciones);
            //Instancia el TextView del nombre de la conversacion
            tvNombre = (TextView) v.findViewById(R.id.tvNombreC);
            //Instancia el TextView del ultimo mensaje
            tvMensaje = (TextView) v.findViewById(R.id.tvUltimoMensaje);
            //Instancia el ImageView de la imagen de contacto
            ivFotoContacto = (ImageView) v.findViewById(R.id.ivFotoConv);

            //Configura un ClickListener para la vista
            v.setOnClickListener(this);
            //Configura un LongClickListener para la vista
            v.setOnLongClickListener(this);

        }

        /**
         * onClick: metodo llamado cuando se hace click sobre la vista del item
         * @param v
         */
        @Override
        public void onClick(View v) {

            if (clickListener != null)
                //Llamamos al metodo onClick del objeto OnItemClickListener definido en el adaptador
                clickListener.onClick(v, getAdapterPosition());
        }

        /**
         * onLongClick: metodo llamado cuando se hace una pulsacion larga sobre la vista del item
         * @param v
         * @return
         */
        @Override
        public boolean onLongClick(View v) {

            if (longClickListener != null) {
                //Llamamos al metodo onClick del objeto OnItemLongClickListener definido en el adaptador
                return longClickListener.onLongClick(v, getAdapterPosition());
            }
            return false;
        }
    }

    /**
     * setOnItemClickListener: metodo utilizado para configurar el clickListener del adaptador
     * @param clickListener escuchador de clicks
     */
    public void setOnItemClickListener(OnItemClickListener clickListener) {
        ConversacionesAdaptador.clickListener = clickListener;
    }

    /**
     * setOnItemLongClickListener: metodo utilizado para configurar el longClickListener del adaptador
     * @param longClickListener escuchador de clicks largos
     */
    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        ConversacionesAdaptador.longClickListener = longClickListener;
    }

}
