package com.fuenteoro.ciclismo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.fuenteoro.ciclismo.Ciclista.PerfilFragment;
import com.fuenteoro.ciclismo.Ciclista.RutasFragment;
import com.fuenteoro.ciclismo.Ciclista.SitiosFragment;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MenuActivity extends AppCompatActivity {

    boolean cerrar = false;
    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        bottomNav = findViewById(R.id.nav_ciclista);

        if (savedInstanceState==null){
            bottomNav.setItemSelected(R.id.home_ciclista, true);
            fragmentManager = getSupportFragmentManager();
            RutasFragment rutasFragment = new RutasFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_ciclista, rutasFragment)
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;
                switch (id){
                    case R.id.home_ciclista:
                        fragment = new RutasFragment();
                        break;
                    case R.id.sitios_ciclista:
                        fragment = new SitiosFragment();
                        break;
                    case R.id.account_ciclista:
                        fragment = new PerfilFragment();
                        break;
                }

                if(fragment != null){
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_ciclista, fragment)
                            .commit();

                } else {
                    Toast.makeText(getApplicationContext(), "No se puede acceder a esta opción", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
}