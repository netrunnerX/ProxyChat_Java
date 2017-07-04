package com.example.user.proxychat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.proxychat.interfaces.OnItemClickListener;
import com.example.user.proxychat.interfaces.OnItemLongClickListener;
import com.example.user.proxychat.R;
import com.example.user.proxychat.activities.MainActivity;
import com.example.user.proxychat.activities.MeetingPointActivity;
import com.example.user.proxychat.adaptadores.MeetingPointsAdaptador;
import com.example.user.proxychat.modelos.MeetingPoint;
import com.example.user.proxychat.modelos.Usuario;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saul Castillo Forte on 20/05/17.
 */

/**
 * MeetingPointsFragment: Fragment que muestra la lista de puntos de encuentro en los que participa el usuario
 */
public class MeetingPointsFragment extends Fragment implements OnItemClickListener,
        OnItemLongClickListener {

    private Usuario usuario;
    private RecyclerView recyclerView;
    private TextView tvNumeroMeetingPoints;
    private List<String> meetingPoints;
    private MeetingPointsAdaptador meetingPointsAdaptador;
    private DatabaseReference databaseReference;


    /**
     * onCreateView: metodo que es llamado a la hora de crear la vista del Fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_meetingpoints, container, false);
        return rootView;
    }

    /**
     * onViewCreated: este metodo es llamado una vez que la vista ha sido creada
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Obtiene un objeto Usuario con los datos del usuario contenido en la actividad principal
        usuario = ((MainActivity)getActivity()).getUsuario();

        //Inicializa el RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerviewMeetingPoints);

        //Inicializa el TextView que muestra el numero de puntos de encuentro
        tvNumeroMeetingPoints = (TextView) view.findViewById(R.id.tvNumeroMeetingPoints);

        //Crea un gestor LinearLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //Configura el RecyclerView con el LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);

        //Inicializa la lista de puntos de encuentro
        meetingPoints = new ArrayList<>();
        //Crea un adaptador de puntos de encuentro
        meetingPointsAdaptador = new MeetingPointsAdaptador(meetingPoints);
        //Configura un escuchador de clicks para el adaptador, el esuchador es el propio Fragment
        meetingPointsAdaptador.setOnItemClickListener(this);
        //Configura un escuchador de clicks largos para el adaptador, el esuchador es el propio Fragment
        meetingPointsAdaptador.setOnItemLongClickListener(this);
        //Configura el RecyclerView con el adaptador de puntos de encuentro
        recyclerView.setAdapter(meetingPointsAdaptador);

        //Obtiene una referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Inicia la escucha de puntos de encuentro
        iniciarEscuchador();

    }

    /**
     * iniciarEscuchador: metodo encargado de realizar una consulta a la base de datos
     * y obtener la lista de puntos de encuentro del usuario a traves del escuchador
     */
    public void iniciarEscuchador() {

        //Realiza una consulta a la base de datos para obtener los puntos de encuentro del usuario
        databaseReference.child("contactos").child("usuarios").child(usuario.getId()).child("meeting_points")
                .addChildEventListener(new ChildEventListener() {

                    /**
                     * onChildAdded: este metodo se ejecuta cuando un nuevo nodo hijo es agregado a la referencia
                     * de la base de datos (un punto de encuentro agregado). Este metodo tambien se ejecuta al crear el
                     * escuchador, obteniendo un resultado inicial.
                     * @param dataSnapshot
                     * @param s
                     */
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        //Obtiene la clave del nodo, este es el id del punto de encuentro
                        String keyMeetingPoint = dataSnapshot.getKey();
                        //Añade el id a la lista de puntos de encuentro
                        meetingPoints.add(keyMeetingPoint);
                        //Notifica al adaptador que el conjunto de datos ha cambiado, de forma que este
                        //se actualice
                        meetingPointsAdaptador.notifyDataSetChanged();
                        //Actualiza el TextView que muestra el numero de puntos de encuentro
                        tvNumeroMeetingPoints.setText("Puntos de encuentro: " + meetingPoints.size());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * onClick: metodo que se ejecuta cuando el usuario pulsa sobre uno de los items del RecyclerView
     * @param view item pulsado
     * @param position posicion del item dentro del RecyclerView
     */
    @Override
    public void onClick(View view, int position) {

        //Realiza una consulta a la base de datos para obtener los datos del punto de encuentro
        databaseReference.child("meeting_points").child(meetingPoints.get(position))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Obtiene un objeto MeetingPoint con los datos del punto de encuentro
                        //a partir del DataSnapshot
                        MeetingPoint meetingPoint = dataSnapshot.getValue(MeetingPoint.class);

                        //Si el objeto MeetingPoint no es nulo
                        if (meetingPoint != null) {
                            //Crea un Bundle
                            Bundle bundle = new Bundle();
                            //Añade el objeto MeetingPoint con los datos del punto de encuentro
                            //al Bundle
                            bundle.putSerializable("meetingPoint", meetingPoint);
                            //Añade el objeto Usuario con los datos del usuario al Bundle
                            bundle.putSerializable("usuario", usuario);

                            //Crea un Intent utilizado para iniciar la actividad del punto de encuentro
                            Intent intent = new Intent(getContext(), MeetingPointActivity.class);
                            //Añade el Bundle al Intent
                            intent.putExtras(bundle);
                            //Inicia la actividad
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * onLongClick: metodo que se ejecuta cuando el usuario realiza una pulsacion larga
     * sobre uno de los items del Recyclerview
     * @param view item pulsado
     * @param position posicion del item dentro del RecyclerView
     * @return
     */
    @Override
    public boolean onLongClick(View view, int position) {
        return false;
    }
}
