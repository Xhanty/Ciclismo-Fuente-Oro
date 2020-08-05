package com.fuenteoro.ciclismo.Ciclista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fuenteoro.ciclismo.Models.GaleriaSitios;
import com.fuenteoro.ciclismo.R;
import com.fuenteoro.ciclismo.Utils.RecyclerGaleria;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetalleSitioActivity extends AppCompatActivity {

    TextView autor;
    RecyclerView galeria;
    ArrayList<GaleriaSitios> listGaleria;
    RecyclerGaleria adapter;

    private DatabaseReference mDatabase;
    TextView nombresitio, detallesitio, longitud, latitud;
    private String ID;
    RatingBar calificacion_detalle_sitio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_sitio);

        ID = getIntent().getStringExtra("ID");
        nombresitio = findViewById(R.id.nombre_sitio_detalle);
        detallesitio = findViewById(R.id.detalle_sitio_detalle);
        longitud = findViewById(R.id.longitud_sitio_detalle);
        latitud = findViewById(R.id.latitud_sitio_detalle);
        calificacion_detalle_sitio = findViewById(R.id.calificacion_sitio_detalle);

        autor = findViewById(R.id.autor_galeria_sitio_detalle);
        galeria = findViewById(R.id.galeria_sitio_detalle);
        galeria.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        listGaleria = new GaleriaSitios().galeriaSitios();
        adapter = new RecyclerGaleria(listGaleria, new RecyclerGaleria.OnclickRecycler() {
            @Override
            public void OnClicItemRecycler(GaleriaSitios galeriasitios) {
                //Glide.with(this).load(galeriasitios.getImagen()).into()
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Sitios");
        mDatabase.child("ubicaciones").child(ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String nombre = dataSnapshot.child("nombre").getValue().toString();
                    String detalle = dataSnapshot.child("detalle").getValue().toString();
                    Double latitud_sitio = (Double) dataSnapshot.child("latitud_sitio").getValue();
                    Double longitud_sitio = (Double) dataSnapshot.child("longitud_sitio").getValue();
                    String imagen = dataSnapshot.child("imagen").getValue().toString();
                    int calificacion = Integer.parseInt(dataSnapshot.child("calificacion").getValue().toString());

                    nombresitio.setText(nombre);
                    latitud.setText(String.valueOf(latitud_sitio));
                    longitud.setText(String.valueOf(longitud_sitio));
                    detallesitio.setText(detalle);
                    //Picasso.with(getApplicationContext()).load(imagen).into(img_sitio);
                    calificacion_detalle_sitio.setProgress(Integer.valueOf(calificacion));

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