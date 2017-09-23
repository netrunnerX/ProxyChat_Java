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
public class RegistroActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private EditText etPassword2;
    private EditText etApodo;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialogRegistro;
    private Task uploadTask;
    private Usuario usuario;

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
        //Obtiene la referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();

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
        else {
            //Realiza una consulta a la base de datos para comprobar que el apodo no exista
            databaseReference.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Realiza un bucle para comprobar si el apodo coincide con alguno de los usuarios
                    for (DataSnapshot dsUser : dataSnapshot.getChildren()) {
                        //Obtiene un usuario
                        Usuario usr = dsUser.getValue(Usuario.class);
                        //Si el apodo coincide
                        if (usr.getApodo().equals(etApodo.getText().toString())) {
                            //Establece un mensaje de error en el campo y finaliza el metodo
                            etApodo.setError("El apodo ya existe");
                            return;
                        }
                    }
                    //Si no se encontraron coincidencias, el metodo iniciarRegistro es ejecutado
                    iniciarRegistro(v);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    /**
     * iniciarRegistro: metodo encargado de realizar el registro
     */
    public void iniciarRegistro(final View v) {
        //Crea un ProgressDialog, este se utiliza para mostrar un dialogo que informa al usuario de
        //que se esta realizando un proceso
        progressDialogRegistro = new ProgressDialog(this);
        //Establece el mensaje del ProgressDialog
        progressDialogRegistro.setMessage("Por favor, espere...");
        //Establece el dialogo como modal
        progressDialogRegistro.setCancelable(false);
        //Muestra el dialogo
        progressDialogRegistro.show();

        //Obtiene una instancia de FirebaseAuth y realiza una llamada al metodo createUserWithEmailAndPassWord,
        //este metodo se encarga de crear una nueva cuenta de usuario con metodo de inicio de sesion
        //email y contraseña
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    /**
                     * onComplete: este metodo se ejecuta cuando se ha completado el proceso de regstro
                     * (puede haber sido exitoso o haber fallado
                     * @param task
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //Si la operacion fue fallida
                        if (!task.isSuccessful()) {
                            //Cierra el ProgressDialog
                            progressDialogRegistro.dismiss();
                            //Muestra un Snackbar informando al usuario del error
                            Snackbar.make(v, "Ocurrió un error al realizar el registro" + task.getException(),
                                    Snackbar.LENGTH_LONG).show();
                        }
                        //Si fue un exito
                        else {
                            // Actualiza la informacion de usuario
                            final FirebaseUser fbUser = task.getResult().getUser();
                            //UserProfileChangeRequest es un objeto que contiene los nuevos datos del usuario
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(etApodo.getText().toString())
                                    .build();
                            //Se actualiza llamando al metodo updateProfile del objeto FirebaseUser
                            //y pasandole como parametro el objeto UserProfileChangeRequest,
                            //tambien se establece un escuchador que realizara acciones dependiendo
                            //de si la operacion tuvo o no exito
                            fbUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {

                                /**
                                 * onComplete: este metodo se ejecuta cuando se ha completado el proceso de regstro
                                 * (puede haber sido exitoso o haber fallado
                                 * @param task
                                 */
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    //Si la operacion fue un exito
                                    if (task.isSuccessful()) {
                                        //Actualiza los datos del objeto Usuario
                                        usuario = new Usuario(fbUser.getUid(), fbUser.getDisplayName());
                                        //Obtiene el token del dispositivo
                                        String token = FirebaseInstanceId.getInstance().getToken();
                                        //Almacena el token en la base de datos
                                        databaseReference.child("tokens").child(usuario.getId()).child(token).setValue(true);
                                        //usuario.setToken(token);
                                        //Obtiene una referencia al almacen Firebase
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

                                        //Establece como referencia la ruta donde se almacenara la imagen
                                        //de perfil del usuario
                                        storageReference =
                                                storageReference.child("usuarios").child(usuario.getId()).child("perfil.png");

                                        //Obtiene la URI de la imagen de perfil por defecto, que se encuentra
                                        //almacenada como un recurso drawable
                                        Uri uri = Uri.parse("android.resource://"+ BuildConfig.APPLICATION_ID+"/" + R.drawable.iconouser);

                                        //Declara un InputStream
                                        InputStream stream = null;
                                        try {
                                            //Crea una instancia de InputStream a partir de la URI de la imagen
                                            stream = getContentResolver().openInputStream(uri);

                                            //Crea un UploadTask utilizado para subir la imagen al servidor
                                            uploadTask = storageReference.putStream(stream);


                                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                /**
                                                 * onFailure: se ejecuta si hubo un fallo en la
                                                 * operacion de subida
                                                 * @param exception
                                                 */
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    //Muestra un Snackbar informando al usuario del error
                                                    Snackbar.make(v,
                                                            "Ocurrio un error al subir la imagen de perfil"
                                                            +" al servidor.",
                                                            Snackbar.LENGTH_LONG).show();
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                /**
                                                 * onSuccess: se ejecuta si la operacion fue exitosa
                                                 * @param taskSnapshot
                                                 */
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                    //Etiqueta para evitar que el IDE se queje de que el metodo getDownloadUrl
                                                    //solo deberia ser visible por tests o en un ambito private
                                                    @SuppressWarnings("VisibleForTests")
                                                    //Obtiene un objeto URI con la URL de descarga de la imagen
                                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                                    //Actualiza la URL contenida en el objeto Usuario
                                                    usuario.setImagenUrl(downloadUrl.toString());

                                                    //Almacena el objeto Usuario con los datos del usuario
                                                    //en la base de datos
                                                    FirebaseDatabase.getInstance().getReference("usuarios")
                                                            .child(fbUser.getUid()).setValue(usuario);
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
                                                    //Termina esta actividad
                                                    finish();
                                                    //Cierra el ProgressDialog
                                                    progressDialogRegistro.dismiss();
                                                }
                                            });
                                        }
                                        catch (FileNotFoundException e) {
                                            //Cierra el ProgressDialog
                                            progressDialogRegistro.dismiss();

                                            //Muestra un Snackbar informando al usuario del error
                                            Snackbar.make(v, "No se ha podido cargar la imagen: "
                                                    + task.getException().getMessage(),
                                                    Snackbar.LENGTH_LONG).show();
                                        }

                                    }
                                    else {
                                        //Cierra el ProgressDialog
                                        progressDialogRegistro.dismiss();
                                        //Muestra un Snackbar informando al usuario del error
                                        Snackbar.make(v,
                                                "Ocurrió un error al actualizar la informacion del usuario: "
                                                + task.getException().getMessage(),
                                                Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
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
