package com.example.user.proxychat.interactor;

import com.example.user.proxychat.presenter.InfoUsuarioPresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by net on 9/11/17.
 */

public class InfoUsuarioInteractor {

    private InfoUsuarioPresenter presenter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public InfoUsuarioInteractor(InfoUsuarioPresenter presenter) {
        this.presenter = presenter;
    }

    public void agregarContacto(final String usuarioId, final String contactoId) {
        //Realiza una consulta para comprobar si el usuario nos ha bloqueado
        databaseReference.child("contactos")
                .child("usuarios")
                .child(contactoId)
                .child("bloqueados")
                .child(usuarioId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean bBloqueado = dataSnapshot.getValue(Boolean.class);

                //Si no estamos en la lista de bloqueados
                if (bBloqueado == null) {
                    //Realiza una consulta en la referencia de la base de datos donde se encuentran almacenados
                    //los contactos del usuario para comprobar si el contacto ya existe en la lista
                    databaseReference.child("contactos").child("usuarios").child(usuarioId).child("usuarios")
                            .child(contactoId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Obtiene el valor booleano que contiene el nodo contacto
                            Boolean bContacto = dataSnapshot.getValue(Boolean.class);

                            //Si el valor no es nulo, significa que el nodo del contacto existe en la lista,
                            //por lo que no es necesario agregarlo
                            if (bContacto != null) {
                                presenter.mostrarMensaje("El contacto ya existe en tu lista de contactos");
                            }
                            //Si el contacto no existe en la lista
                            else {

                                databaseReference.child("invitaciones")
                                        .child("usuarios")
                                        .child(contactoId)
                                        .child(usuarioId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Boolean b = dataSnapshot.getValue(Boolean.TYPE);

                                        if (b == null) {
                                            //Almacena en la base de datos el nuevo contacto
                                            databaseReference.child("invitaciones")
                                                    .child("usuarios")
                                                    .child(contactoId)
                                                    .child(usuarioId)
                                                    .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                /**
                                                 * onSuccess: se ejecuta si la operacion se realizo satisfactoriamente
                                                 * @param aVoid
                                                 */
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    presenter.mostrarMensaje("Petición de contacto enviada");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                /**
                                                 * onFailure: se ejecuta si la operacion fallo
                                                 * @param e
                                                 */
                                                @Override
                                                public void onFailure(Exception e) {
                                                    presenter.mostrarMensaje("Error al enviar la petición de contacto");
                                                }
                                            });
                                        }
                                        else {
                                            presenter.mostrarMensaje("Ya has enviado una petición de contacto al usuario");
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
                else {
                    presenter.mostrarMensaje("No se puede enviar una peticion a este contacto");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
