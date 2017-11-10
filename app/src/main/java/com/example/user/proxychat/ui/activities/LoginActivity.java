package com.example.user.proxychat.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.user.proxychat.R;
import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.presenter.LoginPresenter;

/**
 * LoginActivity: Es la actividad que se lanza al ejecutar la aplicacion.
 * Muestra un formulario de login y realiza el inicio de sesion en el servidor
 */
public class LoginActivity extends AppCompatActivity implements LoginPresenter.LoginView{

    private EditText etUsuario;
    private EditText etPassword;
    private ProgressDialog progressDialog;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicializa el campo de texto del nombre de usuario
        etUsuario = (EditText) findViewById(R.id.etUsuario);
        //Inicializa el campo de texto de la contraseña
        etPassword = (EditText) findViewById(R.id.etPassword);

        presenter = new LoginPresenter(this);

        setProgressDialog();

        AppCompatButton botonLogin = (AppCompatButton) findViewById(R.id.botonLogin);
        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

        AppCompatButton botonRegistro = (AppCompatButton) findViewById(R.id.botonRegistrar);
        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar(v);
            }
        });

        isUsuarioLogueado();

    }

    public void isUsuarioLogueado() {
        presenter.isUsuarioLogueado();
    }

    /**
     * login: metodo encargado de realizar el inicio de sesion utilizando los datos del formulario
     * @param v
     */
    public void login(final View v) {

        //Comprobar campos
        if (TextUtils.isEmpty(etUsuario.getText().toString())) {
            etUsuario.setError("Debes introducir un email");
            return;
        }
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("Debes introducir una contraseña");
            return;
        }

        //Muestra el ProgressDialog
        presenter.mostrarProgressDialog();

        presenter.login(etUsuario.getText().toString(), etPassword.getText().toString());
    }

    /**
     * registrar: metodo encargado de iniciar la actividad de registro
     * @param v
     */
    public void registrar(View v) {
        //inicia la actividad
        //se usa startActivityForResult para obtener un resultado de la actividad cuando esta termine
        //recibe por parametro un intent y un numero que identifica la solicitud
        startActivityForResult(new Intent(this, RegistroActivity.class), 10);
    }

    /**
     * onActivityResult: este metodo es llamado cuando la actividad iniciada con startActivityForResult
     * termina y devuelve un resultado
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Si el numero de la solicitud es la misma que la pasada por parametro a startActivityForResult
        //y el codigo de resultado es RESULT_OK
        if (requestCode == 10 && resultCode == RESULT_OK)
            //Se cierra esta actividad
            finish();
    }

    public void mostrarProgressDialog() {
        progressDialog.show();
    }

    public void ocultarProgressDialog() {
        progressDialog.dismiss();
    }

    public void setProgressDialog() {
        if (progressDialog != null)
            //Crea un ProgressDialog, este se utiliza para mostrar un dialogo que informa al usuario de
            //que se esta realizando un proceso
            progressDialog = new ProgressDialog(this);
        //Establece el mensaje del ProgressDialog
        progressDialog.setMessage("Iniciando sesion, espere");
        //Establece el ProgressDialog como modal
        progressDialog.setCancelable(false);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        Snackbar.make(etUsuario, mensaje, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void cargarMainActivity(Usuario usuario) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("usuario", usuario);
        Intent i = new Intent(this, MainActivity.class);
        i.putExtras(bundle);

        startActivity(i);
        presenter.ocultarProgressDialog();
        finish();

    }
}
