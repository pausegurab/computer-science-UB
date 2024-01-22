package com.example.pisprojecte;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class AjustesActivity extends AppCompatActivity {
    Button guardar, nuevaFotoPerfil, cerrarSesion, back;
    ImageView imagenPerfil;
    private FirebaseAuth mAuth;
    private TextView email;
    private EditText usuarioNombre;
    FirebaseUser user;
    DatabaseReference myRef;
    StorageReference imgRef;
    final long TAMANO_IMG = 3072 * 3072;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(user.getUid());

        usuarioNombre = findViewById(R.id.editTextTextUserName_ajustes);
        email = findViewById(R.id.textView_email_usuario_ajustes);
        imagenPerfil = findViewById(R.id.imageViewFotoPerfil_ajustes);
        nuevaFotoPerfil = findViewById(R.id.button_edit_perfil_reg_ajustes);
        guardar = findViewById(R.id.button_guardar_ajustes);
        cerrarSesion = findViewById(R.id.button_cerrar_sesion_ajustes);
        back = findViewById(R.id.back_settings);

        email.setText(user.getEmail().toString());
        usuarioNombre.setText(user.getDisplayName());


        imgRef = FirebaseStorage.getInstance().getReference("imagenesPerfil/" + user.getUid());
        imgRef.getBytes(TAMANO_IMG).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmapIMG = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imagenPerfil.setImageBitmap(bitmapIMG);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Error visualización imagen\n",Toast.LENGTH_LONG);
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AjustesActivity.this, InicioActivity.class);
                startActivity(intent);
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesionMethod(view);
            }
        });

    }

    public void onClickBotonNuevaFotoPerfilSettings(View view){
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
            imagenPerfil.setImageURI(fileUri);

            StorageReference folder = FirebaseStorage.getInstance().getReference().child("imagenesPerfil");

            final StorageReference file_name = folder.child(user.getUid());

            file_name.putFile(fileUri).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("link", String.valueOf(uri));
                myRef.setValue(hashMap);
                Log.d("Mensaje", "Se subió correctamente");


            }));


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


    public void cerrarSesionMethod(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Sesión cerrada con éxito.",
                Toast.LENGTH_SHORT).show();
    }


    public void modificarNombreUsuarioSettings(View view){
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
                            Log.w("MODIFICACIÓN", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Modificacioón fallida.",
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