package com.example.grocerymate.model;

/**
 * A single grocery product. Holds price + quantity for the budget side and
 * per-unit macro-nutrients (grams) so the on-device AI can analyse the cart.
 */
public class GroceryItem {

    public String name;
    public String category;      // Vegetables, Fruits, Dairy, Grains, Others
    public String unitLabel;     // e.g. "x 500g", "kg", "x 12pcs"
    public int price;            // price for one unit, in rupees
    public int quantity;
    public boolean checked;
    public String emoji;         // lightweight thumbnail

    // Per-unit nutrition
    public int calories;
    public double carbs;
    public double protein;
    public double fats;
    public double fiber;

    public GroceryItem(String name, String category, String unitLabel, int price,
                       int quantity, boolean checked, String emoji,
                       int calories, double carbs, double protein, double fats, double fiber) {
        this.name = name;
        this.category = category;
        this.unitLabel = unitLabel;
        this.price = price;
        this.quantity = quantity;
        this.checked = checked;
        this.emoji = emoji;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fats = fats;
        this.fiber = fiber;
    }

    public int lineTotal() {
        return price * quantity;
    }
}
