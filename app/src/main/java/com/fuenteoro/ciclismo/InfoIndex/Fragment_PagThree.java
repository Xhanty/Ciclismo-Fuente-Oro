package com.fuenteoro.ciclismo.InfoIndex;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.fuenteoro.ciclismo.LoginActivity;
import com.fuenteoro.ciclismo.R;

public class Fragment_PagThree extends Fragment implements View.OnClickListener {

    Button saltar, siguiente;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pag_three, container, false);

        saltar = (Button) view.findViewById(R.id.sal_des_three);
        siguiente = (Button) view.findViewById(R.id.sig_des_three);

        saltar.setOnClickListener(this);
        siguiente.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        if (v == saltar){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            //Finish
        } else if (v == siguiente){
            Toast.makeText(getContext(), "ESPEREEE", Toast.LENGTH_SHORT).show();
        }
    }
}