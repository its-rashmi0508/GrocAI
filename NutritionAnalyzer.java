package com.example.grocerymate.ai;

import com.example.grocerymate.model.GroceryItem;

import java.util.List;

/**
 * On-device "AI" nutrition engine.
 *
 * It aggregates the macro-nutrients of everything in the cart, works out the
 * balance of the diet and produces natural-language coaching messages such as
 * "You've taken a lot of carbs — reduce them for a balanced diet!".
 *
 * No network or API key required, so it always works in a demo. The public
 * surface ({@link #analyze}) is written so a real LLM call could later be
 * swapped in behind the same method without touching the UI.
 */
public class NutritionAnalyzer {

    // Healthy bands, as a share of total macro mass (%).
    private static final int CARBS_HIGH   = 55;
    private static final int CARBS_LOW    = 30;
    private static final int PROTEIN_LOW  = 15;
    private static final int PROTEIN_HIGH = 40;
    private static final int FATS_LOW     = 8;
    private static final int FATS_HIGH    = 35;
    private static final int FIBER_LOW    = 8;

    public NutritionResult analyze(List<GroceryItem> items) {
        NutritionResult r = new NutritionResult();

        double carbs = 0, protein = 0, fats = 0, fiber = 0;
        int calories = 0;
        for (GroceryItem it : items) {
            int q = Math.max(it.quantity, 1);
            carbs   += it.carbs   * q;
            protein += it.protein * q;
            fats    += it.fats    * q;
            fiber   += it.fiber   * q;
            calories += it.calories * q;
        }

        double totalMass = carbs + protein + fats + fiber;
        r.totalCalories = calories;

        if (totalMass <= 0) {
            r.statusLabel = "No data";
            r.headline = "Add items to your list to get nutrition insights.";
            r.insights.add(new Insight(Insight.Level.WARN, "🛒",
                    "Your list is empty. Add groceries to see your balance."));
            return r;
        }

        r.carbsPct   = (int) Math.round(carbs   / totalMass * 100);
        r.proteinPct = (int) Math.round(protein / totalMass * 100);
        r.fatsPct    = (int) Math.round(fats    / totalMass * 100);
        r.fiberPct   = (int) Math.round(fiber   / totalMass * 100);

        buildInsights(r);
        return r;
    }

    private void buildInsights(NutritionResult r) {
        int alerts = 0, warns = 0;

        // --- Carbs -----------------------------------------------------------
        if (r.carbsPct > CARBS_HIGH) {
            alerts++;
            r.insights.add(new Insight(Insight.Level.ALERT, "🍚",
                    "You've taken a lot of carbs (" + r.carbsPct
                            + "%) — reduce them for a balanced diet!"));
        } else if (r.carbsPct < CARBS_LOW) {
            warns++;
            r.insights.add(new Insight(Insight.Level.WARN, "🌾",
                    "Carbs are on the low side (" + r.carbsPct
                            + "%). A little rice or oats will keep your energy up."));
        }

        // --- Protein ---------------------------------------------------------
        if (r.proteinPct < PROTEIN_LOW) {
            warns++;
            r.insights.add(new Insight(Insight.Level.WARN, "🥚",
                    "Protein is low (" + r.proteinPct
                            + "%). Add dal, eggs or paneer to hit your goal."));
        } else if (r.proteinPct > PROTEIN_HIGH) {
            warns++;
            r.insights.add(new Insight(Insight.Level.WARN, "💪",
                    "Protein is quite high (" + r.proteinPct
                            + "%). Balance it with some whole grains."));
        }

        // --- Fats ------------------------------------------------------------
        if (r.fatsPct < FATS_LOW) {
            alerts++;
            r.insights.add(new Insight(Insight.Level.ALERT, "🥑",
                    "Healthy fats are almost missing (" + r.fatsPct
                            + "%). Add nuts, seeds or a little oil."));
        } else if (r.fatsPct > FATS_HIGH) {
            warns++;
            r.insights.add(new Insight(Insight.Level.WARN, "🧈",
                    "Fats are high (" + r.fatsPct + "%). Go easy on fried items."));
        }

        // --- Fiber -----------------------------------------------------------
        if (r.fiberPct < FIBER_LOW) {
            warns++;
            r.insights.add(new Insight(Insight.Level.WARN, "🥦",
                    "Fiber is low (" + r.fiberPct
                            + "%). Add more vegetables and fruits."));
        }

        if (alerts == 0 && warns == 0) {
            r.insights.add(new Insight(Insight.Level.GOOD, "✅",
                    "Great balance! Carbs " + r.carbsPct + "%, protein "
                            + r.proteinPct + "%, fats " + r.fatsPct
                            + "%, fiber " + r.fiberPct + "%."));
        }

        // --- Roll up into a status + headline --------------------------------
        r.hasAlert = alerts > 0;
        if (alerts > 0) {
            r.statusLabel = "Needs Attention";
        } else if (warns > 0) {
            r.statusLabel = "Fair";
        } else {
            r.statusLabel = "Good";
        }
        r.headline = r.insights.get(0).message;
    }
}
