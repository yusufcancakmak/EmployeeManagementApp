package com.hera.employeemanagementapp.ui;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import com.hera.employeemanagementapp.databinding.ActivityDataInsertBinding;
import com.hera.employeemanagementapp.helper.Constans;
import com.hera.employeemanagementapp.local.EmployyeDao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DataInsertActivity extends AppCompatActivity {
    private ActivityDataInsertBinding binding;
    private byte[] photoPath;
    private byte[] oldPhotoPath;

    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
            Uri selectedImage = result.getData().getData();
            try {
                Bitmap photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                compressAndSetImage(photoBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
        }
    });

    private ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Bundle extras = result.getData().getExtras();
            if (extras != null) {
                Bitmap photoBitmap = (Bitmap) extras.get("data");
                compressAndSetImage(photoBitmap);
            }
        } else {
            Toast.makeText(this, "Camera closed", Toast.LENGTH_SHORT).show();
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDataInsertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String type = getIntent().getStringExtra(Constans.Key_type);
        if (type.equals("update")) {
            binding.saveBtn.setText("Update");
            binding.etName.setText(getIntent().getStringExtra(Constans.Key_name));
            binding.etPosition.setText(getIntent().getStringExtra(Constans.Key_position));
            binding.etMail.setText(getIntent().getStringExtra(Constans.Key_mail));
            binding.etPhone.setText(getIntent().getStringExtra(Constans.Key_phone));
            byte[] photo = getIntent().getByteArrayExtra(Constans.Key_image);
            binding.etAdress.setText(getIntent().getStringExtra(Constans.Key_adress));
            if (photo != null) {
                Bitmap photoBitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                binding.saveImage.setImageBitmap(photoBitmap);
                oldPhotoPath = photo.clone();
            }

            int id = getIntent().getIntExtra(Constans.Key_id, 0);
            binding.saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra(Constans.Key_name, binding.etName.getText().toString());
                    intent.putExtra(Constans.Key_position, binding.etPosition.getText().toString());
                    intent.putExtra(Constans.Key_mail, binding.etMail.getText().toString());
                    intent.putExtra(Constans.Key_phone, binding.etPhone.getText().toString());
                    intent.putExtra(Constans.Key_adress, binding.etAdress.getText().toString());

                    if (photoPath != null) {
                        intent.putExtra(Constans.Key_image, photoPath);
                    } else {
                        intent.putExtra(Constans.Key_image, oldPhotoPath);
                    }

                    intent.putExtra(Constans.Key_id, id);
                    setResult(RESULT_OK, intent);
                    finish();

                }
            });
        } else {
            binding.saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = binding.etName.getText().toString().trim();
                    Intent intent = new Intent();
                    intent.putExtra(Constans.Key_name, binding.etName.getText().toString());
                    intent.putExtra(Constans.Key_position, binding.etPosition.getText().toString());
                    intent.putExtra(Constans.Key_mail, binding.etMail.getText().toString());
                    intent.putExtra(Constans.Key_phone, binding.etPhone.getText().toString());
                    intent.putExtra(Constans.Key_adress, binding.etAdress.getText().toString());

                    if (name.isEmpty()) {
                        Toast.makeText(DataInsertActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (photoPath != null) {
                        intent.putExtra(Constans.Key_image, photoPath);
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });


        }

        binding.saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DataInsertActivity.this);
                builder.setTitle("Select Image Source");
                builder.setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                if (ContextCompat.checkSelfPermission(DataInsertActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    cameraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
                                } else {
                                    ActivityCompat.requestPermissions(DataInsertActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                                }

                                /* requestCameraPermission();*/
                                break;
                            case 1:
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                /*startActivityForResult(intent, 1);*/
                                galleryLauncher.launch(intent);
                                break;
                        }
                    }
                });
                builder.show();
            }
        });


    }

    private static final int REQUEST_CAMERA_PERMISSION = 100;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                openCamera();
            } else {

                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 2);
        }
    }
    private void compressAndSetImage(Bitmap photoBitmap) {
        int quality = 50;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        photoPath = stream.toByteArray();
        binding.saveImage.setImageBitmap(photoBitmap);

    }






    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DataInsertActivity.this, MainActivity.class));
    }
}
