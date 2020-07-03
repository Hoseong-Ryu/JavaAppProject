package com.example.javaappproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MarkerItem {
    private File file;
    private ExifInterface exif;

    private double latitude;    // 위도
    private double longitude;   // 경도
    private Date date;
    private String title;
    private String content="";


    public MarkerItem(File file) {
        this.file = file;

        // 위도 경도 설정
        try {
            exif = new ExifInterface(file.getAbsolutePath());
            float[] p = new float[2];
            exif.getLatLong(p);

            this.latitude = p[0];
            this.longitude = p[1];

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 시간, 제목(default: 시간) 설정
        try {

            this.date = new SimpleDateFormat("yyyyMMdd_HHmmss").parse(file.getName().substring(5, file.getName().length()-4));

            this.title = getDateSting();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public BitmapDescriptor getImageBitmapDescritor() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 32;
        Bitmap src = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        return BitmapDescriptorFactory.fromBitmap(src);
    }

    public LatLng getLatLng() { return new LatLng(latitude, longitude); }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private String ConvertTagGPSFormat(double coordinate) {

        if (coordinate < -180.0 || coordinate > 180.0 || Double.isNaN(coordinate)) {
            throw new IllegalArgumentException("coordinate=" + coordinate);
        }

        if (coordinate < 0)
            coordinate *=-1;

        StringBuilder sb = new StringBuilder();


        if (coordinate < 0) {
            sb.append('-');
            coordinate = -coordinate;
        }

        int degrees = (int) Math.floor(coordinate);
        sb.append(degrees);
        sb.append("/1,");
        coordinate -= degrees;
        coordinate *= 60.0;
        int minutes = (int) Math.floor(coordinate);
        sb.append(minutes);
        sb.append("/1,");
        coordinate -= minutes;
        coordinate *= 60.0;
        sb.append(coordinate);
        sb.append("/1000");


        return sb.toString();
    }


    public void setLocation(double latitude, double longitude) {
        // 위도 설정
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, ConvertTagGPSFormat(latitude));
        if(latitude>=0)
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
        else
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
        // 경도 설정
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, ConvertTagGPSFormat(longitude));
        if(longitude>=0)
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
        else
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
        // 저장
        try {
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Date getDate() {
        return date;
    }

    public String getDateSting() {
        return new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초").format(date);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
