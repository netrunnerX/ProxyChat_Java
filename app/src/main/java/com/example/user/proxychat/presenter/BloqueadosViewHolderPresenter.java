package com.example.user.proxychat.presenter;

import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.interactor.BloqueadosViewHolderInteractor;

/**
 * Created by net on 9/11/17.
 */

public class BloqueadosViewHolderPresenter {

    private BloqueadosViewHolderView view;
    private BloqueadosViewHolderInteractor interactor;

    public BloqueadosViewHolderPresenter(BloqueadosViewHolderView view) {
        this.view = view;
        interactor = new BloqueadosViewHolderInteractor(this);
    }

    public void desbloquearUsuario(String usuarioId, String bloqueadoId) {
        interactor.desbloquearUsuario(usuarioId, bloqueadoId);
    }

    public void obtenerUsuarioBloqueado(String bloqueadoId) {
        interactor.obtenerUsuarioBloqueado(bloqueadoId);
    }

    public void mostrarBloqueado(Usuario usuarioBloqueado) {
        if (view != null)
            view.mostrarBloqueado(usuarioBloqueado);
    }

    public void mostrarMensaje(String mensaje) {
        if (view != null)
            view.mostrarMensaje(mensaje);
    }

    public interface BloqueadosViewHolderView {
        void mostrarBloqueado(Usuario usuarioBloqueado);
        void mostrarMensaje(String mensaje);
    }
}
