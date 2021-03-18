package com.gesdes.acmarket.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gesdes.acmarket.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CompartirAppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompartirAppFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String URL_COMPARTIR;

    public CompartirAppFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompartirAppFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompartirAppFragment newInstance(String param1, String param2) {
        CompartirAppFragment fragment = new CompartirAppFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences preferencias =getActivity().getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        URL_COMPARTIR=preferencias.getString("LINK","");
        compartir();
        getActivity().getSupportFragmentManager().popBackStack();

        // Inflate the layout for this fragment
        return null;//inflater.inflate(R.layout.fragment_compartir_app, container, false);
    }

    public void compartir(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = URL_COMPARTIR;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Polar app");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Compartir via"));

    }


}
