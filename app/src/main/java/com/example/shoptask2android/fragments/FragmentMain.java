package com.example.shoptask2android.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.shoptask2android.R;

public class FragmentMain extends Fragment {

    private Button addItemBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        addItemBtn = view.findViewById(R.id.btn_mainAddItem);  // Add a button to open the dialog

        addItemBtn.setOnClickListener(v -> {
            FragmentAddItem addItemDialog = new FragmentAddItem();
            addItemDialog.show(getChildFragmentManager(), "AddItemDialog");
        });

        return view;
    }
}
