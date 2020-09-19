package com.fuenteoro.ciclismo.Admin.ui.Usuarios;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fuenteoro.ciclismo.MenuActivity;
import com.fuenteoro.ciclismo.R;
import com.fuenteoro.ciclismo.Utils.UtilsNetwork;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AddUsuariosActivity extends AppCompatActivity implements View.OnClickListener {

    //Validaciones
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=\\w*\\d)(?=\\w*[a-zA-Z0-9])\\S{6,16}$");

    private static final Pattern NOMBRE_PATTERN =
            Pattern.compile("^[A-Za-z\\s]{3,30}+$");

    private static final Pattern APELLIDO_PATTERN =
            Pattern.compile("^[A-Za-z\\s]{3,30}+$");

    private static final Pattern TELEFONO_PATTERN =
            Pattern.compile("^(3)*([0-9]*){10}$");

    //Progress Dialog
    ProgressDialog progressDialog;

    //Varables de entorno
    TextInputEditText nombre, apellido;
    TextInputEditText email, telefono, clave, conficlave;
    Button registro;

    //VARIABLES QUE VAMOS A REGISTRAR
    private String nombres = "";
    private String apellid = "";
    private String nombrecomple = "";
    private String correo = "";
    private Double telefo;
    private  String contrasena = "";
    private String confirmarclave = "";
    private StringBuffer claveencri = null;
    private String resulthash = "";

    //Registro en Firebase
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_usuarios);

        telefono = (TextInputEditText) findViewById(R.id.tel_regis_admin);
        nombre = (TextInputEditText) findViewById(R.id.nombres_regis_admin);
        apellido = (TextInputEditText) findViewById(R.id.apellidos_regis_admin);
        email = (TextInputEditText) findViewById(R.id.correo_register_admin);
        clave = (TextInputEditText) findViewById(R.id.clave_register_admin);
        conficlave = (TextInputEditText) findViewById(R.id.claveconfir_register_admin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        registro = (Button) findViewById(R.id.btn_register_admin);
        registro.setOnClickListener(this);
    }

    //VALIDACIONES
    private boolean validateTel(){
        String telefopp = telefono.getText().toString().trim();

        if(telefopp.isEmpty()){
            telefono.setError("Ingresa un teléfono");
            return false;
        }

        if(!TELEFONO_PATTERN.matcher(telefopp).matches()){
            telefono.setError("Escribe un teléfono válido");
            return false;

        } else {
            telefono.setError(null);
            return true;
        }
    }

    private boolean validateEmail(){
        correo = email.getText().toString().trim();
        if(correo.isEmpty()){
            email.setError("Escribe un email");
            return false;

        } else if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            email.setError("Escribe un email válido");
            return false;

        } else {
            email.setError(null);
            return true;
        }
    }

    private boolean validatenombre(){
        nombres = nombre.getText().toString().trim();
        if(nombres.isEmpty()){
            nombre.setError("Ingresa un nombre");
            return false;

        } else if(!NOMBRE_PATTERN.matcher(nombres).matches()){
            nombre.setError("Ingresa un nombre válido");
            return false;

        } else {
            nombre.setError(null);
            return true;
        }
    }

    private boolean validateapellido(){
        apellid = apellido.getText().toString().trim();
        if(apellid.isEmpty()){
            apellido.setError("Ingresa un apellido");
            return false;

        } else if(!APELLIDO_PATTERN.matcher(apellid).matches()) {
            apellido.setError("Ingresa un apellido válido");
            return false;

        } else {
            apellido.setError(null);
            return true;
        }
    }

    private boolean validateclave(){
        contrasena = clave.getText().toString().trim();
        if(contrasena.isEmpty()){
            clave.setError("Ingresa una contraseña");
            return false;

        } else if(!PASSWORD_PATTERN.matcher(contrasena).matches()){
            clave.setError("La contraseña debe tener mínimo 6 dígitos y un número");
            return false;

        } else {
            clave.setError(null);
            return true;
        }
    }

    private boolean validateconfirmaclave(){
        confirmarclave = conficlave.getText().toString().trim();
        if(confirmarclave.isEmpty()){
            conficlave.setError("Ingresa la confirmación de la contraseña");
            return false;

        } else if(!PASSWORD_PATTERN.matcher(confirmarclave).matches()){
            conficlave.setError("La confirmación de la contraseña debe tener mínimo 6 dígitos y un número");
            return false;

        } else {
            conficlave.setError(null);
            return true;
        }
    }

    private  boolean validatelasclaves(){
        contrasena = clave.getText().toString().trim();
        confirmarclave = conficlave.getText().toString().trim();

        if(!contrasena.equals(confirmarclave)){
            conficlave.setError("Las contraseñas no coinciden");
            return false;
        } else {
            conficlave.setError(null);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == registro) {
            if (!validateEmail() | !validateTel() | !validatenombre() | !validateapellido() | !validateclave() | !validateconfirmaclave() | !validatelasclaves()) {
                return;

            }
            if(UtilsNetwork.isOnline(this)){
                //Abrimos el progressDialog
                progressDialog = new ProgressDialog(this);
                progressDialog.show();

                //Contenido
                progressDialog.setContentView(R.layout.progress_dialog);

                //Transparente
                progressDialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );

                //Ejecutamos la acción
                registrarusuario();

            } else {
                Toast.makeText(this, "No tienes conexión a internet", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //ACCIÓN DE REGISTRAR
    private void registrarusuario() {
        try {
            telefo = Double.valueOf(telefono.getText().toString().trim());
            correo = email.getText().toString().trim();
            nombrecomple = nombre.getText().toString().trim() + " " + apellido.getText().toString().trim();
            contrasena = clave.getText().toString().trim();


            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(contrasena.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                MD5Hash.append(h);
            }
            claveencri = MD5Hash;

            resulthash = claveencri.toString().trim();

            mAuth.createUserWithEmailAndPassword(correo, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("telefono", telefo);
                        map.put("nombres", nombrecomple);
                        map.put("email", correo);
                        map.put("clave", resulthash);
                        map.put("perfil", "Administrador");

                        String id = mAuth.getCurrentUser().getUid();

                        mDatabase.child("Usuarios").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task2) {
                                if (task2.isSuccessful()) {
                                    //Redireccionar y limpiar los datos
                                    email.setText("");
                                    telefono.setText("");
                                    nombre.setText("");
                                    apellido.setText("");
                                    clave.setText("");
                                    conficlave.setText("");
                                    Toast.makeText(AddUsuariosActivity.this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    finish();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(AddUsuariosActivity.this, "Este correo eléctronico ya se encuentra registrado", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "A ocurrido un error, intenta más tarde", Toast.LENGTH_LONG).show();
        }
    }
}