package com.example.pisprojecte;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;


public class Registro2Activity extends AppCompatActivity {
    Button registrarse, nuevaFotoPerfil;
    ImageView imagenPerfil;
    private static final int File = 1;
    private TextView email;
    private EditText usuarioNombre;
    FirebaseUser user;
    DatabaseReference myRef;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_foto_perfil);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(user.getUid());

        usuarioNombre = findViewById(R.id.editTextTextUserName);
        registrarse = findViewById(R.id.button_Registrarse);
        email = findViewById(R.id.textView_email_usuario);
        imagenPerfil = findViewById(R.id.imageViewFotoPerfil);
        nuevaFotoPerfil = findViewById(R.id.button_edit_perfil_reg);

        email.setText(user.getEmail().toString());

    }

    public void onClickBotonNuevaFotoPerfil(View view){
        cargarImg();
    }
    public void cargarImg(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"), 10);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Uri fileUri = data.getData();

            StorageReference folder = FirebaseStorage.getInstance().getReference().child("imagenesPerfil");

            final StorageReference file_name = folder.child(user.getUid());

            file_name.putFile(fileUri).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("link", String.valueOf(uri));
                myRef.setValue(hashMap);
                Log.d("Mensaje", "Se subió correctamente");

            }));

            imagenPerfil.setImageURI(fileUri);

            try{
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(fileUri).build();
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("ACTUALIZACION_IMAGEN_PERFIL", "Usuario configurado correctamente.");
                                }
                            }
                        });
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);  //En caso que ya esté registrado
    }





    public void modificarUsuario(View view){
        try{
            if(usuarioNombre != null){
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(usuarioNombre.getText().toString()).build();

                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("ACTUALIZACION_NOMBRE_PERFIL", "User profile updated.");
                                    Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "Usuario modificado correctamente.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Log.w("REGISTER", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Modificación fallida.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else{
                Toast.makeText(this,"Debe introducir un nombre de usuario.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }
}