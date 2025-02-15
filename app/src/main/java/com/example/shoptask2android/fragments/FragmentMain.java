package com.example.shoptask2android.fragments;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.shoptask2android.R;
import com.example.shoptask2android.adapters.ProductAdapter;
import com.example.shoptask2android.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentMain extends Fragment {

    private Button addItemBtn;
    private RecyclerView shopRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private TextView userTextView;
    private FirebaseDatabase database;
    private DatabaseReference userProductsRef;
    private int swipedPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        userTextView = view.findViewById(R.id.tvUser);
        addItemBtn = view.findViewById(R.id.btn_mainAddItem);
        shopRecyclerView = view.findViewById(R.id.shop_recycler_view);

        // Initialize RecyclerView
        shopRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        shopRecyclerView.setAdapter(productAdapter);

        addItemBtn.setOnClickListener(v -> {
            FragmentAddItem addItemDialog = new FragmentAddItem();
            addItemDialog.show(getChildFragmentManager(), "AddItemDialog");
        });

        loadProductsFromFirebase();
        attachSwipeToDelete();

        return view;
    }

    private void loadProductsFromFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        userProductsRef = database.getReference("users").child(userId).child("products");
        userTextView.setText(auth.getCurrentUser().getEmail());

        userProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the product list to reload the updated data
                productList.clear();

                // Populate the product list with the updated data from Firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }

                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attachSwipeToDelete() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                swipedPosition = viewHolder.getAdapterPosition(); // Save the swiped item position
                Product productToRemove = productList.get(swipedPosition);
                showRemoveConfirmationDialog(productToRemove, swipedPosition);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(shopRecyclerView);
    }

    @Override
    public void onPause() {
        super.onPause();
        restoreItemRecycleView();
    }

    private void restoreItemRecycleView(){
        if (swipedPosition != -1) {
            productAdapter.notifyItemChanged(swipedPosition);
            swipedPosition = -1;
        }
    }

    private void showRemoveConfirmationDialog(Product productToRemove, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to remove this product?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // User confirmed, proceed with removal
                    removeProductFromFirebase(productToRemove, position);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    restoreItemRecycleView();
                })
                .setOnDismissListener(dialog -> {
                    restoreItemRecycleView();
                })
                .show();
    }

    private void removeProductFromFirebase(Product productToRemove, int position) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userProductsRef = database.getReference("users").child(userId).child("products");

        userProductsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null && productToRemove.equals(product)) {
                        // If the product matches, get its ID and remove it
                        String productIdToRemove = snapshot.getKey();
                        if (productIdToRemove != null) {
                            // Remove the product from Firebase
                            userProductsRef.child(productIdToRemove).removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        // Safely remove the product from the list only after Firebase operation is successful
                                        if (position != RecyclerView.NO_POSITION && swipedPosition == position) {
                                            productList.remove(position);
                                            productAdapter.notifyItemRemoved(position); // Update RecyclerView
                                            Toast.makeText(getContext(), "Product removed", Toast.LENGTH_SHORT).show();
                                            swipedPosition = -1; // Reset position after removal
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Failed to remove product", Toast.LENGTH_SHORT).show();
                                    });
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible database errors
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}