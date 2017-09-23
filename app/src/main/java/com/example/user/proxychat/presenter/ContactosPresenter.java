package com.example.user.proxychat.presenter;

import com.example.user.proxychat.interactor.ContactosInteractor;
import com.example.user.proxychat.data.Usuario;

import java.util.List;

/**
 * Created by netx on 9/23/17.
 */

public class ContactosPresenter {

    private ContactosView view;
    private ContactosInteractor interactor;

    public ContactosPresenter(ContactosView view, String usuarioId) {
        this.view = view;
        interactor = new ContactosInteractor(this, usuarioId);
    }

    public void mostrarMensaje(String mensaje) {
        if (view != null)
            view.mostrarMensaje(mensaje);
    }

    public void consultarContactos() {
        interactor.consultarContactos();
    }

    public void notifyDataSetChanged() {
        if (view != null)
            view.notifyDataSetChanged();
    }

    public void actualizarNumeroContactos(int numeroContactos) {
        if (view != null)
            view.actualizarNumeroContactos(numeroContactos);
    }

    public List<String> getListaContactos() {
        return interactor.getContactos();
    }

    public void eliminarContacto(int position) {
        interactor.eliminarContacto(position);
    }

    public void chatearConContacto(int position) {
        interactor.chatearConContacto(position);
    }

    public void iniciarChat(Usuario contacto) {
        if (view != null)
            view.iniciarChat(contacto);
    }

    public void mostrarDialogoEliminarContacto(int position) {
        if (view != null)
            view.mostrarDialogoEliminarContacto(position);
    }


    public interface ContactosView {

        void mostrarMensaje(String mensaje);

        void actualizarNumeroContactos(int numeroContactos);

        void notifyDataSetChanged();

        void iniciarChat(Usuario contacto);

        void mostrarDialogoEliminarContacto(int position);
    }
}
