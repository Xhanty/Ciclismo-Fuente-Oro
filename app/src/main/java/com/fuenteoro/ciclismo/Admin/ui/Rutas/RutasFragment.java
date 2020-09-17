package com.fuenteoro.ciclismo.Admin.ui.Rutas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fuenteoro.ciclismo.Ciclista.InternetFragment;
import com.fuenteoro.ciclismo.Models.Rutas;
import com.fuenteoro.ciclismo.R;
import com.fuenteoro.ciclismo.Utils.UtilsNetwork;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class RutasFragment extends Fragment {

    private RecyclerView mRutasList;
    private DatabaseReference mDatabase;
    ProgressDialog progressDialog;
    FirebaseRecyclerOptions<Rutas> options;
    FirebaseRecyclerAdapter<Rutas, RutasAdminViewHolder> adapter;
    SearchView searchView;
    String id = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_gallery, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference("Rutas").child("ubicaciones");
        mDatabase.keepSynced(true);

        mRutasList = (RecyclerView) view.findViewById(R.id.recy_rutas_admin);
        mRutasList.setHasFixedSize(true);
        mRutasList.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView = view.findViewById(R.id.bs_ruta_admin);

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
            transaction.replace(R.id.nav_host_fragment, nuevoFragmento);
            transaction.addToBackStack(null);

            // Commit a la transacción
            transaction.commit();
        }
    }

    public void listrutas() {

        options = new FirebaseRecyclerOptions.Builder<Rutas>().setQuery(mDatabase, Rutas.class).build();
        adapter = new FirebaseRecyclerAdapter<Rutas, RutasAdminViewHolder>(options) {
            @Override
            protected void onBindViewHolder(RutasAdminViewHolder rutasViewHolder, final int i, Rutas rutas) {
                rutasViewHolder.setNombre(rutas.getNombre());
                rutasViewHolder.setDistancia(rutas.getDistancia());
                rutasViewHolder.setElevacion(rutas.getElevacion());
                rutasViewHolder.setDificultad(rutas.getDificultad());
                rutasViewHolder.setImage(getContext(), rutas.getImagen());
                rutasViewHolder.setCalificacion(rutas.getCalificacion());
                progressDialog.dismiss();
                id = getRef(i).getKey();


                rutasViewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                        builder.setTitle("Alerta");
                        builder.setMessage("¿Deseas eliminar esta ruta de la aplicación?");

                        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.child(id).setValue(null);
                                Toast.makeText(getContext(), "Eliminada correctamente", Toast.LENGTH_SHORT).show();
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        builder.create();
                        builder.show();
                        return false;
                    }
                });
            }

            @NonNull
            @Override
            public RutasAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rutas_row, parent, false);
                return new RutasAdminViewHolder(v);
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
            adapter = new FirebaseRecyclerAdapter<Rutas, RutasAdminViewHolder>(options) {
                @Override
                protected void onBindViewHolder(RutasAdminViewHolder rutasViewHolder, final int i, Rutas rutas) {
                    rutasViewHolder.setNombre(rutas.getNombre());
                    rutasViewHolder.setDistancia(rutas.getDistancia());
                    rutasViewHolder.setElevacion(rutas.getElevacion());
                    rutasViewHolder.setDificultad(rutas.getDificultad());
                    rutasViewHolder.setImage(getContext(), rutas.getImagen());
                    rutasViewHolder.setCalificacion(rutas.getCalificacion());
                    progressDialog.dismiss();
                    id = getRef(i).getKey();

                    rutasViewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setTitle("Alerta");
                            builder.setMessage("¿Deseas eliminar esta ruta de la aplicación?");

                            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDatabase.child(id).setValue(null);
                                    Toast.makeText(getContext(), "Eliminada correctamente", Toast.LENGTH_SHORT).show();
                                }
                            });

                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            builder.create();
                            builder.show();
                            return false;
                        }
                    });

                }

                @NonNull
                @Override
                public RutasAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rutas_row, parent, false);
                    return new RutasAdminViewHolder(v);
                }
            };

            adapter.startListening();
            mRutasList.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class RutasAdminViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public RutasAdminViewHolder(View itemView){
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