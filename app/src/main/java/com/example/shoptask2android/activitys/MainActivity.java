package com.example.shoptask2android.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;

import com.example.shoptask2android.R;
import com.example.shoptask2android.models.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
    }

    public void login(View view){
        String email = ((EditText) findViewById(R.id.emailLogin)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordLogin)).getText().toString();

        if(!email.isEmpty() && !password.isEmpty()){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(view).navigate(R.id.action_fragmentLogin_to_fragmentMain);
                            } else {
                                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            Toast.makeText(this, "Error: You need to fill the details correct!", Toast.LENGTH_SHORT).show();
        }
    }

    public void register(View view){
        String email = ((EditText) findViewById(R.id.emailRegister)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordRegister)).getText().toString();
        String rePassword = ((EditText) findViewById(R.id.repasswordRegister)).getText().toString();

        if(!email.isEmpty() && !password.isEmpty() && !rePassword.isEmpty() && password.equals(rePassword) && password.length() >= 6 && email.contains("@")) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(MainActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();
                                addData();
                                Navigation.findNavController(view).navigate(R.id.action_fragmentRegister_to_fragmentLogin);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            Toast.makeText(this, "Error: You need to fill the details correct!", Toast.LENGTH_SHORT).show();
        }

    }

    public void addData(){

        String phone = ((EditText) findViewById(R.id.phoneRegister)).getText().toString();
        String email = ((EditText) findViewById(R.id.emailRegister)).getText().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(phone);

        Account s = new Account(email, phone);
        myRef.setValue(s);
    }
}