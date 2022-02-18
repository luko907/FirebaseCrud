package com.lucaskoch.firebasecrud.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemRVModel {
    private String title;
    private String img;
    private String type;
    private String gender;
    private String size;
    private String price;
    private String description;
    private String itemID;
    private String imgUUID;



    public ItemRVModel() {
    }


    public ItemRVModel(String title, String img, String type, String gender, String size, String price, String description, String itemID,String imgUUID) {
        this.title = title;
        this.img = img;
        this.type = type;
        this.gender = gender;
        this.size = size;
        this.price = price;
        this.description = description;
        this.itemID = itemID;
        this.imgUUID = imgUUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getImgUUID() {
        return imgUUID;
    }

    public void setImgUUID(String imgUUID) {
        this.imgUUID = imgUUID;
    }

}
