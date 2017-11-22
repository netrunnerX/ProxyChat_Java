package com.example.user.proxychat.interactor;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import com.example.user.proxychat.data.MeetingPoint;
import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.data.repository.MeetingPointsCercanosRepository;
import com.example.user.proxychat.data.repository.UsuariosCercanosRepository;
import com.example.user.proxychat.presenter.MapPresenter;
import com.example.user.proxychat.servicios.LocationService;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by net on 22/11/17.
 */

public class MapInteractor implements LocationService.LocationObserver {

    private MapPresenter presenter;
    private GeoQuery geoQueryUsuarios;
    private GeoFire geoFireUsuarios;
    private GeoQuery geoQueryMeetingPoints;
    private GeoFire geoFireMeetingPoints;
    private GeoLocation geoLocation;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private UsuariosCercanosRepository usuariosCercanosRepository;
    private MeetingPointsCercanosRepository meetingPointsCercanosRepository;
    private final int RADIO_CONSULTA = 10;
    private boolean invisible;
    private String usuarioId;

    public MapInteractor(MapPresenter presenter, String usuarioId, boolean invisible) {
        this.presenter = presenter;
        this.usuarioId = usuarioId;
        this.invisible = invisible;
        usuariosCercanosRepository = UsuariosCercanosRepository.getInstance();
        meetingPointsCercanosRepository = MeetingPointsCercanosRepository.getInstance();
        //Crea una instancia GeoFire que escuchara en la referencia donde se almacenan las localizaciones
        //de los usuarios
        geoFireUsuarios = new GeoFire(databaseReference.child("locations").child("usuarios"));
        //Crea una instancia GeoFire que escuchara en la referencia donde se almacenan las localizaciones
        //de los puntos de encuentro
        geoFireMeetingPoints = new GeoFire(databaseReference.child("locations").child("meeting_points"));

        //Obtiene la referencia donde se almacena la localizacion del usuario y llama al metodo onDisconnect
        //que nos permite realizar una operacion con dicha referencia en caso de que perdamos la conexion,
        //en esta situacion eliminamos la localizacion
        databaseReference.child("locations").child("usuarios").child(usuarioId).onDisconnect().removeValue();
    }

    public void obtenerLocationUpdates(Context context) {
        LocationService.getInstance(context).addLocationObserver(this);
    }

    @Override
    public void onLocationReceived(Location location) {
        //Asigna una nueva localizacion al objeto GeoLocation
        geoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
        actualizarUbicacion(geoLocation);
    }

    /**
     * actualizarUbicacion: metodo encargado de actualizar la localizacion del usuario almacenada en la
     * base de datos, ademas de actualizar las consultas por localizacion
     */
    public void actualizarUbicacion(GeoLocation geoLocation) {

        if (!invisible) {
            //Almacena la localizacion del usuario en la base de datos
            geoFireUsuarios.setLocation(usuarioId, geoLocation);
        }

        //Dibuja el circulo que representa el radio de alcance en el mapa, tomando como centro
        //la nueva localizacion
        presenter.dibujarCirculo(geoLocation.latitude, geoLocation.longitude);

        //Si el objeto GeoQuery encargado de consultar usuarios cercanos no es nulo
        if (geoQueryUsuarios != null)
            //Actualiza el centro de la consulta con la nueva localizacion
            geoQueryUsuarios.setCenter(geoLocation);
            //Por otra parte, si es nulo
        else
            //Lo configura haciendo una llamada a buscarUsuariosCercanos
            buscarUsuariosCercanos();

        //Si el objeto GeoQuery encargado de consultar puntos de encuentro cercanos no es nulo
        if (geoQueryMeetingPoints != null)
            //Actualiza el centro de la consulta con la nueva localizacion
            geoQueryMeetingPoints.setCenter(geoLocation);
            //Por otra parte, si es nulo
        else
            //Lo configura haciendo una llamada a buscarMeetingPointsCercanos
            buscarMeetingPointsCercanos();
    }

    /**
     * buscarUsuariosCercanos: metodo que realiza la consulta por localizacion para usuarios cercanos
     */
    private void buscarUsuariosCercanos() {
        //Crea una nueva consulta por localizacion
        geoQueryUsuarios = geoFireUsuarios.queryAtLocation(geoLocation, RADIO_CONSULTA);
        //Añade a la consulta un escuchador GeoQuery
        geoQueryUsuarios.addGeoQueryEventListener(new GeoQueryEventListener() {
            /**
             * onKeyEntered: metodo que se ejecuta cuando un usuario entra en el radio de alcance de la consulta
             * @param key clave que identifica al usuario (id de usuario)
             * @param location localizacion del usuario
             */
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {

                //Si la clave no es el id del propio usuario del dispositivo
                if (!key.equals(usuarioId)) {
                    //Realiza una consulta para obtener los datos del usuario encontrado
                    databaseReference.child("usuarios").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Obtiene un objeto Usuario con los datos del usuario encontrado
                            //a partir del DataSnapshot
                            Usuario usrProxy = dataSnapshot.getValue(Usuario.class);

                            //Añade al mapa un marcador ubicado en la localizacion del usuario encontrado
                            Marker marker = presenter.addMarker(new MarkerOptions().title(usrProxy.getApodo())
                                    .position(new LatLng(location.latitude, location.longitude))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                    .visible(invisible?false:true));
                            marker.setTag("u:" + usrProxy.getId());

                            //Añade el usuario y su marcador al repositorio de usuarios cercanos
                            usuariosCercanosRepository.putUsuarioCercano(usrProxy, marker);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            /**
             * onKeyExited: metodo que se ejecuta cuando un usuario sale del radio de alcance de la consulta
             * @param key clave que identifica al usuario (id de usuario)
             */
            @Override
            public void onKeyExited(String key) {
                //Si la clave del marcador no es el id del usuario del dispositivo
                if (!key.equals(usuarioId)) {

                    //Elimina el usuario del repositorio de usuarios cercanos
                    usuariosCercanosRepository.removeUsuarioCercano(key);
                }
            }

            /**
             * onKeyMoved: este metodo se ejecuta cuando un usuario cambia su localizacion dentro del
             * radio de alcance
             * @param key clave del marcador, identifica al usuario
             * @param location nueva localizacion
             */
            @Override
            public void onKeyMoved(String key, GeoLocation location) {

                //Si la clave del marcador no es la misma que el id del usuario del dispositivo
                if (!key.equals(usuarioId)) {
                    //Actualiza la ubicacion en el mapa del marcador que representa al usuario
                    usuariosCercanosRepository.actualizarLocalizacionUsuarioCercano(key,
                            new LatLng(location.latitude, location.longitude));
                }

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    /**
     * buscarMeetingPointsCercanos: metodo que realiza la consulta por localizacion para puntos de encuentro cercanos
     */
    public void buscarMeetingPointsCercanos() {
        //Crea una nueva consulta por localizacion
        geoQueryMeetingPoints = geoFireMeetingPoints.queryAtLocation(geoLocation, RADIO_CONSULTA);
        //Añade a la consulta un escuchador GeoQuery
        geoQueryMeetingPoints.addGeoQueryEventListener(new GeoQueryEventListener() {

            /**
             * onKeyEntered: metodo que se ejecuta cuando un punto de encuentro entra en el radio de alcance de la consulta
             * @param key clave que identifica al punto de encuentro (meeting point id)
             * @param location localizacion del punto de encuentro
             */
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {

                //Realiza una consulta para obtener los datos del punto de encuentro
                databaseReference.child("meeting_points").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Obtiene un objeto MeetingPoint con los datos del punto de encuentro
                        //a partir del DataSnapshot
                        MeetingPoint meetingPoint = dataSnapshot.getValue(MeetingPoint.class);

                        //Añade al mapa un marcador ubicado en la localizacion del punto de encuentro
                        Marker marker = presenter.addMarker(new MarkerOptions().title(meetingPoint.getNombre())
                                .position(new LatLng(location.latitude, location.longitude))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        marker.setTag("p:" + meetingPoint.getId());

                        //Añade el punto de encuentro y su marcador al repositorio de
                        //meetingpoints cercanos
                        meetingPointsCercanosRepository.putMeetingPointCercano(meetingPoint, marker);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            /**
             * onKeyExited: metodo que se ejecuta cuando un punto de encuentro sale del radio de alcance de la consulta
             * @param key clave que identifica al punto de encuentro (meeting point id)
             */
            @Override
            public void onKeyExited(String key) {

                //Elimina el punto de encuentro del repositorio de meetingpoints cercanos
                meetingPointsCercanosRepository.removeMeetingPointCercano(key);
            }

            /**
             * onKeyMoved: este metodo se ejecuta cuando un punto de encuentro cambia su localizacion dentro del
             * radio de alcance
             * @param key clave del marcador, identifica al punto de encuentro
             * @param location nueva localizacion
             */
            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                //Actualiza la ubicacion en el mapa del marcador que representa al punto de encuentro
                meetingPointsCercanosRepository.actualizarLocalizacionMeetingPointCercano(key,
                        new LatLng(location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    public void crearMeetingPoint(final String usuarioId,
                                  String nombreMeetingPoint,
                                  String descMeetingPoint,
                                  LatLng meetingPointLatLng) {

        //Crea un objeto GeoLocation con las coordenadas donde se realizo la pulsacion
        final GeoLocation mpGeoLocation = new GeoLocation(meetingPointLatLng.latitude, meetingPointLatLng.longitude);

        //Obtiene una referencia a la base de datos donde se almacenara el punto de encuentro
        DatabaseReference meetingPointDataRef = databaseReference.child("meeting_points").push();

        //Crea un objeto MeetingPoint con los datos del punto de encuentro
        final MeetingPoint meetingPoint = new MeetingPoint(meetingPointDataRef.getKey(),
                usuarioId, nombreMeetingPoint, descMeetingPoint);

        //Almacena el punto de encuentro en la base de datos
        meetingPointDataRef.setValue(meetingPoint).addOnSuccessListener(new OnSuccessListener<Void>() {

            /**
             * onSuccess: metodo que se ejecuta si la operacion fue un exito
             * @param aVoid
             */
            @Override
            public void onSuccess(Void aVoid) {
                //Almacena la localizacion del punto de encuentro en la base de datos
                geoFireMeetingPoints.setLocation(meetingPoint.getId(), mpGeoLocation);

                //Añade el punto de encuentro a la lista de puntos de encuentro del usuario
                //en la base de datos
                databaseReference.child("contactos")
                        .child("usuarios").child(usuarioId)
                        .child("meeting_points")
                        .child(meetingPoint.getId()).setValue(true);

                //Cierra el dialogo
                presenter.ocultarAlertDialog();

                //Cierra el dialogo de carga
                presenter.ocultarDialogoCarga();

                presenter.mostrarMensaje("Punto creado y agregado a la lista de puntos");

            }
        }).addOnFailureListener(new OnFailureListener() {

            /**
             * onFailure: metodo que se ejecuta si la operacion fallo
             * @param e
             */
            @Override
            public void onFailure(@NonNull Exception e) {

                //Cierra el dialogo
                presenter.ocultarAlertDialog();

                //Cierra el dialogo de carga
                presenter.ocultarDialogoCarga();

                //Muestra un mensaje al usuario informando del error
                presenter.mostrarMensaje("Error al crear el punto de encuentro");

            }
        });
    }

    public void cambiarModoInvisible() {
        if (invisible) {
            invisible = false;

            //Establece los markers de usuarios cercanos como visibles
            usuariosCercanosRepository.setMarkersVisible(true);

            if (geoLocation != null)
                geoFireUsuarios.setLocation(usuarioId, geoLocation);

            presenter.mostrarMensaje("Modo invisible desactivado");
        }
        else {
            invisible = true;

            //Elimina la localizacion del usuario de la base de datos
            databaseReference.child("locations")
                    .child("usuarios")
                    .child(usuarioId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            //Establece como invisibles los marcadores de usuarios cercanos
                            usuariosCercanosRepository.setMarkersVisible(false);

                            presenter.mostrarMensaje("Modo invisible activado");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    invisible = false;
                    presenter.setInvisible(false);
                    presenter.mostrarMensaje("No se ha podido activar el modo invisible");
                }
            });

        }
    }

    public void agregarMeetingPoint(final String meetingPointId) {
        //Realiza una consulta a la base de datos para comprobar si el punto de encuentro ya se encuentra
        //en la lista de puntos del usuario
        databaseReference.child("contactos").child("usuarios").child(usuarioId).child("meeting_points")
                .child(meetingPointId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Obtiene el valor booleano que contiene el nodo que hace referencia al punto de encuentro
                Boolean bContacto = dataSnapshot.getValue(Boolean.class);

                //Si el valor del objeto Boolean no es nulo
                if (bContacto != null) {
                    presenter.mostrarMensaje("El punto ya existe en la lista de puntos");
                }
                //Si el valor del objeto Boolean es nulo (Esto pasa si el nodo para el que se realiza la
                // consulta no existe, por lo que en este caso el punto de encuentro no se encuentra
                // en la lista de puntos del usuario)
                else {
                    //Añade el punto de encuentro a la lista de puntos del usuario en la base de datos
                    databaseReference.child("contactos").child("usuarios").child(usuarioId).child("meeting_points")
                            .child(meetingPointId).setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                        /**
                         * onSuccess: metodo que se ejecuta si la operacion fue un exito
                         * @param aVoid
                         */
                        @Override
                        public void onSuccess(Void aVoid) {
                            presenter.mostrarMensaje("Punto agregado");
                        }
                    }).addOnFailureListener(new OnFailureListener() {

                        /**
                         * onFailure: metodo que se ejecuta si la operacion fallo
                         * @param e
                         */
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            presenter.mostrarMensaje("Error al agregar el punto");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void consultarInfoUsuario(String usuarioId) {
        //Realiza una consulta para obtener los datos del usuario
        databaseReference.child("usuarios").child(usuarioId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Obtiene un objeto Usuario con los datos del contacto a partir del DataSnapshot
                Usuario usr = dataSnapshot.getValue(Usuario.class);

                presenter.iniciarActivityInfoUsuario(usr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
