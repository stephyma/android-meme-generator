package com.example.stephyma.memegenerator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageView mImageView;
    EditText etTopText;
    EditText etBottomText;
    String mCurrentImagePath = "";
    private static final int REQUEST_CODE_TAKE_PICTURE = 100;
    private static final int REQUEST_CODE_CHOOSE_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTakePicture = (Button)findViewById(R.id.btn_take_picture);
        Button btnChoosePicture = (Button) findViewById(R.id.btn_choose_picture);
        Button btnGenerateMeme = (Button) findViewById(R.id.btn_generate_meme);
        etTopText = (EditText) findViewById(R.id.et_top_text);
        etBottomText = (EditText) findViewById(R.id.et_bottom_text);
        mImageView = (ImageView) findViewById(R.id.iv_picture);

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    verifyPermissionsForTakingPicture();
                } else {
                    takeAPicture();
                }
            }
        });

        btnChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    verifyPermissionsForChoosingPicture();
                } else {
                    chooseAPicture();
                }
            }
        });

        btnGenerateMeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MemeResultActivity.class);
                final String topText = etTopText.getText().toString();
                final String bottomText = etBottomText.getText().toString();
                intent.putExtra("topText", topText);
                intent.putExtra("bottomText", bottomText);
                intent.putExtra("imagePath", mCurrentImagePath);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    mImageView.setImageURI(Uri.parse(mCurrentImagePath));
                }
                break;
            case REQUEST_CODE_CHOOSE_PICTURE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    Toast.makeText(getApplicationContext(), selectedImage.toString(), Toast.LENGTH_LONG).show();
                    mImageView.setImageURI(selectedImage);
                    mCurrentImagePath = selectedImage.toString();
                }
                break;
        }
    }

    private void takeAPicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Ensure there'a camera activity to handle intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";

        // Get device's Picture directory
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentImagePath = "file:" + imageFile.getAbsolutePath();

        return imageFile;
    }

    private void chooseAPicture() {
        Intent choosePictureIntent = new Intent(Intent.ACTION_PICK);
        choosePictureIntent.setType("image/*");
        startActivityForResult(choosePictureIntent, REQUEST_CODE_CHOOSE_PICTURE);
    }

    private static final int PERMISSION_REQUEST_TAKE_PICTURE = 1;
    private static final int PERMISSION_REQUEST_CHOOSE_PICTURE = 2;
    private static String[] TAKE_PICTURE_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String[] CHOOSE_PICTURE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private void verifyPermissionsForTakingPicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    TAKE_PICTURE_PERMISSIONS,
                    PERMISSION_REQUEST_TAKE_PICTURE
            );
        } else {
            takeAPicture();
        }
    }

    private void verifyPermissionsForChoosingPicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    CHOOSE_PICTURE_PERMISSIONS,
                    PERMISSION_REQUEST_CHOOSE_PICTURE
            );
        } else {
            chooseAPicture();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_TAKE_PICTURE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    takeAPicture();
                }
                break;
            }
            case PERMISSION_REQUEST_CHOOSE_PICTURE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    chooseAPicture();
                }
                break;
            }
        }
    }
}