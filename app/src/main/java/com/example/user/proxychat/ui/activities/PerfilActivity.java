package com.example.user.proxychat.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.proxychat.R;
import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.PerfilPresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * PerfilActivity: actividad que muestra el perfil del usuario
 */
public class PerfilActivity extends AppCompatActivity implements PerfilPresenter.PerfilView{

    private int PICK_IMAGE_REQUEST = 1;
    private ImageView ivFotoPerfil;
    private TextView tvApodoPerfil;
    private Usuario usuario;
    private PerfilPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        //Configura el ActionBar para mostrar el boton de ir atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Inicializa el TextView que contiene el nombre del usuario
        tvApodoPerfil = (TextView)findViewById(R.id.tvApodoPerfil);
        //Inicializa el ImageView que contiene la imagen de perfil del usuario
        ivFotoPerfil = (ImageView)findViewById(R.id.ivFotoPerfil);

        presenter = new PerfilPresenter(this);

        //Obtiene del Bundle contenido en el Intent el objeto Usuario con los datos del usuario
        usuario = (Usuario)getIntent().getExtras().getSerializable("usuario");
        //Configura el texto del TextView con el apodo del usuario
        tvApodoPerfil.setText(usuario.getApodo());

        presenter.cargarFotoPerfil(Uri.parse(usuario.getImagenUrl()));

    }

    public void cargarFotoPerfil(Uri imagenUrl) {

        //Carga la imagen del usuario en el ImageView a partir de la URL, utilizando la libreria Glide
        Glide.with(getApplicationContext())
                .load(imagenUrl)
                .apply(new RequestOptions().placeholder(R.drawable.iconouser).centerCrop())
                .into(ivFotoPerfil);
    }

    /**
     * seleccionarFotoPerfil: metodo que se ejecuta cuando el usuario pulsa sobre la imagen de perfil
     * para seleccionar una imagen de perfil nueva
     * @param v
     */
    public void seleccionarFotoPerfil(View v) {
        //Crea un Intent
        Intent intent = new Intent();
        //con intent.setType("image/*") indicamos que en la nueva actividad solo se mostraran imagenes
        intent.setType("image/*");
        //Muestra contenido que el usuario puede escoger, y que devolvera una URI resultante
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //Inicia una nueva actividad que mostrara el seleccionador de imagenes
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * onActivityResult: este metodo se ejecutara una vez finalizada la actividad de seleccion de imagen
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            //Obtiene la URI de la imagen
            Uri uri = data.getData();

            //Declara un InputStream
            InputStream inputStream = null;
            try {
                //Inicializa el InputStream utilizando como fuente de datos la URI de la imagen
                inputStream = getContentResolver().openInputStream(uri);

                presenter.subirImagen(inputStream, usuario);
            } catch (FileNotFoundException e) {
                presenter.mostrarMensaje("No se ha podido cargar la imagen: "
                        + e.getMessage());

            }

        }
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        Snackbar.make(tvApodoPerfil, mensaje, Snackbar.LENGTH_LONG).show();
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
