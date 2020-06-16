package com.fuenteoro.ciclismo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    boolean cerrar = false;
    Button cerrar_sesion;
    private FirebaseAuth mAuth;
    TextView nombre, email;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        nombre = findViewById(R.id.pr_name_menu);
        email = findViewById(R.id.pr_email_menu);

        UserInfo();

        cerrar_sesion = findViewById(R.id.btn_cerrarsesion);

        cerrar_sesion.setOnClickListener(this);
    }

    private void UserInfo(){
        try {
            String id = mAuth.getCurrentUser().getUid();
            mDatabase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String namedata = dataSnapshot.child("nombres").getValue().toString();
                        String emaildata = dataSnapshot.child("email").getValue().toString();

                        nombre.setText(namedata);
                        email.setText(emaildata);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception e){
            Toast.makeText(this, "A ocurrido un error, intentalo más tarde", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v == cerrar_sesion){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Alerta");
            builder.setMessage("¿Deseas cerrar la sesión?");

            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Sesión cerrada!", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            builder.create();
            builder.show();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Alerta");
        builder.setMessage("¿Deseas salir de la aplicación?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cerrar = true;
                salirApp(cerrar);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cerrar = false;
                salirApp(cerrar);
            }
        });

        builder.create();
        builder.show();
    }

    public void salirApp(boolean cerrar){
        if(cerrar == true){
            Toast.makeText(this, "Regresa pronto!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}