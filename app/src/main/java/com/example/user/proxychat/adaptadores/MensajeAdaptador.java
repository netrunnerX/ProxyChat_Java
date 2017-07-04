package com.example.user.proxychat.adaptadores;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.user.proxychat.R;
import com.example.user.proxychat.modelos.Mensaje;

import java.util.List;

/**
 * Created by Saul Castillo Forte on 08/04/2017.
 */

/**
 * MensajeAdaptador: adaptador utilizado para cargar mensajes en el RecyclerView de
 * ChatActivity, ProxyFragment y MeetingPointChatFragment
 */
public class MensajeAdaptador extends RecyclerView.Adapter<MensajeAdaptador.MensajesViewHolder> {

    private List<Mensaje> mensajes;

    /**
     * Constructor por defecto
     * @param mensajes lista de mensajes
     */
    public MensajeAdaptador(List<Mensaje> mensajes){
        this.mensajes = mensajes;
    }

    /**
     * onCreateViewHolder: este metodo se ejecuta a la hora de crear un nuevo ViewHolder.
     * Un ViewHolder es un contenedor para un item del RecyclerView
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MensajesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_mensajes, parent, false);
        return new MensajesViewHolder(v);
    }

    /**
     * onBindViewHolder: este metodo lo llama el RecyclerView a la hora de mostrar el item
     * en una posicion determinada
     * @param holder ViewHolder con los datos a mostrar
     * @param position posicion en el RecyclerView donde mostrara el item
     */
    @Override
    public void onBindViewHolder(MensajesViewHolder holder, int position) {
        //Obtiene el tipo de mensaje
        int tipoMensaje = mensajes.get(position).getTipoMensaje();

        //Obtiene un objeto RelativeLayout.LayoutParams del cardView, que se utiliza para configurar
        //los parametros del layout
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                holder.cardView.getLayoutParams();

        //Obtiene un objeto FrameLayout.LayoutParams del LinearLayout del fondo del mensaje,
        //que se utiliza para configurar los parametros del layout
        FrameLayout.LayoutParams flParams = (FrameLayout.LayoutParams)
                holder.fondoMensaje.getLayoutParams();

        //Obtiene un objeto LinearLayout.LayoutParams del TextView de la hora del mensaje,
        //que se utiliza para configurar los parametros del layout
        LinearLayout.LayoutParams llHoraParams = (LinearLayout.LayoutParams)
                holder.tvHora.getLayoutParams();

        //Obtiene un objeto LinearLayout.LayoutParams del TextView del texto del mensaje,
        //que se utiliza para configurar los parametros del layout
        LinearLayout.LayoutParams llMensajeParams = (LinearLayout.LayoutParams)
                holder.tvMensaje.getLayoutParams();

        //Obtiene un objeto LinearLayout.LayoutParams del TextView del nombre del emisor,
        //que se utiliza para configurar los parametros del layout
        LinearLayout.LayoutParams llNombreParams = (LinearLayout.LayoutParams)
                holder.tvNombre.getLayoutParams();

        //Si el valor del tipo de mensaje es 0, es un mensaje saliente
        if (tipoMensaje == 0) {
            //Establece el fondo del mensaje con la imagen del globo verde
            holder.fondoMensaje.setBackgroundResource(R.drawable.globoverde);

            //Alinea los elementos para que aparezcan a la derecha
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            flParams.gravity = Gravity.RIGHT;
            llHoraParams.gravity = Gravity.RIGHT;
            llNombreParams.gravity = Gravity.RIGHT;
            llMensajeParams.gravity = Gravity.RIGHT;
            holder.tvMensaje.setGravity(Gravity.RIGHT);
        }
        //Por otra parte, si es 1, es un mensaje entrante
        else {

            //Establece el fondo del mensaje con la imagen del globo verde
            holder.fondoMensaje.setBackgroundResource(R.drawable.globoazul);

            //Alinea los elementos para que aparezcan a la izquierda
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            flParams.gravity = Gravity.LEFT;
            llHoraParams.gravity = Gravity.LEFT;
            llNombreParams.gravity = Gravity.LEFT;
            llMensajeParams.gravity = Gravity.LEFT;
            holder.tvMensaje.setGravity(Gravity.LEFT);
        }

        //Actualiza los parametros de layout de los elementos del ViewHolder
        holder.cardView.setLayoutParams(layoutParams);
        holder.fondoMensaje.setLayoutParams(flParams);
        holder.tvHora.setLayoutParams(llHoraParams);
        holder.tvMensaje.setLayoutParams(llMensajeParams);
        holder.tvNombre.setLayoutParams(llNombreParams);

        //Actualiza el texto del TextView que muestra el nombre del emisor
        holder.tvNombre.setText(mensajes.get(position).getEmisor());
        //Actualiza el texto del TextView que muestra el texto del mensaje
        holder.tvMensaje.setText(mensajes.get(position).getMensaje());
        //Actualiza el texto del TextView que muestra la hora del mensaje
        holder.tvHora.setText(mensajes.get(position).getHoraMensaje());
    }

    /**
     * getItemCount: este metodo devuelve el total de items en el RecyclerView
     * @return
     */
    @Override
    public int getItemCount() {

        //Como es equivalente al tamaño de la lista de mensajes, se devuelve este
        return mensajes.size();
    }

    /**
     * MensajesViewHolder: clase que define un ViewHolder personalizado de mensajes
     */
    static class MensajesViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView tvNombre;
        TextView tvMensaje;
        TextView tvHora;
        LinearLayout fondoMensaje;

        /**
         * Constructor parametrizado
         * @param v vista del item
         */
        MensajesViewHolder(View v) {
            super(v);

            //Instancia el LinearLayout que hace de fondo de mensaje
            fondoMensaje = (LinearLayout) v.findViewById(R.id.fondoMensaje);
            //Instancia el CardView
            cardView = (CardView) v.findViewById(R.id.cardViewMensajes);
            //Instancia el TextView que muestra el nombre del emisor
            tvNombre = (TextView) v.findViewById(R.id.tvNombre);
            //Instancia el TextView que muestra el texto del mensaje
            tvMensaje = (TextView) v.findViewById(R.id.tvMensaje);
            //Instancia el TextView que muestra la hora del mensaje
            tvHora = (TextView) v.findViewById(R.id.tvHora);
        }
    }
}
