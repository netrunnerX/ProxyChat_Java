package com.example.user.proxychat.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.proxychat.R;
import com.example.user.proxychat.activities.InfoUsuarioActivity;
import com.example.user.proxychat.activities.MainActivity;
import com.example.user.proxychat.adaptadores.InfoWindowAdaptador;
import com.example.user.proxychat.modelos.MeetingPoint;
import com.example.user.proxychat.modelos.Usuario;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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

import java.util.HashMap;
import java.util.Map;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

/**
 * MapFragment: Fragment que muestra el mapa
 */
public class MapFragment extends SupportMapFragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap gMap;

    private Circle circuloMapa;
    private GeoQuery geoQueryUsuarios;
    private GeoFire geoFireUsuarios;
    private GeoQuery geoQueryMeetingPoints;
    private GeoFire geoFireMeetingPoints;
    private GeoLocation geoLocation;
    private DatabaseReference databaseReference;
    private Usuario usuario;
    private Map<String, Marker> usuariosCercanosMark;
    private Map<String, Usuario> usuariosCercanosPerfil;
    private Map<String, Marker> mPointsCercanosMark;
    private Map<String, MeetingPoint> mPointsCercanosPerfil;
    private final int RADIO_CONSULTA = 10;
    private final int RADIO_CIRCULO = 10000;

    public MapFragment() {

    }

    /**
     * onResume: este metodo hace que el Fragment empiece a interactuar con el usuario
     * (basandose en que la actividad en la que esta contenido el Fragment sea reanudada)
     */
    @Override
    public void onResume() {
        super.onResume();
        //Configura el mapa en caso de ser necesario
        setupMapIfNeeded();
    }

    /**
     * setupMapIfNeeded: metodo que configura el mapa en caso de ser necesario
     */
    private void setupMapIfNeeded() {
        //Si el objeto GoogleMap es nulo
        if (gMap == null) {
            //Configura un objeto que sera disparado una vez que el mapa este listo (el objeto es el propio Fragment)
            getMapAsync(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    /**
     * onMapReady: este metodo se usa para manipular el mapa una vez este disponible
     * El metodo se ejecuta cuando el mapa esta listo para ser usado.
     *
     * Si Google Play services no esta instalado en el dispositivo, se solicitara al usuario que lo instale.
     * El metodo solo sera ejecutado una vez que el usuario haya instalado Google Play services y
     * haya vuelto a la aplicacion
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        usuariosCercanosPerfil = new HashMap<>();
        usuariosCercanosMark = new HashMap<>();
        mPointsCercanosPerfil = new HashMap<>();
        mPointsCercanosMark = new HashMap<>();

        //Inicializa el objeto GoogleMap asignandole el objeto recibido por parametro
        gMap = googleMap;

        //Comprueba los permisos
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //Habilita la barra de herramientas del mapa
        gMap.getUiSettings().setMapToolbarEnabled(true);
        //Habilita el boton para mostrar la localizacion del usuario
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        //Habilita la localizacion del usuario (el punto azul)
        gMap.setMyLocationEnabled(true);
        //Habiliza los controles de zoom
        gMap.getUiSettings().setZoomControlsEnabled(true);

        //Obtiene una referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Obtiene el objeto Usuario con los datos del usuario contenido en la actividad principal
        usuario = ((MainActivity)getActivity()).getUsuario();

        //Crea una instancia GeoFire que escuchara en la referencia donde se almacenan las localizaciones
        //de los usuarios
        geoFireUsuarios = new GeoFire(databaseReference.child("locations").child("usuarios"));
        //Crea una instancia GeoFire que escuchara en la referencia donde se almacenan las localizaciones
        //de los puntos de encuentro
        geoFireMeetingPoints = new GeoFire(databaseReference.child("locations").child("meeting_points"));

        //Inicia el geolocalizador
        SmartLocation.with(getContext()).location().start(new OnLocationUpdatedListener() {
            /**
             * onLocationUpdated: este metodo se ejecuta cada vez que se actualiza la localizacion
             * @param location objeto Location con los datos de la nueva localizacion
             */
            @Override
            public void onLocationUpdated(Location location) {
                //Asigna una nueva localizacion al objeto GeoLocation
                geoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
                //Actualiza la ubicacion
                actualizarUbicacion();
            }
        });

        //Configura un escuchador de eventos de pulsacion larga sobre el mapa, (el escuchador es el propio Fragment)
        gMap.setOnMapLongClickListener(this);
        //Configura un adaptador para cargar un infoWindow personalizado en el mapa
        gMap.setInfoWindowAdapter(new InfoWindowAdaptador(getContext(), getActivity().getLayoutInflater(),
                usuariosCercanosPerfil, mPointsCercanosPerfil));

        //Inicia un escuchador de eventos de pulsacion sobre ventanas de informacion de los marcadores
        iniciarEscuchadorInfoWindow();

        //Obtiene la referencia donde se almacena la localizacion del usuario y llama al metodo onDisconnect
        //que nos permite realizar una operacion con dicha referencia en caso de que perdamos la conexion,
        //en esta situacion eliminamos la localizacion
        databaseReference.child("locations").child("usuarios").child(usuario.getId()).onDisconnect().removeValue();
    }

    /**
     * actualizarUbicacion: metodo encargado de actualizar la localizacion del usuario almacenada en la
     * base de datos, ademas de actualizar las consultas por localizacion
     */
    public void actualizarUbicacion() {

        //Almacena la localizacion del usuario en la base de datos
        geoFireUsuarios.setLocation(usuario.getId(), geoLocation);

        //Dibuja el circulo que representa el radio de alcance en el mapa, tomando como centro
        //la nueva localizacion
        dibujarCirculo(geoLocation.latitude, geoLocation.longitude);

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
     * dibujarCirculo: metodo encargado de dibujar en el mapa el circulo que representa el radio de alcance
     * @param latit latitud del punto central del circulo
     * @param longi longitud del punto central del circulo
     */
    private void dibujarCirculo(double latit, double longi) {

        //Crea un objeto LatLng que contiene las coordenadas recibidas por parametro
        LatLng coordenadas = new LatLng(latit, longi);

        //Crea un objeto CameraUpdate utilizado para situar la camara sobre la nueva localizacion
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 11);

        //Si el objeto que representa el circulo no es nulo
        if (circuloMapa != null)
            //Elimina el circulo
            circuloMapa.remove();

        //Crea un objeto CircleOptions en el que se indica las propiedades del circulo a dibujar
        CircleOptions circleOptions = new CircleOptions()
                .center(coordenadas)
                .radius(RADIO_CIRCULO)
                .strokeColor(Color.parseColor("#e6faff"))
                .strokeWidth(4)
                .fillColor(Color.argb(60, 230, 250, 255));

        //Añade un nuevo circulo al mapa haciendo una llamada al metodo addCircle del objeto GoogleMap.
        //Este metodo devuelve el circulo añadido, que es asignado a circuloMapa
        circuloMapa = gMap.addCircle(circleOptions);

        //Mueve la camara a la nueva localizacion
        gMap.animateCamera(miUbicacion);
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
                if (!key.equals(usuario.getId())) {
                    //Realiza una consulta para obtener los datos del usuario encontrado
                    databaseReference.child("usuarios").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Obtiene un objeto Usuario con los datos del usuario encontrado
                            //a partir del DataSnapshot
                            Usuario usrProxy = dataSnapshot.getValue(Usuario.class);

                            //Añade al mapa un marcador ubicado en la localizacion del usuario encontrado
                            Marker marker = gMap.addMarker(new MarkerOptions().title(usrProxy.getApodo())
                                    .position(new LatLng(location.latitude, location.longitude))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            marker.setTag("u:" + usrProxy.getId());

                            //Añade el objeto Usuario al map de usuarios cercanos
                            usuariosCercanosPerfil.put(key, usrProxy);
                            //Añade el marcador al map de marcadores de usuarios cercanos
                            usuariosCercanosMark.put(key, marker);

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
                if (!key.equals(usuario.getId())) {
                    //Elimina el marcador del mapa
                    usuariosCercanosMark.get(key).remove();
                    //Elimina el marcador de la lista de marcadores
                    usuariosCercanosMark.remove(key);
                    //Elimina el usuario de la lista de usuarios
                    usuariosCercanosPerfil.remove(key);
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
                if (!key.equals(usuario.getId())) {
                    //Actualiza la ubicacion en el mapa del marcador que representa al usuario
                    usuariosCercanosMark.get(key).setPosition(new LatLng(location.latitude, location.longitude));
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
                        Marker marker = gMap.addMarker(new MarkerOptions().title(meetingPoint.getNombre())
                                .position(new LatLng(location.latitude, location.longitude))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        marker.setTag("p:" + meetingPoint.getId());

                        //Añade el objeto MeetingPoint al map de puntos de encuentro cercanos
                        mPointsCercanosPerfil.put(key, meetingPoint);
                        //Añade el marcador al map de marcadores de puntos de encuentro cercanos
                        mPointsCercanosMark.put(key, marker);

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
                //Elimina el marcador del mapa
                mPointsCercanosMark.get(key).remove();
                //Elimina el marcador de la lista de marcadores
                mPointsCercanosMark.remove(key);
                //Elimina el marcador de la lista de puntos de encuentro
                mPointsCercanosPerfil.remove(key);
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
                mPointsCercanosMark.get(key).setPosition(new LatLng(location.latitude, location.longitude));
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
     * iniciarEscuchadorInfoWindow: metodo que se encarga de configurar un escuchador de click para
     * las ventanas de informacion de los marcadores
     */
    public void iniciarEscuchadorInfoWindow() {

        //Configura un escuchador de clicks InfoWindow
        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            /**
             * onInfoWindowClick: metodo que se ejecuta cuando el usuario pulsa sobre una
             * ventana de informacion de un marcador
             * @param marker marcador cuyo InfoWindow ha sido pulsado
             */
            @Override
            public void onInfoWindowClick(final Marker marker) {

                //Obtiene la etiqueta del marcador, utilizada para identificar si el marcador
                //es de un usuario o de un punto de encuentro
                final String tag = (String) marker.getTag();

                //Si la etiqueta corresponde con un marcador de usuario
                if (tag.startsWith("u:")) {
                    //Obtiene el id del usuario contenido en la etiqueta del marcador
                    String idPerfil = tag.substring(2);

                    //Realiza una consulta para obtener los datos del usuario
                    databaseReference.child("usuarios").child(idPerfil).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //Obtiene un objeto Usuario con los datos del contacto a partir del DataSnapshot
                            Usuario usr = dataSnapshot.getValue(Usuario.class);

                            //Crea un Bundle
                            Bundle bundle = new Bundle();

                            //Añade al Bundle el objeto Usuario del contacto
                            bundle.putSerializable("contacto", usr);
                            //Añade al Bundle el objeto Usuario del usuario
                            bundle.putSerializable("usuario", usuario);
                            //Crea un Intent utilizado para iniciar la actividad de informacion del Usuario cercano
                            Intent intent = new Intent(getContext(), InfoUsuarioActivity.class);
                            //Añade el Bundle al Intent
                            intent.putExtras(bundle);
                            //Inicia la actividad
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                //Si la etiqueta corresponde con un marcador de punto de encuentro
                else if (tag.startsWith("p:")) {

                    //Inicializa un array CharSequence que contiene la descripcion para cada opcion del menu contextual
                    final CharSequence[] items = {"Unirse al punto"};

                    //Crea un constructor de dialogos
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    //Establece el titulo del dialogo
                    builder.setTitle("Opciones");
                    //Configura el dialogo con los items (opciones) que tendra, tambien se añade un escuchador
                    //que recibira los eventos de click en cada una de las opciones del menu contextual
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {

                            switch (item) {
                                case 0:
                                    //llama al metodo encargado de agregar el punto de encuentro
                                    //a la lista de puntos de encuentro del usuario
                                    agregarMeetingPoint(tag.substring(2));
                                    break;
                            }
                        }
                    });

                    //Muestra el dialogo
                    builder.show();
                }

            }
        });
    }


    /**
     * onMapLongClick: metodo que se ejecuta cuando el usuario realiza una pulsacion larga
     * sobre el mapa
     * @param latLng coordenadas donde se ha realizado la pulsacion
     */
    @Override
    public void onMapLongClick(final LatLng latLng) {

        //Crea una vista cargando en esta el layout del dialogo para crear un punto de encuentro
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialogo_meetingpoint, null);

        //Crea un objeto GeoLocation con las coordenadas donde se realizo la pulsacion
        final GeoLocation mpGeoLocation = new GeoLocation(latLng.latitude, latLng.longitude);

        //Instancia el campo de texto del dialogo que recibe el nombre del punto
        final EditText etNombrePoint = (EditText) v.findViewById(R.id.etNombrePointDialog);
        //Instancia el campo de texto del dialogo que recibe la descripcion del punto
        final EditText etDescripcion = (EditText) v.findViewById(R.id.etDescripcionDialog);

        //Crea el dialogo
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(v)
                .setTitle("Nuevo punto de encuentro")
                .setPositiveButton("Aceptar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        //Configura un escuchador que se disparara cuando se muestre el dialogo
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            //en el metodo onShow, obtenemos el boton de aceptar y le configuramos un escuchador
            //View.OnclickListener
            //De esta forma podemos validar el texto introducido por el usuario,
            //controlando cuando cerrar el dialogo
            @Override
            public void onShow(DialogInterface dialog) {

                //Obtiene una instancia del boton de aceptar del dialogo
                Button botonAceptar = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                //Configura un escuchador de clicks para el boton de aceptar
                botonAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Crea un dialogo para informar al usuario de que la creacion del punto
                        //se esta llevando a cabo
                        final ProgressDialog progressDialog = new ProgressDialog(getContext());
                        //Establece el mensaje del dialogo de carga
                        progressDialog.setMessage("Creando punto de encuentro, por favor espere...");
                        //Establece el dialogo de carga como modal
                        progressDialog.setCancelable(false);
                        //Muestra el dialogo de carga
                        progressDialog.show();

                        //si el EditText del nombre del punto no esta vacio
                        if (!etNombrePoint.getText().toString().equals("")){

                            //Obtiene una referencia a la base de datos donde se almacenara el punto de encuentro
                            DatabaseReference meetingPointDataRef = databaseReference.child("meeting_points").push();

                            //Crea un objeto MeetingPoint con los datos del punto de encuentro
                            final MeetingPoint meetingPoint = new MeetingPoint(meetingPointDataRef.getKey(),
                                    usuario.getId(), etNombrePoint.getText().toString(), etDescripcion.getText().toString());

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
                                            .child("usuarios").child(usuario.getId())
                                            .child("meeting_points")
                                            .child(meetingPoint.getId()).setValue(true);

                                    //Cierra el dialogo
                                    alertDialog.dismiss();

                                    //Cierra el dialogo de carga
                                    progressDialog.dismiss();

                                    //Muestra un Toast informando al usuario de que el punto ha sido creado
                                    Toast.makeText(getContext(),
                                            "Punto creado y agregado a la lista de puntos",
                                            Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {

                                /**
                                 * onFailure: metodo que se ejecuta si la operacion fallo
                                 * @param e
                                 */
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    //Cierra el dialogo
                                    alertDialog.dismiss();

                                    //Cierra el dialogo de carga
                                    progressDialog.dismiss();

                                    //Muestra un Toast al usuario informando del error
                                    Toast.makeText(getContext(), "Error al crear el punto de encuentro", Toast.LENGTH_LONG).show();


                                }
                            });

                        }
                        //si el EditText del nombre del punto esta vacio
                        else {
                            //Cierra el dialogo de carga
                            progressDialog.dismiss();

                            //muestra un mensaje tip informando del error
                            etNombrePoint.setError("Debes introducir un nombre para el punto de encuentro");
                        }
                    }
                });
            }
        });

        //Muestra el dialogo
        alertDialog.show();
    }


    /**
     * agregarMeetingPoint: metoto encargado de agregar el punto de encuentro a la lista de puntos del usuario
     * @param keyMeetingPoint id del punto de encuentro
     */
    public void agregarMeetingPoint(final String keyMeetingPoint) {

        //Realiza una consulta a la base de datos para comprobar si el punto de encuentro ya se encuentra
        //en la lista de puntos del usuario
        databaseReference.child("contactos").child("usuarios").child(usuario.getId()).child("meeting_points")
                .child(keyMeetingPoint).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Obtiene el valor booleano que contiene el nodo que hace referencia al punto de encuentro
                Boolean bContacto = dataSnapshot.getValue(Boolean.class);

                //Si el valor del objeto Boolean no es nulo
                if (bContacto != null) {
                    //Muestra un Toast informando al usuario de que el punto ya existe en su lista de puntos
                    Toast.makeText(getContext(),
                            "El punto ya existe en la lista de puntos", Toast.LENGTH_LONG).show();
                }
                //Si el valor del objeto Boolean es nulo (Esto pasa si el nodo para el que se realiza la
                // consulta no existe, por lo que en este caso el punto de encuentro no se encuentra
                // en la lista de puntos del usuario)
                else {
                    //Añade el punto de encuentro a la lista de puntos del usuario en la base de datos
                    databaseReference.child("contactos").child("usuarios").child(usuario.getId()).child("meeting_points")
                            .child(keyMeetingPoint).setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                        /**
                         * onSuccess: metodo que se ejecuta si la operacion fue un exito
                         * @param aVoid
                         */
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Muestra un Toast informando al usuario de que el punto ha sido agregado
                            Toast.makeText(getContext(),
                                    "Punto agregado", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {

                        /**
                         * onFailure: metodo que se ejecuta si la operacion fallo
                         * @param e
                         */
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Muestra un Toast informando al usuario del error
                            Toast.makeText(getContext(),
                                    "Error al agregar el punto", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * getUsuariosCercanosPerfil: devuelve el Map que contiene los objetos Usuario de los usuarios cercanos
     * @return Map de objetos Usuario de los usuarios cercanos
     */
    public Map<String, Usuario> getUsuariosCercanosPerfil() {
        return usuariosCercanosPerfil;
    }
}
