package com.example.pleasetinder.listeners;

import com.example.pleasetinder.models.User;

public interface ConversionListener {
    void onConversionClicked(User user);
    void onItemClick(Integer position);
}
