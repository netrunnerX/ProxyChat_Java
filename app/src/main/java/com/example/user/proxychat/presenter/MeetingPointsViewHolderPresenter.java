package com.example.user.proxychat.presenter;

import com.example.user.proxychat.data.MeetingPoint;
import com.example.user.proxychat.interactor.MeetingPointsViewHolderInteractor;

/**
 * Created by netx on 9/23/17.
 */

public class MeetingPointsViewHolderPresenter {

    private MeetingPointsViewHolderView view;
    private MeetingPointsViewHolderInteractor interactor;

    public MeetingPointsViewHolderPresenter(MeetingPointsViewHolderView view) {
        this.view = view;
        interactor = new MeetingPointsViewHolderInteractor(this);
    }

    public void mostrarMeetingPoint(MeetingPoint meetingPoint) {
        if (view != null)
            view.mostrarMeetingPoint(meetingPoint);
    }

    public void obtenerDatosMeetingPoint(String meetingPointId) {
        interactor.obtenerDatosMeetingPoint(meetingPointId);
    }

    public interface MeetingPointsViewHolderView {
        void mostrarMeetingPoint(MeetingPoint meetingPoint);
    }
}
