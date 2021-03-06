package com.example.user.proxychat.adaptadores;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
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

import java.util.List;

/**
 * Created by netx on 7/20/17.
 */

public class InvitacionAdaptador extends RecyclerView.Adapter<InvitacionAdaptador.InvitacionViewHolder>{

    private List<String> invitaciones;
    private Context context;
    private String idUsuario;

    /**
     * Constructor por defecto
     * @param invitaciones lista de invitaciones
     */
    public InvitacionAdaptador(Context context, String idUsuario, List<String> invitaciones){
        this.context = context;
        this.idUsuario = idUsuario;
        this.invitaciones = invitaciones;
    }

    /**
     * onCreateViewHolder: este metodo se ejecuta a la hora de crear un nuevo ViewHolder.
     * Un ViewHolder es un contenedor para un item del RecyclerView
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public InvitacionAdaptador.InvitacionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_invitaciones, parent, false);
        return new InvitacionAdaptador.InvitacionViewHolder(v);
    }

    /**
     * onBindViewHolder: este metodo lo llama el RecyclerView a la hora de mostrar el item
     * en una posicion determinada
     * @param holder ViewHolder con los datos a mostrar
     * @param position posicion en el RecyclerView donde mostrara el item
     */
    @Override
    public void onBindViewHolder(final InvitacionAdaptador.InvitacionViewHolder holder, final int position) {

        final String idContacto = invitaciones.get(position);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("usuarios").child(idContacto).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario contacto = dataSnapshot.getValue(Usuario.class);

                holder.tvNombre.setText(contacto.getApodo());

                Uri fotoUri = Uri.parse(contacto.getImagenUrl());

                Glide.with(context.getApplicationContext())
                        .load(fotoUri)
                        .apply(new RequestOptions().placeholder(R.drawable.iconouser).centerCrop())
                        .into(holder.fotoPerfil);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                databaseReference.child("contactos")
                        .child("usuarios")
                        .child(idUsuario)
                        .child("usuarios")
                        .child(invitaciones.get(position)).setValue(true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(view,
                                        "Contacto agregado a la lista de contactos",
                                        Snackbar.LENGTH_LONG).show();

                                databaseReference.child("invitaciones")
                                        .child("usuarios")
                                        .child(idUsuario)
                                        .child(invitaciones.get(position)).removeValue();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(view,
                                        "No se ha podido agregar el contacto",
                                        Snackbar.LENGTH_LONG).show();
                            }
                });

            }
        });

        holder.btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    /**
     * getItemCount: este metodo devuelve el total de items en el RecyclerView
     * @return
     */
    @Override
    public int getItemCount() {

        //Como es equivalente al tamaño de la lista de invitaciones, se devuelve este
        return invitaciones.size();
    }

    /**
     * MensajesViewHolder: clase que define un ViewHolder personalizado de mensajes
     */
    static class InvitacionViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView tvNombre;
        ImageView fotoPerfil;
        ImageButton btAceptar;
        ImageButton btCancelar;

        /**
         * Constructor parametrizado
         * @param v vista del item
         */
        InvitacionViewHolder(View v) {
            super(v);

            //Instancia el CardView
            cardView = (CardView) v.findViewById(R.id.cardViewInvitaciones);
            //Instancia el TextView que muestra el nombre del emisor
            tvNombre = (TextView) v.findViewById(R.id.tvNombreContacto);

            fotoPerfil = (ImageView) v.findViewById(R.id.ivFotoPerfil);

            btAceptar = (ImageButton) v.findViewById(R.id.btAceptar);
            btCancelar = (ImageButton) v.findViewById(R.id.btCancelar);

        }
    }
}
