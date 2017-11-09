package com.example.user.proxychat.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.user.proxychat.ui.interfaces.OnItemClickListener;
import com.example.user.proxychat.R;
import com.example.user.proxychat.ui.adaptadores.UsuariosAdaptador;
import com.example.user.proxychat.ui.interfaces.OnItemLongClickListener;
import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.ContactosPresenter;

/**
 * ContactosActivity: Actividad que muestra la lista de contactos
 */
public class ContactosActivity extends AppCompatActivity implements OnItemClickListener, OnItemLongClickListener, ContactosPresenter.ContactosView {

    private RecyclerView recyclerView;
    private Usuario usuario;
    private TextView tvNumeroContactos;
    private ContactosPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        //Configura el ActionBar para mostrar el boton de ir atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inicializar();
    }

    private void inicializar() {
        //Obtiene el objeto Usuario correspondiente al usuario a traves del intent
        usuario = (Usuario)getIntent().getExtras().getSerializable("usuario");
        presenter = new ContactosPresenter(this, usuario.getId());


        //Inicializa el TextView que muestra el numero de contactos
        tvNumeroContactos = (TextView)findViewById(R.id.tvNumeroContactos);

        setRecyclerView();
        presenter.consultarContactos();
    }

    private UsuariosAdaptador obtenerAdaptador() {
        UsuariosAdaptador adaptadorContactos =
                new UsuariosAdaptador(this, presenter.getListaContactos(), usuario.getId());

        adaptadorContactos.setOnItemClickListener(this);
        adaptadorContactos.setOnItemLongClickListener(this);

        return adaptadorContactos;
    }

    private void setRecyclerView() {
        //Inicializa el RecyclerView a traves del cual se muestra la lista de contactos
        recyclerView = (RecyclerView)findViewById(R.id.recyclerviewContactos);

        //Crea un gestor LinearLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        //Configura el RecyclerView con el LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);

        //Configura el RecyclerView con el adaptador de contactos
        recyclerView.setAdapter(obtenerAdaptador());

    }

    /**
     * onClick: este metodo se ejecuta cuando el usuario pulsa sobre uno de los items del RecyclerView
     * @param view item que ha sido pulsado
     * @param position posicion del item dentro del RecyclerView
     */
    @Override
    public void onClick(View view, int position) {
        presenter.chatearConContacto(position);
    }

    /**
     * onLongClick: metodo que se ejecuta cuando el usuario realiza una pulsacion larga sobre uno de
     * los items del RecyclerView
     * @param view item pulsado
     * @param position posicion del item dentro del RecyclerView
     * @return
     */
    @Override
    public boolean onLongClick(final View view, final int position) {

        presenter.mostrarDialogoEliminarContacto(position);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void mostrarMensaje(String mensaje) {
        Snackbar.make(recyclerView, mensaje, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void actualizarNumeroContactos(int numeroContactos) {
        tvNumeroContactos.setText("Contactos: " + numeroContactos);
    }

    @Override
    public void notifyDataSetChanged() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void iniciarChat(Usuario contacto) {
        //Crea un bundle
        Bundle bundle = new Bundle();
        //Añade al bundle el objeto Usuario del contacto
        bundle.putSerializable("contacto", contacto);
        //Añade al bundle el objeto Usuario del usuario
        bundle.putSerializable("usuario", usuario);

        //Crea un intent que permitira iniciar la actividad de chat
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        //Añade el bundle al intent
        intent.putExtras(bundle);
        //Inicia la actividad de chat a partir del intent
        startActivity(intent);
    }

    @Override
    public void mostrarDialogoEliminarContacto(final int position) {
        //Inicializa un array CharSequence que contiene la descripcion para cada opcion del menu contextual
        final CharSequence[] items = {"Eliminar contacto"};

        //Crea un constructor de dialogos
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Establece el titulo del dialogo
        builder.setTitle("Opciones");
        //Configura el dialogo con los items (opciones) que tendra, tambien se añade un escuchador
        //que recibira los eventos de click en cada una de las opciones del menu contextual
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        presenter.eliminarContacto(position);
                        break;
                }
            }
        });

        //Muestra el dialogo
        builder.show();
    }
}
