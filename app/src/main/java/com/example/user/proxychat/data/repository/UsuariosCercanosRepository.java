package com.example.user.proxychat.data.repository;

import com.example.user.proxychat.data.Usuario;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by net on 22/11/17.
 */

public class UsuariosCercanosRepository {
    private Map<String, Marker> usuariosCercanosMark;
    private Map<String, Usuario> usuariosCercanosPerfil;
    private static UsuariosCercanosRepository usuariosCercanosRepository;

    private UsuariosCercanosRepository() {
        usuariosCercanosMark = new HashMap<>();
        usuariosCercanosPerfil = new HashMap<>();
    }

    public static UsuariosCercanosRepository getInstance() {
        if (usuariosCercanosRepository == null) {
            synchronized (UsuariosCercanosRepository.class) {
                if (usuariosCercanosRepository == null) {
                    usuariosCercanosRepository = new UsuariosCercanosRepository();
                }
            }
        }

        return usuariosCercanosRepository;
    }

    public void putUsuarioCercano(Usuario usuario, Marker marker) {
        usuariosCercanosPerfil.put(usuario.getId(), usuario);
        usuariosCercanosMark.put(usuario.getId(), marker);
    }

    public void removeUsuarioCercano(String usuarioId) {
        //Elimina el marcador del mapa
        usuariosCercanosMark.get(usuarioId).remove();
        //Elimina el marcador de la lista de marcadores
        usuariosCercanosMark.remove(usuarioId);
        //Elimina el usuario de la lista de usuarios
        usuariosCercanosPerfil.remove(usuarioId);
    }

    public void actualizarLocalizacionUsuarioCercano(String usuarioId, LatLng latLng) {
        usuariosCercanosMark.get(usuarioId).setPosition(latLng);
    }

    public void setMarkersVisible(boolean estado) {
        //Itera sobre el mapa de marcadores ocultandolos del mapa
        for (Map.Entry<String, Marker> entry : usuariosCercanosMark.entrySet()) {
            entry.getValue().setVisible(false);
        }
    }

    public Map<String, Usuario> getUsuariosCercanosPerfil() {
        return usuariosCercanosPerfil;
    }

    public Usuario getUsuarioPerfil(String usuarioId) {
        return usuariosCercanosPerfil.get(usuarioId);
    }
}
