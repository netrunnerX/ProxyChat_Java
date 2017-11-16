package com.example.user.proxychat.interactor;

import com.example.user.proxychat.presenter.InvitacionesPresenter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by net on 16/11/17.
 */

public class InvitacionesInteractor {

    private InvitacionesPresenter presenter;
    private List<String> invitaciones = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public InvitacionesInteractor(InvitacionesPresenter presenter) {
        this.presenter = presenter;
    }

    public void obtenerInvitaciones(String usuarioId) {

        databaseReference.child("invitaciones")
                .child("usuarios")
                .child(usuarioId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        invitaciones.add(dataSnapshot.getKey());
                        presenter.notifyDataSetChanged();
                        presenter.actualizarNumeroInvitaciones(invitaciones.size());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        invitaciones.remove(dataSnapshot.getKey());
                        presenter.notifyDataSetChanged();
                        presenter.actualizarNumeroInvitaciones(invitaciones.size());
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public List<String> getInvitacionesList() {
        return invitaciones;
    }
}
