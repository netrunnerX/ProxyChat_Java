package com.example.user.proxychat.servicios;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Saul Castillo Forte on 09/04/2017.
 */

/**
 * FirebaseServicioMensajes: Clase que actua como servicio para gestionar las notificaciones recibidas
 */
public class FirebaseServicioMensajes extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }
}
