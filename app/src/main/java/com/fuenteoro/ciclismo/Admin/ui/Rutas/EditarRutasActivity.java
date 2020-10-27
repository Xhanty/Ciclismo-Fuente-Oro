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

import com.fuenteoro.ciclismo.Admin.ui.Sitios.EditarSitiosActivity;
import com.fuenteoro.ciclismo.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class EditarRutasActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference mDatabase;
    EditText nombre, latitudorigen, longitudorigen, latituddestino, longituddestino;
    EditText elevacion, distancia, dificultad;
    ImageView imagen, imgeditar;
    String ID;

    String txtnombre, txtelevacion, txtdistancia, txtdificultad, txtimagen;
    Double txtlatitudorigen, txtlongitudorigen;
    Double txtlatituddestino, txtlongituddestino;

    Button abrirgaleria, actualizar, comentarios;

    StorageReference storageReference;
    ProgressDialog cargando;
    Bitmap thumb_bitmap = null;
    String aleatorio = "";
    byte [] thumb_byte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_rutas);

        ID = getIntent().getStringExtra("ID");
        imagen = findViewById(R.id.imagen_ruta_admin);
        nombre = findViewById(R.id.nombre_ruta_admin);
        latitudorigen = findViewById(R.id.latitud_rutaorigen_admin);
        longitudorigen = findViewById(R.id.longitud_rutaorigen_admin);
        latituddestino = findViewById(R.id.latitud_rutadestino_admin);
        longituddestino = findViewById(R.id.longitud_rutadestino_admin);
        elevacion = findViewById(R.id.detalle_ruta_elevacion_admin);
        distancia = findViewById(R.id.detalle_ruta_distancia_admin);
        dificultad = findViewById(R.id.detalle_ruta_dificultad_admin);

        abrirgaleria = findViewById(R.id.btn_imagen_ruta_admin);
        actualizar = findViewById(R.id.btn_actualizar_ruta_admin);
        comentarios = findViewById(R.id.btn_vercomentarios_ruta_admin);

        imgeditar = findViewById(R.id.imagen_rutaeditar_admin);
        abrirgaleria.setOnClickListener(this);
        actualizar.setOnClickListener(this);
        comentarios.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Rutas").child("ubicaciones");
        storageReference = FirebaseStorage.getInstance().getReference().child("Rutas").child("ubicaciones");
        cargando = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        mDatabase.child(ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    txtnombre = dataSnapshot.child("nombre").getValue().toString();
                    txtdistancia = dataSnapshot.child("distancia").getValue().toString();
                    txtdificultad = dataSnapshot.child("dificultad").getValue().toString();
                    txtelevacion = dataSnapshot.child("elevacion").getValue().toString();
                    txtlatitudorigen = (Double) dataSnapshot.child("latitud_origen").getValue();
                    txtlongitudorigen = (Double) dataSnapshot.child("longitud_origen").getValue();
                    txtlatituddestino = (Double) dataSnapshot.child("latitud_destino").getValue();
                    txtlongituddestino = (Double) dataSnapshot.child("longitud_destino").getValue();
                    txtimagen = dataSnapshot.child("imagen").getValue().toString();

                    nombre.setText(txtnombre);
                    distancia.setText(txtdistancia);
                    dificultad.setText(txtdificultad);
                    elevacion.setText(txtelevacion);
                    latitudorigen.setText(String.valueOf(txtlatitudorigen));
                    longitudorigen.setText(String.valueOf(txtlongitudorigen));
                    latituddestino.setText(String.valueOf(txtlatituddestino));
                    longituddestino.setText(String.valueOf(txtlongituddestino));
                    Picasso.with(getApplicationContext()).load(txtimagen).into(imagen);

                } else {
                    Toast.makeText(getApplicationContext(), "A ocurrido en error, intentalo más tarde", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "A ocurrido en error, intentalo más tarde", Toast.LENGTH_LONG).show();
            }
        });
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == abrirgaleria){
            CropImage.startPickImageActivity(EditarRutasActivity.this);

        } else if (v == actualizar){
            cargando.setTitle("Actualizando datos");
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
                        mDatabase.child(ID).updateChildren(rutaMap);
                        imgeditar.setImageResource(0);
                        cargando.dismiss();
                        Toast.makeText(EditarRutasActivity.this, "Actualizado correctamente!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

            } else {
                Map<String, Object> rutaMap = new HashMap<>();
                rutaMap.put("nombre", nombre.getText().toString().trim());
                rutaMap.put("distancia", distancia.getText().toString().trim());
                rutaMap.put("dificultad", dificultad.getText().toString().trim());
                rutaMap.put("elevacion", elevacion.getText().toString().trim());
                rutaMap.put("latitud_origen", Double.valueOf(latitudorigen.getText().toString().trim()));
                rutaMap.put("longitud_origen", Double.valueOf(longitudorigen.getText().toString().trim()));
                rutaMap.put("latitud_destino", Double.valueOf(latituddestino.getText().toString().trim()));
                rutaMap.put("longitud_destino", Double.valueOf(longituddestino.getText().toString().trim()));
                mDatabase.child(ID).updateChildren(rutaMap);
                cargando.dismiss();
                Toast.makeText(this, "Actualizado correctamente!", Toast.LENGTH_SHORT).show();
                finish();
            }

        } else if (v == comentarios){
            Toast.makeText(this, "Comentario!", Toast.LENGTH_SHORT).show();
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
                    .setAspectRatio(2, 1).start(EditarRutasActivity.this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                Uri resulturi = result.getUri();
                File url = new File(resulturi.getPath());
                Picasso.with(this).load(url).into(imgeditar);
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