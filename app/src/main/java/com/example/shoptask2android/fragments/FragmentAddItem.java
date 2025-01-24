package com.example.shoptask2android.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.shoptask2android.R;
import com.example.shoptask2android.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FragmentAddItem extends DialogFragment {

    private EditText itemAmount, itemPrice;
    private Spinner itemSpinner;
    private Button addItemButton;

    private FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        itemSpinner = view.findViewById(R.id.item_spinner);
        itemAmount = view.findViewById(R.id.item_amount);
        itemPrice = view.findViewById(R.id.item_price);
        addItemButton = view.findViewById(R.id.add_item_button);

        database = FirebaseDatabase.getInstance();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"Bread", "Milk", "Chocolate"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSpinner.setAdapter(adapter);

        addItemButton.setOnClickListener(v -> addItemToFirebase());

        return view;
    }

    private void addItemToFirebase() {
        String name = itemSpinner.getSelectedItem().toString();
        String amount = itemAmount.getText().toString();
        String price = itemPrice.getText().toString();

        if (name.isEmpty() || amount.isEmpty() || price.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int parsedAmount = Integer.parseInt(amount);
            double parsedPrice = Double.parseDouble(price);
            if (parsedAmount <= 0 || parsedPrice <= 0) {
                Toast.makeText(getContext(), "Amount and price must be positive numbers", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        DatabaseReference userProductsRef = database.getReference("users").child(userId).child("products");

        String productId = userProductsRef.push().getKey();
        if (productId != null) {
            Product newProduct = new Product(productId, name, amount, price);
            userProductsRef.child(productId).setValue(newProduct)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Error adding item", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}