package com.fuenteoro.ciclismo.Ciclista;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SitioMapFragment extends Fragment {
    GoogleMap mMap;
    ImageButton lista;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {

            if (UtilsNetwork.isOnline(getContext())) {

                mMap = googleMap;
                LatLng origen = new LatLng(3.46066, -73.61847);
                mMap.addMarker(new MarkerOptions().position(origen).
                        title("Parroquia Fuente de Oro")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.hermosillo)));
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
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