package com.fuenteoro.ciclismo.Ciclista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fuenteoro.ciclismo.Models.ComentariosRutas;
import com.fuenteoro.ciclismo.Models.Rutas;
import com.fuenteoro.ciclismo.R;
import com.fuenteoro.ciclismo.Utils.UtilsNetwork;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class DetallesRutaActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    TextView nombreruta, detalleruta, comentarioruta, ciclista, fecha;
    ImageView img_ruta;
    private String ID;
    RatingBar calificacion_detalle;
    private int IDCC;
    private RecyclerView mRutasList;
    //Progress Dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_ruta);

        ID = getIntent().getStringExtra("ID");
        nombreruta = findViewById(R.id.nombre_ruta_detalle);
        detalleruta = findViewById(R.id.detalle_ruta_detalle);
        img_ruta = findViewById(R.id.img_ruta_detalle);
        calificacion_detalle = findViewById(R.id.calificacion_ruta_detalle);

        comentarioruta = findViewById(R.id.comentario_ruta);
        ciclista = findViewById(R.id.ciclista_comentario_ruta);
        fecha = findViewById(R.id.fecha_comentario_ruta);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Rutas");
        IDCC = Integer.parseInt(ID);
        mDatabase.keepSynced(true);

        mRutasList = (RecyclerView) findViewById(R.id.recy_rutas_detalles_c);
        mRutasList.setHasFixedSize(true);
        mRutasList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(UtilsNetwork.isOnline(this)) {
            //Abrimos el progressDialog
            progressDialog = new ProgressDialog(this);
            progressDialog.show();

            //Contenido
            progressDialog.setContentView(R.layout.progress_dialog);

            //Transparente
            progressDialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent);


            final Query q = mDatabase.child("comentarios").orderByChild("ruta").equalTo(IDCC);

            FirebaseRecyclerAdapter<ComentariosRutas, DetallesRutaViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ComentariosRutas, DetallesRutaViewHolder>
                    (ComentariosRutas.class, R.layout.comentarios_rutas_row, DetallesRutaViewHolder.class, q) {

                @Override
                protected void populateViewHolder(DetallesRutaViewHolder detallesRutaViewHolder, ComentariosRutas comentariosRutas, final int i) {

                    detallesRutaViewHolder.setCiclista(comentariosRutas.getCiclista());
                    detallesRutaViewHolder.setComentario(comentariosRutas.getComentario());
                    detallesRutaViewHolder.setFecha(comentariosRutas.getFecha());
                    progressDialog.dismiss();
                }
            };
            mRutasList.setAdapter(firebaseRecyclerAdapter);


            mDatabase.child("ubicaciones").child(ID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
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

        } else{
            Toast.makeText(getApplicationContext(), "NO WIFI", Toast.LENGTH_LONG).show();
        }
    }

    public static class DetallesRutaViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public DetallesRutaViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setCiclista(String ciclista){
            TextView ciclista_post = (TextView)mView.findViewById(R.id.ciclista_comentario_ruta);
            ciclista_post.setText(ciclista);
        }

        public void setComentario(String comentario){
            TextView detalle_post = (TextView)mView.findViewById(R.id.comentario_ruta);
            detalle_post.setText(comentario);
        }

        public void setFecha(String fecha){
            TextView calificacion_post = (TextView) mView.findViewById(R.id.fecha_comentario_ruta);
            calificacion_post.setText(fecha);
        }
    }
}