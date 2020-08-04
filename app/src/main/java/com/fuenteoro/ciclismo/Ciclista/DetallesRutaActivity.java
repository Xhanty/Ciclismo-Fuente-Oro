package com.fuenteoro.ciclismo.Ciclista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fuenteoro.ciclismo.Models.ComentariosRutas;
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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private int IDCC;
    private RecyclerView mRutasList;
    //Progress Dialog
    ProgressDialog progressDialog;
    FirebaseRecyclerOptions<ComentariosRutas> options;
    FirebaseRecyclerAdapter<ComentariosRutas, DetallesRutaViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_ruta);

        ID = getIntent().getStringExtra("ID");
        //nombreruta = findViewById(R.id.nombre_ruta_detalle);
        //detalleruta = findViewById(R.id.detalle_ruta_detalle);
        //img_ruta = findViewById(R.id.img_ruta_detalle);
        //calificacion_detalle = findViewById(R.id.calificacion_ruta_detalle);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        IDCC = Integer.parseInt(ID);

       // mRutasList = (RecyclerView) findViewById(R.id.recy_rutas_detalles_c);
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

            mDatabase.child("Rutas").child("ubicaciones").child(ID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String nombre = dataSnapshot.child("nombre").getValue().toString();
                        String distancia = dataSnapshot.child("distancia").getValue().toString();
                        String elevacion = dataSnapshot.child("elevacion").getValue().toString();
                        String dificultad = dataSnapshot.child("dificultad").getValue().toString();
                        Double latitud_origen = (Double) dataSnapshot.child("latitud_origen").getValue();
                        Double longitud_origen = (Double) dataSnapshot.child("longitud_origen").getValue();
                        Double latitud_destino = (Double) dataSnapshot.child("latitud_destino").getValue();
                        Double longitud_destino = (Double) dataSnapshot.child("longitud_destino").getValue();
                        String imagen = dataSnapshot.child("imagen").getValue().toString();
                        int calificacion = Integer.parseInt(dataSnapshot.child("calificacion").getValue().toString());

                        //nombreruta.setText(nombre);
                        //detalleruta.setText(distancia);
                        //Picasso.with(getApplicationContext()).load(imagen).into(img_ruta);
                        //calificacion_detalle.setProgress(Integer.valueOf(calificacion));

                    } else {
                        Toast.makeText(getApplicationContext(), "A ocurrido en error, intentalo más tarde", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "A ocurrido en error, intentalo más tarde", Toast.LENGTH_LONG).show();
                }
            });
            final Query query = FirebaseDatabase.getInstance().getReference("Rutas").child("comentarios").orderByChild("ruta").equalTo(IDCC);

            /*query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){

                        options = new FirebaseRecyclerOptions.Builder<ComentariosRutas>().setQuery(query, ComentariosRutas.class).build();
                        adapter = new FirebaseRecyclerAdapter<ComentariosRutas, DetallesRutaViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(DetallesRutaViewHolder detallesRutaViewHolder, final int i, ComentariosRutas comentariosRutas) {
                                detallesRutaViewHolder.setCiclista(comentariosRutas.getCiclista());
                                detallesRutaViewHolder.setComentario(comentariosRutas.getComentario());
                                detallesRutaViewHolder.setFecha(comentariosRutas.getFecha());
                            }

                            @NonNull
                            @Override
                            public DetallesRutaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comentarios_rutas_row, parent, false);
                                return new DetallesRutaViewHolder(v);
                            }
                        };
                        Toast.makeText(getApplicationContext(), "Encontré comentarios", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        adapter.startListening();
                        mRutasList.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "No hay comentarios", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
progressDialog.dismiss();

                }
            });
*/
        } else{
progressDialog.dismiss();
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
            TextView comentario_post = (TextView)mView.findViewById(R.id.comentario_ruta);
            comentario_post.setText(comentario);
        }

        public void setFecha(String fecha){
            TextView fecha_post = (TextView)mView.findViewById(R.id.fecha_comentario_ruta);
            fecha_post.setText(fecha);
        }
    }

    private void comentariosrutas(){
    }
}