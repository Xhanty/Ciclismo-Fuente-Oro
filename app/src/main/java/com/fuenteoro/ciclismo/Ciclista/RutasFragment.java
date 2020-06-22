package com.fuenteoro.ciclismo.Ciclista;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fuenteoro.ciclismo.Models.ComentariosRutas;
import com.fuenteoro.ciclismo.Models.Rutas;
import com.fuenteoro.ciclismo.R;
import com.fuenteoro.ciclismo.Utils.UtilsNetwork;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class RutasFragment extends Fragment {

    private RecyclerView mRutasList;
    private DatabaseReference mDatabase;
    //Progress Dialog
    ProgressDialog progressDialog;
    FirebaseRecyclerOptions<Rutas> options;
    FirebaseRecyclerAdapter<Rutas, RutasViewHolder> adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rutas, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Rutas").child("ubicaciones");
        mDatabase.keepSynced(true);

        mRutasList = (RecyclerView) view.findViewById(R.id.recy_rutas);
        mRutasList.setHasFixedSize(true);
        mRutasList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

            if(UtilsNetwork.isOnline(getContext())) {
                //Abrimos el progressDialog
                progressDialog = new ProgressDialog(getContext());
                progressDialog.show();

                //Contenido
                progressDialog.setContentView(R.layout.progress_dialog);

                //Transparente
                progressDialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent);

                options = new FirebaseRecyclerOptions.Builder<Rutas>().setQuery(mDatabase, Rutas.class).build();
                adapter = new FirebaseRecyclerAdapter<Rutas, RutasViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(RutasViewHolder rutasViewHolder,final int i, Rutas rutas) {
                        rutasViewHolder.setNombre(rutas.getNombre());
                        rutasViewHolder.setDetalle(rutas.getDetalle());
                        rutasViewHolder.setImage(getContext(), rutas.getImagen());
                        rutasViewHolder.setCalificacion(rutas.getCalificacion());
                        progressDialog.dismiss();


                        rutasViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), DetallesRutaActivity.class);
                                intent.putExtra("ID", getRef(i).getKey());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RutasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rutas_row, parent, false);
                        return new RutasViewHolder(v);
                    }
                };
                adapter.startListening();
                mRutasList.setAdapter(adapter);

            } else {

                // Crea el nuevo fragmento y la transacción.
                Fragment nuevoFragmento = new InternetFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_ciclista, nuevoFragmento);
                transaction.addToBackStack(null);

                // Commit a la transacción
                transaction.commit();
        }
    }

    public static class RutasViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public RutasViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setNombre(String nombre){
            TextView nombre_post = (TextView)mView.findViewById(R.id.nombre_ruta);
            nombre_post.setText(nombre);
        }

        public void setDetalle(String detalle){
            TextView detalle_post = (TextView)mView.findViewById(R.id.detalle_ruta);
            detalle_post.setText(detalle);
        }

        public void setImage(Context ctx, String image){
            ImageView image_post = (ImageView)mView.findViewById(R.id.img_ruta);
            Picasso.with(ctx).load(image).into(image_post);
        }

        public void setCalificacion(int calificacion){
            RatingBar calificacion_post = (RatingBar)mView.findViewById(R.id.calificacion_ruta);
            calificacion_post.setProgress(Integer.valueOf(calificacion));
        }
    }
}