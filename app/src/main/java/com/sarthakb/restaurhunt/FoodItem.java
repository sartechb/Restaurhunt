package com.sarthakb.restaurhunt;

import android.location.Location;
import java.util.Comparator;
import android.net.Uri;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by Sarthak on 10/8/16.
 */

public class FoodItem {
    int id;
    String userLocation;
    double time;
    double price;
    Location gpsLocation;
    ArrayList<String> tags;
    String author;
    String description;
    int numLikes;
    String imageUrl;

    public FoodItem() {
    }

    public FoodItem(double time, double price) {
        this.time = time;
        this.price = price;
    }

    public static final Comparator<FoodItem> FoodComparator = new Comparator<FoodItem>(){

        //@Override
        public int compare(FoodItem one, FoodItem two) {
            return determineRank(one) - determineRank(two);  // This will work because rank is positive integer
        }

        // @Override
        public int determineRank(FoodItem item) {

            int min_time = 24;
            int rank = 100;

            // for every like, increase rank by 1
            rank += item.numLikes;

            // for every hour above up time, decrease rank by (up_time - min_time)2
            if (item.time > min_time){
                rank -= Math.pow((item.time - min_time),2);
            }

            if (rank > 0){
                return rank;
            } else{
                return 0;
            }

        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Location getGpsLocation() {
        return gpsLocation;
    }

    public void setGpsLocation(Location gpsLocation) {
        this.gpsLocation = gpsLocation;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}
