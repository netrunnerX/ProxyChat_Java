package com.example.user.proxychat.presenter;

import com.example.user.proxychat.data.Mensaje;
import com.example.user.proxychat.interactor.ChatInteractor;

import java.util.List;

/**
 * Created by net on 10/11/17.
 */

public class ChatPresenter {

    private ChatView view;
    private ChatInteractor interactor;

    public ChatPresenter(ChatView view) {
        this.view = view;
        interactor = new ChatInteractor(this);
    }

    public void enviarMensaje(String nombreEmisor,
                              String idEmisor,
                              String nombreReceptor,
                              String idReceptor,
                              String mensaje) {
        interactor.enviarMensaje(nombreEmisor,idEmisor,nombreReceptor,idReceptor,mensaje);
    }

    public void obtenerMensajes(String usuarioId, String contactoId) {
        interactor.obtenerMensajes(usuarioId, contactoId);
    }

    public List<Mensaje> getMensajesList() {
        return interactor.getMensajesList();
    }

    public void limpiarTexto() {
        if (view != null)
            view.limpiarTexto();
    }

    public void habilitarComponentes(boolean estado) {
        if (view != null)
            view.habilitarComponentes(estado);
    }

    public void setScrollBarMensajes() {
        if (view != null)
            view.setScrollBarMensajes();
    }

    public void notifyDataSetChanged() {
        if (view != null)
            view.notifyDataSetChanged();
    }

    public void comprobarBloqueado(String usuarioId, String contactoId) {
        interactor.comprobarBloqueado(usuarioId, contactoId);
    }

    public void bloquear(String usuarioId, String contactoId) {
        interactor.bloquear(usuarioId, contactoId);
    }

    public void mostrarMensaje(String mensaje) {
        if (view != null)
            view.mostrarMensaje(mensaje);
    }

    public interface ChatView {
        void mostrarMensaje(String mensaje);
        void limpiarTexto();
        void habilitarComponentes(boolean estado);
        void setScrollBarMensajes();
        void notifyDataSetChanged();
    }
}
