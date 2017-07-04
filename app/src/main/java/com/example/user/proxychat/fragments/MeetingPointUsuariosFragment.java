package com.example.user.proxychat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.proxychat.interfaces.OnItemClickListener;
import com.example.user.proxychat.R;
import com.example.user.proxychat.activities.InfoUsuarioActivity;
import com.example.user.proxychat.adaptadores.UsuariosAdaptador;
import com.example.user.proxychat.modelos.MeetingPoint;
import com.example.user.proxychat.modelos.Usuario;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saul Castillo Forte on 21/05/17.
 */

/**
 * MeetingPointUsuariosFragment: Fragment que muestra los usuarios que participan en un punto de encuentro
 */
public class MeetingPointUsuariosFragment extends Fragment implements OnItemClickListener {

    private RecyclerView recyclerView;
    private UsuariosAdaptador usuariosAdaptador;
    private List<String> usuarios;
    private DatabaseReference databaseReference;
    private Usuario usuario;
    private MeetingPoint meetingPoint;
    private TextView tvNumeroUsuarios;

    public MeetingPointUsuariosFragment() {
    }

    /**
     * onCreateView: metodo que es llamado a la hora de crear la vista del Fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_meetingpoint_usuarios, container, false);
        return rootView;
    }

    /**
     * onViewCreated: este metodo es llamado una vez que la vista ha sido creada
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Obtiene el Bundle pasado al Fragment a la hora de crearlo
        Bundle bundle = getArguments();
        //Obtiene un objeto Usuario con los datos del usuario a partir del Bundle
        usuario = (Usuario) bundle.getSerializable("usuario");
        //Obtiene un objeto MeetingPoint con los datos del punto de encuentro a partir del Bundle
        meetingPoint = (MeetingPoint) bundle.getSerializable("meetingPoint");

        //Inicializa el RecyclerView
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerviewContactos);

        //Inicializa el TextView que muestra el numero de usuarios
        tvNumeroUsuarios = (TextView)view.findViewById(R.id.tvNumeroContactos);

        //Crea un gestor LinearLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //Configura el RecyclerView con el LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);
        //Inicializa la lista de usuarios
        usuarios = new ArrayList<>();

        //Crea un adaptador de usuarios
        usuariosAdaptador = new UsuariosAdaptador(getContext(), usuarios, usuario.getId());
        //Configura el RecyclerView con el adaptador de usuarios
        recyclerView.setAdapter(usuariosAdaptador);
        //Establece un escuchador de clicks para el adaptador, el esuchador es el propio Fragment
        usuariosAdaptador.setOnItemClickListener(this);

        //Obtiene una referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Realiza una consulta a la base de datos para obtener los usuarios que participan en el
        //punto de encuentro
        databaseReference.child("contactos").child("meeting_points").child(meetingPoint.getId())
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
                        //Actualiza el TextView que muestra el numero de usuarios
                        tvNumeroUsuarios.setText("Usuarios: " + usuarios.size());
                        //Notifica al adaptador que el conjunto de datos ha cambiado, de forma que este
                        //se actualice
                        usuariosAdaptador.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * onClick: metodo que se ejecuta cuando el usuario pulsa sobre uno de los items del RecyclerView
     * @param view item pulsado
     * @param position posicion del item dentro del RecyclerView
     */
    @Override
    public void onClick(View view, int position) {
        //Obtiene el id del usuario que corresponde con el item pulsado
        String keyUsuario = usuarios.get(position);

        //Si el id del usuario no es el del propio usuario
        if (!keyUsuario.equals(usuario.getId())) {
            //Realiza una consulta a la base de datos para obtener los datos del usuario
            databaseReference.child("usuarios").child(keyUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Obtiene un objeto Usuario con los datos del usuario a partir del DataSnapshot
                    Usuario usuarioContacto = dataSnapshot.getValue(Usuario.class);
                    //Crea un Bundle
                    Bundle bundle = new Bundle();
                    //Añade al Bundle el objeto Usuario con los datos del usuario consultado
                    bundle.putSerializable("contacto", usuarioContacto);
                    //Añade al Bundle el objeto Usuario con los datos del propio usuario
                    bundle.putSerializable("usuario", usuario);
                    //Crea un Intent utilizado para iniciar la actividad de informacion del usuario
                    Intent intent = new Intent(getContext(), InfoUsuarioActivity.class);
                    //Añade el Bundle al Intent
                    intent.putExtras(bundle);
                    //Inicia la actividad
                    startActivity(intent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
