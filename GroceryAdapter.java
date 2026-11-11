package com.example.grocerymate.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerymate.databinding.ItemGroceryBinding;
import com.example.grocerymate.model.GroceryItem;

import java.util.ArrayList;
import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.VH> {

    public interface Listener {
        void onChanged();          // qty / checked changed -> refresh totals
        void onDelete(GroceryItem item);
    }

    private final List<GroceryItem> items = new ArrayList<>();
    private final Listener listener;

    public GroceryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<GroceryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGroceryBinding b = ItemGroceryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        h.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class VH extends RecyclerView.ViewHolder {
        final ItemGroceryBinding b;

        VH(ItemGroceryBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(GroceryItem item) {
            b.tvEmoji.setText(item.emoji);
            b.tvName.setText(item.name);
            b.tvSub.setText(item.unitLabel + " · ₹" + item.price);
            b.tvQty.setText(String.valueOf(item.quantity));
            applyChecked(item.checked);

            b.check.setOnClickListener(v -> {
                item.checked = !item.checked;
                applyChecked(item.checked);
                listener.onChanged();
            });
            b.btnPlus.setOnClickListener(v -> {
                item.quantity++;
                b.tvQty.setText(String.valueOf(item.quantity));
                listener.onChanged();
            });
            b.btnMinus.setOnClickListener(v -> {
                if (item.quantity > 1) {
                    item.quantity--;
                    b.tvQty.setText(String.valueOf(item.quantity));
                    listener.onChanged();
                }
            });
            b.btnDelete.setOnClickListener(v -> listener.onDelete(item));
        }

        private void applyChecked(boolean checked) {
            b.check.setSelected(checked);
            b.ivCheck.setVisibility(checked ? android.view.View.VISIBLE : android.view.View.GONE);
            b.tvName.setPaintFlags(checked
                    ? b.tvName.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                    : b.tvName.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
}
