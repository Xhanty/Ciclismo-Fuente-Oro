package com.fuenteoro.ciclismo.Utils;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fuenteoro.ciclismo.Models.GaleriaSitios;
import com.fuenteoro.ciclismo.R;

import java.util.ArrayList;

public class RecyclerGaleria extends RecyclerView.Adapter<RecyclerGaleria.MyViewHolder> {
    private ArrayList<GaleriaSitios>listGaleria;
    private OnclickRecycler listener;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_galeria, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GaleriaSitios galeriaSitios = listGaleria.get(position);
        holder.bind(galeriaSitios, listener);
    }

    @Override
    public int getItemCount() {
        return listGaleria.size();
    }

    public interface OnclickRecycler{
        void OnClicItemRecycler(GaleriaSitios galeriasitios);
    }

    public RecyclerGaleria(ArrayList<GaleriaSitios>listGaleria, OnclickRecycler listener){
        this.listGaleria = listGaleria;
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.coleccion_galeria);
        }

        public void bind(final GaleriaSitios galeriaSitios, final OnclickRecycler listener){

            Glide.with(imageView.getContext()).load(galeriaSitios.getImagen()).into(imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(this, "IMG", Toast.LENGTH_SHOR).show();
                }
            });
        }
    }
}
