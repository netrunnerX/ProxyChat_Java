package com.example.user.proxychat.servicios;

import android.content.Context;
import android.location.Location;

import com.firebase.geofire.GeoLocation;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

/**
 * Created by net on 22/11/17.
 */

/**
 * LocationService: Se usa para obtener la localizacion del dispositivo.
 * Utiliza los patrones de diseño Singleton y Observer
 */
public class LocationService {

    private static LocationService locationService;
    private List<LocationObserver> observers;

    private LocationService() {
        observers = new ArrayList<>();
    }

    public static LocationService getInstance(Context context) {
        if (locationService == null) {
            synchronized (LocationService.class) {
                if (locationService == null) {
                    locationService = new LocationService();
                    locationService.iniciarServicio(context);
                }
            }
        }

        return locationService;
    }

    private void iniciarServicio(Context context) {
        //Inicia el geolocalizador
        SmartLocation.with(context).location().start(new OnLocationUpdatedListener() {
            /**
             * onLocationUpdated: este metodo se ejecuta cada vez que se actualiza la localizacion
             * @param location objeto Location con los datos de la nueva localizacion
             */
            @Override
            public void onLocationUpdated(Location location) {
                informarObservers(location);
            }
        });
    }

    public void addLocationObserver(LocationObserver observer) {
        observers.add(observer);
    }

    public void removeLocationObserver(LocationObserver observer) {
        observers.remove(observer);
    }

    public void informarObservers(Location location) {
        for (LocationObserver observer : observers)
            observer.onLocationReceived(location);
    }

    public interface LocationObserver {
        void onLocationReceived(Location location);
    }
}
