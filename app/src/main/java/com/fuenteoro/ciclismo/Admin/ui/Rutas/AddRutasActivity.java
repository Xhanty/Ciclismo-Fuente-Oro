package com.fuenteoro.ciclismo.Admin.ui.Rutas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fuenteoro.ciclismo.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class AddRutasActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference mDatabase;
    EditText nombre, latitudorigen, longitudorigen, latituddestino, longituddestino;
    EditText elevacion, distancia, dificultad;
    ImageView imagen;

    Button abrirgaleria, actualizar;

    StorageReference storageReference;
    ProgressDialog cargando;
    Bitmap thumb_bitmap = null;
    String aleatorio = "";
    byte [] thumb_byte;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rutas);

        imagen = findViewById(R.id.imagen_ruta_admin_add);
        nombre = findViewById(R.id.nombre_ruta_admin_add);
        latitudorigen = findViewById(R.id.latitud_rutaorigen_admin_add);
        longitudorigen = findViewById(R.id.longitud_rutaorigen_admin_add);
        latituddestino = findViewById(R.id.latitud_rutadestino_admin_add);
        longituddestino = findViewById(R.id.longitud_rutadestino_admin_add);
        elevacion = findViewById(R.id.detalle_ruta_elevacion_admin_add);
        distancia = findViewById(R.id.detalle_ruta_distancia_admin_add);
        dificultad = findViewById(R.id.detalle_ruta_dificultad_admin_add);

        abrirgaleria = findViewById(R.id.btn_imagen_ruta_admin_add);
        actualizar = findViewById(R.id.btn_actualizar_ruta_admin_add);

        abrirgaleria.setOnClickListener(this);
        actualizar.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Rutas").child("ubicaciones");
        storageReference = FirebaseStorage.getInstance().getReference().child("Rutas").child("ubicaciones");
        cargando = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        if (v == abrirgaleria){
            CropImage.startPickImageActivity(AddRutasActivity.this);

        } else if (v == actualizar){
            cargando.setTitle("Guardando datos");
            cargando.setMessage("Espera por favor...");
            cargando.show();

            if(!aleatorio.equals("")){
                final StorageReference ref = storageReference.child(aleatorio);
                UploadTask uploadTask = ref.putBytes(thumb_byte);
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw Objects.requireNonNull(task.getException());
                        }
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri downloaduri = task.getResult();
                        Map<String, Object> rutaMap = new HashMap<>();
                        rutaMap.put("nombre", nombre.getText().toString().trim());
                        rutaMap.put("distancia", distancia.getText().toString().trim());
                        rutaMap.put("dificultad", dificultad.getText().toString().trim());
                        rutaMap.put("elevacion", elevacion.getText().toString().trim());
                        rutaMap.put("imagen", downloaduri.toString());
                        rutaMap.put("latitud_origen", Double.valueOf(latitudorigen.getText().toString().trim()));
                        rutaMap.put("longitud_origen", Double.valueOf(longitudorigen.getText().toString().trim()));
                        rutaMap.put("latitud_destino", Double.valueOf(latituddestino.getText().toString().trim()));
                        rutaMap.put("longitud_destino", Double.valueOf(longituddestino.getText().toString().trim()));
                        mDatabase.push().setValue(rutaMap);
                        cargando.dismiss();
                        mandarnotificacion();
                        Toast.makeText(AddRutasActivity.this, "Guardado correctamente!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

            } else {
                Toast.makeText(AddRutasActivity.this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void mandarnotificacion() {
        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json = new JSONObject();
        try{
            json.put("to", "/topics/"+"enviaratodos");
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo", "Nueva ruta disponible");
            notificacion.put("detalle", "Que esperas para recorrer Fuente de oro con nosotros!");

            json.put("data", notificacion);

            String URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json, null, null){
                @Override
                public Map<String, String> getHeaders(){
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAULSQiXY:APA91bGSHTj9v0aD4aRvpcm-OtiEnYmzlDJwqJaIpwD5g_zrhCIlTAWC1mvHCVp2jxwUMr-yt-TAQrgaZDHfILf49Vp_Kfu7TS-goETbKzdB6Qfcqe8PDFVxqngG_HaYCmOZvshoUrg-");
                    return header;
                }
            };
            myrequest.add(request);

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri imageuri = CropImage.getPickImageResultUri(this, data);

            //Recortar imagen
            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setRequestedSize(640,480)
                    .setAspectRatio(2, 1).start(AddRutasActivity.this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                Uri resulturi = result.getUri();
                File url = new File(resulturi.getPath());
                Picasso.with(this).load(url).into(imagen);
                //COMPRIMIENDO IMAGEN
                try{
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(90)
                            .compressToBitmap(url);

                } catch (IOException e){
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                thumb_byte = byteArrayOutputStream.toByteArray();

                //FIN DEL COMPRESOR
                int p = (int) Math.floor(Math.random()*(23+1+1)+(1));
                int s = (int) Math.floor(Math.random()*(23+1+1)+(1));
                int t = (int) Math.floor(Math.random()*(23+1+1)+(1));
                int c = (int) Math.floor(Math.random()*(23+1+1)+(1));
                int numero1 = (int) Math.floor(Math.random()*(2111+1012+1)+(1012));
                int numero2 = (int) Math.floor(Math.random()*(2111+1012+1)+(1012));

                String [] elementos = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                        "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

                aleatorio = elementos[p] + elementos[s] +
                        numero1 + elementos[t] + elementos[c] + numero2 + "comprimido.jpg";
            }
        }
    }
}