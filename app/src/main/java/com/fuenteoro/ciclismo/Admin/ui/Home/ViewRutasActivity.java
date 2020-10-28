package com.fuenteoro.ciclismo.Admin.ui.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fuenteoro.ciclismo.Admin.ui.Rutas.EditarRutasActivity;
import com.fuenteoro.ciclismo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewRutasActivity extends AppCompatActivity implements View.OnClickListener {

    String ID;
    DatabaseReference mDatabase;
    EditText nombre, latitudorigen, longitudorigen, latituddestino, longituddestino;
    EditText elevacion, distancia, dificultad;
    ImageView img;

    Button ver, aprobar, borrar;
    ProgressDialog cargando;

    String txtnombre, txtelevacion, txtdistancia, txtdificultad, txtimagen;
    Double txtlatitudorigen, txtlongitudorigen;
    Double txtlatituddestino, txtlongituddestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rutas);

        ID = getIntent().getStringExtra("ID");
        img = findViewById(R.id.imagen_ruta_admin_view);
        nombre = findViewById(R.id.nombre_ruta_admin_view);
        latitudorigen = findViewById(R.id.latitud_rutaorigen_admin_view);
        longitudorigen = findViewById(R.id.longitud_rutaorigen_admin_view);
        latituddestino = findViewById(R.id.latitud_rutadestino_admin_view);
        longituddestino = findViewById(R.id.longitud_rutadestino_admin_view);
        elevacion = findViewById(R.id.detalle_ruta_elevacion_admin_view);
        distancia = findViewById(R.id.detalle_ruta_distancia_admin_view);
        dificultad = findViewById(R.id.detalle_ruta_dificultad_admin_view);

        ver = findViewById(R.id.btn_map_ruta_admin);
        aprobar = findViewById(R.id.btn_aprobar_ruta_admin);
        borrar = findViewById(R.id.btn_borrar_ruta_admin);

        ver.setOnClickListener(this);
        aprobar.setOnClickListener(this);
        borrar.setOnClickListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Rutas");
        cargando = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase.child("propuestas").child(ID).addValueEventListener(new ValueEventListener() {
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
                    Picasso.with(getApplicationContext()).load(txtimagen).into(img);

                } else {
                    Toast.makeText(getApplicationContext(), "A ocurrido en error, intentalo más tarde", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "A ocurrido en error, intentalo más tarde", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == ver){
            try {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("geo:"+txtlatitudorigen+","+txtlongitudorigen+"?z=16&q="+txtlatitudorigen+","+txtlongitudorigen+"("+txtnombre+")"));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);

            } catch (Exception e) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
                startActivity( new Intent(Intent.ACTION_VIEW, uri));
                //finish();
            }


        } else if(v == aprobar){
            cargando.setTitle("Guardando datos");
            cargando.setMessage("Espera por favor...");
            cargando.show();

            Map<String, Object> rutaMap = new HashMap<>();
            rutaMap.put("nombre", nombre.getText().toString().trim());
            rutaMap.put("distancia", distancia.getText().toString().trim());
            rutaMap.put("dificultad", dificultad.getText().toString().trim());
            rutaMap.put("elevacion", elevacion.getText().toString().trim());
            rutaMap.put("imagen", txtimagen);
            rutaMap.put("latitud_origen", Double.valueOf(latitudorigen.getText().toString().trim()));
            rutaMap.put("longitud_origen", Double.valueOf(longitudorigen.getText().toString().trim()));
            rutaMap.put("latitud_destino", Double.valueOf(latituddestino.getText().toString().trim()));
            rutaMap.put("longitud_destino", Double.valueOf(longituddestino.getText().toString().trim()));
            mDatabase.child("ubicaciones").child(ID).updateChildren(rutaMap);
            mDatabase.child("propuestas").child(ID).setValue(null);
            cargando.dismiss();
            mandarnotificacion();
            Toast.makeText(ViewRutasActivity.this, "Guardado correctamente!", Toast.LENGTH_SHORT).show();
            finish();


        } else if (v == borrar){
            mDatabase.child("propuestas").child(ID).setValue(null);
            Toast.makeText(this, "Eliminado correctamente", Toast.LENGTH_SHORT).show();
            finish();
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
}