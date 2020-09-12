package com.fuenteoro.ciclismo.Ciclista;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fuenteoro.ciclismo.LoginActivity;
import com.fuenteoro.ciclismo.R;
import com.fuenteoro.ciclismo.Utils.UtilsNetwork;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PerfilFragment extends Fragment implements View.OnClickListener {

    Button cerrar_sesion, guardar;
    //Progress Dialog
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    TextView nombre;
    TextInputEditText fullname, email;
    private DatabaseReference mDatabase;
    String id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        nombre = view.findViewById(R.id.pr_name_menu);
        email = view.findViewById(R.id.pr_email_menu);

        fullname = view.findViewById(R.id.full_name_profile);


        if(UtilsNetwork.isOnline(getContext())) {
            //Abrimos el progressDialog
            progressDialog = new ProgressDialog(getContext());
            progressDialog.show();

            //Contenido
            progressDialog.setContentView(R.layout.progress_dialog);

            //Transparente
            progressDialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent);
            UserInfo();

        } else {
            // Crea el nuevo fragmento y la transacción.
            Fragment nuevoFragmento = new InternetFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_ciclista, nuevoFragmento);
            transaction.addToBackStack(null);

            // Commit a la transacción
            transaction.commit();
        }

        cerrar_sesion = view.findViewById(R.id.btn_cerrarsesion);
        guardar = view.findViewById(R.id.btn_guardardatos);

        cerrar_sesion.setOnClickListener(this);
        guardar.setOnClickListener(this);
        cerrar_sesion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                fullname.setEnabled(true);
                cerrar_sesion.setVisibility(View.INVISIBLE);
                guardar.setVisibility(View.VISIBLE);
                return false;
            }
        });

        return view;
    }

    private void UserInfo(){
        try {
            id = mAuth.getCurrentUser().getUid();
            mDatabase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String namedata = dataSnapshot.child("nombres").getValue().toString();
                        String emaildata = dataSnapshot.child("email").getValue().toString();

                        nombre.setText(namedata);
                        fullname.setText(namedata);
                        email.setText(emaildata);
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception e){
            Toast.makeText(getContext(), "A ocurrido un error, intentalo más tarde", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        if(v == cerrar_sesion){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Alerta");
            builder.setMessage("¿Deseas cerrar la sesión?");

            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getContext(), "Sesión cerrada!", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    //finish();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            builder.create();
            builder.show();
        } else if (v == guardar){
                Map<String, Object> personMap = new HashMap<>();
                personMap.put("nombres", fullname.getText().toString().trim());
                mDatabase.child("Usuarios").child(id).updateChildren(personMap);

                fullname.setEnabled(false);
                cerrar_sesion.setVisibility(View.VISIBLE);
                guardar.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Actualizado correctamente!", Toast.LENGTH_SHORT).show();
        }
    }
}