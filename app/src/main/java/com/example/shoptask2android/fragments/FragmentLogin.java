package com.example.shoptask2android.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.shoptask2android.R;
import com.example.shoptask2android.activitys.MainActivity;

public class FragmentLogin extends Fragment {

    public FragmentLogin() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button buttonLogin = view.findViewById(R.id.btn_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.login(v);
            }
        });

        Button buttonRegister = view.findViewById(R.id.btn_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_fragmentLogin_to_fragmentRegister);
            }
        });

        return view;
    }
}