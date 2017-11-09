package com.example.user.proxychat.servicios;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Saul Castillo Forte on 09/04/2017.
 */

/**
 * FirebaseId: Clase que actua como servicio para gestionar los tokens del dispositivo
 */
public class FirebaseId extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
    }
}
