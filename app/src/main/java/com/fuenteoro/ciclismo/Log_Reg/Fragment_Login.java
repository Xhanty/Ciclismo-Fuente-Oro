package com.fuenteoro.ciclismo.Log_Reg;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.fuenteoro.ciclismo.R;
import com.google.android.material.textfield.TextInputLayout;

public class Fragment_Login extends Fragment {

    private TextInputLayout textEmail, textClave;
    Button login, olvidoclave, demo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        textEmail = (TextInputLayout) view.findViewById(R.id.correo_login);
        textClave = (TextInputLayout) view.findViewById(R.id.clave_login);

        login = (Button) view.findViewById(R.id.btn_login);
        olvidoclave = (Button) view.findViewById(R.id.btn_olvido_login);
        demo = (Button) view.findViewById(R.id.btn_verdemo_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmInput(v);
            }
        });

        olvidoclave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FUNCIÓN
            }
        });

        demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FUNCIÓN
            }
        });

        return view;
    }

    private boolean validateEmail(){
        String email = textEmail.getEditText().toString().trim();

        if (email.isEmpty()){
            textEmail.setError("Falta el correo");
            return false;
        } else {
            textEmail.setError(null);
            return true;
        }
    }

    private boolean validateClave(){
        String clave = textClave.getEditText().toString().trim();

        if (clave.isEmpty()){
            textClave.setError("Falta la contraseña");
            return false;
        } else {
            textClave.setError(null);
            return true;
        }
    }

    public void confirmInput(View v){
        if (!validateEmail() | !validateClave()){
            return;
        }

        Toast.makeText(getContext(), "OKKK", Toast.LENGTH_SHORT).show();

    }
}