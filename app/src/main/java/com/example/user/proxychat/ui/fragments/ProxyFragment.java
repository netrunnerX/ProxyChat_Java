package com.example.user.proxychat.ui.fragments;

/**
 * Created by Saul Castillo Forte on 02/04/2017.
 */

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.user.proxychat.R;
import com.example.user.proxychat.ui.activities.MainActivity;
import com.example.user.proxychat.ui.adaptadores.MensajeAdaptador;
import com.example.user.proxychat.data.Mensaje;
import com.example.user.proxychat.data.Usuario;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ProxyFragment: Fragment que muestra el chat por proximidad
 */
public class ProxyFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageButton botonEnviar;
    private EditText etMensaje;
    private DatabaseReference databaseReference;

    private MensajeAdaptador maProxy;
    private List<Mensaje> mensajesProxy;
    private Usuario usuario;

    private List<String> usuariosCercanos;

    /**
     * onCreateView: metodo que es llamado a la hora de crear la vista del Fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mensajes, container, false);
        return rootView;


    }

    /**
     * onViewCreated: este metodo es llamado una vez que la vista ha sido creada
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Obtiene un objeto Usuario con los datos del usuario contenido en la actividad principal
        usuario = ((MainActivity)getActivity()).getUsuario();

        //Obtiene una referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Inicializa la lista de mensajes
        mensajesProxy = new ArrayList<>();

        //Crea un adaptador de mensajes
        maProxy = new MensajeAdaptador(mensajesProxy);

        //Inicializa el RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        //Crea un gestor LinearLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //setStackFromEnd: cuando el RecyclerView rellena su contenido, empieza desde el final
        //de la vista, asi se muestra la lista desde el final, y cuando abrimos el teclado la
        //lista se ajusta al borde del teclado
        linearLayoutManager.setStackFromEnd(true);
        //Configura el RecyclerView con el LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);
        //Configura el RecyclerView con el adaptador de mensajes
        recyclerView.setAdapter(maProxy);

        //Inicia la escucha de mensajes
        iniciarEscuchadorProxy();

        //Obtiene una intancia del campo de texto
        etMensaje = (EditText) getActivity().findViewById(R.id.etMensaje);

        //Obtiene una instancia para el boton de enviar
        botonEnviar = (ImageButton) getActivity().findViewById(R.id.botonEnviarProxy);
        //Configura un escuchador de clicks en el boton
        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Si el campo de texto no esta vacio
                if (!TextUtils.isEmpty(etMensaje.getText().toString())) {
                    //Llama al metodo encargado de enviar el mensaje
                    enviarMensajeProxy(etMensaje.getText().toString());
                }
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /**
     * enviarMensajeProxy: metodo encargado de enviar el mensaje por proximidad
     * @param textoMensaje texto del mensaje
     */
    public void enviarMensajeProxy(String textoMensaje) {

        //Obtiene la lista de usuarios cercanos contenido en el MapFragment
        //Como solo se necesitan las claves, se obtienen llamando al metodo keySet del Map
        //de usuarios cercanos
        usuariosCercanos = new ArrayList<>(((MainActivity)getActivity()).getMapFragment().getUsuariosCercanosPerfil().keySet());

        //Si la lista no es null
        if (usuariosCercanos != null) {

            //Crea un SimpleDateFormat utilizado para dar formato a la fecha del mensaje
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            //Crea un objeto Mensaje con los datos del mensaje a enviar
            Mensaje mensaje = new Mensaje(usuario.getApodo(),usuario.getId(),
                    new ArrayList<String>(), usuariosCercanos, textoMensaje, 0,
                    simpleDateFormat.format(new Date()));

            //Almacena el mensaje en la base de datos. El servidor se encargara despues de redireccionarlo
            //a la bandeja de mensajes de cada usuario cercano, asi como la del propio usuario
            databaseReference.child("mensajesproxy").push().setValue(mensaje);

            //Vacia el campo de texto
            etMensaje.setText("");
        }
        //Si la lista es null
        else {
            //Vacia el campo de texto
            etMensaje.setText("");
            //Muestra un Snackbar informando al usuario del error
            Snackbar.make(getView(), "No se ha podido enviar el mensaje: usuariosCercanos null",
                    Snackbar.LENGTH_SHORT).show();
        }

    }

    /**
     * iniciarEscuchadorProxy: metodo encargado de realizar una consulta a la base de datos
     * y recibir los mensajes almacenados en el nodo proxy de la bandeja de mensajes
     */
    public void iniciarEscuchadorProxy() {

        //Realiza una consulta a la base de datos para obtener los mensajes
        databaseReference.child("mensajes").child("proxy").child(usuario.getId()).addChildEventListener(new ChildEventListener() {

            /**
             * onChildAdded: este metodo se ejecuta cuando un nuevo nodo hijo es agregado a la referencia
             * de la base de datos (un nuevo mensaje). Este metodo tambien se ejecuta al crear el
             * escuchador, obteniendo un resultado inicial.
             * @param dataSnapshot
             * @param s
             */
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //Obtiene un objeto Mensaje con los datos del mensaje a partir del DataSnapshot
                Mensaje mensajeTexto = dataSnapshot.getValue(Mensaje.class);

                //Si el emisor del mensaje no es el propio usuario
                if (!mensajeTexto.getIdEmisor().equals(usuario.getId()))
                    //Establece el tipo de mensaje en 1 (mensaje entrante)
                    mensajeTexto.setTipoMensaje(1);

                //Añade el mensaje a la lista de mensajes
                mensajesProxy.add(mensajeTexto);
                //Notifica al adaptador que el conjunto de datos ha cambiado, de forma que este
                //se actualice
                maProxy.notifyDataSetChanged();
                //Realiza scroll hasta el final de la lista en el RecyclerView
                setScrollBarMensajes();
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
     * setScrollBarMensajes: metodo encargado de desplazar la lista al final
     */
    public void setScrollBarMensajes() {
        //Realiza scroll hasta el final de la lista del RecyclerView
        recyclerView.scrollToPosition(maProxy.getItemCount() - 1);
    }

}