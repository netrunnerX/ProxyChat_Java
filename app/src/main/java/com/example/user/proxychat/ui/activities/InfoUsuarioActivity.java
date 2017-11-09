package com.example.user.proxychat.ui.activities;


import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.proxychat.R;
import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.InfoUsuarioPresenter;

/**
 * InfoUsuarioActivity: actividad encargada de mostrar informacion de un usuario, asi como permitir
 * agregar al usuario a la lista de contactos o enviarle un mensaje
 */
public class InfoUsuarioActivity extends AppCompatActivity implements InfoUsuarioPresenter.InfoUsuarioView {

    private TextView tvApodo;
    private Usuario contacto;
    private Usuario usuario;
    private ImageView fotoPerfil;
    private InfoUsuarioPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_usuario);

        //Configura el ActionBar para mostrar el boton de ir atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Obtiene el bundle a traves del intent
        Bundle bundle = getIntent().getExtras();
        //Obtiene del bundle el objeto Usuario del contacto
        contacto = (Usuario)bundle.getSerializable("contacto");
        //Obtiene del bundle el objeto Usuario del usuario
        usuario = (Usuario)bundle.getSerializable("usuario");

        //Inicializa el TextView del nombre del contacto
        tvApodo = (TextView)findViewById(R.id.tvApodoPerfil);
        //Establece el nombre del contacto en el TextView
        tvApodo.setText(contacto.getApodo());

        //Inicializa el ImageView que contendra la imagen de perfil del contacto
        fotoPerfil = (ImageView) findViewById(R.id.ivFotoPerfil);

        //Crea un objeto Uri a partir de la URL de la imagen del contacto
        Uri uri = Uri.parse(contacto.getImagenUrl());

        //Descarga la imagen de la URL y la carga en el ImageView utilizando la libreria Glide
        Glide.with(getApplicationContext())
                .load(uri)
                .apply(new RequestOptions().placeholder(R.drawable.iconouser).centerCrop())
                .into(fotoPerfil);

        presenter = new InfoUsuarioPresenter(this);

        AppCompatButton botonAgregarContacto = (AppCompatButton) findViewById(R.id.botonAgregarContacto);
        botonAgregarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarContacto(usuario.getId(), contacto.getId());
            }
        });

    }

    /**
     * agregarContacto: metodo encargado de agregar el contacto a la lista de contactos
     * @param usuarioId Id del usuario al que pertenece el dispositivo
     * @param contactoId Id del contacto a agregar
     */
    public void agregarContacto(String usuarioId, String contactoId) {
        presenter.agregarContacto(usuario.getId(), contacto.getId());
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

    public void mostrarMensaje(String mensaje) {
        Snackbar.make(tvApodo, mensaje, Snackbar.LENGTH_LONG).show();
    }
}
