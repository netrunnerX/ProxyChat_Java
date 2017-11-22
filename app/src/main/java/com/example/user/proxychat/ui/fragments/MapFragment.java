package com.example.user.proxychat.ui.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.proxychat.R;
import com.example.user.proxychat.presenter.MapPresenter;
import com.example.user.proxychat.ui.activities.InfoUsuarioActivity;
import com.example.user.proxychat.ui.activities.MainActivity;
import com.example.user.proxychat.ui.adaptadores.InfoWindowAdaptador;
import com.example.user.proxychat.data.Usuario;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * MapFragment: Fragment que muestra el mapa
 */
public class MapFragment extends SupportMapFragment implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, MapPresenter.MapView {

    private GoogleMap gMap;
    private MapPresenter presenter;

    private Circle circuloMapa;
    private Usuario usuario;
    private final int RADIO_CIRCULO = 10000;
    private boolean invisible = false;

    private AlertDialog alertDialogMeetingPoint;
    private ProgressDialog progressDialog;

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

        //Obtiene el objeto Usuario con los datos del usuario contenido en la actividad principal
        usuario = ((MainActivity)getActivity()).getUsuario();

        presenter = new MapPresenter(this, usuario.getId(), invisible);
        presenter.obtenerLocationUpdates(getActivity().getApplicationContext());

        //Configura un escuchador de eventos de pulsacion larga sobre el mapa, (el escuchador es el propio Fragment)
        gMap.setOnMapLongClickListener(this);
        //Configura un adaptador para cargar un infoWindow personalizado en el mapa
        gMap.setInfoWindowAdapter(new InfoWindowAdaptador(getContext(), getActivity().getLayoutInflater()));

        //Inicia un escuchador de eventos de pulsacion sobre ventanas de informacion de los marcadores
        iniciarEscuchadorInfoWindow();

    }

    /**
     * dibujarCirculo: metodo encargado de dibujar en el mapa el circulo que representa el radio de alcance
     * @param latit latitud del punto central del circulo
     * @param longi longitud del punto central del circulo
     */
    public void dibujarCirculo(double latit, double longi) {

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

    @Override
    public Marker addMarker(MarkerOptions options) {
        return gMap.addMarker(options);
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

                    presenter.consultarInfoUsuario(idPerfil);
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

        //Inicializa un array CharSequence que contiene la descripcion para cada opcion del menu contextual
        final CharSequence[] items = new CharSequence[2];

        items[0] = "Crear punto de encuentro";
        if (invisible) {
            items[1] = "Desactivar modo invisible";
        }
        else {
            items[1] = "Activar modo invisible";
        }

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
                        crearMeetingPoint(latLng);
                        break;
                    case 1:
                        cambiarModoInvisible();
                        break;
                }
            }
        });

        //Muestra el dialogo
        builder.show();

    }

    /**
     * agregarMeetingPoint: metoto encargado de agregar el punto de encuentro a la lista de puntos del usuario
     * @param keyMeetingPoint id del punto de encuentro
     */
    public void agregarMeetingPoint(String keyMeetingPoint) {

        presenter.agregarMeetingPoint(keyMeetingPoint);

    }

    public void crearMeetingPoint(LatLng latLng) {
        crearAlertDialogMeetingPoint(latLng);

        mostrarAlertDialog();
    }

    public void cambiarModoInvisible() {
        presenter.cambiarModoInvisible();
    }

    public void crearAlertDialogMeetingPoint(final LatLng latLng) {
        //**********************************************
        //Crea una vista cargando en esta el layout del dialogo para crear un punto de encuentro
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialogo_meetingpoint, null);

        //Instancia el campo de texto del dialogo que recibe el nombre del punto
        final EditText etNombrePoint = (EditText) v.findViewById(R.id.etNombrePointDialog);
        //Instancia el campo de texto del dialogo que recibe la descripcion del punto
        final EditText etDescripcion = (EditText) v.findViewById(R.id.etDescripcionDialog);

        //Crea el dialogo
        alertDialogMeetingPoint = new AlertDialog.Builder(getContext())
                .setView(v)
                .setTitle("Nuevo punto de encuentro")
                .setPositiveButton("Aceptar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        //Configura un escuchador que se disparara cuando se muestre el dialogo
        alertDialogMeetingPoint.setOnShowListener(new DialogInterface.OnShowListener() {
            //en el metodo onShow, obtenemos el boton de aceptar y le configuramos un escuchador
            //View.OnclickListener
            //De esta forma podemos validar el texto introducido por el usuario,
            //controlando cuando cerrar el dialogo
            @Override
            public void onShow(DialogInterface dialog) {

                //Obtiene una instancia del boton de aceptar del dialogo
                Button botonAceptar = alertDialogMeetingPoint.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                //Configura un escuchador de clicks para el boton de aceptar
                botonAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        crearProgressDialog();
                        mostrarProgressDialog();

                        //si el EditText del nombre del punto no esta vacio
                        if (!etNombrePoint.getText().toString().equals("")){

                            //Crea el punto de encuentro a partir del id de usuario y los datos
                            //obtenidos de los campos del dialogo
                            presenter.crearMeetingPoint(usuario.getId(),
                                    etNombrePoint.getText().toString(),
                                    etDescripcion.getText().toString(),
                                    latLng);

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
    }

    public void mostrarAlertDialog() {
        //Muestra el dialogo
        alertDialogMeetingPoint.show();
    }

    public void crearProgressDialog() {
        //Crea un dialogo para informar al usuario de que la creacion del punto
        //se esta llevando a cabo
        progressDialog = new ProgressDialog(getContext());
        //Establece el mensaje del dialogo de carga
        progressDialog.setMessage("Creando punto de encuentro, por favor espere...");
        //Establece el dialogo de carga como modal
        progressDialog.setCancelable(false);
    }

    public void mostrarProgressDialog() {
        //Muestra el dialogo de carga
        progressDialog.show();
    }

    @Override
    public void ocultarAlertDialog() {
        alertDialogMeetingPoint.dismiss();
    }

    @Override
    public void ocultarDialogoCarga() {
        progressDialog.dismiss();
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        Snackbar.make(getView(), mensaje, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setInvisible(boolean estado) {
        invisible = estado;
    }

    @Override
    public void iniciarActivityInfoUsuario(Usuario usr) {
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
}
