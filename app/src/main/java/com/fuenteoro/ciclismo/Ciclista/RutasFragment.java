package com.fuenteoro.ciclismo.Ciclista;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fuenteoro.ciclismo.Models.Rutas;
import com.fuenteoro.ciclismo.Utils.RutasUtilidades;
import com.fuenteoro.ciclismo.R;
import com.fuenteoro.ciclismo.Utils.UtilsNetwork;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RutasFragment extends Fragment {

    private RecyclerView mRutasList;
    private DatabaseReference mDatabase;
    ProgressDialog progressDialog;
    FirebaseRecyclerOptions<Rutas> options;
    FirebaseRecyclerAdapter<Rutas, RutasViewHolder> adapter;
    SearchView searchView;
    ImageButton mapabtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rutas, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference("Rutas").child("ubicaciones");
        mDatabase.keepSynced(true);

        mRutasList = (RecyclerView) view.findViewById(R.id.recy_rutas);
        mRutasList.setHasFixedSize(true);
        mRutasList.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView = view.findViewById(R.id.bs_ruta);
        mapabtn = view.findViewById(R.id.btn_mapa_ruta);

        mapabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RutaMapFragment fr = new RutaMapFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_ciclista,fr)
                        .addToBackStack(null)
                        .commit();

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearchRuta(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText == null || newText.trim().isEmpty()){
                    listrutas();
                    return false;
                }

                firebaseSearchRuta(newText);
                return false;
            }
        });
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

            listrutas();

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

    public void listrutas() {

            options = new FirebaseRecyclerOptions.Builder<Rutas>().setQuery(mDatabase, Rutas.class).build();
            adapter = new FirebaseRecyclerAdapter<Rutas, RutasViewHolder>(options) {
                @Override
                protected void onBindViewHolder(RutasViewHolder rutasViewHolder,final int i, Rutas rutas) {
                    rutasViewHolder.setNombre(rutas.getNombre());
                    rutasViewHolder.setDistancia(rutas.getDistancia());
                    rutasViewHolder.setElevacion(rutas.getElevacion());
                    rutasViewHolder.setDificultad(rutas.getDificultad());
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
        }

    public void firebaseSearchRuta(String searchText) {
            try {
                String quary = searchText.toLowerCase();
                Query firebaseSearch = mDatabase.orderByChild("nombre").startAt(quary).endAt(quary + "\uf8ff");
                options = new FirebaseRecyclerOptions.Builder<Rutas>().setQuery(firebaseSearch, Rutas.class).build();
                adapter = new FirebaseRecyclerAdapter<Rutas, RutasViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(RutasViewHolder rutasViewHolder, final int i, Rutas rutas) {
                        rutasViewHolder.setNombre(rutas.getNombre());
                        rutasViewHolder.setDistancia(rutas.getDistancia());
                        rutasViewHolder.setElevacion(rutas.getElevacion());
                        rutasViewHolder.setDificultad(rutas.getDificultad());
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

            } catch (Exception e) {
                e.printStackTrace();
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

        public void setDistancia(String distancia){
            TextView distancia_post = (TextView)mView.findViewById(R.id.detalle_distancia_ruta);
            distancia_post.setText(distancia);
        }

        public void setElevacion(String elevacion){
            TextView elevacion_post = (TextView)mView.findViewById(R.id.detalle_elevacion_ruta);
            elevacion_post.setText(elevacion);
        }

        public void setDificultad(String dificultad){
            TextView dificultad_post = (TextView)mView.findViewById(R.id.detalle_dificultad_ruta);
            dificultad_post.setText(dificultad);
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
