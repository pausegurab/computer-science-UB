package com.example.pisprojecte;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private Button registro, inicioSesion;
    private EditText email, contrasena;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registro = findViewById(R.id.buttonRegistrarse);
        inicioSesion = findViewById(R.id.buttonIniciarSesion);
        email = findViewById(R.id.editTextEmailAddressInicioSesion);
        contrasena = findViewById(R.id.editTextPasswordInicioSesion);

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);  //En caso que ya esté registrado
    }

    public void signInFirebase(View view) {
        try {
            mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), contrasena.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(getApplicationContext(), "Inicio de sesión correcto.", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                                startActivity(intent);
                                //updateUI(user); //En caso que ya esté registrado
                            } else {
                                // If sign in fails, display a message to the user.

                                Log.w("LOG IN", "signInUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Inicio de sesión fallido.",
                                        Toast.LENGTH_SHORT).show();
                                contrasena.setText("");
                                email.setText("");
                                //updateUI(null); //En caso que ya esté registrado
                            }
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Inicio de sesión fallido.",
                    Toast.LENGTH_SHORT).show();
            contrasena.setText("");
            email.setText("");
        }


    }


}






