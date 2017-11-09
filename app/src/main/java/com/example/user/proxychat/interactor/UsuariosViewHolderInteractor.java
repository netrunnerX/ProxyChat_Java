package com.example.user.proxychat.interactor;

import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.UsuariosViewHolderPresenter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by netx on 9/23/17.
 */

public class UsuariosViewHolderInteractor {

    private UsuariosViewHolderPresenter presenter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public UsuariosViewHolderInteractor(UsuariosViewHolderPresenter presenter) {
        this.presenter = presenter;
    }

    public void obtenerDatosUsuario(String usuarioId) {
        //Realiza una consulta a la base de datos para obtener los datos de un usuario
        databaseReference.child("usuarios").child(usuarioId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Obtiene un objeto Usuario con los datos del usuario a partir del DataSnapshot
                        Usuario contacto = dataSnapshot.getValue(Usuario.class);

                        presenter.mostrarUsuario(contacto);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
