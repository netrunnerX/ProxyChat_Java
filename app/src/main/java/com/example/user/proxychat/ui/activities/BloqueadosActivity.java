package com.example.user.proxychat.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.user.proxychat.R;
import com.example.user.proxychat.presenter.BloqueadosPresenter;
import com.example.user.proxychat.ui.adaptadores.BloqueadosAdaptador;
import com.example.user.proxychat.data.Usuario;

public class BloqueadosActivity extends AppCompatActivity implements BloqueadosPresenter.BloqueadosView{

    private TextView tvNumeroBloqueados;
    private RecyclerView recyclerView;
    private Usuario usuario;
    private BloqueadosAdaptador bloqueadosAdaptador;
    private BloqueadosPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloqueados);


        //Configura el ActionBar para mostrar el boton de ir atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Obtiene el bundle a traves del intent
        Bundle bundle = getIntent().getExtras();
        //Obtiene del bundle el objeto Usuario del usuario
        usuario = (Usuario)bundle.getSerializable("usuario");

        presenter = new BloqueadosPresenter(this);

        //Inicializa el RecyclerView a traves del cual se muestra la lista de contactos
        recyclerView = (RecyclerView)findViewById(R.id.recyclerviewBloqueados);
        //Inicializa el TextView que muestra el numero de contactos
        tvNumeroBloqueados = (TextView)findViewById(R.id.tvNumeroBloqueados);

        //Crea un gestor LinearLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        //Configura el RecyclerView con el LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);

        bloqueadosAdaptador = new BloqueadosAdaptador(this, usuario.getId(), presenter.getBloqueados());
        recyclerView.setAdapter(bloqueadosAdaptador);

        obtenerUsuariosBloqueados(usuario.getId());
    }

    public void obtenerUsuariosBloqueados(String usuarioId) {
        presenter.obtenerUsuariosBloqueados(usuarioId);
    }

    @Override
    public void notifyDataSetChanged() {
        bloqueadosAdaptador.notifyDataSetChanged();
    }

    @Override
    public void actualizarNumeroBloqueados(int numero) {
        tvNumeroBloqueados.setText("Bloqueados: " + numero);
    }

    /**
     * onOptionsItemSelected: en este metodo se realizan los acciones para cada item de menu cuando estos
     * son seleccionados
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //Si el item corresponde con el boton de ir atras
            case android.R.id.home:
                //Termina la actividad
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
