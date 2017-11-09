package com.example.user.proxychat.interactor;

import com.example.user.proxychat.data.MeetingPoint;
import com.example.user.proxychat.presenter.MeetingPointsViewHolderPresenter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by netx on 9/23/17.
 */

public class MeetingPointsViewHolderInteractor {

    private MeetingPointsViewHolderPresenter presenter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public MeetingPointsViewHolderInteractor(MeetingPointsViewHolderPresenter presenter) {
        this.presenter = presenter;
    }

    public void obtenerDatosMeetingPoint(String meetingPointId) {
        //Realiza una consulta para obtener los datos del punto de encuentro
        databaseReference.child("meeting_points").child(meetingPointId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Obtiene un objeto MeetingPoint con los datos del punto de encuentro a partir
                        //del DataSnapshot
                        MeetingPoint meetingPoint = dataSnapshot.getValue(MeetingPoint.class);

                        presenter.mostrarMeetingPoint(meetingPoint);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
