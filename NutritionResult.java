package com.example.grocerymate.ai;

import java.util.ArrayList;
import java.util.List;

/** Result produced by {@link NutritionAnalyzer}. */
public class NutritionResult {
    // Macro split as a percentage of total macro mass (0..100)
    public int carbsPct;
    public int proteinPct;
    public int fatsPct;
    public int fiberPct;

    public int totalCalories;

    /** Overall label shown as a pill: "Good", "Fair", "Needs Attention". */
    public String statusLabel = "Good";

    /** True when at least one ALERT-level issue was found. */
    public boolean hasAlert = false;

    /** The single most important message — used for the notification. */
    public String headline = "Your nutrition looks balanced. Keep it up! 🎉";

    public final List<Insight> insights = new ArrayList<>();
}
