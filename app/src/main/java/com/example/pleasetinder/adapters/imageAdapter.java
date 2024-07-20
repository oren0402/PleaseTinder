package com.example.pleasetinder.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pleasetinder.R;
import com.example.pleasetinder.listeners.ConversionListener;
import com.example.pleasetinder.listeners.OnItemClickListener;
import com.example.pleasetinder.ui.home.HomeFragment;

import java.util.List;

public class imageAdapter extends RecyclerView.Adapter<imageAdapter.MyViewHolder> {

    private List<Integer> imageResIds;
    private static ConversionListener listener;

    public imageAdapter(List<Integer> imageResIds, ConversionListener listener) {
        this.imageResIds = imageResIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_container, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int imageResId = imageResIds.get(position);
        holder.imageView.setImageResource(imageResId);
    }

    @Override
    public int getItemCount() {
        return imageResIds.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageAdd);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    listener.onItemClick(position);

                }
            });
        }
    }
}
