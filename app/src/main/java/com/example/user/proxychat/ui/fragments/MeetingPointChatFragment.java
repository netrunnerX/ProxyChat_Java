package com.example.user.proxychat.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.user.proxychat.R;
import com.example.user.proxychat.ui.adaptadores.MensajeAdaptador;
import com.example.user.proxychat.data.MeetingPoint;
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
 * MeetinPointChatFragment: Fragment que muestra el chat del punto de encuentro
 */
public class MeetingPointChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText etMensaje;
    private ImageButton botonEnviar;
    private MeetingPoint meetingPoint;
    private Usuario usuario;
    private List<Mensaje> mensajes;
    private DatabaseReference databaseReference;
    private MensajeAdaptador mensajesAdaptador;

    public MeetingPointChatFragment() {
        // Required empty public constructor
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
        View rootView = inflater.inflate(R.layout.mensajes, container, false);
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

        //Obtiene el Bundle pasado al Fragment cuando se creo
        Bundle bundle = getArguments();

        //Obtiene un objeto MeetingPoint con los datos del punto de encuentro a partir del Bundle
        meetingPoint = (MeetingPoint) bundle.getSerializable("meetingPoint");
        //Obtiene un objeto Usuario con los datos del usuario a partir del Bundle
        usuario = (Usuario)bundle.getSerializable("usuario");

        //Inicializa la lista de mensajes
        mensajes = new ArrayList<>();

        //Obtiene una referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Inicializa el RecyclerView
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);

        //Crea un gestor LinearLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //setStackFromEnd: cuando el RecyclerView rellena su contenido, empieza desde el final
        //de la vista, asi se muestra la lista desde el final, y cuando abrimos el teclado la
        //lista se ajusta al borde del teclado
        linearLayoutManager.setStackFromEnd(true);
        //Configura el RecyclerView con el LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);
        //Crea un adaptador de mensajes
        mensajesAdaptador = new MensajeAdaptador(mensajes);
        //Configura el RecyclerView con el adaptador de mensajes
        recyclerView.setAdapter(mensajesAdaptador);

        //Inicia la escucha de mensajes
        iniciarEscuchadorMensajes();

        //Obtiene una intancia del campo de texto
        etMensaje = (EditText)view.findViewById(R.id.etMensaje);

        //Obtiene una instancia para el boton de enviar
        botonEnviar = (ImageButton) view.findViewById(R.id.botonEnviarProxy);
        //Configura un escuchador de clicks en el boton
        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Si el campo de texto no esta vacio
                if (!TextUtils.isEmpty(etMensaje.getText().toString())) {
                    //Llama al metodo encargado de enviar el mensaje
                    enviarMensaje(etMensaje.getText().toString());
                }
            }
        });
    }

    /**
     * enviarMensaje: metodo encargado de enviar el mensaje
     * @param mensaje texto del mensaje
     */
    public void enviarMensaje(String mensaje) {

        //Crea un SimpleDateFormat utilizado para dar formato a la fecha del mensaje
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        //Se introducen en listas el apodo y el id del punto de encuentro para incluirlos en el constructor
        //del nuevo mensaje
        List<String> receptores = new ArrayList<>();
        receptores.add(meetingPoint.getNombre());
        List<String> idReceptores = new ArrayList<>();
        idReceptores.add(meetingPoint.getId());

        //Crea un objeto mensaje
        Mensaje mensajeTexto = new Mensaje(usuario.getApodo(), usuario.getId(), receptores,
                idReceptores, mensaje, 0, simpleDateFormat.format(new Date()));

        //Almacena en la base de datos el mensaje
        databaseReference.child("mensajes").child("meeting_points")
                .child(meetingPoint.getId()).push().setValue(mensajeTexto);

        //Vacia el campo de texto
        etMensaje.setText("");
    }

    /**
     * setScrollBarMensajes: metodo encargado de desplazar la lista al final
     */
    public void setScrollBarMensajes() {
        //Realiza scroll hasta el final de la lista del RecyclerView
        recyclerView.scrollToPosition(mensajesAdaptador.getItemCount() - 1);
    }


    /**
     * iniciarEscuchadorMensajes: metodo encargado de obtener los mensajes de la base de datos
     * a traves de un escuchador
     */
    public void iniciarEscuchadorMensajes() {
        //Establece un escuchador en la referencia de la base de datos donde se almacenan los mensajes
        //del punto de encuentro, obtiene los ultimos 40 nodos (limitToLast(40))
        databaseReference.child("mensajes").child("meeting_points")
                .child(meetingPoint.getId()).limitToLast(40).addChildEventListener(new ChildEventListener() {

            /**
             * onChildAdded: este metodo se ejecuta cuando un nuevo nodo hijo es agregado a la referencia
             * de la base de datos (un nuevo mensaje). Este metodo tambien se ejecuta al crear el
             * escuchador, obteniendo un resultado inicial.
             * @param dataSnapshot
             * @param s
             */
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //Obtiene el mensaje a partir del DataSnapShot
                Mensaje mensajeTexto = dataSnapshot.getValue(Mensaje.class);
                //Si el emisor del mensaje es el usuario
                if (!mensajeTexto.getIdEmisor().equals(usuario.getId()))
                    //Establece el tipo de mensaje en 1 (mensaje entrante)
                    mensajeTexto.setTipoMensaje(1);

                //Añade el mensaje a la lista de mensajes
                mensajes.add(mensajeTexto);
                //Notifica al adaptador que el conjunto de datos ha cambiado, de forma que este
                //se actualice
                mensajesAdaptador.notifyDataSetChanged();
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
}
