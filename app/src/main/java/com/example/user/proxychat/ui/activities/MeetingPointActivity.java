package com.example.user.proxychat.ui.activities;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import android.view.MenuItem;

import com.example.user.proxychat.R;
import com.example.user.proxychat.ui.fragments.MeetingPointChatFragment;
import com.example.user.proxychat.ui.fragments.MeetingPointUsuariosFragment;
import com.example.user.proxychat.data.MeetingPoint;
import com.example.user.proxychat.data.Usuario;

/**
 * MeetingPointActivity: actividad que presenta la funcionalidad de un punto de encuentro,
 * se compone de 2 pestañas con un Fragment cada una para facilitar al usuario navegar
 * por las funcionalidades
 */
public class MeetingPointActivity extends AppCompatActivity {

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
    private Usuario usuario;
    private MeetingPoint meetingPoint;
    private Bundle bundle;
    private MeetingPointChatFragment meetingPointChatFragment;
    private MeetingPointUsuariosFragment meetingPointUsuariosFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_point);

        //Obtiene el Bundle a traves del Intent
        bundle = getIntent().getExtras();

        //Obtiene del Bundle el objeto Usuario con los datos del usuario
        usuario = (Usuario)bundle.getSerializable("usuario");
        //Obtiene del Bundle el objeto MeetingPoint con los datos del punto de encuentro
        meetingPoint = (MeetingPoint)bundle.getSerializable("meetingPoint");
        //Establece el titulo de la actividad con el nombre del punto de encuentro
        this.setTitle(meetingPoint.getNombre());

        //Obtiene una instancia para el ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Configura el ActionBar para mostrar el boton de ir atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Crea el adaptador que devolvera un Fragment para cada una de las pestañas de la actividad
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //Configura el adaptador con el SectionsPagerAdapter
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Crea un TabLayout y lo configura con el ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //Establece un icono para cada pestaña del TabLayout
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_sms);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_people);

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
                    //Devuelve un MeetingPointChatFragment que muestra el chat del punto de encuentro
                    meetingPointChatFragment = new MeetingPointChatFragment();
                    meetingPointChatFragment.setArguments(bundle);
                    return meetingPointChatFragment;
                case 1:
                    //Devuelve un MeetingPointUsuariosFragment que muestra la lista de usuarios
                    //del punto de encuentro
                    meetingPointUsuariosFragment = new MeetingPointUsuariosFragment();
                    meetingPointUsuariosFragment.setArguments(bundle);
                    return meetingPointUsuariosFragment;
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
            //Muestra 2 pestañas en total
            return 2;
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
                    //return "chat";
                case 1:
                    //return "usuarios";
            }
            //Se devuelve siempre null, de este modo no se muestra el titulo y solo aparece el icono en la pestaña
            return null;
        }
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
