package com.example.user.proxychat.presenter;

import android.content.Context;

import com.example.user.proxychat.data.MeetingPoint;
import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.interactor.MapInteractor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by net on 22/11/17.
 */

public class MapPresenter {

    private MapInteractor interactor;
    private MapView view;

    public MapPresenter(MapView view, String usuarioId, boolean invisible) {
        this.view = view;
        interactor = new MapInteractor(this, usuarioId, invisible);
    }

    public void obtenerLocationUpdates(Context context) {
        interactor.obtenerLocationUpdates(context);
    }

    public void dibujarCirculo(double latitud, double longitud) {
        if (view != null)
            view.dibujarCirculo(latitud, longitud);
    }

    public Marker addMarker(MarkerOptions options) {
        if (view != null)
            return view.addMarker(options);
        return null;
    }

    public void crearMeetingPoint(String usuarioId,
                                  String nombreMeetingPoint,
                                  String descMeetingPoint,
                                  LatLng meetinPointLatLng) {
        interactor.crearMeetingPoint(usuarioId, nombreMeetingPoint, descMeetingPoint, meetinPointLatLng);
    }

    public void ocultarAlertDialog() {
        if (view != null)
            view.ocultarAlertDialog();
    }

    public void ocultarDialogoCarga() {
        if (view != null)
            view.ocultarDialogoCarga();
    }

    public void mostrarMensaje(String mensaje) {
        if (view != null)
            view.mostrarMensaje(mensaje);
    }

    public void cambiarModoInvisible() {
        interactor.cambiarModoInvisible();
    }

    public void setInvisible(boolean estado) {
        if (view != null)
            view.setInvisible(estado);
    }

    public void agregarMeetingPoint(String meetingPointId) {
        interactor.agregarMeetingPoint(meetingPointId);
    }

    public void consultarInfoUsuario(String usuarioId) {
        interactor.consultarInfoUsuario(usuarioId);
    }

    public void iniciarActivityInfoUsuario(Usuario usuario) {
        if (view != null)
            view.iniciarActivityInfoUsuario(usuario);
    }

    public interface MapView {
        void dibujarCirculo(double latitud, double longitud);
        Marker addMarker(MarkerOptions options);
        void ocultarAlertDialog();
        void ocultarDialogoCarga();
        void mostrarMensaje(String mensaje);
        void setInvisible(boolean estado);
        void iniciarActivityInfoUsuario(Usuario usuario);
    }
}
