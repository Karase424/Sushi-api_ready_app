package com.example.api_sushi.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Products implements Serializable{
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("price")
    private int price;

    @SerializedName("image")
    private String image;

    @SerializedName("category")
    private String category;

    @SerializedName("description")
    private String description;

    // Конструктор без параметров
    public Products() {}

    // Конструктор с параметрами
    public Products(int id, int price, String title,
                    String image, String category,
                    String description) {
        this.id = id;
        this.price = price;
        this.title = title;
        this.image = image;
        this.category = category;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}