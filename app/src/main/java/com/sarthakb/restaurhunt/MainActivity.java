package com.sarthakb.restaurhunt;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.content.Intent;

import java.io.File;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.Random;

public class MainActivity extends AppCompatActivity{

    SwipeFlingAdapterView flingContainer;
    ArrayList<FoodItem> history; // ADDED THIS
    MyAppAdapter myAppAdapter;
    private int PICK_IMAGE_REQUEST = 1;
    private static FirebaseStorage storage;
    private static StorageReference storageRef;
    private static FirebaseDatabase database;
    private static DatabaseReference databaseRef;
    static DatabaseReference localUserRef;
    private ArrayList<FoodItem> items;
    private ArrayList<ValueEventListener> mLikeListener;
    private int foodItemStartSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = new ArrayList<FoodItem>();
        history = new ArrayList<FoodItem>();
        final Context context = this;
        
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://restaurhunter.appspot.com");
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
        localUserRef = databaseRef.child("user0");

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_main);
        toolbar.setTitle("Restaurhunt");
        setSupportActionBar(toolbar);

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.card_container);

        // Initialize current user
        final DatabaseReference localUser = databaseRef.child("users").child("user0");

        /*
        // Read in initial FoodItems
        ValueEventListener startListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    FoodItem newItem = child.getValue(FoodItem.class);
                    newItem.id = i;
                    items.add(newItem);
                    i++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseRef.child("cards").addListenerForSingleValueEvent(startListener);

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }
        */

        Log.d("DEBUG: ", "Items size - " + Integer.toString(items.size()));
        // Initialize FoodItems
        //Grab them from Firebase (snapshot?)
        // Three test items in ArrayList<FoodItem>
        //final FoodItem item1 = new FoodItem();
        //item1.setImageUrl("http://www.sarthakb.com/images/otriangles.png");
        //item1.id = 0;
        //final FoodItem item2 = new FoodItem();
        //item2.setImageUrl("https://firebasestorage.googleapis.com/v0/b/restaurhunter.appspot.com/o/images%2F1.jpg?alt=media&token=83539e6e-4772-45b5-9fd3-d4d7cd45b148");
        //item2.id = 1;
        //final FoodItem item3 = new FoodItem();
        //item3.setImageUrl("http://www.sarthakb.com/images/blueTriangles.png");
        //item3.id = 2;

        // Initialize card container
        items = new ArrayList<>();
        final FoodItem item1 = new FoodItem();
        item1.setImageUrl("http://www.sarthakb.com/images/otriangles.png");
        final FoodItem item2 = new FoodItem();
        item2.setImageUrl("https://firebasestorage.googleapis.com/v0/b/restaurhunter.appspot.com/o/images%2F1.jpg?alt=media&token=83539e6e-4772-45b5-9fd3-d4d7cd45b148");
        final FoodItem item3 = new FoodItem();
        item3.setImageUrl("http://www.sarthakb.com/images/blueTriangles.png");

        // Add cards
        items.add(item1);
        items.add(item2);
        items.add(item3);

        foodItemStartSize = items.size();

        // Initialize itemCounter
        // Initialize local history card container
        history = new ArrayList<>();

        // Initialize itemCounter and historyCounter

        final LocalData ld = new LocalData();
        ld.itemCounter = 0;
        ld.historyCounter = 0;

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

        mLikeListener = new ArrayList<ValueEventListener>();

        Log.d("DEBUG: ", Integer.toString(items.size()));
        for (ld.listenerCount = 0; ld.listenerCount < foodItemStartSize; ld.listenerCount++) {
            DatabaseReference cardRef = databaseRef.child("cards").child("card" + Integer.toString(ld.listenerCount));
            // Add value event listener to the post
            // [START post_value_event_listener]
            ValueEventListener likeListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    FoodItem fi = dataSnapshot.getValue(FoodItem.class);
                    Log.d("DEBUG: ", Integer.toString(fi.id) + "WAHOO THE DATA CHANGED!");
                    Log.d("DEBUG: ", "WAHOO THE DATA CHANGED!" + Integer.toString(items.size()));
                    //if (items.size() == foodItemStartSize)
                    if (fi.id - (foodItemStartSize - items.size()) >= 0)
                   // if (fi.id + items.size() >= foodItemStartSize)
                        items.get(fi.id - (foodItemStartSize - items.size())).setNumLikes(fi.getNumLikes());
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.d("Like Listener: ", "Snapshot cancelled!");
                }
            };
            cardRef.addValueEventListener(likeListener);
            // [END post_value_event_listener]

            // Keep copy of post listener so we can remove it when app stops
            mLikeListener.add(likeListener);
        }
        ld.listenerCount = items.size() - 1;

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
                        }else if(drawerItem.getIdentifier() == 3){
                            Intent intent = new Intent(context, HistoryActivity.class);
                            intent.putExtra("History", history);
                            startActivity(intent);
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

                if (mLikeListener.get(ld.itemCounter) != null)
                    databaseRef.child("cards").child("card" + Integer.toString(ld.itemCounter)).removeEventListener(mLikeListener.get(ld.itemCounter));
                items.remove(0);

                ld.itemCounter++;

                myAppAdapter.notifyDataSetChanged();
            }

            // when app opens, contact firebase and get all the pictures
            // sort those pictures to determine which one to display next

            @Override
            public void onRightCardExit(Object o) {

                DatabaseReference currentCard = databaseRef.child("cards").child("card"+Integer.toString(ld.itemCounter));

                // increment number of likes
                Log.d("DEBUG: ", Integer.toString(items.get(0).getNumLikes()));
                items.get(0).setNumLikes(items.get(0).getNumLikes() + 1);
                Log.d("DEBUG: ", Integer.toString(items.get(0).getNumLikes()));

                // TODO: write back object to server to update # of likes
                currentCard.setValue(items.get(0));

                if (mLikeListener.get(ld.itemCounter) != null)
                    databaseRef.child("cards").child("card" + Integer.toString(ld.itemCounter)).removeEventListener(mLikeListener.get(ld.itemCounter));
                Log.d("DEBUG", "Listener removed");

                // add this item to history
                if (items.get(0).numLikes > 0){
                    history.add(items.get(0));
                }

                // test history
                // System.out.println(history.size());

                items.remove(0);

                ld.itemCounter++;
                ld.historyCounter++;

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
    public void onStop() {
        super.onStop();

        // Remove post value event listener

        int i = 0;
        while (i < mLikeListener.size() && mLikeListener.get(i) != null) {
            databaseRef.child("cards").child("card" + Integer.toString(i)).removeEventListener(mLikeListener.get(i));
            i++;
        }
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
