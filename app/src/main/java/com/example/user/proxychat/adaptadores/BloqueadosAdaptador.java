package com.example.user.proxychat.adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import com.example.user.proxychat.modelos.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by netx on 7/29/17.
 */

public class BloqueadosAdaptador extends RecyclerView.Adapter<BloqueadosAdaptador.BloqueadosViewHolder> {

    private ArrayList<String> bloqueados;
    private Context context;
    private String usuarioId;

    public BloqueadosAdaptador(Context context, String usuarioId, ArrayList<String> bloqueados) {
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
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("usuarios").child(bloqueados.get(position))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Obtiene un objeto Usuario con los datos del usuario a partir del DataSnapshot
                        Usuario contacto = dataSnapshot.getValue(Usuario.class);

                        //Configura el texto del TextView que muestra el nombre del usuario
                        holder.tvNombre.setText(contacto.getApodo());

                        //Crea un objeto Uri a partir de la URL de la imagen del usuario
                        Uri fotoUri = Uri.parse(contacto.getImagenUrl());

                        //Descarga la imagen y la carga en el ImageView que muestra la imagen del usuario
                        //utilizando la libreria Glide
                        Glide.with(context.getApplicationContext())
                                .load(fotoUri)
                                .apply(new RequestOptions().placeholder(R.drawable.iconouser).centerCrop())
                                .into(holder.imageView);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        holder.btDesbloquear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                databaseReference.child("contactos")
                        .child("usuarios")
                        .child(usuarioId)
                        .child("bloqueados")
                        .child(bloqueados.get(position)).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(view, "Usuario desbloqueado", Snackbar.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(view, "Error, no se ha podido desbloquear al usuario",
                                        Snackbar.LENGTH_LONG).show();
                            }
                });
            }

        });
    }

    @Override
    public int getItemCount() {
        return bloqueados.size();
    }

    class BloqueadosViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView tvNombre;
        ImageView imageView;
        ImageButton btDesbloquear;


        public BloqueadosViewHolder(View v) {
            super(v);

            cardView = (CardView)v.findViewById(R.id.cardViewBloqueados);
            tvNombre = (TextView)v.findViewById(R.id.tvNombreBloqueado);
            imageView = (ImageView)v.findViewById(R.id.ivFotoBloqueado);
            btDesbloquear = (ImageButton)v.findViewById(R.id.btDesbloquear);

        }
    }
}
