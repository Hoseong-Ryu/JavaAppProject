package com.example.javaappproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.graphics.Bitmap.createScaledBitmap;

public class MarkerItem {
    private File file;

    private double latitude;    // 위도
    private double longitude;   // 경도
    private Date date;
    private String title;
    private String content;


    public MarkerItem(File file) {
        this.file = file;

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file.getAbsolutePath());
            float[] p = new float[2];
            exif.getLatLong(p);

            this.latitude = p[0];
            this.longitude = p[1];

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            date = new SimpleDateFormat("yyyyMMdd_HHmmss").parse(file.getName().substring(5, file.getName().length()-4));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public BitmapDescriptor getImageBitmapDescritor() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 32;
        Bitmap src = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        return BitmapDescriptorFactory.fromBitmap(src);
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

    public Date getDate() {
        return date;
    }

    public String getDateSting() {
        return new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초").format(date);
    }
}
