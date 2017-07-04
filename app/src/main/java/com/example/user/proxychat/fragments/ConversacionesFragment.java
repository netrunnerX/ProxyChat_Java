package com.example.user.proxychat.fragments;

/**
 * Created by Saul Castillo Forte on 02/04/2017.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.proxychat.interfaces.OnItemClickListener;
import com.example.user.proxychat.interfaces.OnItemLongClickListener;
import com.example.user.proxychat.R;
import com.example.user.proxychat.activities.ChatActivity;
import com.example.user.proxychat.activities.ContactosActivity;
import com.example.user.proxychat.activities.MainActivity;
import com.example.user.proxychat.modelos.Conversacion;
import com.example.user.proxychat.adaptadores.ConversacionesAdaptador;
import com.example.user.proxychat.modelos.Mensaje;
import com.example.user.proxychat.modelos.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ConversacionesFragment: Fragment que muestra la lista de conversaciones
 */
public class ConversacionesFragment extends Fragment implements OnItemClickListener,
        OnItemLongClickListener {

    private RecyclerView recyclerView;
    private ConversacionesAdaptador conversacionesAdaptador;
    private List<Conversacion> conversaciones;
    private DatabaseReference databaseReference;
    private FloatingActionButton floatingActionButton;
    private Usuario usuario;
    private TextView tvNumeroConversaciones;


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
        //Se crea una nueva vista cargando en esta el layout de conversaciones
        View rootView = inflater.inflate(R.layout.fragment_conversaciones, container, false);
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

        //Obtiene el objeto Usuario contenido en la actividad principal
        usuario = ((MainActivity)getActivity()).getUsuario();
        //Inicializa el RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        //Inicializa el TextView que muestra el numero de conversaciones
        tvNumeroConversaciones = (TextView) view.findViewById(R.id.tvNumeroConversaciones);

        //Crea un gestor LinearLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //Configura el RecyclerView con el gestor LinearLayout
        recyclerView.setLayoutManager(linearLayoutManager);

        //Inicializa la lista de conversaciones
        conversaciones = new ArrayList<>();

        //Inicializa el adaptador de conversaciones
        conversacionesAdaptador = new ConversacionesAdaptador(getContext(), conversaciones);
        //Configura un OnItemClickListener para el adaptador, pasando como esuchador el propio Fragment
        conversacionesAdaptador.setOnItemClickListener(this);
        //Configura un OnItemLongClickListener para el adaptador, pasando como esuchador el propio Fragment
        conversacionesAdaptador.setOnItemLongClickListener(this);
        //Configura como adaptador del RecyclerView el adaptador de conversaciones
        recyclerView.setAdapter(conversacionesAdaptador);

        //Obtiene una referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Inicia el escuchador encargado de obtener los datos de los usuarios
        iniciarEscuchador();

        //Inicializa el FloatingActionButon
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        //Establece un escuchador de clicks para el boton
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            /**
             * onClick: metodo que se ejecuta cuando se pulsa sobre el boton flotante
             * @param v
             */
            @Override
            public void onClick(View v) {
                //Crea un Bundle
                Bundle bundle = new Bundle();
                //Añade el objeto Usuario con los datos de nuestro usuario al Bundle
                bundle.putSerializable("usuario", ((MainActivity)getActivity()).getUsuario());
                //Crea un Intent utilizado para iniciar la actividad de contactos
                Intent intent = new Intent(getContext(), ContactosActivity.class);
                //Añade el Bundle al Intent
                intent.putExtras(bundle);

                //Inicia la actividad
                startActivity(intent);
            }
        });


    }

    /**
     * iniciarEscuchador: este metodo se encarga de realizar una consulta a la base de datos para obtener
     * los datos de cada conversacion
     */
    public void iniciarEscuchador() {
        //Realiza una consulta a la referencia donde se encuentra la bandeja de mensajes privados del usuario
        databaseReference.child("mensajes").child("usuarios")
                .child(usuario.getId()).addChildEventListener(new ChildEventListener() {
            /**
             * onChildAdded: este metodo se ejecuta para obtener un resultado inicial, y despues
             * se ejecutara cada vez que se añade un nuevo nodo (por ejemplo en el caso de que se almacene
             * un mensaje de un contanto que no se encuentre previamente en la bandeja de mensajes del usuario)
             * @param dataSnapshot
             * @param s
             */
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Declara un objeto Mensaje que contendra el ultimo mensaje
                Mensaje ultimoMensaje = null;

                //Obtiene un iterador con los nodos hijos del Snapshot, estos nodos hijos son los
                //mensajes de uno de los contactos
                Iterator<DataSnapshot> dataSnaps = dataSnapshot.getChildren().iterator();

                //Recorre los nodos asignando el mensaje a ultimoMensaje en cada iteracion,
                //de esta forma cuando termina el bucle dispondremos del ultimo mensaje
                while (dataSnaps.hasNext()) {
                    ultimoMensaje = dataSnaps.next().getValue(Mensaje.class);
                }

                //Obtiene el id del emisor
                String idEmisor = ultimoMensaje.getIdEmisor();
                //Obtiene el nombre del emisor
                String emisor = ultimoMensaje.getEmisor();
                //Obtiene el id del receptor
                String idReceptor = ultimoMensaje.getIdReceptores().get(0);
                //Obtiene el nombre del receptor
                String receptor = ultimoMensaje.getReceptores().get(0);

                //Declara una variable para almacenar el nombre del contacto de la conversacion
                String contacto;
                //Declara una variable para almacenar el id del contacto de la conversacion
                String idContacto;

                //Si el receptor del mensaje no es el propio usuario
                if (!idReceptor.equals(usuario.getId())) {
                    //Establece como contacto el receptor del mensaje
                    contacto = receptor;
                    idContacto = idReceptor;
                }
                //Por otra parte, si el receptor del mensaje es el usuario
                else {
                    //Establece como contacto el emisor del mensaje
                    contacto = emisor;
                    idContacto = idEmisor;
                }

                //Obtiene el texto del mensaje
                String ultMensaje = ultimoMensaje.getMensaje();

                //Si la longitud del texto del mensaaje es mayor de 25 caracteres
                if (ultMensaje.length() > 25) {
                    //Acota el mensaje a 25 caracteres y le concatena puntos suspensivos
                    ultMensaje = ultMensaje.substring(0, 25) + "...";
                }

                //Crea una conversacion con los datos obtenidos
                Conversacion conversacion = new Conversacion(contacto, idContacto, ultMensaje);

                //Añade la conversacion a la lista
                conversaciones.add(conversacion);
                //Notifica al adaptador que hubo cambios en el conjunto de datos, de forma
                //que este actualice el RecyclerView
                conversacionesAdaptador.notifyDataSetChanged();
                //Actualiza el TextView que muestra el numero de conversaciones
                tvNumeroConversaciones.setText("Conversaciones: " + conversaciones.size());

            }

            /**
             * onChildChanged: este metodo se ejecuta cuando el valor que contiene uno de los nodos
             * cambia, esto es en el caso de que sea almacenado un nuevo mensaje de un contacto
             * que ya se encuentra presente en la bandeja de mensajes del usuario
             * @param dataSnapshot
             * @param s
             */
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //Declara un objeto Mensaje que contendra el ultimo mensaje
                Mensaje ultimoMensaje = null;

                //Obtiene un iterador con los nodos hijos del Snapshot, estos nodos hijos son los
                //mensajes de uno de los contactos
                Iterator<DataSnapshot> dataSnaps = dataSnapshot.getChildren().iterator();

                //Recorre los nodos asignando el mensaje a ultimoMensaje en cada iteracion,
                //de esta forma cuando termina el bucle dispondremos del ultimo mensaje
                while (dataSnaps.hasNext()) {
                    ultimoMensaje = dataSnaps.next().getValue(Mensaje.class);
                }

                //Obtiene el nombre del emisor
                String emisor = ultimoMensaje.getEmisor();
                //Obtiene el id del emisor
                String idEmisor = ultimoMensaje.getIdEmisor();
                //Obtiene el nombre del receptor
                String receptor = ultimoMensaje.getReceptores().get(0);
                //Obtiene el id del receptor
                String idReceptor = ultimoMensaje.getIdReceptores().get(0);

                //Declara una variable para almacenar el nombre del contacto de la conversacion
                String contacto;
                //Declara una variable para almacenar el id del contacto de la conversacion
                String idContacto;

                //Si el receptor del mensaje no es el propio usuario
                if (!idReceptor.equals(usuario.getId())) {
                    //Establece como contacto el receptor del mensaje
                    contacto = receptor;
                    idContacto = idReceptor;
                }
                //Por otra parte, si el receptor del mensaje es el usuario
                else {
                    //Establece como contacto el emisor del mensaje
                    contacto = emisor;
                    idContacto = idEmisor;
                }

                //Obtiene el texto del mensaje
                String ultMensaje = ultimoMensaje.getMensaje();

                //Si la longitud del texto del mensaaje es mayor de 25 caracteres
                if (ultMensaje.length() > 25) {
                    //Acota el mensaje a 25 caracteres y le concatena puntos suspensivos
                    ultMensaje = ultMensaje.substring(0, 25) + "...";
                }

                //Crea una conversacion con los datos del mensaje
                Conversacion conversacion = new Conversacion(contacto, idContacto, ultMensaje);

                //Recorre la lista de conversaciones
                for (int i = 0; i < conversaciones.size(); i++) {
                    //Si encuentra en la lista la conversacion cuyo id es el id del contacto
                    if (conversaciones.get(i).getIdContacto().equals(conversacion.getIdContacto())) {
                        //Actualiza el ultimo mensaje de la conversacion
                        conversaciones.get(i).setUltimoMensaje(conversacion.getUltimoMensaje());
                        //Notifica al adaptador que hubo cambios en el conjunto de datos, de forma
                        //que este actualice el RecyclerView
                        conversacionesAdaptador.notifyDataSetChanged();
                        //Sale del bucle
                        break;
                    }
                }

            }

            /**
             * onChildRemoved: metodo que se ejecuta cuando un nodo es eliminado de la base de datos,
             * este es el caso en el que el usuario elimina una conversacion de la lista de conversaciones,
             * lo que hace que el nodo que hace referencia al contacto en la bandeja de mensajes del usuario
             * sea eliminado
             * @param dataSnapshot
             */
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //Obtiene el id del contacto, que corresponde con la clave del nodo
                String key = dataSnapshot.getKey();

                //Recorre la lista de conversaciones
                for (int i = 0; i<conversaciones.size(); i++) {
                    //Si el id de la conversacion coincide con el id del contacto
                    if (conversaciones.get(i).getIdContacto().equals(key)) {
                        //Elimina de la lista la conversacion
                        conversaciones.remove(i);

                        //Notifica al adaptador que hubo cambios en el conjunto de datos, de forma
                        //que este actualice el RecyclerView
                        conversacionesAdaptador.notifyDataSetChanged();
                        //Actualiza el TextView que muestra el numero de conversaciones
                        tvNumeroConversaciones.setText("Conversaciones: " + conversaciones.size());
                        break;
                    }
                }
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

        //Obtiene el id de contacto a partir de la lista de conversaciones
        String idContacto = conversaciones.get(position).getIdContacto();

        //Realiza una consulta a la base de datos para obtener los datos del contacto
        databaseReference.child("usuarios").child(idContacto).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Obtiene un objeto Usuario con los datos del contacto a partir del DataSnaphot
                Usuario contacto = dataSnapshot.getValue(Usuario.class);
                //Crea un Bundle
                Bundle bundle = new Bundle();
                //Añade al Bundle el objeto Usuario con los datos del contacto
                bundle.putSerializable("contacto", contacto);
                //Añade al Bundle el objeto Usuario con los datos del usuario
                bundle.putSerializable("usuario", usuario);

                //Crea un Intent utilizado para iniciar la actividad de chat
                Intent intent = new Intent(getContext(), ChatActivity.class);
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

    /**
     * onLongClick: metodo que se ejecuta cuando el usuario realiza una pulsacion larga sobre uno de
     * los items del RecyclerView
     * @param view item pulsado
     * @param position posicion del item dentro del RecyclerView
     * @return
     */
    @Override
    public boolean onLongClick(View view, final int position) {

        //Inicializa un array CharSequence que contiene la descripcion para cada opcion del menu contextual
        final CharSequence[] items = {"Eliminar Conversación"};

        //Crea un constructor de dialogos
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //Establece el titulo del dialogo
        builder.setTitle("Opciones");
        //Configura el dialogo con los items (opciones) que tendra, tambien se añade un escuchador
        //que recibira los eventos de click en cada una de las opciones del menu contextual
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        //Elimina de la base de datos el nodo correspondiente al contacto de la conversacion
                        //en la bandeja de mensajes del usuario
                        //se añaden ademas escuchadores que realizaran acciones dependiendo de si la operacion
                        //fue o no un exito
                        databaseReference.child("mensajes").child("usuarios").child(usuario.getId())
                                .child(conversaciones.get(position).getIdContacto()).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    /**
                                     * onSuccess: metodo que se ejecuta si la operacion fue un exito
                                     * @param aVoid
                                     */
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Muestra un Snackbar informando al usuario de que la conversacion se ha eliminado
                                        Snackbar.make(getView(), "Conversacion eliminada",
                                                Snackbar.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            /**
                             * onFailure: metodo que se ejecuta si la operacion fallo
                             * @param e
                             */
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Muestra un Snackbar informando al usuario del error
                                Snackbar.make(getView(), "No se ha podido eliminar la conversacion",
                                        Snackbar.LENGTH_LONG).show();
                            }
                        });
                        break;
                }
            }
        });

        //Muestra el dialogo
        builder.show();
        return true;
    }
}
