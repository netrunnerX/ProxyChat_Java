package com.example.user.proxychat.interactor;

import android.support.annotation.NonNull;

import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.ContactosPresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by netx on 9/23/17.
 */

public class ContactosInteractor {

    private ContactosPresenter presenter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private List<String> contactos;
    private String usuarioId;

    public ContactosInteractor(ContactosPresenter presenter, String usuarioId) {
        this.presenter = presenter;
        this.usuarioId = usuarioId;
        contactos = new ArrayList<>();
    }

    public void consultarContactos() {
        //Establece un escuchador en la referencia de la base de datos donde se almacenan los contactos
        //del usuario
        databaseReference.child("contactos").child("usuarios").child(usuarioId).child("usuarios")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        //Obtiene el id del contacto a partir del DataSnapShot
                        String keyContacto = dataSnapshot.getKey();

                        //Añade el id del contacto a la lista de contactos
                        contactos.add(keyContacto);

                        presenter.actualizarNumeroContactos(contactos.size());
                        presenter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    /**
                     * onChildRemoved: este metodo se ejecuta cuando un nodo hijo es borrado de la base de datos
                     * @param dataSnapshot
                     */
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        //Elimina el id de contacto de la lista de contactos
                        contactos.remove(dataSnapshot.getKey());
                        presenter.notifyDataSetChanged();
                        //Actualiza el numero de contactos
                        presenter.actualizarNumeroContactos(contactos.size());

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        presenter.mostrarMensaje("Error: " + databaseError.getMessage());
                    }
                });
    }

    public void eliminarContacto(int position) {
        //Elimina de la base de datos el nodo correspondiente al contacto en la lista
        //de contactos del usuario,
        //se añaden ademas escuchadores que realizaran acciones dependiendo de si la operacion
        //fue o no un exito
        databaseReference.child("contactos").child("usuarios").child(usuarioId)
                .child("usuarios").child(contactos.get(position)).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    /**
                     * onSuccess: metodo que se ejecuta si la operacion fue un exito
                     * @param aVoid
                     */
                    @Override
                    public void onSuccess(Void aVoid) {
                        presenter.mostrarMensaje("Contacto eliminado");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            /**
             * onFailure: metodo que se ejecuta si la operacion fallo
             * @param e
             */
            @Override
            public void onFailure(@NonNull Exception e) {
                presenter.mostrarMensaje("No se ha podido eliminar el contacto");
            }
        });
    }

    public void chatearConContacto(int position) {
        String contactoId = contactos.get(position);

        //Establece un escuchador para obtener los datos del contacto
        databaseReference.child("usuarios").child(contactoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Obtiene un objeto Usuario con los datos del contacto a partir del DataSnapshot
                Usuario usuarioContacto = dataSnapshot.getValue(Usuario.class);
                presenter.iniciarChat(usuarioContacto);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                presenter.mostrarMensaje("Error: " + databaseError.getMessage());
            }
        });
    }

    public List<String> getContactos() {
        return contactos;
    }
}
