package com.example.javaappproject;

import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.File;
import java.io.IOException;

import static android.graphics.Bitmap.createScaledBitmap;

public class MarkerItem {
    private double latitude;    // 위도
    private double longitude;   // 경도
    private File file;
    private int width = 120;
    private int height = 180;

    public MarkerItem(File file) {
        this.file = file;

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file);
            float[] p = new float[2];
            exif.getLatLong(p);

            this.latitude = p[0];
            this.longitude = p[1];

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MarkerItem(File file, int width, int height) {
        this(file);
        setImageScale(width, height);
    }

    public void setImageScale(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public BitmapDescriptor getImageBitmapDescritor() {
        return BitmapDescriptorFactory.fromBitmap(createScaledBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()), width, height, false));
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
