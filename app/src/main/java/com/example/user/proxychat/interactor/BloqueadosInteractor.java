package com.example.user.proxychat.interactor;

import com.example.user.proxychat.presenter.BloqueadosPresenter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by net on 9/11/17.
 */

public class BloqueadosInteractor {

    private BloqueadosPresenter presenter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    List<String> bloqueados = new ArrayList<>();

    public BloqueadosInteractor(BloqueadosPresenter presenter) {
        this.presenter = presenter;
    }

    public void obtenerUsuariosBloqueados(String usuarioId) {
        databaseReference.child("contactos")
                .child("usuarios")
                .child(usuarioId)
                .child("bloqueados").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                bloqueados.add(id);
                presenter.notifyDataSetChanged();
                presenter.actualizarNumeroBloqueados(bloqueados.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String id = dataSnapshot.getKey();
                bloqueados.remove(id);
                presenter.notifyDataSetChanged();
                presenter.actualizarNumeroBloqueados(bloqueados.size());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public List<String> getBloqueados() {
        return bloqueados;
    }
}
