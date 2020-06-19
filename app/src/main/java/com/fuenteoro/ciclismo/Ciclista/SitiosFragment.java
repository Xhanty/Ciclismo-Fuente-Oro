package com.fuenteoro.ciclismo.Ciclista;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sitios, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Sitios").child("ubicaciones");
        mDatabase.keepSynced(true);

        mRutasList = (RecyclerView) view.findViewById(R.id.recy_sitios);
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

            FirebaseRecyclerAdapter<Sitios, SitiosFragment.SitiosViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Sitios, SitiosFragment.SitiosViewHolder>
                (Sitios.class, R.layout.sitios_row, SitiosFragment.SitiosViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(SitiosFragment.SitiosViewHolder sitiosViewHolder, Sitios sitios, final int i) {

                sitiosViewHolder.setNombre(sitios.getNombre());
                sitiosViewHolder.setDetalle(sitios.getDetalle());
                sitiosViewHolder.setImage(getContext(), sitios.getImagen());
                sitiosViewHolder.setCalificacion(sitios.getCalificacion());
                progressDialog.dismiss();

                sitiosViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), DetalleSitioActivity.class);
                        intent.putExtra("ID", getRef(i).getKey());
                        startActivity(intent);
                    }
                });
            }
        };
        mRutasList.setAdapter(firebaseRecyclerAdapter);

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

    public static class SitiosViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public SitiosViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setNombre(String nombre){
            TextView nombre_post = (TextView)mView.findViewById(R.id.nombre_sitio);
            nombre_post.setText(nombre);
        }

        public void setDetalle(String detalle){
            TextView detalle_post = (TextView)mView.findViewById(R.id.detalle_sitio);
            detalle_post.setText(detalle);
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