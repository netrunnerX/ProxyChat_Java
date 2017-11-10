package com.example.user.proxychat.ui.activities;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.user.proxychat.R;
import com.example.user.proxychat.presenter.ChatPresenter;
import com.example.user.proxychat.ui.adaptadores.MensajeAdaptador;
import com.example.user.proxychat.data.Usuario;
import com.google.firebase.database.FirebaseDatabase;


/**
 *ChatActivity: actividad que muestra el chat entre contactos
 */
public class ChatActivity extends AppCompatActivity implements ChatPresenter.ChatView {
    private RecyclerView recyclerView;
    private EditText etMensaje;
    private ImageButton botonEnviar;
    private Usuario contacto;
    private Usuario usuario;
    private MensajeAdaptador mensajesAdaptador;
    private ChatPresenter presenter;

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

        presenter = new ChatPresenter(this);

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
        mensajesAdaptador = new MensajeAdaptador(presenter.getMensajesList());
        //Configura el RecyclerView con el adaptador de mensajes
        recyclerView.setAdapter(mensajesAdaptador);

        //Inicia la escucha de mensajes
        obtenerMensajes();

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

        habilitarComponentes(false);
        comprobarBloqueado(usuario.getId(), contacto.getId());

    }

    /**
     * enviarMensaje: metodo encargado de enviar el mensaje
     * @param mensaje texto del mensaje
     */
    public void enviarMensaje(String mensaje) {
        presenter.enviarMensaje(usuario.getApodo(),
                usuario.getId(),
                contacto.getApodo(),
                contacto.getId(), mensaje);

    }


    /**
     * obtenerMensajes: metodo encargado de obtener los mensajes de la base de datos
     * a traves de un escuchador
     */
    public void obtenerMensajes() {
        presenter.obtenerMensajes(usuario.getId(), contacto.getId());

    }

    public void comprobarBloqueado(String usuarioId, String contactoId) {
        presenter.comprobarBloqueado(usuarioId, contactoId);

    }

    public void bloquear(String usuarioId, String contactoId) {
        presenter.bloquear(usuarioId, contactoId);
    }


    /**
     * onCreateOptionsMenu: este metodo se redefine para crear un menu de opciones
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
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

            case R.id.action_bloquear:
                bloquear(usuario.getId(), contacto.getId());

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * setScrollBarMensajes: metodo encargado de desplazar la lista al final
     */
    public void setScrollBarMensajes() {
        //Realiza scroll hasta el final de la lista del RecyclerView
        recyclerView.scrollToPosition(mensajesAdaptador.getItemCount() - 1);
    }

    public void habilitarComponentes(boolean estado) {
        etMensaje.setEnabled(estado);
        botonEnviar.setEnabled(estado);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        Snackbar.make(etMensaje, mensaje, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void limpiarTexto() {
        etMensaje.setText("");
    }

    @Override
    public void notifyDataSetChanged() {
        mensajesAdaptador.notifyDataSetChanged();
    }
}
