package com.fuenteoro.ciclismo.Sitios;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.fuenteoro.ciclismo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CalificarSitioActivity extends AppCompatActivity {

    String ID;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String id;

    ImageView img;
    TextInputEditText comentario;
    Spinner calificacion;
    Button abrirgaleria, guardar, addsitio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar_sitio);

        img = findViewById(R.id.imagen_sitio_ciclista_coment);
        calificacion = findViewById(R.id.califica_sitio_ciclis_coment);
        comentario = findViewById(R.id.comentario_sitio_ciclista_add);

        abrirgaleria = findViewById(R.id.btn_imagen_sitio_ciclista_add);
        guardar = findViewById(R.id.btn_sitio_ciclis_coment);
        addsitio = findViewById(R.id.btn_sitio_ciclis_add);

        ID = getIntent().getStringExtra("ID");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Ya llegaste", Toast.LENGTH_SHORT).show();

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

            }
        });

        builder.create();
        builder.show();
        super.onBackPressed();
    }
}