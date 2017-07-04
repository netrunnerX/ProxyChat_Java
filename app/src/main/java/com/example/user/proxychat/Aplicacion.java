package com.example.user.proxychat;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Saul Castillo Forte on 24/05/17.
 */

/**
 * Aplicacion: clase que representa la aplicacion (vease AndroidManifest.xml)
 */
public class Aplicacion extends Application {

    /**
     * onCreate: metodo que se ejecuta cuando la aplicacion se esta iniciando (antes de iniciar ninguna
     * actividad, servicio o receptor excluyendo proveedores de contenido)
     */
    @Override
    public void onCreate() {
        super.onCreate();
        //Habilita la persistencia en disco de los datos obtenidos de Firebase, manteniendo el estado de
        //la aplicacion al reiniciar
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
