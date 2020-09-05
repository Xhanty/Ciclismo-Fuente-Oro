package com.fuenteoro.ciclismo.Ciclista;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fuenteoro.ciclismo.Models.Rutas;
import com.fuenteoro.ciclismo.Models.Sitios;
import com.fuenteoro.ciclismo.R;
import com.fuenteoro.ciclismo.Utils.RutasUtilidades;
import com.fuenteoro.ciclismo.Utils.UtilsNetwork;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SitioMapFragment extends Fragment {
    GoogleMap mMap;
    ImageButton lista;
    String ID;
    DatabaseReference mDatabase;
    private ArrayList<Marker> tmpRealtimeMarker = new ArrayList<>();
    private ArrayList<Marker> realtimeMarkers = new ArrayList<>();

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (UtilsNetwork.isOnline(getContext())) {
                mMap = googleMap;
                mDatabase.child("Sitios").child("ubicaciones").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for(Marker marker:realtimeMarkers){
                                marker.remove();
                            }

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Sitios sitios = snapshot.getValue(Sitios.class);

                                ID = snapshot.getKey();
                                String nombre = sitios.getNombre();
                                Double latitud_origen = sitios.getLatitud_sitio();
                                Double longitud_origen = sitios.getLongitud_sitio();
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(new LatLng(latitud_origen, longitud_origen))
                                        .title(nombre)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.sitio));
                                tmpRealtimeMarker.add(mMap.addMarker(markerOptions));
                            }
                            realtimeMarkers.clear();
                            realtimeMarkers.addAll(tmpRealtimeMarker);

                        } else {
                            Toast.makeText(getContext(), "Hasta el momento no hay sitios", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                LatLng origen = new LatLng(3.46066, -73.61847);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origen, 15));

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
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sitio_map, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        lista = view.findViewById(R.id.btn_mapa_lista_s);
        lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SitiosFragment fr = new SitiosFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_ciclista,fr)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_sitio);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}