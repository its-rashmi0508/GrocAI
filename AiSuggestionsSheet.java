package com.example.grocerymate.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.grocerymate.R;
import com.example.grocerymate.adapter.AiTipAdapter;
import com.example.grocerymate.ai.NutritionAnalyzer;
import com.example.grocerymate.ai.NutritionResult;
import com.example.grocerymate.data.DataStore;
import com.example.grocerymate.databinding.FragmentAiSuggestionsBinding;
import com.example.grocerymate.util.NotificationHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.NumberFormat;
import java.util.Locale;

/** Shows the on-device AI nutrition analysis and can push it as a notification. */
public class AiSuggestionsSheet extends BottomSheetDialogFragment {

    private FragmentAiSuggestionsBinding b;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c,
                             @Nullable Bundle s) {
        b = FragmentAiSuggestionsBinding.inflate(inflater, c, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        NutritionResult r = new NutritionAnalyzer().analyze(DataStore.get().getItems());

        b.tvAiStatus.setText(r.statusLabel);
        b.tvAiSummary.setText("Estimated "
                + NumberFormat.getInstance(Locale.US).format(r.totalCalories)
                + " kcal  ·  Carbs " + r.carbsPct + "%  ·  Protein " + r.proteinPct
                + "%  ·  Fats " + r.fatsPct + "%  ·  Fiber " + r.fiberPct + "%");

        AiTipAdapter adapter = new AiTipAdapter();
        b.recyclerTips.setAdapter(adapter);
        adapter.submit(r.insights);

        b.btnNotify.setOnClickListener(x -> {
            NotificationHelper.showNutritionAlert(requireContext(),
                    getString(R.string.ai_title), r.headline);
            Toast.makeText(getContext(), "Alert sent to notifications", Toast.LENGTH_SHORT).show();
        });
        b.btnClose.setOnClickListener(x -> dismiss());

        // Proactively push the alert once when the diet is out of balance.
        if (r.hasAlert) {
            NotificationHelper.showNutritionAlert(requireContext(),
                    getString(R.string.ai_title), r.headline);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
