package com.example.user.proxychat.interactor;

import com.example.user.proxychat.data.Conversacion;
import com.example.user.proxychat.data.Mensaje;
import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.ConversacionesPresenter;
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
 * Created by net on 15/11/17.
 */

public class ConversacionesInteractor {

    private ConversacionesPresenter presenter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private List<Conversacion> conversaciones = new ArrayList<>();

    public ConversacionesInteractor(ConversacionesPresenter presenter) {
        this.presenter = presenter;
    }

    public void obtenerConversaciones(final String usuarioId) {
        //Realiza una consulta a la referencia donde se encuentra la bandeja de mensajes privados del usuario
        databaseReference.child("mensajes").child("usuarios")
                .child(usuarioId).addChildEventListener(new ChildEventListener() {
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
                if (!idReceptor.equals(usuarioId)) {
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
                presenter.notifyDataSetChanged();
                presenter.actualizarNumeroConversaciones(conversaciones.size());

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
                if (!idReceptor.equals(usuarioId)) {
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

                        presenter.notifyDataSetChanged();
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

                        presenter.notifyDataSetChanged();

                        presenter.actualizarNumeroConversaciones(conversaciones.size());
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

    public void iniciarChat(final Usuario usuario, int contactoPosition) {
        //Obtiene el id de contacto a partir de la lista de conversaciones
        String idContacto = conversaciones.get(contactoPosition).getIdContacto();

        //Realiza una consulta a la base de datos para obtener los datos del contacto
        databaseReference.child("usuarios").child(idContacto).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Obtiene un objeto Usuario con los datos del contacto a partir del DataSnaphot
                Usuario contacto = dataSnapshot.getValue(Usuario.class);

                presenter.iniciarActividadChat(usuario, contacto);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void eliminarConversacion(String usuarioId, int conversacionPosition) {
        //Elimina de la base de datos el nodo correspondiente al contacto de la conversacion
        //en la bandeja de mensajes del usuario
        //se añaden ademas escuchadores que realizaran acciones dependiendo de si la operacion
        //fue o no un exito
        databaseReference.child("mensajes").child("usuarios").child(usuarioId)
                .child(conversaciones.get(conversacionPosition).getIdContacto()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    /**
                     * onSuccess: metodo que se ejecuta si la operacion fue un exito
                     * @param aVoid
                     */
                    @Override
                    public void onSuccess(Void aVoid) {
                        presenter.mostrarMensaje("Conversacion eliminada");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            /**
             * onFailure: metodo que se ejecuta si la operacion fallo
             * @param e
             */
            @Override
            public void onFailure(Exception e) {
                presenter.mostrarMensaje("No se ha podido eliminar la conversacion");
            }
        });
    }

    public List<Conversacion> getConversacionesList() {
        return conversaciones;
    }
}
