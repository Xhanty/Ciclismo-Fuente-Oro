package com.fuenteoro.ciclismo.Log_Reg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fuenteoro.ciclismo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Fragment_Register extends Fragment implements View.OnClickListener {

    Button registro;
    //EditText nombre, apellido;
    //EditText email, clave, conficlave;

    TextInputEditText nombre, apellido;
    TextInputEditText email, clave, conficlave;

    //VARIABLES QUE VAMOS A REGISTRAR
    private String nombres = "";
    private String correo = "";
    private  String contrasena = "";

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        nombre = (TextInputEditText) view.findViewById(R.id.nombres_regis);
        apellido = (TextInputEditText) view.findViewById(R.id.apellidos_regis);
        email = (TextInputEditText) view.findViewById(R.id.correo_register);
        clave = (TextInputEditText) view.findViewById(R.id.clave_register);
        conficlave = (TextInputEditText) view.findViewById(R.id.claveconfir_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        registro = (Button) view.findViewById(R.id.btn_register);
        registro.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == registro){
            nombres = nombre.getText().toString() + " " + apellido.getText().toString();
            correo = email.getText().toString();
            contrasena = clave.getText().toString();

            if (!nombres.isEmpty() && !correo.isEmpty() && !contrasena.isEmpty()) {

                if (contrasena.length() >= 6){
                    registrarusuario();

                } else {
                    Toast.makeText(getContext(), "La clave debe tener 6 dígitos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Tienes un campo vacío", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registrarusuario(){
        mAuth.createUserWithEmailAndPassword(correo, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Map<String, Object> map = new HashMap<>();
                    map.put("nombres", nombres);
                    map.put("email", correo);
                    map.put("clave", contrasena);

                    String id = mAuth.getCurrentUser().getUid();

                    mDatabase.child("Usuarios").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()){
                                Toast.makeText(getContext(), "OHH YEAHHH", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(getContext(), "No se pudo registrar este usuario en realtime", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "No se pudo registrar este usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}