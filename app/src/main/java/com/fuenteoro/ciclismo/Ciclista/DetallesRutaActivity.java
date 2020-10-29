package com.fuenteoro.ciclismo.Ciclista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fuenteoro.ciclismo.Models.ComentariosRutas;
import com.fuenteoro.ciclismo.Models.GaleriaSitios;
import com.fuenteoro.ciclismo.R;
import com.fuenteoro.ciclismo.Rutas.RecorridoRutaActivity;
import com.fuenteoro.ciclismo.Sitios.RecorridoSitioActivity;
import com.fuenteoro.ciclismo.Utils.RecyclerGaleria;
import com.fuenteoro.ciclismo.Utils.UtilsNetwork;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DetallesRutaActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;
    TextView nombreruta, distancia, dificultad;
    private String ID;
    RatingBar calificacion_detalle;

    TextView autor;
    RecyclerView galeria;
    ArrayList<GaleriaSitios> listGaleria;
    RecyclerGaleria adapter;

    String txtnombre, txtdistancia, txtdificultad;
    Double latitud_origen, latitud_destino, longitud_origen, longitud_destino;
    Button ir_sitio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_ruta);

        ID = getIntent().getStringExtra("ID");
        nombreruta = findViewById(R.id.nombre_ruta_detalle);
        distancia = findViewById(R.id.distancia_ruta_detalle);
        dificultad = findViewById(R.id.dificultad_ruta_detalle);
        calificacion_detalle = findViewById(R.id.calificacion_ruta_detalle);

        autor = findViewById(R.id.autor_galeria_ruta_detalle);
        galeria = findViewById(R.id.galeria_ruta_detalle);
        galeria.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        listGaleria = new GaleriaSitios().galeriaSitios();
        ir_sitio = findViewById(R.id.btn_ir_ruta);
        ir_sitio.setOnClickListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Rutas");
    }

    @Override
    protected void onStart() {
        Detalle_ruta_map fragment = new Detalle_ruta_map();
        Bundle bundle = new Bundle();
        bundle.putString("ID", ID);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map_detalle_ruta, fragment, null);
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
                    if (dataSnapshot.exists()) {
                        txtnombre = dataSnapshot.child("nombre").getValue().toString();
                        txtdistancia = dataSnapshot.child("distancia").getValue().toString();
                        txtdificultad = dataSnapshot.child("dificultad").getValue().toString();
                        int calificacion = Integer.parseInt(dataSnapshot.child("calificacion").getValue().toString());

                        nombreruta.setText(txtnombre);
                        distancia.setText(txtdistancia);
                        dificultad.setText(txtdificultad);
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
                        Intent intent = new Intent(DetallesRutaActivity.this, RecorridoRutaActivity.class);
                        intent.putExtra("ID", ID);
                        intent.putExtra("Nombre", txtnombre);
                        intent.putExtra("Latitud_Origen", latitud_origen);
                        intent.putExtra("Longitud_Origen", longitud_origen);
                        intent.putExtra("Latitud_Destino", latitud_destino);
                        intent.putExtra("Longitud_Destino", longitud_destino);
                        startActivity(intent);
                        finish();
                        //CON CONEXIÓN
                    } else {
                        //SIN CONEXIÓN
                        try {

                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("geo:"+latitud_origen+","+longitud_origen+"?z=16&q="+latitud_origen+","+longitud_origen+"("+txtnombre+")"));
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            startActivity(intent);
                            finish();

                        } catch (Exception e) {
                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
                            startActivity( new Intent(Intent.ACTION_VIEW, uri));
                            //finish();
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