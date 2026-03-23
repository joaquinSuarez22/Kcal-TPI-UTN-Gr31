package com.example.tif_gr31.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FoodSearchResponse {

    @SerializedName("foods")
    private List<FoodProduct> foods;

    @SerializedName("totalHits")
    private int totalHits;

    public List<FoodProduct> getFoods() { return foods; }
    public int getTotalHits()           { return totalHits; }
}
