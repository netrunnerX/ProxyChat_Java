package com.example.user.proxychat.adaptadores;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.proxychat.R;
import com.example.user.proxychat.modelos.MeetingPoint;
import com.example.user.proxychat.modelos.Usuario;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.Map;

/**
 * Created by Saul Castillo Forte on 16/05/17.
 */

/**
 * InfoWindowAdaptador: clase utilizada para personalizar el InfoWindow que muestran los marcadores del mapa
 */
public class InfoWindowAdaptador implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater inflater;
    private Marker ultimoMarker;
    private View viewUsuario;
    private View viewMeetingPoint;

    private Map<String, Usuario> usuariosCercanosPerfil;
    private Map<String, MeetingPoint> mPointsCercanosPerfil;
    private Context context;

    /**
     * Constructor parametrizado
     * @param context contexto
     * @param inflater objeto Inflater utilizado para cargar el layout
     * @param usuariosCercanosPerfil map de usuarios cercanos
     * @param mPointsCercanosPerfil map de puntos de encuentro cercanos
     */
    public InfoWindowAdaptador(Context context, LayoutInflater inflater,
                               Map<String, Usuario> usuariosCercanosPerfil,
                               Map<String, MeetingPoint> mPointsCercanosPerfil) {
        this.context = context;
        this.inflater = inflater;
        this.usuariosCercanosPerfil = usuariosCercanosPerfil;
        this.mPointsCercanosPerfil = mPointsCercanosPerfil;
    }

    /**
     * getInfoWindow: esta clase es llamada para obtener el InfoWindow personalizado
     * @param marker marcador para el que se cargara el InfoWindow
     * @return
     */
    @Override
    public View getInfoWindow(Marker marker) {
        //Se devuelve null para cargar el InfoWindow por defecto
        return null;
    }

    /**
     * getInfoContents: este metodo es llamado para cargar en el InfoWindow una vista con el contenido a mostrar
     * @param marker marcador en cuyo InfoWindow se cargara el contenido
     * @return
     */
    @Override
    public View getInfoContents(Marker marker) {

        //Obtiene el tag del marcador
        String tag = (String)marker.getTag();

        //Si el tag es usuario
        if (tag.startsWith("u:")) {

            //Crea una instancia para viewUsuario a partir de un layout en caso de que la vista sea null
            if (viewUsuario == null) {
                viewUsuario = inflater.inflate(R.layout.infowindow_usuario, null);
            }

            //Si no hay un ultimo marcador (null) o el id del marcador no corresponde con el marcador actual
            if (ultimoMarker == null || !ultimoMarker.getId().equals(marker.getId())) {
                //Asigna como ultimo marcador el marcador actual
                ultimoMarker = marker;

                //Instancia el ImageView que muestra la imagen del usuario
                ImageView ivFotoPerfilMapa = (ImageView) viewUsuario.findViewById(R.id.ivFotoMapa);
                //Instancia el TextView que muestra el nombre del usuario
                TextView tvApodo = (TextView)viewUsuario.findViewById(R.id.tvApodoPerfilMapa);

                //Obtiene el usuario del mapa de usuarios cercanos
                Usuario usuarioProxy = usuariosCercanosPerfil.get(tag.substring(2));
                //Establece el texto del TextView con el nombre del usuario
                tvApodo.setText(usuarioProxy.getApodo());

                //Crea un objeto Uri a partir de la URL del usuario
                Uri uri = Uri.parse(usuarioProxy.getImagenUrl());

                //Descarga la imagen y la añade al ImageView del InfoWindow
                //utilizando la libreria Glide
                Glide.with(context.getApplicationContext())
                        .load(uri)
                        .apply(new RequestOptions().placeholder(R.drawable.iconouser).centerCrop())
                        .into(ivFotoPerfilMapa);

            }

            //Devuelve la vista viewUsuario
            return viewUsuario;
        }
        //Si el tag es meetingPoint
        else if (tag.startsWith("p:")) {

            //Crea una instancia para viewMeetingPoint a partir de un layout en caso de que la vista sea null
            if (viewMeetingPoint == null)
                viewMeetingPoint = inflater.inflate(R.layout.infowindow_meetingpoint, null);

            //Instancia el TextView que muestra el nombre del punto de encuentro
            TextView nombrePunto = (TextView) viewMeetingPoint.findViewById(R.id.tvNombrePoint);
            //Instancia el TextView que muestra la descripcion del punto de encuentro
            TextView descripcion = (TextView) viewMeetingPoint.findViewById(R.id.tvDescripcion);

            //Obtiene el objeto MeetingPoint del mapa de puntos de encuentros cercanos
            MeetingPoint meetingPoint = mPointsCercanosPerfil.get(tag.substring(2));

            //Establece el texto del TextView nombrePunto con el nombre del punto de encuentro
            nombrePunto.setText(meetingPoint.getNombre());
            //Establece el text del TextView descripcion con la descripcion del punto de encuentro
            descripcion.setText(meetingPoint.getDescripcion());

            //Devuelve la vista viewMeetingPoint
            return viewMeetingPoint;
        }

        //Devuelve null si no se cumplen los casos anteriores
        return null;
    }

}
