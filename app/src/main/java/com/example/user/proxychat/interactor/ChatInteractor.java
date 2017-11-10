package com.example.user.proxychat.interactor;


import com.example.user.proxychat.data.Mensaje;
import com.example.user.proxychat.presenter.ChatPresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by net on 10/11/17.
 */

public class ChatInteractor {

    private ChatPresenter presenter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private List<Mensaje> mensajes = new ArrayList<>();

    public ChatInteractor(ChatPresenter presenter) {
        this.presenter = presenter;
    }

    public void enviarMensaje(String nombreEmisor,
                              String idEmisor,
                              String nombreReceptor,
                              String idReceptor,
                              String mensaje) {
        //Crea un SimpleDateFormat utilizado para dar formato a la fecha del mensaje
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        //Se introducen en listas el apodo y el id del contacto para incluirlos en el constructor
        //del nuevo mensaje
        List<String> receptores = new ArrayList<>();
        receptores.add(nombreReceptor);
        List<String> idReceptores = new ArrayList<>();
        idReceptores.add(idReceptor);

        //Crea un objeto mensaje
        Mensaje mensajeTexto = new Mensaje(nombreEmisor, idEmisor, receptores,
                idReceptores, mensaje, 0, simpleDateFormat.format(new Date()));

        //Almacena en la base de datos el mensaje
        databaseReference.child("mensajes").child("usuarios")
                .child(idEmisor)
                .child(idReceptor).push().setValue(mensajeTexto);

        presenter.limpiarTexto();
    }

    public void obtenerMensajes(final String usuarioId, String contactoId) {
        //Establece un escuchador en la referencia de la base de datos donde se almacenan los mensajes
        //entre usuarios, obtiene los ultimos 40 nodos (limitToLast(40))
        databaseReference.child("mensajes").child("usuarios")
                .child(usuarioId)
                .child(contactoId).limitToLast(40).addChildEventListener(new ChildEventListener() {

            /**
             * onChildAdded: este metodo se ejecuta cuando un nuevo nodo hijo es agregado a la referencia
             * de la base de datos (un nuevo mensaje). Este metodo tambien se ejecuta al crear el
             * escuchador, obteniendo un resultado inicial.
             * @param dataSnapshot
             * @param s
             */
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //Obtiene el mensaje a partir del DataSnapShot
                Mensaje mensajeTexto = dataSnapshot.getValue(Mensaje.class);
                //Si el receptor del mensaje es el usuario
                if (mensajeTexto.getIdReceptores().get(0).equals(usuarioId))
                    //Establece el tipo de mensaje en 1 (mensaje entrante)
                    mensajeTexto.setTipoMensaje(1);

                //Añade el mensaje a la lista de mensajes
                mensajes.add(mensajeTexto);
                //Notifica al adaptador que el conjunto de datos ha cambiado, de forma que este
                //se actualice
                presenter.notifyDataSetChanged();
                //Realiza scroll hasta el final de la lista en el RecyclerView
                presenter.setScrollBarMensajes();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void comprobarBloqueado(final String usuarioId, final String contactoId) {
        databaseReference.child("contactos")
                .child("usuarios")
                .child(usuarioId)
                .child("bloqueados")
                .child(contactoId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean bBloqueado = dataSnapshot.getValue(Boolean.class);

                if (bBloqueado == null) {
                    databaseReference.child("contactos")
                            .child("usuarios")
                            .child(usuarioId)
                            .child("bloqueados")
                            .child(contactoId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean bBloqueado = dataSnapshot.getValue(Boolean.class);

                            if (bBloqueado == null)
                                presenter.habilitarComponentes(true);
                            else
                                presenter.habilitarComponentes(false);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                    presenter.habilitarComponentes(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void bloquear(final String usuarioId, final String contactoId) {
        databaseReference.child("contactos")
                .child("usuarios")
                .child(usuarioId)
                .child("bloqueados")
                .child(contactoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean bBloqueado = dataSnapshot.getValue(Boolean.class);

                if (bBloqueado == null) {
                    databaseReference.child("contactos")
                            .child("usuarios")
                            .child(usuarioId)
                            .child("bloqueados")
                            .child(contactoId)
                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            presenter.mostrarMensaje("Contacto bloqueado");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            presenter.mostrarMensaje("No se ha podido bloquear al contacto");
                        }
                    });
                }
                else {
                    presenter.mostrarMensaje("El contacto ya se encuentra bloqueado");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public List<Mensaje> getMensajesList() {
        return mensajes;
    }
}
