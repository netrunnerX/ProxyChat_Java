package com.example.user.proxychat.presenter;

import com.example.user.proxychat.interactor.InvitacionesInteractor;

import java.util.List;

/**
 * Created by net on 16/11/17.
 */

public class InvitacionesPresenter {

    private InvitacionesInteractor interactor;
    private InvitacionesView view;

    public InvitacionesPresenter(InvitacionesView view) {
        this.view = view;
        interactor = new InvitacionesInteractor(this);
    }

    public void obtenerInvitaciones(String usuarioId) {
        interactor.obtenerInvitaciones(usuarioId);
    }

    public void notifyDataSetChanged() {
        if (view != null)
            view.notifyDataSetChanged();
    }

    public void actualizarNumeroInvitaciones(int numero) {
        if (view != null)
            view.actualizarNumeroInvitaciones(numero);
    }

    public List<String> getInvitacionesList() {
        return interactor.getInvitacionesList();
    }

    public interface InvitacionesView {
        void notifyDataSetChanged();
        void actualizarNumeroInvitaciones(int numero);
    }
}
