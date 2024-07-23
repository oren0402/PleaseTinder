package com.example.pleasetinder;

import android.graphics.Bitmap;
import android.net.Uri;

import java.net.URI;

public class ItemModel {
    private Bitmap image;
    private String nama,usia, kota;

    public ItemModel() {

    }

    public ItemModel(Bitmap image, String nama, String usia, String kota) {
        this.image = image;
        this.nama = nama;
        this.usia = usia;
        this.kota = kota;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getNama() {
        return nama;
    }

    public String getUsia() {
        return usia;
    }

    public String getKota() {
        return kota;
    }
}
