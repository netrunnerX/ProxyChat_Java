package com.example.user.proxychat.interactor;

import android.support.annotation.NonNull;

import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.LoginPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by net on 10/11/17.
 */

public class LoginInteractor {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private LoginPresenter presenter;

    public LoginInteractor(LoginPresenter presenter) {
        this.presenter = presenter;
    }

    public void isUsuarioLogueado() {
        //si getCurrentUser no es null, significa que el usuario ya esta logueado
        if (firebaseAuth.getCurrentUser() != null) {
            //Muestra el ProgressDialog
            presenter.mostrarProgressDialog();

            obtenerDatosEInciar();
        }
    }

    public void login(String email, String password) {

        //singInWithEmailAndPassword es llamado para realizar el inicio de sesion en el servidor
        //utilizando como metodo de autenticacion email y contraseña
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    /**
                     * onComplete: se ejecuta cuando la operacion de inicio de sesion se ha completado.
                     * El inicio de sesion puede haber sido un exito o haber fallado
                     * @param task
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //Si la operacion fue exitosa
                        if (task.isSuccessful()) {
                            obtenerDatosEInciar();
                        }
                        //Por el contrario, si el inicio de sesion fue fallido
                        else {
                            //Cierra el progressDialog
                            presenter.ocultarProgressDialog();
                            presenter.mostrarMensaje(task.getException().getMessage());
                        }
                    }
                });
    }

    public void obtenerDatosEInciar() {
        //Obtiene el id del usuario
        final String usuarioId = firebaseAuth.getCurrentUser().getUid();

        //Obtiene el token del dispositivo
        final String token = FirebaseInstanceId.getInstance().getToken();

        //Crea una consulta a la base de datos para obtener el objeto Usuario del usuario
        databaseReference.child("usuarios")
                .child(usuarioId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Obtiene el objeto Usuario con los datos del usuario a partir del DataSnapshot
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);

                        //Almacena el token del dispositivo en la base de datos
                        databaseReference.child("tokens")
                                .child(usuarioId)
                                .child(token)
                                .setValue(true);


                        presenter.cargarMainActivity(usuario);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
