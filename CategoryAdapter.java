package com.example.grocerymate.adapter;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerymate.databinding.ItemCategorySpendBinding;
import com.example.grocerymate.model.CategorySpend;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {

    private final List<CategorySpend> data = new ArrayList<>();
    private int total = 1;

    public void submit(List<CategorySpend> list, int total) {
        this.total = Math.max(total, 1);
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategorySpendBinding b = ItemCategorySpendBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        CategorySpend c = data.get(position);
        int pct = Math.round(c.amount * 100f / total);
        int color = ContextCompat.getColor(h.itemView.getContext(), c.colorRes);

        h.b.tvCat.setText(c.name);
        h.b.tvAmount.setText("₹" + NumberFormat.getInstance(Locale.US).format(c.amount)
                + " (" + pct + "%)");
        h.b.dot.getBackground().mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        h.b.fill.getBackground().mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        setWeight(h.b.fill, pct);
        setWeight(h.b.rest, 100 - pct);
    }

    private void setWeight(View v, float w) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();
        lp.weight = w;
        v.setLayoutParams(lp);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemCategorySpendBinding b;
        VH(ItemCategorySpendBinding b) { super(b.getRoot()); this.b = b; }
    }
}
