package com.example.javaappproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    final String TAG = getClass().getSimpleName();
    Button btnCamera;
    final static int TAKE_PICTURE = 1;

    String CurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    ArrayList<MarkerItem> markerItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region 레이아웃 연결
        btnCamera = findViewById(R.id.btnCamera);
        //endregion

        //region 구글맵 로드
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //endregion

        //region
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        //endreion

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        //ImgLoad();
    }

    private void ImgLoad() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.javaappproject/files/Pictures";
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i=0; i< files.length; i++) {
            addMarker(files[i]);
        }
        Log.d("filesNameList", "ImgPath: "+path);

        //File file = new File(CurrentPhotoPath);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case TAKE_PICTURE:
                try {
                    switch (requestCode) {
                        case REQUEST_TAKE_PHOTO: {
                            if (resultCode == RESULT_OK) {
                                File file = new File(CurrentPhotoPath);
                                addMarker(file);
                            }
                            break;
                        }
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                }
                break;
        }
    }


    private void addMarker(File file) {
        final MarkerItem item = new MarkerItem(file);
        markerItems.add(item);

        //region    위치 강제 설정 기능 (완성후 activity_setlocation.xml과 같이 삭제)
        final View dialogView = View.inflate(MainActivity.this, R.layout.activity_set_location, null);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("위치 설정")
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("완료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            item.setLocation(Double.parseDouble(((EditText)dialogView.findViewById(R.id.editTextLatitude)).getText().toString()), Double.parseDouble(((EditText)dialogView.findViewById(R.id.editTextLongitude)).getText().toString()));
                        } catch (ParseException e) {  }


                        addMarkerMethod(item);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addMarkerMethod(item);
                    }
                })
                .show();
        //endregion

        //addMarkerMethod(item);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 변수 선언
                View detailView = View.inflate(MainActivity.this, R.layout.activity_picture_detail, null);
                ImageView picture;
                final TextView title, date, location, content;
                int i;


                // 레이아웃 연결
                picture = detailView.findViewById(R.id.imageViewPicture);
                title = detailView.findViewById(R.id.textViewTitle);
                date = detailView.findViewById(R.id.textViewDate);
                location = detailView.findViewById(R.id.textViewLocation);
                content = detailView.findViewById(R.id.textViewContent);

                // 선택한 marker의 MarkerItem 찾기
                for (i = 0; i<markerItems.size(); i++) {
                    if(marker.getPosition().equals(markerItems.get(i).getLatLng())) {
                        break;
                    }
                }
                if(i<markerItems.size()) {
                    final MarkerItem item = markerItems.get(i);
                }
                else {
                    Log.e(TAG, "marker를 찾지 못함");
                    return true;
                }


                // 레이아웃 세팅
                picture.setImageURI(Uri.fromFile(item.getFile()));
                title.setText(item.getTitle());
                date.setText(item.getDateSting());
                location.setText(item.getLatitude()+", "+item.getLongitude());
                if(item.getContent().equals("")) {
                    content.setText("내용을 입력해 주세요");
                } else {
                    content.setText(item.getContent());
                }

                final EditText editText = new EditText(MainActivity.this);
                title.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("제목 수정")
                                .setMessage("현재 제목 : " + title.getText())
                                .setView(editText)
                                .setPositiveButton("완료", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        item.setTitle(editText.getText().toString());
                                        title.setText(item.getTitle());
                                    }
                                })
                                .setNegativeButton("취소", null)
                                .show();

                        return true;
                    }
                });

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("상세 정보")
                        .setView(detailView)
                        .show();
                return true;
            }
        });
    }

    // 완성할때 이 함수의 코드를 위의 주석에 대입한다.
    private void addMarkerMethod(MarkerItem item) {
        mMap.addMarker(new MarkerOptions()
                .position(item.getLatLng())
                .icon(item.getImageBitmapDescritor())
                .title(item.getDateSting())
        );
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.javaappproject.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        CurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}