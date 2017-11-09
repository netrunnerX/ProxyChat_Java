package com.example.user.proxychat.ui.adaptadores;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.proxychat.R;
import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.BloqueadosViewHolderPresenter;


import java.util.List;

/**
 * Created by netx on 7/29/17.
 */

public class BloqueadosAdaptador extends RecyclerView.Adapter<BloqueadosAdaptador.BloqueadosViewHolder> {

    private List<String> bloqueados;
    private Context context;
    private String usuarioId;

    public BloqueadosAdaptador(Context context, String usuarioId, List<String> bloqueados) {
        this.context = context;
        this.usuarioId = usuarioId;
        this.bloqueados = bloqueados;
    }

    @Override
    public BloqueadosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_bloqueados, parent, false);
        return new BloqueadosAdaptador.BloqueadosViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final BloqueadosViewHolder holder, final int position) {
        holder.viewHolderPresenter.obtenerUsuarioBloqueado(bloqueados.get(position));

        holder.btDesbloquear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                holder.viewHolderPresenter.desbloquearUsuario(usuarioId, bloqueados.get(position));
            }

        });
    }

    @Override
    public int getItemCount() {
        return bloqueados.size();
    }

    class BloqueadosViewHolder extends RecyclerView.ViewHolder implements BloqueadosViewHolderPresenter.BloqueadosViewHolderView {

        CardView cardView;
        TextView tvNombre;
        ImageView imageView;
        ImageButton btDesbloquear;
        BloqueadosViewHolderPresenter viewHolderPresenter;


        public BloqueadosViewHolder(View v) {
            super(v);

            cardView = (CardView)v.findViewById(R.id.cardViewBloqueados);
            tvNombre = (TextView)v.findViewById(R.id.tvNombreBloqueado);
            imageView = (ImageView)v.findViewById(R.id.ivFotoBloqueado);
            btDesbloquear = (ImageButton)v.findViewById(R.id.btDesbloquear);
            viewHolderPresenter = new BloqueadosViewHolderPresenter(this);

        }

        @Override
        public void mostrarBloqueado(Usuario usuarioBloqueado) {
            //Configura el texto del TextView que muestra el nombre del usuario
            tvNombre.setText(usuarioBloqueado.getApodo());

            //Crea un objeto Uri a partir de la URL de la imagen del usuario
            Uri fotoUri = Uri.parse(usuarioBloqueado.getImagenUrl());

            //Descarga la imagen y la carga en el ImageView que muestra la imagen del usuario
            //utilizando la libreria Glide
            Glide.with(context.getApplicationContext())
                    .load(fotoUri)
                    .apply(new RequestOptions().placeholder(R.drawable.iconouser).centerCrop())
                    .into(imageView);
        }

        @Override
        public void mostrarMensaje(String mensaje) {
            Snackbar.make(tvNombre, mensaje, Snackbar.LENGTH_LONG).show();
        }
    }
}
