package com.fuenteoro.ciclismo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fuenteoro.ciclismo.Utils.UtilsNetwork;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn_reset;
    TextInputEditText correo;
    String valcorreo = "";

    //Progress Dialog
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        correo = findViewById(R.id.correo_reset);

        btn_reset = findViewById(R.id.btn_reset);

        btn_reset.setOnClickListener(this);
    }

    //VALIDACIONES
    private boolean validateEmail(){
        valcorreo = correo.getText().toString().trim();
        if(valcorreo.isEmpty()){
            correo.setError("Escribe un email");
            return false;

        } else if(!Patterns.EMAIL_ADDRESS.matcher(valcorreo).matches()){
            correo.setError("Escribe un email válido");
            return false;

        } else {
            correo.setError(null);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btn_reset){
            if(!validateEmail()){
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
                resetpassword();

            } else {
                Toast.makeText(this, "No tienes conexión a internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void resetpassword() {
        try {

            valcorreo = correo.getText().toString().trim();
            mAuth.setLanguageCode("es");
            mAuth.sendPasswordResetEmail(valcorreo).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        correo.setText("");
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Te hemos enviado un correo eléctronico para recuperar tú contraseña",
                                Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Este correo eléctronico no se encuentra registrado", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (Exception e){
            Toast.makeText(getApplicationContext(),"A ocurrido un error, intenta más tarde", Toast.LENGTH_LONG).show();
        }

    }
}