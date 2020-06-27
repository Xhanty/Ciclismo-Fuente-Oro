package com.fuenteoro.ciclismo.Ciclista;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.fuenteoro.ciclismo.Utils.RutasUtilidades;
import com.fuenteoro.ciclismo.R;
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

public class RutaMapFragment extends Fragment {
    GoogleMap mMap;
    ImageButton listabtn;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            LatLng origen = new LatLng(3.462, -73.629);
            mMap.addMarker(new MarkerOptions().position(origen).
                    title("Ruta Benjamín")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.rutamap)));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origen,15));

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    LatLng center = null;
                    ArrayList<LatLng> points = null;
                    PolylineOptions lineOptions = null;

                    // setUpMapIfNeeded();

                    // recorriendo todas las rutas
                    for(int i = 0; i< RutasUtilidades.routes.size(); i++){
                        points = new ArrayList<LatLng>();
                        lineOptions = new PolylineOptions();

                        // Obteniendo el detalle de la ruta
                        List<HashMap<String, String>> path = RutasUtilidades.routes.get(i);

                        // Obteniendo todos los puntos y/o coordenadas de la ruta
                        for(int j=0;j<path.size();j++){
                            HashMap<String,String> point = path.get(j);

                            double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                            double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));

                            LatLng position = new LatLng(lat, lng);

                            if (center == null) {
                                //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                                center = new LatLng(lat, lng);
                            }
                            points.add(position);
                        }

                        // Agregamos todos los puntos en la ruta al objeto LineOptions
                        lineOptions.addAll(points);
                        //Definimos el grosor de las Polilíneas
                        lineOptions.width(7);
                        //Definimos el color de la Polilíneas
                        lineOptions.color(Color.rgb(7,137,48));
                    }

                    // Dibujamos las Polilineas en el Google Map para cada ruta
                    if(points!=null) {
                        mMap.addPolyline(lineOptions);
                    }

                    LatLng destino = new LatLng(RutasUtilidades.coordenadas.getLatitud_destino(), RutasUtilidades.coordenadas.getLongitud_destino());
                    mMap.addMarker(new MarkerOptions().position(destino));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 15));
                    return false;
                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ruta_map, container, false);

        listabtn = view.findViewById(R.id.btn_mapa_lista);
        listabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RutasFragment fr = new RutasFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_ciclista,fr)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}