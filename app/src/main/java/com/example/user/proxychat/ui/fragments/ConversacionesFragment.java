package com.example.user.proxychat.ui.fragments;

/**
 * Created by Saul Castillo Forte on 02/04/2017.
 */

import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.user.proxychat.presenter.ConversacionesPresenter;
import com.example.user.proxychat.ui.interfaces.OnItemClickListener;
import com.example.user.proxychat.ui.interfaces.OnItemLongClickListener;
import com.example.user.proxychat.R;
import com.example.user.proxychat.ui.activities.ChatActivity;
import com.example.user.proxychat.ui.activities.ContactosActivity;
import com.example.user.proxychat.ui.activities.MainActivity;
import com.example.user.proxychat.ui.adaptadores.ConversacionesAdaptador;
import com.example.user.proxychat.data.Usuario;


/**
 * ConversacionesFragment: Fragment que muestra la lista de conversaciones
 */
public class ConversacionesFragment extends Fragment implements OnItemClickListener,
        OnItemLongClickListener, ConversacionesPresenter.ConversacionesView {

    private RecyclerView recyclerView;
    private ConversacionesAdaptador conversacionesAdaptador;
    private FloatingActionButton floatingActionButton;
    private Usuario usuario;
    private TextView tvNumeroConversaciones;
    private ConversacionesPresenter presenter;


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

        presenter = new ConversacionesPresenter(this);

        //Crea un gestor LinearLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //Configura el RecyclerView con el gestor LinearLayout
        recyclerView.setLayoutManager(linearLayoutManager);


        //Inicializa el adaptador de conversaciones
        conversacionesAdaptador = new ConversacionesAdaptador(getContext(), presenter.getConversacionesList());
        //Configura un OnItemClickListener para el adaptador, pasando como esuchador el propio Fragment
        conversacionesAdaptador.setOnItemClickListener(this);
        //Configura un OnItemLongClickListener para el adaptador, pasando como esuchador el propio Fragment
        conversacionesAdaptador.setOnItemLongClickListener(this);
        //Configura como adaptador del RecyclerView el adaptador de conversaciones
        recyclerView.setAdapter(conversacionesAdaptador);

        //Inicia el escuchador encargado de obtener los datos de los usuarios
        presenter.obtenerConversaciones(usuario.getId());

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
                iniciarActividadContactos(usuario);
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

        presenter.iniciarChat(usuario, position);

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
                        presenter.eliminarConversacion(usuario.getId(), position);
                        break;
                }
            }
        });

        //Muestra el dialogo
        builder.show();
        return true;
    }

    public void iniciarActividadContactos(Usuario usuario) {
        //Crea un Bundle
        Bundle bundle = new Bundle();
        //Añade el objeto Usuario con los datos de nuestro usuario al Bundle
        bundle.putSerializable("usuario", usuario);
        //Crea un Intent utilizado para iniciar la actividad de contactos
        Intent intent = new Intent(getContext(), ContactosActivity.class);
        //Añade el Bundle al Intent
        intent.putExtras(bundle);

        //Inicia la actividad
        startActivity(intent);
    }

    public void iniciarActividadChat(Usuario usuario, Usuario contacto) {
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
    public void notifyDataSetChanged() {
        conversacionesAdaptador.notifyDataSetChanged();
    }

    @Override
    public void actualizarNumeroConversaciones(int numero) {
        tvNumeroConversaciones.setText("Conversaciones: " + numero);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        Snackbar.make(getView(), mensaje, Snackbar.LENGTH_LONG).show();
    }
}
