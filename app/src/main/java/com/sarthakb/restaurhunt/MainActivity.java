package com.sarthakb.restaurhunt;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.content.Intent;

import java.io.File;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    SwipeFlingAdapterView flingContainer;
    ArrayList<FoodItem> items;
    MyAppAdapter myAppAdapter;
    private int PICK_IMAGE_REQUEST = 1;
    static FirebaseStorage storage;
    static StorageReference storageRef;
    static FirebaseDatabase database;
    static DatabaseReference databaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://restaurhunter.appspot.com");
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_main);
        toolbar.setTitle("Restaurhunt");
        setSupportActionBar(toolbar);

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.card_container);

        // Initialize Firebase systems
        storage = FirebaseStorage.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        storageRef = storage.getReferenceFromUrl("gs://restaurhunter.appspot.com");

        // Initialize current user
        final DatabaseReference localUser = databaseRef.child("users").child("user0");

        // Initialize FoodItems
        // Three test items in ArrayList<FoodItem>
        final FoodItem item1 = new FoodItem();
        item1.setImageUrl("http://www.sarthakb.com/images/otriangles.png");
        final FoodItem item2 = new FoodItem();
        item2.setImageUrl("https://firebasestorage.googleapis.com/v0/b/restaurhunter.appspot.com/o/images%2F1.jpg?alt=media&token=83539e6e-4772-45b5-9fd3-d4d7cd45b148");
        final FoodItem item3 = new FoodItem();
        item3.setImageUrl("http://www.sarthakb.com/images/blueTriangles.png");

        // Initialize card container
        items = new ArrayList<>();
        // Add cards

        items.add(item1);
        items.add(item2);
        items.add(item3);
        
        // Initialize itemCounter
        final LocalData ld = new LocalData();
        ld.itemCounter = 0;

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://restaurhunter.appspot.com");

        storageRef.child("images/1.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
//                item2.setImageUrl(uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

//        https://github.com/kikoso/Swipeable-Cards
//        https://github.com/mikepenz/MaterialDrawer


        AccountHeader profileHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.otriangles)
                .addProfiles(
                        new ProfileDrawerItem()
                        .withName("Test McTestFace")
                        .withEmail("hello@sarthakb.com")
                ).build();

        PrimaryDrawerItem imagesItem = new PrimaryDrawerItem()
                .withIdentifier(1).withName("Images");
        PrimaryDrawerItem uploadItem = new PrimaryDrawerItem().withIdentifier(2).withName("Upload");
        PrimaryDrawerItem myImagesItem = new PrimaryDrawerItem().withIdentifier(3).withName("My Images");



        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(profileHeader)
                .addDrawerItems(imagesItem, uploadItem, myImagesItem)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if(drawerItem.getIdentifier() == 2){
                            // Upload single picture
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                        }
                        return false;
                    }
                })
                .build();

        myAppAdapter = new MyAppAdapter(items, MainActivity.this);
        flingContainer.setAdapter(myAppAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object o) {

                items.remove(0);
                ld.itemCounter++;

                myAppAdapter.notifyDataSetChanged();
            }

            // when app opens, contact firebase and get all the pictures
            // sort those pictures to determine which one to display next

            @Override
            public void onRightCardExit(Object o) {

                DatabaseReference currentCard = databaseReference.child("cards").child("card"+Integer.toString(ld.itemCounter));

                // save object in history, pass to server to save (get Sarthak to save locally using his Android voodoo)
                //localUser.child("history").child("hCard" + Integer.toString(localUser.child("historyCounter").)).setValue(currentCard);


                // increment number of likes
                items.get(0).setNumLikes(items.get(0).getNumLikes() + 1);

                // TODO: write back object to server to update # of likes
                currentCard.child("numLikes").setValue(items.get(0).getNumLikes());

                items.remove(0);
                ld.itemCounter++;


                myAppAdapter.notifyDataSetChanged();

            }

            @Override
            public void onAdapterAboutToEmpty(int i) {

            }

            @Override
            public void onScroll(float v) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri file = data.getData();
                StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
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
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        if (downloadUrl != null) {
                            FoodItem newItem = new FoodItem();
                            newItem.setImageUrl(downloadUrl.toString());
                            int last_index = downloadUrl.getPath().split("/").length - 1;
                            databaseRef.child("cards/" + downloadUrl.getPath().split("/")[last_index]).setValue(newItem);
                        }
                    }
                });
            }
        }
    }

    public static class ViewHolder {
        public ImageView cardImage;
    }

    public class MyAppAdapter extends BaseAdapter {

        public List<FoodItem> foodItemList;
        public Context context;

        public MyAppAdapter(List<FoodItem> foodItemList, Context context) {
            this.foodItemList = foodItemList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return foodItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return foodItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            ViewHolder viewHolder;
            if (rowView == null) {

                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.image_card_view, parent, false);
                // configure view holder
                viewHolder = new ViewHolder();
                viewHolder.cardImage = (ImageView) rowView.findViewById(R.id.card_image);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Picasso.with(MainActivity.this).load(foodItemList.get(position).getImageUrl()).into(viewHolder.cardImage);

            return rowView;
        }
    }

}
