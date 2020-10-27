package com.fuenteoro.ciclismo.Admin.ui.Home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fuenteoro.ciclismo.Admin.ui.Sitios.EditarSitiosActivity;
import com.fuenteoro.ciclismo.Admin.ui.Sitios.SitiosFragment;
import com.fuenteoro.ciclismo.Ciclista.InternetFragment;
import com.fuenteoro.ciclismo.Models.Sitios;
import com.fuenteoro.ciclismo.R;
import com.fuenteoro.ciclismo.Utils.UtilsNetwork;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private RecyclerView mRutasList;
    private DatabaseReference mDatabase;

    //Progress Dialog
    ProgressDialog progressDialog;

    FirebaseRecyclerOptions<Sitios> options;
    FirebaseRecyclerAdapter<Sitios, SitiosAdminViewHolder> adapter;

    Button rutas;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rutas = view.findViewById(R.id.rutas_aprobar_admin);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Sitios").child("propuestas");
        mDatabase.keepSynced(true);

        mRutasList = (RecyclerView) view.findViewById(R.id.recy_sitios_admin_view);
        mRutasList.setHasFixedSize(true);
        mRutasList.setLayoutManager(new LinearLayoutManager(getContext()));

        rutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rutas.setVisibility(View.INVISIBLE);
                Fragment nuevoFragmento = new RutasFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, nuevoFragmento);
                transaction.addToBackStack(null);

                // Commit a la transacción
                transaction.commit();
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

            options = new FirebaseRecyclerOptions.Builder<Sitios>().setQuery(mDatabase, Sitios.class).build();

            adapter = new FirebaseRecyclerAdapter<Sitios, SitiosAdminViewHolder>(options) {
                @Override
                protected void onBindViewHolder(SitiosAdminViewHolder sitiosViewHolder, final int i, Sitios sitios) {
                    sitiosViewHolder.setNombre(sitios.getNombre());
                    sitiosViewHolder.setImage(getContext(), sitios.getImagen());
                    sitiosViewHolder.setCalificacion(sitios.getCalificacion());
                    progressDialog.dismiss();

                    sitiosViewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setTitle("Alerta");
                            builder.setMessage("¿Deseas eliminar este sitio de la aplicación?");

                            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDatabase.child(getRef(i).getKey()).setValue(null);
                                    Toast.makeText(getContext(), "Eliminado correctamente", Toast.LENGTH_SHORT).show();
                                }
                            });

                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.create();
                            builder.show();

                            return false;
                        }
                    });

                    sitiosViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), ViewSitiosActivity.class);
                            intent.putExtra("ID", getRef(i).getKey());
                            startActivity(intent);
                        }
                    });
                }

                @NonNull
                @Override
                public SitiosAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sitios_row, parent, false);
                    return new SitiosAdminViewHolder(v);
                }
            };
            adapter.startListening();
            mRutasList.setAdapter(adapter);

        }

        else {
            // Crea el nuevo fragmento y la transacción.
            Fragment nuevoFragmento = new InternetFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, nuevoFragmento);
            transaction.addToBackStack(null);

            // Commit a la transacción
            transaction.commit();
        }
    }

    public static class SitiosAdminViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public SitiosAdminViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setNombre(String nombre){
            TextView nombre_post = (TextView)mView.findViewById(R.id.nombre_sitio);
            nombre_post.setText(nombre);
        }

        public void setImage(Context ctx, String image){
            ImageView image_post = (ImageView)mView.findViewById(R.id.img_sitio);
            Picasso.with(ctx).load(image).into(image_post);
        }

        public void setCalificacion(int calificacion){
            RatingBar calificacion_post = (RatingBar)mView.findViewById(R.id.calificacion_sitio);
            calificacion_post.setProgress(Integer.valueOf(calificacion));
        }
    }
}