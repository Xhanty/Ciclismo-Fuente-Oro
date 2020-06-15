package com.fuenteoro.ciclismo.InfoIndex;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fuenteoro.ciclismo.LoginActivity;
import com.fuenteoro.ciclismo.R;

public class Fragment_PagFour extends Fragment {

    Button pasar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pag_four, container, false);

        pasar = (Button) view.findViewById(R.id.btn_emp_inx);

        pasar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                //Finish
            }
        });

        return view;
    }
}