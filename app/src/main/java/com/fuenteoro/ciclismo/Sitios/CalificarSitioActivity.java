package com.fuenteoro.ciclismo.Sitios;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.fuenteoro.ciclismo.Admin.ui.Sitios.AddSitiosActivity;
import com.fuenteoro.ciclismo.R;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class CalificarSitioActivity extends AppCompatActivity implements View.OnClickListener{

    String ID;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String id, nombre;

    ImageView imagen;
    TextInputEditText comentario;
    RatingBar calificacion;
    Button abrirgaleria, guardar;

    int sitiof = 0;
    Long sitios;
    ProgressDialog cargando;
    Bitmap thumb_bitmap = null;
    String aleatorio = "";
    byte[] thumb_byte;
    StorageReference storageReference;
    private Date fecha = new Date();
    SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/YYYY");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar_sitio);

        imagen = findViewById(R.id.imagen_sitio_ciclista_coment);
        calificacion = findViewById(R.id.califica_sitio_ciclis_coment);
        comentario = findViewById(R.id.comentario_sitio_ciclista_add);

        abrirgaleria = findViewById(R.id.btn_imagen_sitio_ciclista_add);
        guardar = findViewById(R.id.btn_sitio_ciclis_coment);

        ID = getIntent().getStringExtra("ID");
        nombre = getIntent().getStringExtra("Nombre");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("Sitios").child("imagenes");
        cargando = new ProgressDialog(this);

        abrirgaleria.setOnClickListener(this);
        guardar.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alerta");
        builder.setMessage("¿Deseas cerrar la calificación?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create();
        builder.show();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        id = mAuth.getCurrentUser().getUid();
        super.onStart();
    }


    @Override
    public void onClick(View v) {
        if (v == abrirgaleria){
            CropImage.startPickImageActivity(CalificarSitioActivity.this);
        }

        else if (v == guardar){
            String validarcc = comentario.getText().toString().trim();
            if(validarcc.isEmpty()){
                Toast.makeText(CalificarSitioActivity.this, "Ingresa un comentario!", Toast.LENGTH_SHORT).show();

            } else {
                if (!aleatorio.equals("")) {
                    cargando.setTitle("Guardando datos");
                    cargando.setMessage("Espera por favor...");
                    cargando.show();

                    mDatabase.child("Usuarios").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                sitiof = 0;
                                sitios = (Long) dataSnapshot.child("sitios").getValue();
                                sitiof = (int) (sitios + 1);

                                Map<String, Object> sitiosMap = new HashMap<>();
                                sitiosMap.put("sitios", sitiof);
                                mDatabase.child("Usuarios").child(id).updateChildren(sitiosMap);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

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
                            Map<String, Object> calificaMap = new HashMap<>();
                            calificaMap.put("calificacion", calificacion.getRating());
                            calificaMap.put("ciclista", nombre);
                            calificaMap.put("comentario", comentario.getText().toString().trim());
                            calificaMap.put("fecha", formatFecha.format(fecha));
                            calificaMap.put("imagen", downloaduri.toString().trim());
                            calificaMap.put("sitio", ID);
                            mDatabase.child("Sitios").child("comentarios").push().setValue(calificaMap);
                            cargando.dismiss();
                            Toast.makeText(CalificarSitioActivity.this, "Calificación guardada correctamente!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CalificarSitioActivity.this, ActivityNewSitio.class);
                            intent.putExtra("Nombre", nombre);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

                else {
                    cargando.dismiss();
                    Toast.makeText(CalificarSitioActivity.this, "Selecciona una imagen!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageuri = CropImage.getPickImageResultUri(this, data);

            //Recortar imagen
            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setRequestedSize(640, 480)
                    .setAspectRatio(2, 1).start(CalificarSitioActivity.this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resulturi = result.getUri();
                File url = new File(resulturi.getPath());
                Picasso.with(this).load(url).into(imagen);
                //Picasso.with(this).load(url).into(aimg);
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
        }
    }
}