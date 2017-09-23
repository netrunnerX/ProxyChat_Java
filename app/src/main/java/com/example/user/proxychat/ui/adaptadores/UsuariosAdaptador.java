package com.example.user.proxychat.ui.adaptadores;

import android.content.Context;
import android.graphics.Color;
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
import com.example.user.proxychat.ui.interfaces.OnItemClickListener;
import com.example.user.proxychat.ui.interfaces.OnItemLongClickListener;
import com.example.user.proxychat.R;
import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.UsuariosViewHolderPresenter;

import java.util.List;

/**
 * Created by Saul Castillo Forte on 25/04/17.
 */

/**
 * UsuariosAdaptador: adaptador utilizado para cargar los usuarios que pertenecen a un punto de encuentro
 * en el RecyclerView de MeetingPointUsuariosFragment
 */
public class UsuariosAdaptador extends RecyclerView.Adapter<UsuariosAdaptador.UsuariosViewHolder> {

    //El atributo clickListener nos permitira manejar eventos
    //de click en cada item desde la clase que crea la instancia del adaptador.
    //La clase que instancia al adaptador debera implementar la interfaz OnItemClickListener
    //y establecerse a si misma como escuchador para poder gestionar
    //los eventos de click en cada item del RecyclerView
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private List<String> contactos;
    private Context context;
    private String idUsuario;


    /**
     * Constructor parametrizado
     * @param context contexto
     * @param contactos lista de usuarios
     * @param idUsuario id del usuario que utiliza la aplicacion
     */
    public UsuariosAdaptador(Context context, List<String> contactos, String idUsuario) {
        this.contactos = contactos;
        this.context = context;
        this.idUsuario = idUsuario;
    }

    /**
     * onCreateViewHolder: este metodo se ejecuta a la hora de crear un nuevo ViewHolder.
     * Un ViewHolder es un contenedor para un item del RecyclerView
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public UsuariosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_contactos, parent, false);
        return new UsuariosViewHolder(v);
    }

    /**
     * onBindViewHolder: este metodo lo llama el RecyclerView a la hora de mostrar el item
     * en una posicion determinada
     * @param holder ViewHolder con los datos a mostrar
     * @param position posicion en el RecyclerView donde mostrara el item
     */
    @Override
    public void onBindViewHolder(final UsuariosViewHolder holder, int position) {
        holder.obtenerDatosUsuario(contactos.get(position));
    }

    /**
     * getItemCount: este metodo devuelve el total de items en el RecyclerView
     * @return
     */
    @Override
    public int getItemCount() {

        //Como es equivalente al tamaño de la lista de usuarios, se devuelve este
        return contactos.size();
    }

    /**
     * UsuariosViewHolder: clase que define un ViewHolder personalizado de usuarios del punto de encuentro
     */
    class UsuariosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
    View.OnLongClickListener, UsuariosViewHolderPresenter.UsuariosViewHolderView {

        CardView cardView;
        TextView tvNombre;
        ImageView ivFotoContacto;
        UsuariosViewHolderPresenter viewHolderPresenter;

        /**
         * Constructor parametrizado
         * @param v vista del item
         */
        UsuariosViewHolder(View v) {
            super(v);
            viewHolderPresenter = new UsuariosViewHolderPresenter(this);

            //Instancia el CardView
            cardView = (CardView) v.findViewById(R.id.cardViewContactos);
            //Instancia el TextView que muestra el nombre del usuario
            tvNombre = (TextView) v.findViewById(R.id.tvNombreContacto);
            //Instancia el ImageView que muestra la imagen del usuario
            ivFotoContacto = (ImageView) v.findViewById(R.id.ivFotoPerfil);

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
                //Llamamos al metodo onClick del objeto OnItemLongClickListener definido en el adaptador
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

        @Override
        public void mostrarUsuario(Usuario usuario) {
            //Configura el texto del TextView que muestra el nombre del usuario
            tvNombre.setText(usuario.getApodo());

            //Si el usuario es el propio usuario
            if (usuario.getId().equals(idUsuario)) {
                //Cambia el color del texto por el color verde
                tvNombre.setTextColor(Color.rgb(48, 191, 0));
            }

            //Crea un objeto Uri a partir de la URL de la imagen del usuario
            Uri fotoUri = Uri.parse(usuario.getImagenUrl());

            //Descarga la imagen y la carga en el ImageView que muestra la imagen del usuario
            //utilizando la libreria Glide
            Glide.with(context.getApplicationContext())
                    .load(fotoUri)
                    .apply(new RequestOptions().placeholder(R.drawable.iconouser).centerCrop())
                    .into(ivFotoContacto);
        }

        @Override
        public void obtenerDatosUsuario(String usuarioId) {
            viewHolderPresenter.obtenerDatosUsuario(usuarioId);
        }
    }

    /**
     * setOnItemClickListener: metodo utilizado para configurar el clickListener del adaptador
     * @param clickListener escuchador de clicks
     */
    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    /**
     * setOnItemLongClickListener: metodo utilizado para configurar el clickListener del adaptador
     * @param longClickListener escuchador de clicks
     */
    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

}
