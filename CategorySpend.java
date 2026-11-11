package com.example.grocerymate.model;

/** Aggregated spend for one category, used by the Budget screen. */
public class CategorySpend {
    public String name;
    public int amount;
    public int colorRes;

    public CategorySpend(String name, int amount, int colorRes) {
        this.name = name;
        this.amount = amount;
        this.colorRes = colorRes;
    }
}
