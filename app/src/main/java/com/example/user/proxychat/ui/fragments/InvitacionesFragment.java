package com.example.user.proxychat.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.proxychat.R;
import com.example.user.proxychat.presenter.InvitacionesPresenter;
import com.example.user.proxychat.ui.activities.MainActivity;
import com.example.user.proxychat.ui.adaptadores.InvitacionAdaptador;
import com.example.user.proxychat.data.Usuario;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class InvitacionesFragment extends Fragment implements InvitacionesPresenter.InvitacionesView {
    private RecyclerView recyclerView;

    private InvitacionAdaptador invitacionAdaptador;
    private Usuario usuario;
    private TextView tvNumeroInvitaciones;

    private InvitacionesPresenter presenter;

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

        tvNumeroInvitaciones = (TextView) getActivity().findViewById(R.id.tvNumeroInvitaciones);

        presenter = new InvitacionesPresenter(this);

        //Crea un adaptador de invitaciones
        invitacionAdaptador = new InvitacionAdaptador(getContext(), usuario.getId(), presenter.getInvitacionesList());

        //Inicializa el RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerviewInvitaciones);

        //Crea un gestor LinearLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //Configura el RecyclerView con el LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);
        //Configura el RecyclerView con el adaptador de invitaciones
        recyclerView.setAdapter(invitacionAdaptador);

        presenter.obtenerInvitaciones(usuario.getId());

    }

    @Override
    public void notifyDataSetChanged() {
        invitacionAdaptador.notifyDataSetChanged();
    }

    @Override
    public void actualizarNumeroInvitaciones(int numero) {
        tvNumeroInvitaciones.setText("Invitaciones: " + numero);
    }
}
