package com.fuenteoro.ciclismo.Sitios;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fuenteoro.ciclismo.MenuActivity;
import com.fuenteoro.ciclismo.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class ActivityNewSitio extends AppCompatActivity implements View.OnClickListener {

    Button abtnabrir, abtnguardar, abtncancel;
    TextInputEditText anombre, alatitud, alongitud, adetalle;
    ImageView aimg;


    ProgressDialog cargando;
    Bitmap thumb_bitmap = null;
    String aleatorio = "", nombre;
    byte[] thumb_byte;
    StorageReference storageReference;
    DatabaseReference mDatabase;

    LocationManager locationManager;
    Location location;
    Double LatitudO, LongitudO;
    int val = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sitio);

        nombre = getIntent().getStringExtra("Nombre");
        aimg = findViewById(R.id.txt_new_sitio_ciclis_img);
        anombre = findViewById(R.id.txt_new_sitio_ciclis_nombre);
        alatitud = findViewById(R.id.txt_new_sitio_ciclis_latitud);
        alongitud = findViewById(R.id.txt_new_sitio_ciclis_longitud);
        adetalle = findViewById(R.id.txt_new_sitio_ciclis_descripcion);
        abtnabrir = findViewById(R.id.btnimg_new_sitio_ciclis);
        abtncancel = findViewById(R.id.btnimg_new_sitio_ciclis_cancel);
        abtnguardar = findViewById(R.id.btnimg_new_sitio_ciclis_save);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("Sitios").child("imagenes");
        cargando = new ProgressDialog(this);

        abtnabrir.setOnClickListener(this);
        abtncancel.setOnClickListener(this);
        abtnguardar.setOnClickListener(this);
        miUbicacion();
    }

    @Override
    protected void onStart() {
        if(val == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityNewSitio.this);
            builder.setTitle("Alerta");
            builder.setMessage("¿Deseas proponer un nuevo sitio?");

            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    val = 1;
                    dialog.dismiss();
                    Toast.makeText(ActivityNewSitio.this, "Dirigete hasta la ubicación, sin cerrar la aplicación", Toast.LENGTH_LONG).show();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.create();
            builder.show();
        }
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityNewSitio.this);
        builder.setTitle("Alerta");
        builder.setMessage("¿No deseas proponer un nuevo sitio?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.create();
        builder.show();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageuri = CropImage.getPickImageResultUri(this, data);

            //Recortar imagen
            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setRequestedSize(640, 480)
                    .setAspectRatio(2, 1).start(ActivityNewSitio.this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resulturi = result.getUri();
                File url = new File(resulturi.getPath());
                Picasso.with(this).load(url).into(aimg);
                //COMPRIMIENDO IMAGEN
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(90)
                            .compressToBitmap(url);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                thumb_byte = byteArrayOutputStream.toByteArray();

                //FIN DEL COMPRESOR
                int p = (int) Math.floor(Math.random() * (23 + 1 + 1) + (1));
                int s = (int) Math.floor(Math.random() * (23 + 1 + 1) + (1));
                int t = (int) Math.floor(Math.random() * (23 + 1 + 1) + (1));
                int c = (int) Math.floor(Math.random() * (23 + 1 + 1) + (1));
                int numero1 = (int) Math.floor(Math.random() * (2111 + 1012 + 1) + (1012));
                int numero2 = (int) Math.floor(Math.random() * (2111 + 1012 + 1) + (1012));

                String[] elementos = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                        "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

                aleatorio = elementos[p] + elementos[s] +
                        numero1 + elementos[t] + elementos[c] + numero2 + "comprimido.jpg";
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        if (abtnabrir == v){
            CropImage.startPickImageActivity(ActivityNewSitio.this);
        }

        else if (abtncancel == v){
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityNewSitio.this);
            builder.setTitle("Alerta");
            builder.setMessage("¿No deseas proponer un nuevo sitio?");

            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.create();
            builder.show();

        }

        else if(abtnguardar == v){
            String txtnombre = anombre.getText().toString().trim();
            String txtdetalle = adetalle.getText().toString().trim();

            if(txtnombre.isEmpty()){
                anombre.setError("Ingresa un nombre");

            } else if (txtdetalle.isEmpty()){
                adetalle.setError("Ingresa un detalle");

            } else {
                if (!aleatorio.equals("")) {
                    locationManager.removeUpdates(locListener);
                    cargando.setTitle("Guardando datos");
                    cargando.setMessage("Espera por favor...");
                    cargando.show();

                    final StorageReference ref = storageReference.child(aleatorio);
                    UploadTask uploadTask = ref.putBytes(thumb_byte);
                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri downloaduri = task.getResult();
                            Map<String, Object> newsitio = new HashMap<>();
                            newsitio.put("calificacion", 0);
                            newsitio.put("detalle", adetalle.getText().toString().trim());
                            newsitio.put("imagen", downloaduri.toString().trim());
                            newsitio.put("latitud_sitio", Double.valueOf(alatitud.getText().toString().trim()));
                            newsitio.put("longitud_sitio", Double.valueOf(alongitud.getText().toString().trim()));
                            newsitio.put("nombre", anombre.getText().toString().trim());
                            newsitio.put("ciclista", nombre);

                            mDatabase.child("Sitios").child("propuestas").push().setValue(newsitio);
                            cargando.dismiss();
                            Toast.makeText(ActivityNewSitio.this, "Guardado correctamente!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ActivityNewSitio.this, MenuActivity.class);
                            startActivity(intent);
                        }
                    });
                }

                else {
                    cargando.dismiss();
                    Toast.makeText(getApplicationContext(), "Selecciona una imagen!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            LatitudO = location.getLatitude();
            LongitudO = location.getLongitude();

            alatitud.setText(String.valueOf(LatitudO));
            alongitud.setText(String.valueOf(LongitudO));
        }
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000,0, locListener);
    }

}