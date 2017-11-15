package com.example.user.proxychat.interactor;

import android.net.Uri;

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

import java.io.InputStream;

/**
 * Created by net on 15/11/17.
 */

public class RegistroInteractor {

    private RegistroPresenter presenter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String apodo, email, password;

    public RegistroInteractor(RegistroPresenter presenter) {
        this.presenter = presenter;
    }

    public void setCampos(String apodo, String email, String password) {
        this.apodo = apodo;
        this.email = email;
        this.password = password;
    }

    public void registrar() {

        //Realiza una consulta a la base de datos para comprobar que el apodo no exista
        databaseReference.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Realiza un bucle para comprobar si el apodo coincide con alguno de los usuarios
                for (DataSnapshot dsUser : dataSnapshot.getChildren()) {
                    //Obtiene un usuario
                    Usuario usr = dsUser.getValue(Usuario.class);
                    //Si el apodo coincide
                    if (usr.getApodo().equals(apodo)) {
                        //Establece un mensaje de error en el campo y finaliza el metodo
                        presenter.setApodoError();
                        return;
                    }
                }
                //Si no se encontraron coincidencias, el metodo iniciarRegistro es ejecutado
                iniciarRegistro();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * iniciarRegistro: metodo encargado de realizar el registro
     */
    public void iniciarRegistro() {

        //Muestra el dialogo
        presenter.mostrarProgressDialog();

        //Obtiene una instancia de FirebaseAuth y realiza una llamada al metodo createUserWithEmailAndPassWord,
        //este metodo se encarga de crear una nueva cuenta de usuario con metodo de inicio de sesion
        //email y contraseña
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    /**
                     * onComplete: este metodo se ejecuta cuando se ha completado el proceso de regstro
                     * (puede haber sido exitoso o haber fallado
                     * @param task
                     */
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                        //Si la operacion fue fallida
                        if (!task.isSuccessful()) {
                            //Cierra el ProgressDialog
                            presenter.ocultarProgressDialog();
                            //Muestra un mensaje informando al usuario del error
                            presenter.mostrarMensaje("Ocurrió un error al realizar el registro");
                        }
                        //Si fue un exito
                        else {
                            // Actualiza la informacion de usuario
                            final FirebaseUser fbUser = task.getResult().getUser();
                            //UserProfileChangeRequest es un objeto que contiene los nuevos datos del usuario
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(apodo)
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
                                public void onComplete(Task<Void> task) {

                                    //Si la operacion fue un exito
                                    if (task.isSuccessful()) {
                                        //Actualiza los datos del objeto Usuario
                                        final Usuario usuario = new Usuario(fbUser.getUid(), fbUser.getDisplayName());
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


                                        //Crea una instancia de InputStream a partir de la URI de la imagen
                                        InputStream stream = presenter.getImagenPerfilStream();

                                        if (stream != null) {
                                            presenter.mostrarMensaje("Error al subir imagen de perfil (null InputStream)");
                                            return;
                                        }

                                        //Crea un UploadTask utilizado para subir la imagen al servidor
                                        UploadTask uploadTask = storageReference.putStream(stream);


                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                            /**
                                             * onFailure: se ejecuta si hubo un fallo en la
                                             * operacion de subida
                                             * @param exception
                                             */
                                            @Override
                                            public void onFailure(Exception exception) {
                                                presenter.mostrarMensaje("Ocurrio un error al subir la imagen de perfil"
                                                        +" al servidor.");
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

                                                presenter.iniciarActividadPrincipal(usuario);
                                            }
                                        });

                                    }
                                    else {
                                        //Cierra el ProgressDialog
                                        presenter.ocultarProgressDialog();

                                        presenter.mostrarMensaje("Ocurrió un error al actualizar la informacion del usuario: "
                                                + task.getException().getMessage());
                                    }
                                }
                            });
                        }
                    }
                });

    }
}
