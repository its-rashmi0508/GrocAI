package com.example.grocerymate.adapter;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerymate.R;
import com.example.grocerymate.ai.Insight;
import com.example.grocerymate.databinding.ItemAiTipBinding;

import java.util.ArrayList;
import java.util.List;

public class AiTipAdapter extends RecyclerView.Adapter<AiTipAdapter.VH> {

    private final List<Insight> tips = new ArrayList<>();

    public void submit(List<Insight> newTips) {
        tips.clear();
        tips.addAll(newTips);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAiTipBinding b = ItemAiTipBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Insight t = tips.get(position);
        h.b.tvTipEmoji.setText(t.emoji);
        h.b.tvTipMsg.setText(t.message);
        int stroke;
        switch (t.level) {
            case ALERT: stroke = R.color.danger; break;
            case WARN:  stroke = R.color.macro_carbs; break;
            default:    stroke = R.color.green_300; break;
        }
        int color = ContextCompat.getColor(h.itemView.getContext(), stroke);
        h.b.getRoot().getBackground().mutate()
                .setColorFilter(lighten(color), PorterDuff.Mode.SRC_ATOP);
    }

    /** Soft tint so the accent colour stays readable behind dark text. */
    private int lighten(int color) {
        int a = 0x22;
        return (a << 24) | (color & 0x00FFFFFF);
    }

    @Override
    public int getItemCount() {
        return tips.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemAiTipBinding b;
        VH(ItemAiTipBinding b) { super(b.getRoot()); this.b = b; }
    }
}
