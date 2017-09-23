package com.example.user.proxychat.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.user.proxychat.R;
import com.example.user.proxychat.ui.adaptadores.BloqueadosAdaptador;
import com.example.user.proxychat.data.Usuario;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BloqueadosActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TextView tvNumeroBloqueados;
    private RecyclerView recyclerView;
    private Usuario usuario;
    private ArrayList<String> bloqueados;
    private BloqueadosAdaptador bloqueadosAdaptador;

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

        bloqueados = new ArrayList<>();

        //Obtiene una referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Inicializa el RecyclerView a traves del cual se muestra la lista de contactos
        recyclerView = (RecyclerView)findViewById(R.id.recyclerviewBloqueados);
        //Inicializa el TextView que muestra el numero de contactos
        tvNumeroBloqueados = (TextView)findViewById(R.id.tvNumeroBloqueados);

        //Crea un gestor LinearLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        //Configura el RecyclerView con el LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);

        bloqueadosAdaptador = new BloqueadosAdaptador(this, usuario.getId(), bloqueados);
        recyclerView.setAdapter(bloqueadosAdaptador);

        iniciarEscuchadorBloqueados();
    }

    public void iniciarEscuchadorBloqueados() {
        databaseReference.child("contactos")
                .child("usuarios")
                .child(usuario.getId())
                .child("bloqueados").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String id = dataSnapshot.getKey();
                        bloqueados.add(id);
                        bloqueadosAdaptador.notifyDataSetChanged();
                        tvNumeroBloqueados.setText("Bloqueados: " + bloqueados.size());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        String id = dataSnapshot.getKey();
                        bloqueados.remove(id);
                        bloqueadosAdaptador.notifyDataSetChanged();
                        tvNumeroBloqueados.setText("Bloqueados: " + bloqueados.size());
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
