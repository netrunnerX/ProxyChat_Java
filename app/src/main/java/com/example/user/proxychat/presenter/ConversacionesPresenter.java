package com.example.user.proxychat.presenter;

import com.example.user.proxychat.data.Conversacion;
import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.interactor.ConversacionesInteractor;

import java.util.List;

/**
 * Created by net on 15/11/17.
 */

public class ConversacionesPresenter {

    private ConversacionesView view;
    private ConversacionesInteractor interactor;

    public ConversacionesPresenter(ConversacionesView view) {
        this.view = view;
        interactor = new ConversacionesInteractor(this);
    }

    public void obtenerConversaciones(String usuarioId) {
        interactor.obtenerConversaciones(usuarioId);
    }

    public void iniciarChat(Usuario usuario, int contactoPosition) {
        interactor.iniciarChat(usuario, contactoPosition);
    }

    public void iniciarActividadChat(Usuario usuario, Usuario contacto) {
        if (view != null)
            view.iniciarActividadChat(usuario, contacto);
    }

    public List<Conversacion> getConversacionesList() {
        return interactor.getConversacionesList();
    }

    public void notifyDataSetChanged() {
        if (view != null)
            view.notifyDataSetChanged();
    }

    public void actualizarNumeroConversaciones(int numero) {
        if (view != null)
            view.actualizarNumeroConversaciones(numero);
    }

    public void mostrarMensaje(String mensaje) {
        if (view != null)
            view.mostrarMensaje(mensaje);
    }

    public void eliminarConversacion(String usuarioId, int conversacionPosition) {
        interactor.eliminarConversacion(usuarioId, conversacionPosition);
    }

    public interface ConversacionesView {
        void notifyDataSetChanged();
        void actualizarNumeroConversaciones(int numero);
        void mostrarMensaje(String mensaje);
        void iniciarActividadChat(Usuario usuario, Usuario contacto);
    }
}
