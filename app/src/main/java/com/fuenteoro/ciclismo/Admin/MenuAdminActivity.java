package com.fuenteoro.ciclismo.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.fuenteoro.ciclismo.Admin.ui.Rutas.AddRutasActivity;
import com.fuenteoro.ciclismo.Admin.ui.Sitios.AddSitiosActivity;
import com.fuenteoro.ciclismo.Admin.ui.Usuarios.AddUsuariosActivity;
import com.fuenteoro.ciclismo.LoginActivity;
import com.fuenteoro.ciclismo.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MenuAdminActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    boolean cerrar = false;
    private FirebaseAuth mAuth;
    FloatingActionMenu actionMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);
        actionMenu = findViewById(R.id.fab);
        actionMenu.setClosedOnTouchOutside(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_usuarios, R.id.nav_perfil)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "Sesión cerrada!", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Alerta");
        builder.setMessage("¿Deseas salir de la aplicación?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cerrar = true;
                salirApp(cerrar);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cerrar = false;
                salirApp(cerrar);
            }
        });

        builder.create();
        builder.show();
    }

    public void salirApp(boolean cerrar){
        if(cerrar == true){
            Toast.makeText(this, "Regresa pronto!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void AddUsuarios(View view){
        actionMenu.close(true);
        Intent intent = new Intent(this, AddUsuariosActivity.class);
        startActivity(intent);
    }

    public void AddSitios(View view){
        actionMenu.close(true);
        Intent intent = new Intent(this, AddSitiosActivity.class);
        startActivity(intent);
    }

    public void AddRutas(View view){
        actionMenu.close(true);
        Intent intent = new Intent(this, AddRutasActivity.class);
        startActivity(intent);
    }
}