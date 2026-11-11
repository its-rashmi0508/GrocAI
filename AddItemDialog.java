package com.example.grocerymate.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.grocerymate.data.DataStore;
import com.example.grocerymate.databinding.DialogAddItemBinding;
import com.example.grocerymate.model.GroceryItem;
import com.example.grocerymate.util.FoodIcons;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/** Bottom sheet used by the FAB and the List "+" button to add a grocery item. */
public class AddItemDialog extends BottomSheetDialogFragment {

    /** Notified after a successful add so the caller can refresh its view. */
    public interface OnAdded { void added(); }

    private final OnAdded callback;
    private DialogAddItemBinding b;

    private static final String[] CATEGORIES =
            {"Vegetables", "Fruits", "Dairy", "Grains", "Meats", "Others"};

    public AddItemDialog(OnAdded callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c,
                             @Nullable Bundle s) {
        b = DialogAddItemBinding.inflate(inflater, c, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, CATEGORIES);
        b.acCategory.setAdapter(catAdapter);
        b.acCategory.setText(CATEGORIES[0], false);

        b.btnCancel.setOnClickListener(x -> dismiss());
        b.btnAdd.setOnClickListener(x -> save());
    }

    private void save() {
        String name = text(b.etName);
        if (TextUtils.isEmpty(name)) {
            b.etName.setError("Enter a name");
            return;
        }
        String category = b.acCategory.getText().toString();
        if (TextUtils.isEmpty(category)) category = "Others";
        String unit = text(b.etUnit);
        if (TextUtils.isEmpty(unit)) unit = "1 unit";
        int price = parse(text(b.etPrice), 0);
        int qty = Math.max(parse(text(b.etQty), 1), 1);

        double[] macros = macrosFor(category);
        GroceryItem item = new GroceryItem(name, category, unit, price, qty, false,
                FoodIcons.emojiFor(name, category),
                (int) macros[0], macros[1], macros[2], macros[3], macros[4]);
        DataStore.get().addItem(item);

        Toast.makeText(getContext(), name + " added", Toast.LENGTH_SHORT).show();
        if (callback != null) callback.added();
        dismiss();
    }

    /** Rough per-category nutrition so the AI analyzer still reflects new items. */
    private double[] macrosFor(String category) {
        switch (category) {
            case "Vegetables": return new double[]{60, 10, 4, 1, 5};
            case "Fruits":     return new double[]{90, 22, 1, 0, 4};
            case "Dairy":      return new double[]{140, 12, 9, 8, 0};
            case "Grains":     return new double[]{340, 70, 9, 2, 6};
            case "Meats":      return new double[]{200, 0, 26, 12, 0};
            default:           return new double[]{120, 15, 6, 4, 2};
        }
    }

    private String text(com.google.android.material.textfield.TextInputEditText e) {
        return e.getText() == null ? "" : e.getText().toString().trim();
    }

    private int parse(String s, int def) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return def; }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
