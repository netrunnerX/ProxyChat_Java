package com.example.user.proxychat.interactor;


import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.MeetingPointUsuariosPresenter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by netx on 9/23/17.
 */

public class MeetingPointUsuariosInteractor {

    private MeetingPointUsuariosPresenter presenter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String meetingPointId, usuarioId;
    private List<String> usuarios;

    public MeetingPointUsuariosInteractor(MeetingPointUsuariosPresenter presenter, String usuarioId,
                                          String meetingPointId) {
        this.presenter = presenter;
        this.usuarioId = usuarioId;
        this.meetingPointId = meetingPointId;
        usuarios = new ArrayList<>();
    }

    public void consultarUsuarios() {
        //Realiza una consulta a la base de datos para obtener los usuarios que participan en el
        //punto de encuentro
        databaseReference.child("contactos").child("meeting_points").child(meetingPointId)
                .addChildEventListener(new ChildEventListener() {

                    /**
                     * onChildAdded: este metodo se ejecuta cuando un nuevo nodo hijo es agregado a la referencia
                     * de la base de datos (un nuevo usuario agregado al punto de encuentro).
                     * Este metodo tambien se ejecuta al crear el
                     * escuchador, obteniendo un resultado inicial.
                     * @param dataSnapshot
                     * @param s
                     */
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        //Obtiene la clave que identifica al usuario a partir del DataSnapshot
                        String keyContacto = dataSnapshot.getKey();

                        //Añade el id del usuario a la lista de usuarios
                        usuarios.add(keyContacto);

                        presenter.notifyDataSetChanged();
                        presenter.actualizarNumeroUsuarios(usuarios.size());

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        //Obtiene la clave que identifica al usuario a partir del DataSnapshot
                        String keyContacto = dataSnapshot.getKey();

                        //Añade el id del usuario a la lista de usuarios
                        usuarios.add(keyContacto);

                        presenter.notifyDataSetChanged();
                        presenter.actualizarNumeroUsuarios(usuarios.size());
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void obtenerInformacionUsuario(int position) {
        //Obtiene el id del usuario que corresponde con el item pulsado
        String keyUsuario = usuarios.get(position);

        //Si el id del usuario no es el del propio usuario
        if (!keyUsuario.equals(usuarioId)) {
            //Realiza una consulta a la base de datos para obtener los datos del usuario
            databaseReference.child("usuarios").child(keyUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Obtiene un objeto Usuario con los datos del usuario a partir del DataSnapshot
                    Usuario usuarioContacto = dataSnapshot.getValue(Usuario.class);

                    presenter.mostrarInfoUsuario(usuarioContacto);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public List<String> getUsuarios() {
        return usuarios;
    }
}
