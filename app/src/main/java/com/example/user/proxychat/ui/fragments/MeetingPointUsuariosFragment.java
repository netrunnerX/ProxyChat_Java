package com.example.user.proxychat.ui.fragments;

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

import com.example.user.proxychat.presenter.MeetingPointUsuariosPresenter;
import com.example.user.proxychat.ui.interfaces.OnItemClickListener;
import com.example.user.proxychat.R;
import com.example.user.proxychat.ui.activities.InfoUsuarioActivity;
import com.example.user.proxychat.ui.adaptadores.UsuariosAdaptador;
import com.example.user.proxychat.data.MeetingPoint;
import com.example.user.proxychat.data.Usuario;

/**
 * Created by Saul Castillo Forte on 21/05/17.
 */

/**
 * MeetingPointUsuariosFragment: Fragment que muestra los usuarios que participan en un punto de encuentro
 */
public class MeetingPointUsuariosFragment extends Fragment implements OnItemClickListener,
        MeetingPointUsuariosPresenter.MeetingPointUsuariosView {

    private RecyclerView recyclerView;
    private Usuario usuario;
    private MeetingPoint meetingPoint;
    private TextView tvNumeroUsuarios;
    private MeetingPointUsuariosPresenter presenter;

    public MeetingPointUsuariosFragment() {
    }

    /**
     * onCreateView: metodo que es llamado a la hora de crear la vista del Fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_meetingpoint_usuarios, container, false);
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
        //Obtiene el Bundle pasado al Fragment a la hora de crearlo
        Bundle bundle = getArguments();
        //Obtiene un objeto Usuario con los datos del usuario a partir del Bundle
        usuario = (Usuario) bundle.getSerializable("usuario");
        //Obtiene un objeto MeetingPoint con los datos del punto de encuentro a partir del Bundle
        meetingPoint = (MeetingPoint) bundle.getSerializable("meetingPoint");

        presenter = new MeetingPointUsuariosPresenter(this, usuario.getId(), meetingPoint.getId());
        //Inicializa el TextView que muestra el numero de usuarios
        tvNumeroUsuarios = (TextView)view.findViewById(R.id.tvNumeroContactos);

        setRecyclerView(view);

        presenter.consultarUsuarios();
    }

    private void setRecyclerView(View view) {
        //Inicializa el RecyclerView
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerviewContactos);

        //Crea un gestor LinearLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //Configura el RecyclerView con el LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);

        //Configura el RecyclerView con el adaptador de usuarios
        recyclerView.setAdapter(obtenerAdaptador());
    }

    private UsuariosAdaptador obtenerAdaptador() {
        //Crea un adaptador de usuarios
        UsuariosAdaptador usuariosAdaptador =
                new UsuariosAdaptador(getContext(), presenter.getListaUsuarios() , usuario.getId());
        //Establece un escuchador de clicks para el adaptador, el esuchador es el propio Fragment
        usuariosAdaptador.setOnItemClickListener(this);

        return usuariosAdaptador;
    }

    /**
     * onClick: metodo que se ejecuta cuando el usuario pulsa sobre uno de los items del RecyclerView
     * @param view item pulsado
     * @param position posicion del item dentro del RecyclerView
     */
    @Override
    public void onClick(View view, int position) {

        presenter.obtenerInformacionUsuario(position);

    }

    @Override
    public void actualizarNumeroUsuarios(int numeroUsuarios) {
        tvNumeroUsuarios.setText("Usuarios: " + numeroUsuarios);
    }

    @Override
    public void notifyDataSetChanged() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void mostrarInfoUsuario(Usuario usuario) {
        //Crea un Bundle
        Bundle bundle = new Bundle();
        //Añade al Bundle el objeto Usuario con los datos del usuario consultado
        bundle.putSerializable("contacto", usuario);
        //Añade al Bundle el objeto Usuario con los datos del propio usuario
        bundle.putSerializable("usuario", this.usuario);
        //Crea un Intent utilizado para iniciar la actividad de informacion del usuario
        Intent intent = new Intent(getContext(), InfoUsuarioActivity.class);
        //Añade el Bundle al Intent
        intent.putExtras(bundle);
        //Inicia la actividad
        startActivity(intent);
    }
}
