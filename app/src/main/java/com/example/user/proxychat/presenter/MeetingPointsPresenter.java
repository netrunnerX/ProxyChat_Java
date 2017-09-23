package com.example.user.proxychat.presenter;

import com.example.user.proxychat.data.MeetingPoint;
import com.example.user.proxychat.interactor.MeetingPointsInteractor;

import java.util.List;

/**
 * Created by netx on 9/23/17.
 */

public class MeetingPointsPresenter {

    private MeetingPointsView view;
    private MeetingPointsInteractor interactor;

    public MeetingPointsPresenter(MeetingPointsView view, String usuarioId) {
        this.view = view;
        interactor = new MeetingPointsInteractor(this, usuarioId);
    }

    public void consultarMeetingPoints() {
        interactor.consultarMeetingPoints();
    }

    public List<String> getListaMeetingPoints() {
        return interactor.getMeetingPoints();
    }

    public void actualizarNumeroMeetingPoints(int numeroMeetingPoints) {
        if (view != null)
            view.actualizarNumeroMeetingPoints(numeroMeetingPoints);
    }

    public void notifyDataSetChanged() {
        if (view != null)
            view.notifyDataSetChanged();
    }

    public void iniciarMeetingPoint(MeetingPoint meetingPoint) {
        if (view != null)
            view.iniciarMeetingPoint(meetingPoint);
    }

    public void visitarMeetingPoint(int position) {
        interactor.visitarMeetingPoint(position);
    }

    public void mostrarDialogoEliminarMeetingPoint(int position) {
        if (view != null)
            view.mostrarDialogoEliminarMeetingPoint(position);
    }

    public void eliminarMeetingPoint(int position) {
        interactor.eliminarMeetingPoint(position);
    }

    public void mostrarMensaje(String mensaje) {
        if (view != null)
            view.mostrarMensaje(mensaje);
    }

    public interface MeetingPointsView {
        void actualizarNumeroMeetingPoints(int numeroMeetingPoints);
        void notifyDataSetChanged();
        void iniciarMeetingPoint(MeetingPoint meetingPoint);
        void mostrarDialogoEliminarMeetingPoint(int position);
        void mostrarMensaje(String mensaje);
    }
}
