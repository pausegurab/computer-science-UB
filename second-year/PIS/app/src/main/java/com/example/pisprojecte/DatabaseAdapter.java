package com.example.pisprojecte;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pisprojecte.Entidad.Sonido;
import com.example.pisprojecte.Entidad.Letra;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseAdapter extends Activity {


    public static final String TAG = "DatabaseAdapter";
    public String a;
    public QueryDocumentSnapshot docmuentReturn;
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseUser user;


    public static vmInterface listener;
    public static DatabaseAdapter databaseAdapter;

    public DatabaseAdapter(vmInterface listener){
        this.listener = listener;
        databaseAdapter = this;
        FirebaseFirestore.setLoggingEnabled(true);
        initFirebase();



    }


    public interface vmInterface{
        void setCollection(ArrayList<Letra> ac);

    }







    public void initFirebase(){

        user = mAuth.getCurrentUser();

        if (user == null) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");

                                user = mAuth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());


                            }
                        }
                    });
        }
        else{


        }
    }


    public void a() {
        String auth = mAuth.getCurrentUser().getEmail();

        Query firstQuery = DatabaseAdapter.db.collection("Lyrics").whereEqualTo("owner", auth);
        Query secondQuery = DatabaseAdapter.db.collection("Lyrics").whereEqualTo("publico", true);
        Task firstTask = firstQuery.get();
        Task secondTask = secondQuery.get();

        ArrayList<Letra> retrieved_lyrics = new ArrayList();
        Tasks.whenAllSuccess(firstTask, secondTask).addOnSuccessListener(list -> {
            for (Object qSnap : list) {
                Log.d("=== obj", qSnap.toString());

                for (QueryDocumentSnapshot document : (QuerySnapshot) qSnap) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    retrieved_lyrics.add(
                            new Letra(
                                    document.getString("title"),
                                    document.getString("text"),
                                    document.getString("author"),
                                    document.getBoolean("publico"))
                    );
                }
                listener.setCollection(retrieved_lyrics);
            }
        });
    }


    public void getCollectionLyrics() {
        Log.d(TAG, "getLyrics");
        a();
    }
        /*DatabaseAdapter.db.collection("Lyrics")
                .whereEqualTo("publico",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Letra> retrieved_ac = new ArrayList<Letra>() ;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                retrieved_ac.add(new Letra( document.getString("title"), document.getString("text"), document.getString("author"),document.getBoolean("publico")));
                            }
                            listener.setCollection(retrieved_ac);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }*/




    public void saveDocument (String title, String text, String owner, boolean publico) {

        // Create a new user with a first and last name

        CollectionReference Lyrics = db.collection("Lyrics");


        Map<String, Object> data1 = new HashMap<>();
        data1.put("title", title);
        data1.put("text", text);
        data1.put("owner", owner);
        data1.put("publico", publico);


        Lyrics.document().set(data1);

        Log.d(TAG, "saveDocument");

    }
    public void editDocument(String title, String text, String owner, boolean publico,String id){
        if(publico==false){
            DocumentReference Lyrics = db.collection("Lyrics").document(id);
            Lyrics.update("text",text).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG,"DocumentUpdated");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG,"ERROR", e);
                }
            });

        }else if(publico == true){
            CollectionReference Lyrics = db.collection("Lyrics");
            title += "-" + user.getEmail();
            Map<String, Object> data1 = new HashMap<>();
            data1.put("title", title);
            data1.put("text", text);
            data1.put("owner", owner);
            data1.put("publico", false);


            Lyrics.document().set(data1);

            Log.d(TAG, "saveDocument");
        }

    }
    public QueryDocumentSnapshot getOneDocument(String title) {

        db.collection("Lyrics")
                .whereEqualTo("title",title )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                docmuentReturn = document;

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }



                });

        return docmuentReturn;


    }

/*
    public void saveDocumentWithFile (String id, String description, String userid, String path) {
        Uri file = Uri.fromFile(new File(path));
        StorageReference storageRef = storage.getReference();
        StorageReference audioRef = storageRef.child("audio"+File.separator+file.getLastPathSegment());
        UploadTask uploadTask = audioRef.putFile(file);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return audioRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    saveDocument(id, description, userid, downloadUri.toString());
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload is " + progress + "% done");
            }
        });
    }
    */

/*
    public void getBases () {
        String user = mAuth.getCurrentUser().getUid() + "/";
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("bases/").child(user);
        ArrayList<Base> retrieved_bases = new ArrayList<Base>();
        pathReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference fileRef : listResult.getItems()){
                    retrieved_bases.add(new Base(fileRef.getName()));

                }

            }
        });
        listener.setCollection(retrieved_bases);
    }
    */


}
