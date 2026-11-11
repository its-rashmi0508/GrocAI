package com.example.grocerymate.ui;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.grocerymate.MainActivity;
import com.example.grocerymate.R;
import com.example.grocerymate.adapter.CategoryAdapter;
import com.example.grocerymate.data.DataStore;
import com.example.grocerymate.databinding.FragmentBudgetBinding;

public class BudgetFragment extends Fragment {

    private FragmentBudgetBinding b;
    private final DataStore data = DataStore.get();
    private CategoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c,
                             @Nullable Bundle s) {
        b = FragmentBudgetBinding.inflate(inflater, c, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        adapter = new CategoryAdapter();
        b.recyclerCats.setAdapter(adapter);

        bind();

        b.btnBack.setOnClickListener(x -> ((MainActivity) requireActivity()).openHome());
        b.btnEdit.setOnClickListener(x -> editBudget());
    }

    /** (Re)draw every number from the live data. */
    private void bind() {
        if (b == null) return;
        int ringColor = data.isOverBudget() ? R.color.cat_fruit : R.color.green_600;
        b.tvUsedPct.setText(data.rawUsedPercent() + "%");
        b.donut.setProgressColor(ContextCompat.getColor(requireContext(), ringColor));
        b.donut.setProgress(data.usedPercent() / 100f);

        b.tvBudget.setText(DataStore.rupees(data.profile.monthlyBudget));
        b.tvSpent.setText("Spent " + DataStore.rupees(data.spent()));
        b.tvRemaining.setText("Remaining " + DataStore.rupees(data.remaining()));

        adapter.submit(data.categorySpending(), data.spent());
    }

    /** Lets the user change the monthly budget. */
    private void editBudget() {
        android.content.Context c = requireContext();
        EditText et = new EditText(c);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.setText(String.valueOf(data.profile.monthlyBudget));
        et.setSelection(et.getText().length());

        FrameLayout box = new FrameLayout(c);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        box.setPadding(pad, pad / 2, pad, 0);
        box.addView(et);

        new AlertDialog.Builder(c)
                .setTitle("Monthly budget (₹)")
                .setView(box)
                .setPositiveButton("Save", (d, w) -> {
                    int value;
                    try {
                        value = Integer.parseInt(et.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        value = 0;
                    }
                    if (value <= 0) {
                        Toast.makeText(c, "Enter an amount greater than 0",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    data.profile.monthlyBudget = value;
                    data.save();
                    bind();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
