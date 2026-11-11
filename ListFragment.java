package com.example.grocerymate.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.grocerymate.MainActivity;
import com.example.grocerymate.R;
import com.example.grocerymate.adapter.GroceryAdapter;
import com.example.grocerymate.data.DataStore;
import com.example.grocerymate.databinding.FragmentListBinding;
import com.example.grocerymate.model.GroceryItem;

import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListFragment extends Fragment implements GroceryAdapter.Listener {

    private FragmentListBinding b;
    private final DataStore data = DataStore.get();
    private GroceryAdapter adapter;
    private String selectedCategory = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c,
                             @Nullable Bundle s) {
        b = FragmentListBinding.inflate(inflater, c, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        adapter = new GroceryAdapter(this);
        b.recycler.setAdapter(adapter);

        buildChips();
        refresh();

        b.btnBack.setOnClickListener(x -> ((MainActivity) requireActivity()).openHome());
        b.btnAdd.setOnClickListener(x ->
                new AddItemDialog(this::onItemAdded).show(getParentFragmentManager(), "add"));
    }

    private void buildChips() {
        b.chipContainer.removeAllViews();
        for (String cat : data.categories()) {
            android.widget.TextView chip = (android.widget.TextView) getLayoutInflater()
                    .inflate(R.layout.item_chip, b.chipContainer, false);
            chip.setText(cat);
            chip.setSelected(cat.equals(selectedCategory));
            chip.setOnClickListener(v -> {
                selectedCategory = cat;
                for (int i = 0; i < b.chipContainer.getChildCount(); i++) {
                    View c = b.chipContainer.getChildAt(i);
                    c.setSelected(((android.widget.TextView) c).getText().toString().equals(cat));
                }
                refresh();
            });
            b.chipContainer.addView(chip);
        }
    }

    private void refresh() {
        List<GroceryItem> filtered = new ArrayList<>();
        for (GroceryItem it : data.getItems()) {
            if (selectedCategory.equals("All") || it.category.equals(selectedCategory)) {
                filtered.add(it);
            }
        }
        adapter.submit(filtered);
        updateTotals();
    }

    private void updateTotals() {
        b.tvTotal.setText("₹" + NumberFormat.getInstance(Locale.US).format(data.estimatedTotal()));
        b.tvCount.setText(data.totalUnits() + " Items");
    }

    private void onItemAdded() {
        selectedCategory = "All";
        buildChips();
        refresh();
    }

    @Override
    public void onChanged() {
        data.save();
        updateTotals();
    }

    @Override
    public void onDelete(GroceryItem item) {
        int index = data.getItems().indexOf(item);
        data.removeItem(item);
        refresh();
        Snackbar sb = Snackbar.make(b.getRoot(), item.name + " removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO", v -> {
                    data.insertItem(index, item);
                    if (b != null) refresh();
                });
        View fab = requireActivity().findViewById(R.id.fab);
        if (fab != null) sb.setAnchorView(fab);
        sb.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
