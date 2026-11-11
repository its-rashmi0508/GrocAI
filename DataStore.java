package com.example.grocerymate.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.grocerymate.R;
import com.example.grocerymate.model.CategorySpend;
import com.example.grocerymate.model.GroceryItem;
import com.example.grocerymate.model.UserProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Data source shared across the screens (singleton).
 *
 * Everything is now SAVED on the device (SharedPreferences + JSON), so the
 * shopping list, monthly budget, family size and reminder switch survive
 * closing the app. Call {@link #init(Context)} once at startup.
 *
 * "Spent" is no longer a hard-coded number: it is the total of the items the
 * user has TICKED OFF (bought) in the shopping list, so the Home and Budget
 * screens always match the list.
 */
public class DataStore {

    private static final String PREF = "grocerymate_data";
    private static final String K_ITEMS = "items";
    private static final String K_BUDGET = "budget";
    private static final String K_FAMILY = "family";
    private static final String K_REMINDER = "reminder";

    private static final String[] CATS =
            {"Vegetables", "Fruits", "Dairy", "Grains", "Meats", "Others"};

    private static DataStore instance;

    public static DataStore get() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    /** Load saved data (or the sample list on first launch). Safe to call many times. */
    public static void init(Context ctx) {
        DataStore d = get();
        if (d.appCtx == null) {
            d.appCtx = ctx.getApplicationContext();
            d.load();
        }
    }

    public final UserProfile profile = new UserProfile();
    private final List<GroceryItem> items = new ArrayList<>();
    private Context appCtx;

    private DataStore() { }

    // ----------------------------------------------------------------------
    // Persistence
    // ----------------------------------------------------------------------
    private SharedPreferences prefs() {
        return appCtx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    private void load() {
        SharedPreferences p = prefs();
        profile.monthlyBudget = p.getInt(K_BUDGET, profile.monthlyBudget);
        profile.familyMembers = p.getInt(K_FAMILY, profile.familyMembers);
        profile.reminderOn = p.getBoolean(K_REMINDER, profile.reminderOn);

        items.clear();
        String json = p.getString(K_ITEMS, null);
        if (json == null) {            // first launch -> sample data
            seed();
            save();
            return;
        }
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                items.add(new GroceryItem(
                        o.getString("name"), o.getString("category"), o.getString("unit"),
                        o.getInt("price"), o.getInt("qty"), o.getBoolean("checked"),
                        o.getString("emoji"), o.getInt("cal"),
                        o.getDouble("carbs"), o.getDouble("protein"),
                        o.getDouble("fats"), o.getDouble("fiber")));
            }
        } catch (JSONException e) {    // corrupted data -> start fresh
            items.clear();
            seed();
            save();
        }
    }

    /** Write the list + settings to disk. Call after any change. */
    public void save() {
        if (appCtx == null) return;
        JSONArray arr = new JSONArray();
        try {
            for (GroceryItem it : items) {
                JSONObject o = new JSONObject();
                o.put("name", it.name);
                o.put("category", it.category);
                o.put("unit", it.unitLabel);
                o.put("price", it.price);
                o.put("qty", it.quantity);
                o.put("checked", it.checked);
                o.put("emoji", it.emoji);
                o.put("cal", it.calories);
                o.put("carbs", it.carbs);
                o.put("protein", it.protein);
                o.put("fats", it.fats);
                o.put("fiber", it.fiber);
                arr.put(o);
            }
        } catch (JSONException ignored) { }
        prefs().edit()
                .putString(K_ITEMS, arr.toString())
                .putInt(K_BUDGET, profile.monthlyBudget)
                .putInt(K_FAMILY, profile.familyMembers)
                .putBoolean(K_REMINDER, profile.reminderOn)
                .apply();
    }

    private void seed() {
        // name, category, unit, price, qty, checked, emoji, cal, carbs, protein, fats, fiber
        items.add(new GroceryItem("Brown Rice", "Grains", "kg", 80, 1, false, "🌾",
                350, 73, 7, 3, 4));
        items.add(new GroceryItem("Moong Dal", "Grains", "x 500g", 60, 1, true, "🫘",
                340, 59, 24, 1, 16));
        items.add(new GroceryItem("Milk", "Dairy", "x 1L", 60, 2, true, "🥛",
                250, 24, 16, 13, 0));
        items.add(new GroceryItem("Eggs", "Dairy", "x 12pcs", 84, 1, false, "🥚",
                900, 6, 78, 60, 0));
        items.add(new GroceryItem("Apple", "Fruits", "x 6pcs", 120, 1, false, "🍎",
                480, 126, 3, 2, 26));
        items.add(new GroceryItem("Broccoli", "Vegetables", "x 250g", 45, 1, false, "🥦",
                85, 17, 7, 1, 6));
    }

    // ----------------------------------------------------------------------
    // Shopping list
    // ----------------------------------------------------------------------
    public List<GroceryItem> getItems() {
        return items;
    }

    public void addItem(GroceryItem item) {
        items.add(item);
        save();
    }

    public void insertItem(int index, GroceryItem item) {
        items.add(Math.max(0, Math.min(index, items.size())), item);
        save();
    }

    public void removeItem(GroceryItem item) {
        items.remove(item);
        save();
    }

    public int estimatedTotal() {
        int t = 0;
        for (GroceryItem it : items) t += it.lineTotal();
        return t;
    }

    public int totalUnits() {
        int t = 0;
        for (GroceryItem it : items) t += it.quantity;
        return t;
    }

    public int estimatedCalories() {
        int c = 0;
        for (GroceryItem it : items) c += it.calories * it.quantity;
        return c;
    }

    public List<String> categories() {
        return Arrays.asList("All", "Vegetables", "Fruits", "Dairy", "Grains", "Meats", "Others");
    }

    // ----------------------------------------------------------------------
    // Budget (calculated from the ticked-off items)
    // ----------------------------------------------------------------------
    public int spent() {
        int t = 0;
        for (GroceryItem it : items) if (it.checked) t += it.lineTotal();
        return t;
    }

    public int remaining() {
        return profile.monthlyBudget - spent();
    }

    /** Real percentage used (can go above 100 when over budget). */
    public int rawUsedPercent() {
        if (profile.monthlyBudget <= 0) return 0;
        return Math.round(spent() * 100f / profile.monthlyBudget);
    }

    /** Percentage clamped to 0..100, safe for progress bars. */
    public int usedPercent() {
        return Math.max(0, Math.min(100, rawUsedPercent()));
    }

    public boolean isOverBudget() {
        return spent() > profile.monthlyBudget;
    }

    public List<CategorySpend> categorySpending() {
        List<CategorySpend> out = new ArrayList<>();
        for (String cat : CATS) {
            int sum = 0;
            for (GroceryItem it : items) {
                if (it.checked && cat.equals(it.category)) sum += it.lineTotal();
            }
            if (sum > 0) out.add(new CategorySpend(cat, sum, colorFor(cat)));
        }
        return out;
    }

    private int colorFor(String cat) {
        switch (cat) {
            case "Vegetables": return R.color.cat_veg;
            case "Dairy":      return R.color.cat_dairy;
            case "Grains":     return R.color.cat_grain;
            case "Fruits":     return R.color.cat_fruit;
            case "Meats":      return R.color.cat_meat;
            default:           return R.color.cat_other;
        }
    }

    // ----------------------------------------------------------------------
    public static String rupees(int amount) {
        String s = NumberFormat.getInstance(Locale.US).format(Math.abs(amount));
        return (amount < 0 ? "-" : "") + "₹" + s;
    }
}
