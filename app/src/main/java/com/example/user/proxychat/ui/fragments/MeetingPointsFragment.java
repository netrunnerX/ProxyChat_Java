package com.example.user.proxychat.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.proxychat.presenter.MeetingPointsPresenter;
import com.example.user.proxychat.ui.interfaces.OnItemClickListener;
import com.example.user.proxychat.ui.interfaces.OnItemLongClickListener;
import com.example.user.proxychat.R;
import com.example.user.proxychat.ui.activities.MainActivity;
import com.example.user.proxychat.ui.activities.MeetingPointActivity;
import com.example.user.proxychat.ui.adaptadores.MeetingPointsAdaptador;
import com.example.user.proxychat.data.MeetingPoint;
import com.example.user.proxychat.data.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
        OnItemLongClickListener, MeetingPointsPresenter.MeetingPointsView {

    private Usuario usuario;
    private RecyclerView recyclerView;
    private TextView tvNumeroMeetingPoints;
    private MeetingPointsPresenter presenter;


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

        inicializar(view);

    }

    private void inicializar(View view) {
        //Obtiene un objeto Usuario con los datos del usuario contenido en la actividad principal
        usuario = ((MainActivity)getActivity()).getUsuario();
        presenter = new MeetingPointsPresenter(this, usuario.getId());

        //Inicializa el TextView que muestra el numero de puntos de encuentro
        tvNumeroMeetingPoints = (TextView) view.findViewById(R.id.tvNumeroMeetingPoints);

        setRecyclerView(view);
        presenter.consultarMeetingPoints();
    }

    private MeetingPointsAdaptador obtenerAdaptador() {
        MeetingPointsAdaptador meetingPointsAdaptador =
                new MeetingPointsAdaptador(presenter.getListaMeetingPoints());

        meetingPointsAdaptador.setOnItemClickListener(this);
        meetingPointsAdaptador.setOnItemLongClickListener(this);

        return meetingPointsAdaptador;
    }

    private void setRecyclerView(View view) {
        //Inicializa el RecyclerView
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerviewMeetingPoints);

        //Crea un gestor LinearLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //Configura el RecyclerView con el LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);

        //Configura el RecyclerView con el adaptador
        recyclerView.setAdapter(obtenerAdaptador());

    }


    /**
     * onClick: metodo que se ejecuta cuando el usuario pulsa sobre uno de los items del RecyclerView
     * @param view item pulsado
     * @param position posicion del item dentro del RecyclerView
     */
    @Override
    public void onClick(View view, int position) {

        presenter.visitarMeetingPoint(position);

    }

    /**
     * onLongClick: metodo que se ejecuta cuando el usuario realiza una pulsacion larga
     * sobre uno de los items del Recyclerview
     * @param view item pulsado
     * @param position posicion del item dentro del RecyclerView
     * @return
     */
    @Override
    public boolean onLongClick(View view, final int position) {

        presenter.mostrarDialogoEliminarMeetingPoint(position);

        return true;
    }

    @Override
    public void actualizarNumeroMeetingPoints(int numeroMeetingPoints) {
        tvNumeroMeetingPoints.setText("Puntos de encuentro: " + numeroMeetingPoints);
    }

    @Override
    public void notifyDataSetChanged() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void iniciarMeetingPoint(MeetingPoint meetingPoint) {
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

    @Override
    public void mostrarDialogoEliminarMeetingPoint(final int position) {
        //Inicializa un array CharSequence que contiene la descripcion para cada opcion del menu contextual
        final CharSequence[] items = {"Eliminar Punto de Encuentro"};

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
                        presenter.eliminarMeetingPoint(position);
                        break;
                }
            }
        });

        //Muestra el dialogo
        builder.show();
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        Snackbar.make(recyclerView, mensaje, Snackbar.LENGTH_LONG).show();
    }
}
