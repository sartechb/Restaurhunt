package com.sarthakb.restaurhunt;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.andtinder.model.CardModel;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    CardContainer mCardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_main);
        toolbar.setTitle("Restaurhunt");
        setSupportActionBar(toolbar);

        mCardContainer = (CardContainer) findViewById(R.id.cardContainerView);
        CardModel card = new CardModel("Hello", "hi", ContextCompat.getDrawable(this, R.drawable.picture1));
        card.setOnCardDimissedListener(new CardModel.OnCardDimissedListener() {
            @Override
            public void onLike() {
                Log.d("Swipeable Card", "I liked it");
            }

            @Override
            public void onDislike() {
                Log.d("Swipeable Card", "I disliked it");

            }
        });

        SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(this);
        adapter.add(card);
        mCardContainer.setAdapter(adapter);

//        https://github.com/kikoso/Swipeable-Cards
//        https://github.com/mikepenz/MaterialDrawer


        AccountHeader profileHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem()
                        .withName("Test McTestFace")
                        .withEmail("hello@sarthakb.com")
                ).build();

        PrimaryDrawerItem imagesItem = new PrimaryDrawerItem()
                .withIdentifier(1).withName("Images");
        PrimaryDrawerItem uploadItem = new PrimaryDrawerItem().withIdentifier(2).withName("Upload");
        PrimaryDrawerItem myImagesItem = new PrimaryDrawerItem().withIdentifier(2).withName("My Images");


        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(profileHeader)
                .addDrawerItems(imagesItem, uploadItem, myImagesItem)
                .build();
    }
}
