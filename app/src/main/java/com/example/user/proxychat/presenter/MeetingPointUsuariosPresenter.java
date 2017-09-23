package com.example.user.proxychat.presenter;

import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.interactor.MeetingPointUsuariosInteractor;

import java.util.List;

/**
 * Created by netx on 9/23/17.
 */

public class MeetingPointUsuariosPresenter {

    private MeetingPointUsuariosView view;
    private MeetingPointUsuariosInteractor interactor;

    public MeetingPointUsuariosPresenter(MeetingPointUsuariosView view, String usuarioId, String meetingPointId) {
        this.view = view;
        interactor = new MeetingPointUsuariosInteractor(this, usuarioId, meetingPointId);
    }

    public void consultarUsuarios() {
        interactor.consultarUsuarios();
    }

    public void obtenerInformacionUsuario(int position) {
        interactor.obtenerInformacionUsuario(position);
    }

    public void mostrarInfoUsuario(Usuario usuario) {
        if (view != null)
            view.mostrarInfoUsuario(usuario);
    }

    public void actualizarNumeroUsuarios(int numeroContactos) {
        if (view != null)
            view.actualizarNumeroUsuarios(numeroContactos);
    }

    public void notifyDataSetChanged() {
        if (view != null)
            view.notifyDataSetChanged();
    }

    public List<String> getListaUsuarios() {
        return interactor.getUsuarios();
    }

    public interface MeetingPointUsuariosView {
        void actualizarNumeroUsuarios(int numeroContactos);
        void notifyDataSetChanged();
        void mostrarInfoUsuario(Usuario usuario);
    }
}
