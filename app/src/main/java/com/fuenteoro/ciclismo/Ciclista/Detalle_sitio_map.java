package com.fuenteoro.ciclismo.Ciclista;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fuenteoro.ciclismo.Models.Rutas;
import com.fuenteoro.ciclismo.R;
import com.fuenteoro.ciclismo.Utils.UtilsNetwork;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Detalle_sitio_map extends Fragment {

    GoogleMap mMap;
    String ID = "1";
    DatabaseReference mDatabase;
    Double latitud_sitio;
    Double longitud_sitio;
    String nombre;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (UtilsNetwork.isOnline(getContext())) {
                mMap = googleMap;
                mDatabase.child("ubicaciones").child(ID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            nombre = dataSnapshot.child("nombre").getValue().toString();
                            latitud_sitio = (Double) dataSnapshot.child("latitud_sitio").getValue();
                            longitud_sitio = (Double) dataSnapshot.child("longitud_sitio").getValue();

                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                            LatLng origen = new LatLng(latitud_sitio, longitud_sitio);
                            mMap.addMarker(new MarkerOptions().position(origen)
                                    .title(nombre)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.sitio)));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origen, 15));

                        } else {
                            Toast.makeText(getContext(), "A ocurrido en error, intentalo más tarde", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "A ocurrido en error, intentalo más tarde", Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                //NO HAY INTERNET
            }
        }
    };

    public Detalle_sitio_map() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            ID = getArguments().getString("ID", "1");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_detalle_sitio_map, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Sitios");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_detalle_sitio);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}