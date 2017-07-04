package com.example.user.proxychat.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.user.proxychat.R;
import com.example.user.proxychat.adaptadores.MensajeAdaptador;
import com.example.user.proxychat.modelos.Mensaje;
import com.example.user.proxychat.modelos.Usuario;
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
 *ChatActivity: actividad que muestra el chat entre contactos
 */
public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText etMensaje;
    private ImageButton botonEnviar;
    private Usuario contacto;
    private Usuario usuario;
    private List<Mensaje> mensajes;
    private DatabaseReference databaseReference;
    private MensajeAdaptador mensajesAdaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mensajes);

        //Configura el ActionBar para mostrar el boton de ir atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Obtiene el bundle del intent
        Bundle bundle = getIntent().getExtras();
        //Obtiene el objeto Usuario correspondiente al contacto
        contacto = (Usuario)bundle.getSerializable("contacto");
        //Establece el titulo de la actividad con el apodo del contacto
        this.setTitle(contacto.getApodo());
        //Obtiene el objecto Usuario del usuario
        usuario = (Usuario)bundle.getSerializable("usuario");

        //Inicializa la lista de mensajes
        mensajes = new ArrayList<>();
        //Obtiene una referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Inicializa el RecyclerView a traves del cual se muestra la lista de mensajes
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);

        //Crea un gestor LinearLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
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
        etMensaje = (EditText)findViewById(R.id.etMensaje);

        //Obtiene una instancia para el boton de enviar
        botonEnviar = (ImageButton) findViewById(R.id.botonEnviarProxy);
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

        //Se introducen en listas el apodo y el id del contacto para incluirlos en el constructor
        //del nuevo mensaje
        List<String> receptores = new ArrayList<>();
        receptores.add(contacto.getApodo());
        List<String> idReceptores = new ArrayList<>();
        idReceptores.add(contacto.getId());

        //Crea un objeto mensaje
        Mensaje mensajeTexto = new Mensaje(usuario.getApodo(), usuario.getId(), receptores,
                idReceptores, mensaje, 0, simpleDateFormat.format(new Date()));

        //Almacena en la base de datos el mensaje
        databaseReference.child("mensajes").child("usuarios")
                .child(usuario.getId())
                .child(contacto.getId()).push().setValue(mensajeTexto);

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
        //entre usuarios, obtiene los ultimos 40 nodos (limitToLast(40))
        databaseReference.child("mensajes").child("usuarios")
                .child(usuario.getId())
                .child(contacto.getId()).limitToLast(40).addChildEventListener(new ChildEventListener() {

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
                //Si el receptor del mensaje es el usuario
                if (mensajeTexto.getIdReceptores().get(0).equals(usuario.getId()))
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

    /**
     * onOptionsItemSelected: en este metodo se realizan los acciones para cada item de menu cuando estos
     * son seleccionados
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //Si el item corresponde con el boton de ir atras
            case android.R.id.home:
                //Termina la actividad
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
