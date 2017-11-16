package com.example.user.proxychat.interactor;

import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.ConversacionesViewHolderPresenter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by net on 16/11/17.
 */

public class ConversacionesViewHolderInteractor {

    private ConversacionesViewHolderPresenter presenter;

    public ConversacionesViewHolderInteractor(ConversacionesViewHolderPresenter presenter) {
        this.presenter = presenter;
    }

    public void consultarImagenContacto(String contactoId) {
        //Obtiene una referencia a la base de datos
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        //Realiza una consulta a la base de datos para obtener la URL de la imagen del contacto
        //de la conversacion
        databaseReference.child("usuarios").child(contactoId)
                .addValueEventListener(new ValueEventListener() {
                    /**
                     * onDataChange: este metodo es llamado cuando cambian los datos en la base de datos,
                     * ademas de para obtener un resultado inicial
                     * @param dataSnapshot
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Obtiene un objeto Usuario con los datos del Contacto
                        Usuario contacto = dataSnapshot.getValue(Usuario.class);

                        presenter.cargarImagenContacto(contacto.getImagenUrl());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
