package com.fuenteoro.ciclismo.Ciclista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fuenteoro.ciclismo.Models.Rutas;
import com.fuenteoro.ciclismo.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class DetallesRutaActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    TextView nombreruta, detalleruta;
    ImageView img_ruta;
    private String ID;
    RatingBar calificacion_detalle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_ruta);

        ID = getIntent().getStringExtra("ID");
        nombreruta = findViewById(R.id.nombre_ruta_detalle);
        detalleruta = findViewById(R.id.detalle_ruta_detalle);
        img_ruta = findViewById(R.id.img_ruta_detalle);
        calificacion_detalle = findViewById(R.id.calificacion_ruta_detalle);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Rutas");
        mDatabase.child(ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String nombre = dataSnapshot.child("nombre").getValue().toString();
                    String detalle = dataSnapshot.child("detalle").getValue().toString();
                    Double latitud_origen = (Double) dataSnapshot.child("latitud_origen").getValue();
                    Double longitud_origen = (Double) dataSnapshot.child("longitud_origen").getValue();
                    Double latitud_destino = (Double) dataSnapshot.child("latitud_destino").getValue();
                    Double longitud_destino = (Double) dataSnapshot.child("longitud_destino").getValue();
                    String imagen = dataSnapshot.child("imagen").getValue().toString();
                    int calificacion = Integer.parseInt(dataSnapshot.child("calificacion").getValue().toString());

                    nombreruta.setText(nombre);
                    detalleruta.setText(detalle);
                    Picasso.with(getApplicationContext()).load(imagen).into(img_ruta);
                    calificacion_detalle.setProgress(Integer.valueOf(calificacion));

                } else {
                    Toast.makeText(getApplicationContext(), "A ocurrido en error, intentalo más tarde", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "A ocurrido en error, intentalo más tarde", Toast.LENGTH_LONG).show();
            }
        });
    }
}