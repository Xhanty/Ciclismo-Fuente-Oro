package com.fuenteoro.ciclismo.Ciclista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fuenteoro.ciclismo.Models.GaleriaSitios;
import com.fuenteoro.ciclismo.R;
import com.fuenteoro.ciclismo.Sitios.RecorridoSitioActivity;
import com.fuenteoro.ciclismo.Utils.RecyclerGaleria;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetalleSitioActivity extends AppCompatActivity implements View.OnClickListener {

    TextView autor;
    RecyclerView galeria;
    ArrayList<GaleriaSitios> listGaleria;
    RecyclerGaleria adapter;

    private DatabaseReference mDatabase;
    TextView nombresitio, detallesitio, longitud, latitud;
    private String ID;
    RatingBar calificacion_detalle_sitio;
    Button ir_sitio;

    String nombre, detalle;
    Double latitud_sitio;
    Double longitud_sitio;
    Double LatitudL, LongitudL;

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
        ir_sitio = findViewById(R.id.btn_ir_sitio);
        ir_sitio.setOnClickListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Sitios");
    }

    @Override
    protected void onStart() {
        Detalle_sitio_map fragment = new Detalle_sitio_map();
        Bundle bundle = new Bundle();
        bundle.putString("ID", ID);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_detallesitio, fragment, null);
        fragmentTransaction.commit();


        adapter = new RecyclerGaleria(listGaleria, new RecyclerGaleria.OnclickRecycler() {
            @Override
            public void OnClicItemRecycler(GaleriaSitios galeriasitios) {
                //Glide.with(this).load(galeriasitios.getImagen()).into()
            }
        });

        mDatabase.child("ubicaciones").child(ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    nombre = dataSnapshot.child("nombre").getValue().toString();
                    detalle = dataSnapshot.child("detalle").getValue().toString();
                    latitud_sitio = (Double) dataSnapshot.child("latitud_sitio").getValue();
                    longitud_sitio = (Double) dataSnapshot.child("longitud_sitio").getValue();
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
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if(v == ir_sitio){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Selecciona una opción");
            builder.setIcon(R.drawable.ic_buscar);

            final CharSequence[] opciones = new CharSequence[2];
            opciones[0] = "Dirigirse con conexión";
            opciones[1] = "Dirigirse sin conexión";

            builder.setItems(opciones, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (opciones[which] == opciones[0]){
                        Intent intent = new Intent(DetalleSitioActivity.this, RecorridoSitioActivity.class);
                        intent.putExtra("ID", ID);
                        intent.putExtra("Nombre", nombre);
                        intent.putExtra("Latitud", latitud_sitio);
                        intent.putExtra("Longitud", longitud_sitio);
                        startActivity(intent);
                        finish();
                        //CON CONEXIÓN
                    } else {
                        //SIN CONEXIÓN
                        try {

                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("geo:"+latitud_sitio+","+longitud_sitio+"?z=16&q="+latitud_sitio+","+longitud_sitio+"("+nombre+")"));
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            startActivity(intent);
                            finish();

                        } catch (Exception e) {
                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
                            startActivity( new Intent(Intent.ACTION_VIEW, uri));
                            finish();
                        }
                    }
                }
            });

            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}