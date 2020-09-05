package com.fuenteoro.ciclismo.Utils;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fuenteoro.ciclismo.Sitios.RecorridoSitioActivity;

public class Localizacion implements LocationListener {

    RecorridoSitioActivity detalleSitioActivity;
    Double LatitudL, LongitudL;

    public RecorridoSitioActivity getDetalleSitioActivity(){
        return detalleSitioActivity;
    }

    public Localizacion(RecorridoSitioActivity detalleSitioActivity, Double latitudL, Double longitudL) {
        this.detalleSitioActivity = detalleSitioActivity;
        LatitudL = latitudL;
        LongitudL = longitudL;
    }

    @Override
    public void onLocationChanged(Location location) {
        LatitudL = location.getLatitude();
        LongitudL = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status){
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                 Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                 break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                 Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                 break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(detalleSitioActivity, "GPS Activado correctamente", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(detalleSitioActivity, "Activa tú GPS para trazarte la ruta", Toast.LENGTH_SHORT).show();

    }
}
