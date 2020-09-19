package com.fuenteoro.ciclismo.Admin.ui.Usuarios;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fuenteoro.ciclismo.Ciclista.InternetFragment;
import com.fuenteoro.ciclismo.Models.Usuarios;
import com.fuenteoro.ciclismo.R;
import com.fuenteoro.ciclismo.Utils.UtilsNetwork;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UsuariosFragment extends Fragment {

    private RecyclerView mRutasList;
    private DatabaseReference mDatabase;

    //Progress Dialog
    ProgressDialog progressDialog;

    FirebaseRecyclerOptions<Usuarios> options;
    FirebaseRecyclerAdapter<Usuarios, UsuariosAdminViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usuarios, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        mDatabase.keepSynced(true);

        mRutasList = (RecyclerView) view.findViewById(R.id.recy_usuarios_admin);
        mRutasList.setHasFixedSize(true);
        mRutasList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(UtilsNetwork.isOnline(getContext())) {
            //Abrimos el progressDialog
            progressDialog = new ProgressDialog(getContext());
            progressDialog.show();

            //Contenido
            progressDialog.setContentView(R.layout.progress_dialog);

            //Transparente
            progressDialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent);

            options = new FirebaseRecyclerOptions.Builder<Usuarios>().setQuery(mDatabase, Usuarios.class).build();

            adapter = new FirebaseRecyclerAdapter<Usuarios, UsuariosAdminViewHolder>(options) {
                @Override
                protected void onBindViewHolder(UsuariosAdminViewHolder sitiosViewHolder, final int i, Usuarios sitios) {
                    sitiosViewHolder.setNombres(sitios.getNombres());
                    sitiosViewHolder.setTelefono(String.valueOf(sitios.getTelefono()));
                    sitiosViewHolder.setPerfil(sitios.getPerfil());
                    final String email = sitios.getEmail();
                    final Long tel = sitios.getTelefono();
                    progressDialog.dismiss();

                    sitiosViewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setTitle("Alerta");
                            builder.setMessage("¿Deseas eliminar este usuario de la aplicación?");

                            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDatabase.child(getRef(i).getKey()).setValue(null);
                                    Toast.makeText(getContext(), "Eliminado correctamente", Toast.LENGTH_SHORT).show();
                                }
                            });

                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            builder.create();
                            builder.show();

                            return false;
                        }
                    });

                    sitiosViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setTitle("Contactalo");
                            builder.setMessage("¿Teléfono o E-Mail?");

                            builder.setPositiveButton("Teléfono", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("tel:"+tel));
                                    startActivity(intent);
                                }
                            });

                            builder.setNegativeButton("E-Mail", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String[] TO = {email}; //Direcciones email  a enviar.

                                    Intent emailIntent = new Intent(Intent.ACTION_SEND);

                                    emailIntent.setData(Uri.parse("mailto:"));
                                    emailIntent.setType("text/plain");
                                    emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Escribe el asunto");
                                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Ciclismo Fuente De Oro");

                                    try {
                                        startActivity(Intent.createChooser(emailIntent, "Enviar email."));
                                        Log.i("EMAIL", "Enviando email...");
                                    }
                                    catch (android.content.ActivityNotFoundException e) {
                                        Toast.makeText(getContext(), "No existe ningún cliente de email instalado!.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            builder.create();
                            builder.show();
                        }
                    });
                }

                @NonNull
                @Override
                public UsuariosAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuarios_row, parent, false);
                    return new UsuariosAdminViewHolder(v);
                }
            };
            adapter.startListening();
            mRutasList.setAdapter(adapter);

        } else {
            // Crea el nuevo fragmento y la transacción.
            Fragment nuevoFragmento = new InternetFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, nuevoFragmento);
            transaction.addToBackStack(null);

            // Commit a la transacción
            transaction.commit();
        }
    }

    public static class UsuariosAdminViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public UsuariosAdminViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setNombres(String nombres){
            TextView nombre_post = (TextView)mView.findViewById(R.id.nombre_usuario_admin);
            nombre_post.setText(nombres);
        }

        public void setTelefono(String telefono){
            TextView nombre_post = (TextView)mView.findViewById(R.id.tel_usuario_admin);
            nombre_post.setText(telefono);
        }

        public void setPerfil(String perfil){
            TextView nombre_post = (TextView)mView.findViewById(R.id.perfil_usuario_admin);
            nombre_post.setText(perfil);
        }
    }
}