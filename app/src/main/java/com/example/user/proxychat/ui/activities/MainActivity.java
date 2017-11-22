package com.example.user.proxychat.ui.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.user.proxychat.servicios.LocationService;
import com.example.user.proxychat.ui.fragments.ConversacionesFragment;
import com.example.user.proxychat.ui.fragments.InvitacionesFragment;
import com.example.user.proxychat.ui.fragments.MapFragment;
import com.example.user.proxychat.ui.fragments.MeetingPointsFragment;
import com.example.user.proxychat.ui.fragments.ProxyFragment;
import com.example.user.proxychat.R;
import com.example.user.proxychat.data.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * MainActivity: actividad principal de la aplicacion,
 * se compone de 4 pestañas con un Fragment cada una para facilitar al usuario navegar
 * por las distintas funcionalidades
 */
public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Bundle bundle;
    private Usuario usuario;
    private MapFragment mapFragment;
    private ProxyFragment proxyFragment;
    private MeetingPointsFragment meetingPointsFragment;
    private ConversacionesFragment conversacionesFragment;
    private InvitacionesFragment invitacionesFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtiene el bundle a partir del intent
        bundle = getIntent().getExtras();
        //Obtiene el objeto Usuario con los datos del usuario
        usuario = (Usuario)bundle.getSerializable("usuario");

        //Crea un ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Crea el adaptador que devolvera un fragmento por cada una de las pestañas de la actividad
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //Configura el ViewPager con el SectionsPagerAdapter
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Crea un TabLayout y lo configura con el ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //Establece un icono para cada pestaña del TabLayout
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_map);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_location_on);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_rss_feed);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_message);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_action_add);

        //Al llamar a getInstance de LocationService, iniciamos el servicio si la instancia no fue
        //creada previamente
        LocationService.getInstance(getApplicationContext());

    }

    /**
     * onCreateOptionsMenu: este metodo se redefine para crear un menu de opciones
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * onOptionsItemSelected: en este metodo se realizan los acciones para cada opcion cuando estas
     * son seleccionadas
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            //Si la opcion pulsada es la de perfil, iniciara la actividad de perfil de usuario
            case R.id.action_perfil:
                //Obtiene una referencia a la base de datos
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                //Realiza una consulta a la base de datos para obtener los datos del usuario
                databaseReference.child("usuarios")
                        .child(usuario.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Usuario usr = dataSnapshot.getValue(Usuario.class);

                        //Crea un bundle
                        Bundle bundle = new Bundle();
                        //Añade el objeto Usuario con los datos del usuario al bundle
                        bundle.putSerializable("usuario", usr);
                        //Crea un Intent utilizado para iniciar la actividad de Perfil
                        Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
                        //Añade el bundle al Intent
                        intent.putExtras(bundle);
                        //Inicia la actividad
                        startActivity(intent);

                        //La consulta es realizada para poder iniciar la actividad pasandole
                        //un objeto Usuario actualizado, de esta forma se podra ver la imagen
                        //de perfil actual cuando se muestra el perfil del usuario

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return true;

            case R.id.action_bloqueados:

                //Crea un bundle
                Bundle bundle = new Bundle();
                //Añade el objeto Usuario con los datos del usuario al bundle
                bundle.putSerializable("usuario", usuario);
                //Crea un Intent utilizado para iniciar la actividad de Perfil
                Intent intent = new Intent(MainActivity.this, BloqueadosActivity.class);
                //Añade el bundle al Intent
                intent.putExtras(bundle);
                //Inicia la actividad
                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Un {@link FragmentPagerAdapter} que devuelve un Fragment correspondiente a una de las pestañas
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    //Devuelve un MapFragment que muestra el mapa
                    mapFragment = new MapFragment();
                    return mapFragment;
                case 1:
                    //Devuelve un MeetingPointsFragment que muestra la lista de puntos de encuentro
                    meetingPointsFragment = new MeetingPointsFragment();
                    return meetingPointsFragment;
                case 2:
                    //Devuelve un ProxyFragment, donde se envian y reciben mensajes entre usuarios cercanos
                    proxyFragment = new ProxyFragment();
                    return proxyFragment;
                case 3:
                    //Devuelve un ConversacionesFragment que muestra la lista de conversaciones
                    conversacionesFragment = new ConversacionesFragment();
                    return conversacionesFragment;
                case 4:
                    //Devuelve un InvitacionesFragment que muestra la lista de invitaciones
                    invitacionesFragment = new InvitacionesFragment();
                    return invitacionesFragment;
                default:
                    return null;
            }
        }

        /**
         * getCount: devuelve el numero de pestañas
         * @return
         */
        @Override
        public int getCount() {
            //Muestra 5 pestañas en total.
            return 5;
        }

        /**
         * getPageTitle: devuelve el titulo de la pestaña que se encuentra en la posicion pasada por parametro
         * @param position posicion de la pestaña
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    //return titulo de pestaña 0
                case 1:
                    //return titulo de pestaña 1
                case 2:
                    //return titulo de pestaña 2
                case 3:
                    //return titulo de pestaña 3
                case 4:
                    //return titulo de pestaña 4
            }
            //Se devuelve siempre null, de este modo no se muestra el titulo y solo aparece el icono en la pestaña
            return null;
        }

    }

    /**
     * getUsuario: devuelve el objeto Usuario del usuario
     * @return
     */
    public Usuario getUsuario() {
        return usuario;
    }

    public MapFragment getMapFragment() {
        return mapFragment;
    }
}
