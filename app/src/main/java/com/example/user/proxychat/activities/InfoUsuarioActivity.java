package com.example.user.proxychat.activities;


import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
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

/**
 * InfoUsuarioActivity: actividad encargada de mostrar informacion de un usuario, asi como permitir
 * agregar al usuario a la lista de contactos o enviarle un mensaje
 */
public class InfoUsuarioActivity extends AppCompatActivity {

    private TextView tvApodo;
    private DatabaseReference databaseReference;
    private Usuario contacto;
    private Usuario usuario;
    private ImageView fotoPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_usuario);

        //Configura el ActionBar para mostrar el boton de ir atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Obtiene el bundle a traves del intent
        Bundle bundle = getIntent().getExtras();
        //Obtiene del bundle el objeto Usuario del contacto
        contacto = (Usuario)bundle.getSerializable("contacto");
        //Obtiene del bundle el objeto Usuario del usuario
        usuario = (Usuario)bundle.getSerializable("usuario");

        //Obtiene una referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Inicializa el TextView del nombre del contacto
        tvApodo = (TextView)findViewById(R.id.tvApodoPerfil);
        //Establece el nombre del contacto en el TextView
        tvApodo.setText(contacto.getApodo());

        //Inicializa el ImageView que contendra la imagen de perfil del contacto
        fotoPerfil = (ImageView) findViewById(R.id.ivFotoPerfil);

        //Crea un objeto Uri a partir de la URL de la imagen del contacto
        Uri uri = Uri.parse(contacto.getImagenUrl());

        //Descarga la imagen de la URL y la carga en el ImageView utilizando la libreria Glide
        Glide.with(getApplicationContext())
                .load(uri)
                .apply(new RequestOptions().placeholder(R.drawable.iconouser).centerCrop())
                .into(fotoPerfil);


        AppCompatButton botonAgregarContacto = (AppCompatButton) findViewById(R.id.botonAgregarContacto);
        botonAgregarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarContacto(v);
            }
        });

    }

    /**
     * agregarContacto: metodo encargado de agregar el contacto a la lista de contactos
     * @param v
     */
    public void agregarContacto(final View v) {

        //Realiza una consulta en la referencia de la base de datos donde se encuentran almacenados
        //los contactos del usuario para comprobar si el contacto ya existe en la lista
        databaseReference.child("contactos").child("usuarios").child(usuario.getId()).child("usuarios")
                .child(contacto.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Obtiene el valor booleano que contiene el nodo contacto
                Boolean bContacto = dataSnapshot.getValue(Boolean.class);

                //Si el valor no es nulo, significa que el nodo del contacto existe en la lista,
                //por lo que no es necesario agregarlo
                if (bContacto != null) {
                    //Muestra un Snackbar informando al usuario de que el contacto ya existe en la lista
                    //de contactos
                    Snackbar.make(v, "El usuario ya existe en la lista de contactos",
                            Snackbar.LENGTH_LONG).show();
                }
                //Si el contacto no existe en la lista
                else {

                    databaseReference.child("invitaciones")
                            .child("usuarios")
                            .child(usuario.getId())
                            .child(contacto.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean b = dataSnapshot.getValue(Boolean.TYPE);

                            if (b == null) {
                                //Almacena en la base de datos el nuevo contacto
                                databaseReference.child("invitaciones")
                                        .child("usuarios")
                                        .child(usuario.getId())
                                        .child(contacto.getId())
                                        .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    /**
                                     * onSuccess: se ejecuta si la operacion se realizo satisfactoriamente
                                     * @param aVoid
                                     */
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Muestra un Snackbar informando al usuario de que el contacto h
                                        // a sido añadido
                                        //a la lista de contactos
                                        Snackbar.make(v, "Petición de contacto enviada",
                                                Snackbar.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    /**
                                     * onFailure: se ejecuta si la operacion fallo
                                     * @param e
                                     */
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Muestra un Snackbar informando al usuario de que hubo un error en la
                                        //operacion
                                        Snackbar.make(v, "Error al enviar la petición de contacto",
                                                Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else {
                                Snackbar.make(v, "Ya has enviado una petición de contacto al usuario",
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    /**
     * onOptionsItemSelected: en este metodo se realizan los acciones para cada item de menu cuando estos
     * son seleccionados
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //Si el item corresponde con el boton de ir atras
            case android.R.id.home:
                //Termina la actividad
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
