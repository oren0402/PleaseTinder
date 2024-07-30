package com.example.pleasetinder.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pleasetinder.R;

import java.util.ArrayList;
import java.util.List;

public class recyclerViewSelection extends RecyclerView.Adapter<recyclerViewSelection.MyViewHolder> {
    private List<String> strings;

    public recyclerViewSelection(List<String> strings) {
        this.strings = strings;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);

        }
    }

    @NonNull
    @Override
    public recyclerViewSelection.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_strings_home, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull recyclerViewSelection.MyViewHolder holder, int position) {
        String name = strings.get(position);
        holder.textView.setText(name);
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }
}
