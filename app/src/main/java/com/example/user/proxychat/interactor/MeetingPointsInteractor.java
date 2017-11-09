package com.example.user.proxychat.interactor;

import android.support.annotation.NonNull;

import com.example.user.proxychat.data.MeetingPoint;
import com.example.user.proxychat.presenter.MeetingPointsPresenter;
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

public class MeetingPointsInteractor {

    private MeetingPointsPresenter presenter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String usuarioId;
    private List<String> meetingPoints;

    public MeetingPointsInteractor(MeetingPointsPresenter presenter, String usuarioId) {
        this.presenter = presenter;
        this.usuarioId = usuarioId;
        meetingPoints = new ArrayList<>();
    }

    public void consultarMeetingPoints() {
        //Realiza una consulta a la base de datos para obtener los puntos de encuentro del usuario
        databaseReference.child("contactos").child("usuarios").child(usuarioId).child("meeting_points")
                .addChildEventListener(new ChildEventListener() {

                    /**
                     * onChildAdded: este metodo se ejecuta cuando un nuevo nodo hijo es agregado a la referencia
                     * de la base de datos (un punto de encuentro agregado). Este metodo tambien se ejecuta al crear el
                     * escuchador, obteniendo un resultado inicial.
                     * @param dataSnapshot
                     * @param s
                     */
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        //Obtiene la clave del nodo, este es el id del punto de encuentro
                        String keyMeetingPoint = dataSnapshot.getKey();
                        //Añade el id a la lista de puntos de encuentro
                        meetingPoints.add(keyMeetingPoint);
                        presenter.notifyDataSetChanged();
                        presenter.actualizarNumeroMeetingPoints(meetingPoints.size());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        //Elimina el id del punto de encuentro de la lista de puntos de encuentro
                        meetingPoints.remove(dataSnapshot.getKey());
                        //Actualiza el numero de puntos de encuentro
                        presenter.notifyDataSetChanged();
                        presenter.actualizarNumeroMeetingPoints(meetingPoints.size());
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void visitarMeetingPoint(int position) {
        //Realiza una consulta a la base de datos para obtener los datos del punto de encuentro
        databaseReference.child("meeting_points").child(meetingPoints.get(position))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Obtiene un objeto MeetingPoint con los datos del punto de encuentro
                        //a partir del DataSnapshot
                        MeetingPoint meetingPoint = dataSnapshot.getValue(MeetingPoint.class);

                        //Si el objeto MeetingPoint no es nulo
                        if (meetingPoint != null) {
                            presenter.iniciarMeetingPoint(meetingPoint);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void eliminarMeetingPoint(int position) {
        //Elimina de la base de datos el nodo correspondiente al punto de encuentro en la lista
        //de puntos de encuentro del usuario,
        //se añaden ademas escuchadores que realizaran acciones dependiendo de si la operacion
        //fue o no un exito
        databaseReference.child("contactos").child("usuarios").child(usuarioId)
                .child("meeting_points").child(meetingPoints.get(position)).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    /**
                     * onSuccess: metodo que se ejecuta si la operacion fue un exito
                     * @param aVoid
                     */
                    @Override
                    public void onSuccess(Void aVoid) {
                        presenter.mostrarMensaje("Punto de encuentro eliminado");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            /**
             * onFailure: metodo que se ejecuta si la operacion fallo
             * @param e
             */
            @Override
            public void onFailure(@NonNull Exception e) {
                presenter.mostrarMensaje("No se ha podido eliminar el punto de encuentro");
            }
        });
    }

    public List<String> getMeetingPoints() {
        return meetingPoints;
    }


}
