package com.fuenteoro.ciclismo.Admin.ui.Sitios;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.fuenteoro.ciclismo.Ciclista.DetalleSitioActivity;
import com.fuenteoro.ciclismo.Ciclista.InternetFragment;
import com.fuenteoro.ciclismo.Ciclista.SitioMapFragment;
import com.fuenteoro.ciclismo.Models.Sitios;
import com.fuenteoro.ciclismo.R;
import com.fuenteoro.ciclismo.Utils.UtilsNetwork;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SitiosFragment extends Fragment {

    private RecyclerView mRutasList;
    private DatabaseReference mDatabase;

    //Progress Dialog
    ProgressDialog progressDialog;

    FirebaseRecyclerOptions<Sitios> options;
    FirebaseRecyclerAdapter<Sitios, SitiosAdminViewHolder> adapter;
    String id = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Sitios").child("ubicaciones");
        mDatabase.keepSynced(true);

        mRutasList = (RecyclerView) view.findViewById(R.id.recy_sitios_admin);
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

            options = new FirebaseRecyclerOptions.Builder<Sitios>().setQuery(mDatabase, Sitios.class).build();

            adapter = new FirebaseRecyclerAdapter<Sitios, SitiosAdminViewHolder>(options) {
                @Override
                protected void onBindViewHolder(SitiosAdminViewHolder sitiosViewHolder, final int i, Sitios sitios) {
                    sitiosViewHolder.setNombre(sitios.getNombre());
                    sitiosViewHolder.setImage(getContext(), sitios.getImagen());
                    sitiosViewHolder.setCalificacion(sitios.getCalificacion());
                    progressDialog.dismiss();
                    id = getRef(i).getKey();

                    sitiosViewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setTitle("Alerta");
                            builder.setMessage("¿Deseas eliminar este sitio de la aplicación?");

                            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDatabase.child(id).setValue(null);
                                    Toast.makeText(getContext(), "Eliminado correctamente", Toast.LENGTH_SHORT).show();
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
                public SitiosAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sitios_row, parent, false);
                    return new SitiosAdminViewHolder(v);
                }
            };
            adapter.startListening();
            mRutasList.setAdapter(adapter);

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