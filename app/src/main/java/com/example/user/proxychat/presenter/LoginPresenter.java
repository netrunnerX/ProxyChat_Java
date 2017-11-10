package com.example.user.proxychat.presenter;

import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.interactor.LoginInteractor;

/**
 * Created by net on 10/11/17.
 */

public class LoginPresenter {

    private LoginView view;
    private LoginInteractor interactor;

    public LoginPresenter(LoginView view) {
        this.view = view;
        interactor = new LoginInteractor(this);
    }

    public void isUsuarioLogueado() {
        interactor.isUsuarioLogueado();
    }

    public void mostrarProgressDialog() {
        if (view != null)
            view.mostrarProgressDialog();
    }

    public void ocultarProgressDialog() {
        if (view != null)
            view.ocultarProgressDialog();
    }

    public void cargarMainActivity(Usuario usuario) {
        if (view != null)
            view.cargarMainActivity(usuario);
    }

    public void login(String email, String password) {
        interactor.login(email, password);
    }

    public void mostrarMensaje(String mensaje) {
        if (view != null)
            view.mostrarMensaje(mensaje);
    }

    public interface LoginView {
        void mostrarProgressDialog();
        void ocultarProgressDialog();
        void mostrarMensaje(String mensaje);
        void cargarMainActivity(Usuario usuario);
    }
}
