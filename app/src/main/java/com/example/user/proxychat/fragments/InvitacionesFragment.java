package com.example.user.proxychat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.proxychat.R;
import com.example.user.proxychat.activities.MainActivity;
import com.example.user.proxychat.adaptadores.InvitacionAdaptador;
import com.example.user.proxychat.modelos.Usuario;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class InvitacionesFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;

    private InvitacionAdaptador invitacionAdaptador;
    private List<String> invitaciones;
    private Usuario usuario;
    private TextView tvNumeroInvitaciones;

    /**
     * onCreateView: metodo que es llamado a la hora de crear la vista del Fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invitaciones, container, false);
        return rootView;

    }

    /**
     * onViewCreated: este metodo es llamado una vez que la vista ha sido creada
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Obtiene un objeto Usuario con los datos del usuario contenido en la actividad principal
        usuario = ((MainActivity)getActivity()).getUsuario();

        //Obtiene una referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Inicializa la lista de invitaciones
        invitaciones = new ArrayList<>();

        tvNumeroInvitaciones = (TextView) getActivity().findViewById(R.id.tvNumeroInvitaciones);

        //Crea un adaptador de invitaciones
        invitacionAdaptador = new InvitacionAdaptador(getContext(), usuario.getId(), invitaciones);

        //Inicializa el RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerviewInvitaciones);

        //Crea un gestor LinearLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //Configura el RecyclerView con el LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);
        //Configura el RecyclerView con el adaptador de invitaciones
        recyclerView.setAdapter(invitacionAdaptador);

        iniciarEscuchadorInvitaciones();

    }

    public void iniciarEscuchadorInvitaciones() {

        databaseReference.child("invitaciones")
                .child(usuario.getId())
                .child("usuarios")
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                invitaciones.add(dataSnapshot.getKey());
                invitacionAdaptador.notifyDataSetChanged();
                tvNumeroInvitaciones.setText("Invitaciones: " + invitaciones.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                invitaciones.remove(dataSnapshot.getKey());
                invitacionAdaptador.notifyDataSetChanged();
                tvNumeroInvitaciones.setText("Invitaciones: " + invitaciones.size());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
