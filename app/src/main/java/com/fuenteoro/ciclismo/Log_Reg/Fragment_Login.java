package com.fuenteoro.ciclismo.Log_Reg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.fuenteoro.ciclismo.MenuActivity;
import com.fuenteoro.ciclismo.R;
import com.fuenteoro.ciclismo.ResetPasswordActivity;
import com.fuenteoro.ciclismo.Utils.UtilsNetwork;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Fragment_Login extends Fragment implements View.OnClickListener{

    //Validaciones
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=\\w*\\d)(?=\\w*[a-zA-Z0-9])\\S{6,16}$");

    //Progress Dialog
    ProgressDialog progressDialog;

    //VARIABLES QUE VAMOS A REGISTRAR
    private String correo = "";
    private String contrasena = "";


    private TextInputEditText email, clave;
    Button login, olvidoclave, demo;

    //Login en Firebase
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        email = (TextInputEditText) view.findViewById(R.id.correo_login);
        clave = (TextInputEditText) view.findViewById(R.id.clave_login);

        login = (Button) view.findViewById(R.id.btn_login);
        olvidoclave = (Button) view.findViewById(R.id.btn_olvido_login);
        demo = (Button) view.findViewById(R.id.btn_verdemo_login);

        mAuth = FirebaseAuth.getInstance();

        olvidoclave.setOnClickListener(this);
        demo.setOnClickListener(this);
        login.setOnClickListener(this);

        return view;
    }

    //VALIDACIONES
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

    @Override
    public void onClick(View v) {
        if(v == login){
            if(!validateEmail() | !validateclave()){
                return;
            }

            if(UtilsNetwork.isOnline(getContext())){
                //Abrimos el progressDialog
                progressDialog = new ProgressDialog(getContext());
                progressDialog.show();

                //Contenido
                progressDialog.setContentView(R.layout.progress_dialog);

                //Transparente
                progressDialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );

                //Ejecutamos la acción
                ingresarsesion();

            } else {
                Toast.makeText(getContext(), "No tienes conexión a internet", Toast.LENGTH_SHORT).show();
            }

        } else if(v == olvidoclave){
            Intent intent = new Intent(getContext(), ResetPasswordActivity.class);
            startActivity(intent);

        } else if(v == demo){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=tbneQDc2H3I")));
        }
    }


    private void ingresarsesion() {
        try {
            correo = email.getText().toString().trim();
            contrasena = clave.getText().toString().trim();

            mAuth.signInWithEmailAndPassword(correo, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        email.setText("");
                        clave.setText("");
                        Toast.makeText(getActivity(), "Sesión iniciada correctamente", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        startActivity(new Intent(getContext(), MenuActivity.class));

                    } else {
                        Toast.makeText(getActivity(), "Correo y/o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                        clave.setText("");
                        progressDialog.dismiss();
                    }
                }
            });
        } catch (Exception e){
            Toast.makeText(getContext(), "A ocurrido un error, intenta más tarde", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getContext(), MenuActivity.class));
        }
    }
}