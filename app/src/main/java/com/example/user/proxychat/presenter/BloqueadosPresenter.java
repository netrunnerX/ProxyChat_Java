package com.example.user.proxychat.presenter;

import com.example.user.proxychat.interactor.BloqueadosInteractor;

import java.util.List;

/**
 * Created by net on 9/11/17.
 */

public class BloqueadosPresenter {

    private BloqueadosView view;
    private BloqueadosInteractor interactor;

    public BloqueadosPresenter(BloqueadosView view) {
        this.view = view;
        interactor = new BloqueadosInteractor(this);
    }

    public void obtenerUsuariosBloqueados(String usuarioId) {
        interactor.obtenerUsuariosBloqueados(usuarioId);
    }

    public void notifyDataSetChanged() {
        if (view != null)
            view.notifyDataSetChanged();
    }

    public List<String> getBloqueados() {
        return interactor.getBloqueados();
    }

    public void actualizarNumeroBloqueados(int numero) {
        if (view != null)
            view.actualizarNumeroBloqueados(numero);
    }

    public interface BloqueadosView {
        void notifyDataSetChanged();
        void actualizarNumeroBloqueados(int numero);
    }
}
