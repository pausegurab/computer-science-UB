package com.example.pisprojecte;

import android.content.Intent;
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

public class RegistroActivity extends AppCompatActivity {
    Button inicioSesion, siguiente;
    private FirebaseAuth mAuth;
    private EditText email, contrasena, repetirContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        inicioSesion = findViewById(R.id.button_inicio_sesion);
        siguiente = findViewById(R.id.button_Siguiente);
        email = findViewById(R.id.editTextTextEmailAddress_Registro);
        contrasena = findViewById(R.id.editTextTextPassword);
        repetirContrasena = findViewById(R.id.editTextTextPasswordVerificacion);

        inicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
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

    public void registrarUsuario(View view){
        try{
            if(contrasena.getText().toString().equals(repetirContrasena.getText().toString())){
                mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), contrasena.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "Usuario creado correctamente.", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), Registro2Activity.class);
                            startActivity(intent);
                            //updateUI(user); //En caso que ya esté registrado
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("REGISTER", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "No se ha podido registrar al usuario",

                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null); //En caso que ya esté registrado
                        }
                    }
                });
            }else{
                Toast.makeText(this,"Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Registro fallido.",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
