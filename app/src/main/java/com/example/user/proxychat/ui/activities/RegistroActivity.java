package com.example.user.proxychat.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.user.proxychat.BuildConfig;
import com.example.user.proxychat.R;
import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.RegistroPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * RegistroActivity: actividad encargada de mostrar el formulario de registro
 */
public class RegistroActivity extends AppCompatActivity implements RegistroPresenter.RegistroView{

    private EditText etEmail;
    private EditText etPassword;
    private EditText etPassword2;
    private EditText etApodo;
    private ProgressDialog progressDialogRegistro;
    private RegistroPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Configura el ActionBar para mostrar el boton de ir atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Inicializa el campo de texto de email
        etEmail = (EditText) findViewById(R.id.etEmailReg);
        //Inicializa el campo de texto de la contraseña
        etPassword = (EditText) findViewById(R.id.etPasswordReg);
        //Inicializa el campo de texto de la contraseña por duplicado
        etPassword2 = (EditText) findViewById(R.id.etPasswordReg2);
        //Inicializa el campo de texto del apodo
        etApodo = (EditText) findViewById(R.id.etApodoReg);

        setProgressDialogRegistro();

        presenter = new RegistroPresenter(this);

        AppCompatButton botonRegistrar = (AppCompatButton) findViewById(R.id.botonRegistrarse);
        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar(v);
            }
        });

    }

    /**
     * registrar: metodo encargado de comprobar los campos e iniciar el registro del usuario
     * @param v
     */
    public void registrar(final View v) {
        //Comprueba que el campo de texto concuerde con el patron
        if (!etEmail.getText().toString().matches("^[\\w-]+(\\.[\\w-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
            etEmail.setError("Debes introducir un email válido");
            return;
        }

        //Comprueba que el campo de la contraseña no este vacio
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("Debes introducir una contraseña");
            return;
        }

        //Comprueba que el campo de la contraseña por duplicado no este vacio
        if (TextUtils.isEmpty(etPassword2.getText().toString())) {
            etPassword2.setError("Debes introducir la contraseña por duplicado");
            return;
        }

        //Comprueba que las dos contraseñas coincidan
        if (!etPassword.getText().toString().equals(etPassword2.getText().toString())) {
            etPassword.setError("Las contraseñas deben coincidir");
            etPassword2.setError("Las contraseñas deben coincidir");
            return;
        }

        //Comprueba que el campo del apodo no este vacio
        if (TextUtils.isEmpty(etApodo.getText().toString())) {
            etApodo.setError("Debes introducir un apodo");
            return;
        }

        presenter.setCampos(etApodo.getText().toString(),
                etEmail.getText().toString(),
                etPassword.getText().toString());

        presenter.registrar();
    }



    public void setProgressDialogRegistro() {
        //Crea un ProgressDialog, este se utiliza para mostrar un dialogo que informa al usuario de
        //que se esta realizando un proceso
        progressDialogRegistro = new ProgressDialog(this);
        //Establece el mensaje del ProgressDialog
        progressDialogRegistro.setMessage("Por favor, espere...");
        //Establece el dialogo como modal
        progressDialogRegistro.setCancelable(false);
    }

    public void mostrarProgressDialog() {
        progressDialogRegistro.show();
    }

    public void ocultarProgressDialog() {
        progressDialogRegistro.dismiss();
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        Snackbar.make(etApodo, mensaje, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void iniciarActividadPrincipal(Usuario usuario) {
        //Crea un Bundle
        Bundle bundle = new Bundle();
        //Añade al Bundle el objeto Usuario
        bundle.putSerializable("usuario", usuario);
        //Crea un Intent utilizado para iniciar la actividad principal
        Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
        //Añade el bundle al Intent
        intent.putExtras(bundle);
        //Inicia la actividad
        startActivity(intent);

        presenter.ocultarProgressDialog();
        //Termina esta actividad
        finish();
    }

    @Override
    public void setApodoError() {
        etApodo.setError("El apodo ya existe");
    }

    @Override
    public InputStream getImagenPerfilStream() {
        //Obtiene la URI de la imagen de perfil por defecto, que se encuentra
        //almacenada como un recurso drawable
        Uri uri = Uri.parse("android.resource://"+ BuildConfig.APPLICATION_ID+"/" + R.drawable.iconouser);

        try {
            return getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            return null;
        }

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
