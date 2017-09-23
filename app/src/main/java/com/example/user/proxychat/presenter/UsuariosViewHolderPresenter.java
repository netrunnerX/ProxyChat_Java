package com.example.user.proxychat.presenter;

import com.example.user.proxychat.interactor.UsuariosViewHolderInteractor;
import com.example.user.proxychat.data.Usuario;

/**
 * Created by netx on 9/23/17.
 */

public class UsuariosViewHolderPresenter {

    private UsuariosViewHolderView view;
    private UsuariosViewHolderInteractor interactor;

    public UsuariosViewHolderPresenter(UsuariosViewHolderView view) {
        this.view = view;
        interactor = new UsuariosViewHolderInteractor(this);
    }

    public void obtenerDatosUsuario(String usuarioId) {
        interactor.obtenerDatosUsuario(usuarioId);
    }

    public void mostrarUsuario(Usuario usuario) {
        if (view != null)
            view.mostrarUsuario(usuario);
    }

    public interface UsuariosViewHolderView {
        void mostrarUsuario(Usuario usuario);
    }
}
