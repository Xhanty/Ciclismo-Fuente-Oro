package com.fuenteoro.ciclismo.Sitios;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.fuenteoro.ciclismo.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class RecorridoSitioActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String Nombre, ID, Descripcion;
    private Double Latitud, Longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorrido_sitio);

        ID = getIntent().getStringExtra("ID");
        Nombre = getIntent().getStringExtra("Nombre");
        Descripcion = getIntent().getStringExtra("Descripcion");
        Latitud = getIntent().getDoubleExtra("Latitud", 1);
        Longitud = getIntent().getDoubleExtra("Longitud", 1);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Latitud, Longitud);
        mMap.addMarker(new MarkerOptions().position(sydney).title(Nombre));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
    }
}