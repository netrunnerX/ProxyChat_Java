package com.example.user.proxychat.interactor;

import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.InvitacionesViewHolderPresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by net on 16/11/17.
 */

public class InvitacionesViewHolderInteractor {

    private InvitacionesViewHolderPresenter presenter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public InvitacionesViewHolderInteractor(InvitacionesViewHolderPresenter presenter) {
        this.presenter = presenter;
    }

    public void obtenerContacto(String contactoId) {
        databaseReference.child("usuarios").child(contactoId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario contacto = dataSnapshot.getValue(Usuario.class);

                presenter.mostrarDatosContacto(contacto);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void aceptarInvitacion(final String usuarioId, final String contactoId) {
        databaseReference.child("contactos")
                .child("usuarios")
                .child(usuarioId)
                .child("usuarios")
                .child(contactoId).setValue(true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        presenter.mostrarMensaje("Contacto agregado a la lista de contactos");

                        databaseReference.child("invitaciones")
                                .child("usuarios")
                                .child(usuarioId)
                                .child(contactoId).removeValue();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                presenter.mostrarMensaje("No se ha podido agregar el contacto");
            }
        });
    }
}
