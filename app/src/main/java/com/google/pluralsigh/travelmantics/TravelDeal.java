package com.google.pluralsigh.travelmantics;

import java.io.Serializable;

public class TravelDeal implements Serializable {
    private String id, title, description, price, imageUrl;

    public TravelDeal(){

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public TravelDeal(String id, String title, String description, String price, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }
}
