package com.example.user.proxychat.interactor;

import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.BloqueadosViewHolderPresenter;
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

public class BloqueadosViewHolderInteractor {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private BloqueadosViewHolderPresenter presenter;

    public BloqueadosViewHolderInteractor(BloqueadosViewHolderPresenter presenter) {
        this.presenter = presenter;
    }

    public void desbloquearUsuario(String usuarioId, String bloqueadoId) {
        databaseReference.child("contactos")
                .child("usuarios")
                .child(usuarioId)
                .child("bloqueados")
                .child(bloqueadoId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        presenter.mostrarMensaje("Usuario desbloqueado");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                presenter.mostrarMensaje("Error, no se ha podido desbloquear al usuario");
            }
        });
    }

    public void obtenerUsuarioBloqueado(String bloqueadoId) {
        databaseReference.child("usuarios").child(bloqueadoId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Obtiene un objeto Usuario con los datos del usuario a partir del DataSnapshot
                        Usuario usuarioBloqueado = dataSnapshot.getValue(Usuario.class);

                        presenter.mostrarBloqueado(usuarioBloqueado);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}
