package com.example.user.proxychat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.user.proxychat.R;
import com.example.user.proxychat.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * LoginActivity: Es la actividad que se lanza al ejecutar la aplicacion.
 * Muestra un formulario de login y realiza el inicio de sesion en el servidor
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario;
    private EditText etPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private String userId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicializa el campo de texto del nombre de usuario
        etUsuario = (EditText) findViewById(R.id.etUsuario);
        //Inicializa el campo de texto de la contraseña
        etPassword = (EditText) findViewById(R.id.etPassword);

        //Crea un ProgressDialog, este se utiliza para mostrar un dialogo que informa al usuario de
        //que se esta realizando un proceso
        progressDialog = new ProgressDialog(this);
        //Establece el mensaje del ProgressDialog
        progressDialog.setMessage("Iniciando sesion, espere");
        //Establece el ProgressDialog como modal
        progressDialog.setCancelable(false);

        //Obtiene una instancia de FirebaseAuth, utilizada para gestionar la autenticacion en el servidor
        firebaseAuth = FirebaseAuth.getInstance();

        //si getCurrentUser no es null, significa que el usuario ya esta logueado
        if (firebaseAuth.getCurrentUser() != null) {
            //Muestra el ProgressDialog
            progressDialog.show();

            //Obtiene un Objeto FirebaseUser que contiene los datos del usuario
            FirebaseUser user = firebaseAuth.getCurrentUser();
            //Obtiene el id del usuario
            userId = user.getUid();

            //Obtiene una referencia a la base de datos
            databaseReference = FirebaseDatabase.getInstance().getReference();

            //Obtiene el token del dispositivo
            token = FirebaseInstanceId.getInstance().getToken();

            //Crea una consulta a la base de datos para obtener el objeto Usuario del usuario
            databaseReference.child("usuarios").child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Obtiene el objeto Usuario con los datos del usuario a partir del DataSnapshot
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);

                    //Almacena el token del dispositivo en la base de datos
                    databaseReference.child("tokens").child(userId).child(token).setValue(true);
                    //Crea un bundle
                    Bundle bundle = new Bundle();
                    //Añade al bundle el objeto Usuario del usuario
                    bundle.putSerializable("usuario", usuario);

                    //Crea un Intent utilizado para iniciar la actividad principal
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    //Añade el bundle al Intent
                    intent.putExtras(bundle);

                    //Cierra esta actividad
                    finish();

                    //inicia la nueva actividad
                    startActivity(intent);

                    //Cierra el ProgressDialog
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        AppCompatButton botonLogin = (AppCompatButton) findViewById(R.id.botonLogin);
        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

        AppCompatButton botonRegistro = (AppCompatButton) findViewById(R.id.botonRegistrar);
        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar(v);
            }
        });

    }

    /**
     * login: metodo encargado de realizar el inicio de sesion utilizando los datos del formulario
     * @param v
     */
    public void login(final View v) {

        //Comprobar campos
        if (TextUtils.isEmpty(etUsuario.getText().toString())) {
            etUsuario.setError("Debes introducir un email");
            return;
        }
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("Debes introducir una contraseña");
            return;
        }

        //Muestra el ProgressDialog
        progressDialog.show();

        //singInWithEmailAndPassword es llamado para realizar el inicio de sesion en el servidor
        //utilizando como metodo de autenticacion email y contraseña
        firebaseAuth.signInWithEmailAndPassword(etUsuario.getText().toString(), etPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    /**
                     * onComplete: se ejecuta cuando la operacion de inicio de sesion se ha completado.
                     * El inicio de sesion puede haber sido un exito o haber fallado
                     * @param task
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //Si la operacion fue exitosa
                        if (task.isSuccessful()) {
                            //Obtiene el usuario FirebaseUser
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //Obtiene el id de usuario
                            userId = user.getUid();

                            //Obtiene el token del dispositivo
                            token = FirebaseInstanceId.getInstance().getToken();

                            //Obtiene una referencia a la base de datos
                            databaseReference = FirebaseDatabase.getInstance().getReference();


                            databaseReference.child("usuarios").child(userId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Usuario usuario = dataSnapshot.getValue(Usuario.class);

                                    //Crea una consulta a la base de datos para obtener el objeto Usuario del usuario
                                    databaseReference.child("tokens").child(userId).child(token).setValue(true);
                                    //Crea un bundle
                                    Bundle bundle = new Bundle();
                                    //Añade el objeto Usuario al bundle
                                    bundle.putSerializable("usuario", usuario);

                                    //Crea un Intent utilizado para iniciar la actividad principal
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    //Añade el bundle al intent
                                    intent.putExtras(bundle);

                                    //Cierra esta actividad
                                    finish();
                                    //Inicia la nueva actividad
                                    startActivity(intent);
                                    //Cierra el ProgressDialog
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        //Por el contrario, si el inicio de sesion fue fallido
                        else {
                            //Cierra el progressDialog
                            progressDialog.dismiss();
                            //Muestra un Snackbar al usuario informando del error
                            Snackbar.make(v, task.getException().getMessage(),
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * registrar: metodo encargado de iniciar la actividad de registro
     * @param v
     */
    public void registrar(View v) {
        //inicia la actividad
        //se usa startActivityForResult para obtener un resultado de la actividad cuando esta termine
        //recibe por parametro un intent y un numero que identifica la solicitud
        startActivityForResult(new Intent(this, RegistroActivity.class), 10);
    }

    /**
     * onActivityResult: este metodo es llamado cuando la actividad iniciada con startActivityForResult
     * termina y devuelve un resultado
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Si el numero de la solicitud es la misma que la pasada por parametro a startActivityForResult
        //y el codigo de resultado es RESULT_OK
        if (requestCode == 10 && resultCode == RESULT_OK)
            //Se cierra esta actividad
            finish();
    }
}
