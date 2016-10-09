package com.sarthakb.restaurhunt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class FormActivity extends Activity {

    EditText restName, loc, price;
    Button upload;
    final private int PICK_IMAGE_REQUEST = 1;
    private static FirebaseStorage storage;
    private static StorageReference storageRef;
    private static FirebaseDatabase database;
    private static DatabaseReference databaseRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://restaurhunter.appspot.com");
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
        restName = (EditText) findViewById(R.id.etName);
        loc = (EditText) findViewById(R.id.etLoc);
        price = (EditText) findViewById(R.id.etPrice);
        upload = (Button) findViewById(R.id.btnUpload);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Upload single picture
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri file = data.getData();
                Random generator = new Random();
                int i = generator.nextInt(100000);
                StorageReference riversRef = storageRef.child("images/"+ Integer.toString(i) + "_" + file.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(file);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        if (downloadUrl != null) {
                            // Get
                            ValueEventListener getChildrenListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    FoodItem newItem = new FoodItem();
                                    newItem.setImageUrl(downloadUrl.toString());
                                    long numChildren = dataSnapshot.getChildrenCount();
                                    newItem.setId((int)numChildren);
                                    databaseRef.child("cards/card" + Long.toString(numChildren)).setValue(newItem);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            databaseRef.child("cards").addListenerForSingleValueEvent(getChildrenListener);
                        }
                    }
                });
            }
        }
    }

}
