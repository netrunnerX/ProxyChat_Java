package com.example.user.proxychat.interactor;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.PerfilPresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

/**
 * Created by net on 10/11/17.
 */

public class PerfilInteractor {
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private PerfilPresenter presenter;

    public PerfilInteractor(PerfilPresenter presenter) {
        this.presenter = presenter;
    }


    public void subirImagen(InputStream inputStream, final Usuario usuario) {

        //Situa la referencia en la ruta donde se almacena la imagen de perfil del usuario
        storageReference = storageReference.child("usuarios").child(usuario.getId()).child("perfil.png");
        //Crea un objeto UploadTask, utilizado para subir la imagen al almacen de Firebase
        UploadTask uploadTask;
        //Inicializa el UploadTask haciendo una llamada al metodo putStream del objeto StorageReference
        //y pasandole por parametro el InputStream
        uploadTask = storageReference.putStream(inputStream);
        //Añade escuchadores al UploadTask que recibiran los eventos de operacion exitosa u operacion
        //fallida
        uploadTask.addOnFailureListener(new OnFailureListener() {
            /**
             * onFailure: se ejecuta si la operacion fallo
             * @param e
             */
            @Override
            public void onFailure(@NonNull Exception e) {
                //Muestra un mensaje informando al usuario del error
                presenter.mostrarMensaje("Error al subir la imagen al servidor: " + e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            /**
             * onSuccess: se ejecuta si la operacion se realizo exitosamente
             * @param taskSnapshot
             */
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //Etiqueta para evitar que el IDE se queje de que el metodo getDownloadUrl
                //solo deberia ser visible por tests o en un ambito private
                @SuppressWarnings("VisibleForTests")
                //Obtiene del TaskSnapShot la URI de la imagen
                        Uri uri = taskSnapshot.getDownloadUrl();
                //Establece la URL de la imagen de usuario en el objeto Usuario con la nueva URL
                usuario.setImagenUrl(uri.toString());

                //Almacena en la base de datos el objeto Usuario actualizado
                databaseReference.child("usuarios").child(usuario.getId()).setValue(usuario);

                presenter.cargarFotoPerfil(uri);
            }
        });
    }
}
