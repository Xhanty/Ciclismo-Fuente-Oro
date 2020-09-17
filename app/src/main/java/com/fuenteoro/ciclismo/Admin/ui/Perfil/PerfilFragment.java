package com.fuenteoro.ciclismo.Admin.ui.Perfil;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fuenteoro.ciclismo.Ciclista.InternetFragment;
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

public class PerfilFragment extends Fragment implements View.OnClickListener{


    Button guardar;
    //Progress Dialog
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    TextView nombre;
    TextInputEditText fullname, email;
    DatabaseReference mDatabase;
    String id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_admin, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        nombre = view.findViewById(R.id.pr_name_menu_admin);
        email = view.findViewById(R.id.pr_email_menu_admin);

        fullname = view.findViewById(R.id.full_name_profile_admin);

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
            transaction.replace(R.id.nav_host_fragment, nuevoFragmento);
            transaction.addToBackStack(null);

            // Commit a la transacción
            transaction.commit();
        }
        guardar = view.findViewById(R.id.btn_actualizar_datos_admin);
        guardar.setOnClickListener(this);


        guardar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                fullname.setEnabled(true);
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

        if (v == guardar){
            Map<String, Object> personMap = new HashMap<>();
            personMap.put("nombres", fullname.getText().toString().trim());
            mDatabase.child("Usuarios").child(id).updateChildren(personMap);


            fullname.setEnabled(false);
            Toast.makeText(getContext(), "Actualizado correctamente!", Toast.LENGTH_SHORT).show();
        }
    }
}