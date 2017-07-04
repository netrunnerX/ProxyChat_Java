package com.example.user.proxychat.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.proxychat.interfaces.OnItemClickListener;
import com.example.user.proxychat.R;
import com.example.user.proxychat.adaptadores.UsuariosAdaptador;
import com.example.user.proxychat.interfaces.OnItemLongClickListener;
import com.example.user.proxychat.modelos.Usuario;
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
 * ContactosActivity: Actividad que muestra la lista de contactos
 */
public class ContactosActivity extends AppCompatActivity implements OnItemClickListener, OnItemLongClickListener {

    private RecyclerView recyclerView;
    private UsuariosAdaptador contactosAdaptador;
    private List<String> contactos;
    private DatabaseReference databaseReference;
    private Usuario usuario;
    private TextView tvNumeroContactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        //Configura el ActionBar para mostrar el boton de ir atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Obtiene el objeto Usuario correspondiente al usuario a traves del intent
        usuario = (Usuario)getIntent().getExtras().getSerializable("usuario");

        //Inicializa el RecyclerView a traves del cual se muestra la lista de contactos
        recyclerView = (RecyclerView)findViewById(R.id.recyclerviewContactos);
        //Inicializa el TextView que muestra el numero de contactos
        tvNumeroContactos = (TextView)findViewById(R.id.tvNumeroContactos);

        //Crea un gestor LinearLayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        //Configura el RecyclerView con el LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);
        //Inicializa la lista de contactos
        contactos = new ArrayList<>();

        //Crea un adaptador de contactos
        contactosAdaptador = new UsuariosAdaptador(this, contactos, usuario.getId());
        //Configura el RecyclerView con el adaptador de contactos
        recyclerView.setAdapter(contactosAdaptador);
        //Establece un escuchador de clicks para el adaptador (el escuchador es la propia actividad)
        contactosAdaptador.setOnItemClickListener(this);
        //Establece un escuchador de pulsaciones largas para el adaptador (el escuchador es la propia actividad)
        contactosAdaptador.setOnItemLongClickListener(this);

        //Obtiene una referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Establece un escuchador en la referencia de la base de datos donde se almacenan los contactos
        //del usuario
        databaseReference.child("contactos").child("usuarios").child(usuario.getId()).child("usuarios")
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Obtiene el id del contacto a partir del DataSnapShot
                String keyContacto = dataSnapshot.getKey();

                //Añade el id del contacto a la lista de contactos
                contactos.add(keyContacto);
                //Actualiza el numero de contactos
                tvNumeroContactos.setText("Contactos: " + contactos.size());
                //Notifica al adaptador que el conjunto de datos ha cambiado, de forma que este
                //se actualice
                contactosAdaptador.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            /**
             * onChildRemoved: este metodo se ejecuta cuando un nodo hijo es borrado de la base de datos
             * @param dataSnapshot
             */
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //Elimina el id de contacto de la lista de contactos
                contactos.remove(dataSnapshot.getKey());
                //Actualiza el numero de contactos
                tvNumeroContactos.setText("Contactos: " + contactos.size());
                //Notifica al adaptador que el conjunto de datos ha cambiado, de forma que este
                //se actualice
                contactosAdaptador.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * onClick: este metodo se ejecuta cuando el usuario pulsa sobre uno de los items del RecyclerView
     * @param view item que ha sido pulsado
     * @param position posicion del item dentro del RecyclerView
     */
    @Override
    public void onClick(View view, int position) {
        //Obtiene de la lista el id del contacto
        String contacto = contactos.get(position);

        //Establece un escuchador para obtener los datos del contacto
        databaseReference.child("usuarios").child(contacto).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Obtiene un objeto Usuario con los datos del contacto a partir del DataSnapshot
                Usuario usuarioContacto = dataSnapshot.getValue(Usuario.class);
                //Crea un bundle
                Bundle bundle = new Bundle();
                //Añade al bundle el objeto Usuario del contacto
                bundle.putSerializable("contacto", usuarioContacto);
                //Añade al bundle el objeto Usuario del usuario
                bundle.putSerializable("usuario", usuario);

                //Crea un intent que permitira iniciar la actividad de chat
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                //Añade el bundle al intent
                intent.putExtras(bundle);
                //Inicia la actividad de chat a partir del intent
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * onLongClick: metodo que se ejecuta cuando el usuario realiza una pulsacion larga sobre uno de
     * los items del RecyclerView
     * @param view item pulsado
     * @param position posicion del item dentro del RecyclerView
     * @return
     */
    @Override
    public boolean onLongClick(View view, final int position) {
        //Inicializa un array CharSequence que contiene la descripcion para cada opcion del menu contextual
        final CharSequence[] items = {"Eliminar contacto"};

        //Crea un constructor de dialogos
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Establece el titulo del dialogo
        builder.setTitle("Opciones");
        //Configura el dialogo con los items (opciones) que tendra, tambien se añade un escuchador
        //que recibira los eventos de click en cada una de las opciones del menu contextual
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        //Elimina de la base de datos el nodo correspondiente al contacto en la lista
                        //de contactos del usuario,
                        //se añaden ademas escuchadores que realizaran acciones dependiendo de si la operacion
                        //fue o no un exito
                        databaseReference.child("contactos").child("usuarios").child(usuario.getId())
                                .child("usuarios").child(contactos.get(position)).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    /**
                                     * onSuccess: metodo que se ejecuta si la operacion fue un exito
                                     * @param aVoid
                                     */
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Muestra un Toast informando al usuario de que el contacto se ha eliminado
                                        Toast.makeText(getApplicationContext(), "Contacto eliminado",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    /**
                                     * onFailure: metodo que se ejecuta si la operacion fallo
                                     * @param e
                                     */
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Muestra un Toast informando al usuario del error
                                        Toast.makeText(getApplicationContext(),
                                                "No se ha podido eliminar el contacto", Toast.LENGTH_LONG).show();
                                    }
                        });
                        break;
                }
            }
        });

        //Muestra el dialogo
        builder.show();
        return true;
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
