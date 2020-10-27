package com.fuenteoro.ciclismo.Admin.ui.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.fuenteoro.ciclismo.R;

public class ViewRutasActivity extends AppCompatActivity {

    String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rutas);

        ID = getIntent().getStringExtra("ID");
    }
}