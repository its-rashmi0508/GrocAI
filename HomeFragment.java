package com.example.grocerymate.ui;

import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.grocerymate.MainActivity;
import com.example.grocerymate.R;
import com.example.grocerymate.ai.NutritionAnalyzer;
import com.example.grocerymate.ai.NutritionResult;
import com.example.grocerymate.data.DataStore;
import com.example.grocerymate.databinding.FragmentHomeBinding;

import java.text.NumberFormat;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding b;
    private final DataStore data = DataStore.get();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c,
                             @Nullable Bundle s) {
        b = FragmentHomeBinding.inflate(inflater, c, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        b.tvGreeting.setText(greetingForNow());
        b.tvName.setText(data.profile.name);
        bindBudget();
        bindSummary();
        bindNutrition();
        wireActions();
    }

    /** Greeting that changes with the time of day. */
    private String greetingForNow() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) return "Good Morning ☀️";
        if (hour >= 12 && hour < 17) return "Good Afternoon 🌤️";
        if (hour >= 17 && hour < 21) return "Good Evening 🌆";
        return "Good Night 🌙";
    }

    private void bindBudget() {
        b.tvBudgetAmount.setText(rupees(data.profile.monthlyBudget));
        b.tvSpent.setText(rupees(data.spent()));
        b.tvRemaining.setText(rupees(data.remaining()));
        // Proportional white fill inside the budget bar.
        int used = data.usedPercent();
        setWeight(b.budgetFill, used);
        setWeight(b.budgetRest, 100 - used);
    }

    private void bindSummary() {
        b.tvItemsCount.setText(String.valueOf(data.totalUnits()));
        b.tvEstTotal.setText(rupees(data.estimatedTotal()));
        b.tvCalories.setText(NumberFormat.getInstance(Locale.US)
                .format(data.estimatedCalories()) + " kcal");
    }

    private void bindNutrition() {
        NutritionResult r = new NutritionAnalyzer().analyze(data.getItems());

        int cCarbs = color(R.color.macro_carbs);
        int cProtein = color(R.color.macro_protein);
        int cFats = color(R.color.macro_fats);
        int cFiber = color(R.color.macro_fiber);

        b.pieNutrition.setData(
                new float[]{r.carbsPct, r.proteinPct, Math.max(r.fatsPct, 0.5f), r.fiberPct},
                new int[]{cCarbs, cProtein, cFats, cFiber});
        b.pieNutrition.setHoleRatio(0f);

        legend(b.legendCarbs, cCarbs, "Carbs", r.carbsPct);
        legend(b.legendProtein, cProtein, "Protein", r.proteinPct);
        legend(b.legendFats, cFats, "Fats", r.fatsPct);
        legend(b.legendFiber, cFiber, "Fiber", r.fiberPct);

        b.tvStatusPill.setText(r.statusLabel);
        b.tvAiHeadline.setText(r.headline);
    }

    private void legend(com.example.grocerymate.databinding.RowLegendBinding row,
                        int c, String label, int pct) {
        row.label.setText(label);
        row.value.setText(pct + "%");
        row.dot.getBackground().setColorFilter(c, PorterDuff.Mode.SRC_IN);
    }

    private void wireActions() {
        View.OnClickListener toList = x -> ((MainActivity) requireActivity()).openList();
        b.actionList.setOnClickListener(toList);
        b.tvViewAll.setOnClickListener(toList);
        View.OnClickListener toAi = x ->
                new AiSuggestionsSheet().show(getChildFragmentManager(), "ai");
        b.actionAi.setOnClickListener(toAi);
        b.cardAiBanner.setOnClickListener(toAi);
        b.btnBell.setOnClickListener(toAi);
        b.actionInsights.setOnClickListener(x -> ((MainActivity) requireActivity()).openBudget());
        b.actionScan.setOnClickListener(x -> android.widget.Toast.makeText(getContext(),
                "Receipt scanning coming soon", android.widget.Toast.LENGTH_SHORT).show());
    }

    private void setWeight(View view, float weight) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
        lp.weight = weight;
        view.setLayoutParams(lp);
    }

    private int color(int res) {
        return ContextCompat.getColor(requireContext(), res);
    }

    private String rupees(int amount) {
        return DataStore.rupees(amount);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
