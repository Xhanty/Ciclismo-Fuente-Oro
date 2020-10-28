package com.fuenteoro.ciclismo.Admin.ui.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fuenteoro.ciclismo.Admin.ui.Sitios.EditarSitiosActivity;
import com.fuenteoro.ciclismo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewSitiosActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView img;
    TextInputEditText nombre, latitud, longitud, detalle;
    Button aprobar, borrar, ver;
    String ID;

    DatabaseReference mDatabase;
    ProgressDialog cargando;

    String txtnombre, txtdetalle, txtimagen;
    Double txtlatitud;
    Double txtlongitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sitios);

        ID = getIntent().getStringExtra("ID");

        img = findViewById(R.id.imagen_sitio_admin_view);
        nombre = findViewById(R.id.nombre_sitio_admin_view);
        latitud = findViewById(R.id.latitud_sitio_admin_view);
        longitud = findViewById(R.id.longitud_sitio_admin_view);
        detalle = findViewById(R.id.detalle_sitio_admin_view);
        aprobar = findViewById(R.id.btn_aprobar_sitio_admin);
        borrar = findViewById(R.id.btn_borrar_sitio_admin);
        ver = findViewById(R.id.btn_map_sitio_admin);

        aprobar.setOnClickListener(this);
        borrar.setOnClickListener(this);
        ver.setOnClickListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Sitios");
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
                txtdetalle = dataSnapshot.child("detalle").getValue().toString();
                txtlatitud = (Double) dataSnapshot.child("latitud_sitio").getValue();
                txtlongitud = (Double) dataSnapshot.child("longitud_sitio").getValue();
                txtimagen = dataSnapshot.child("imagen").getValue().toString();

                nombre.setText(txtnombre);
                latitud.setText(String.valueOf(txtlatitud));
                longitud.setText(String.valueOf(txtlongitud));
                detalle.setText(txtdetalle);
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
        if (v == aprobar){
            cargando.setTitle("Guardando datos");
            cargando.setMessage("Espera por favor...");
            cargando.show();

            Map<String, Object> sitioMap = new HashMap<>();
            sitioMap.put("nombre", nombre.getText().toString().trim());
            sitioMap.put("latitud_sitio", Double.valueOf(latitud.getText().toString().trim()));
            sitioMap.put("longitud_sitio", Double.valueOf(longitud.getText().toString().trim()));
            sitioMap.put("detalle", detalle.getText().toString().trim());
            sitioMap.put("imagen", txtimagen);
            mDatabase.child("ubicaciones").child(ID).updateChildren(sitioMap);
            mDatabase.child("propuestas").child(ID).setValue(null);
            cargando.dismiss();
            mandarnotificacion();
            Toast.makeText(ViewSitiosActivity.this, "Guardado correctamente!", Toast.LENGTH_SHORT).show();
            finish();

        } else if (v == borrar){
            mDatabase.child("propuestas").child(ID).setValue(null);
            Toast.makeText(this, "Eliminado correctamente", Toast.LENGTH_SHORT).show();
            finish();

        } else if (v == ver){
            try {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("geo:"+txtlatitud+","+txtlongitud+"?z=16&q="+txtlatitud+","+txtlongitud+"("+txtnombre+")"));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);

            } catch (Exception e) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
                startActivity( new Intent(Intent.ACTION_VIEW, uri));
                //finish();
            }
        }
    }

    private void mandarnotificacion() {
        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json = new JSONObject();
        try{
            json.put("to", "/topics/"+"enviaratodos");
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo", "Nuevo sitio disponible");
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