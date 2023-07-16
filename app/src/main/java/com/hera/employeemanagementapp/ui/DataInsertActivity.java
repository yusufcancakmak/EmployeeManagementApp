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
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DataInsertActivity extends AppCompatActivity {
    private ActivityDataInsertBinding binding;
    private byte[] photoPath;
    private byte[] oldPhotoPath;


    private ActivityResultLauncher<Intent> activityResultContract = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Bundle extras = result.getData().getExtras();
        if (extras != null) {
            Bitmap photoBitmap = (Bitmap) extras.get("data");
            binding.saveImage.setImageBitmap(photoBitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            photoPath = stream.toByteArray();
        }
    });
    private ActivityResultLauncher<String> activityResultContract2 = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultContract.launch(takePictureIntent);

        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent();
                    intent.putExtra(Constans.Key_name, binding.etName.getText().toString());
                    intent.putExtra(Constans.Key_position, binding.etPosition.getText().toString());
                    intent.putExtra(Constans.Key_mail, binding.etMail.getText().toString());
                    intent.putExtra(Constans.Key_phone, binding.etPhone.getText().toString());
                    intent.putExtra(Constans.Key_adress, binding.etAdress.getText().toString());
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
                // Kamera veya galeri seçimi için bir diyalog göster
                AlertDialog.Builder builder = new AlertDialog.Builder(DataInsertActivity.this);
                builder.setTitle("Select Image Source");
                builder.setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                activityResultContract2.launch(Manifest.permission.CAMERA);
                                /* requestCameraPermission();*/
                                break;
                            case 1:
                                // Galeri uygulamasını başlat
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, 1);
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

                Toast.makeText(this, "Kamera izni reddedildi", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 2);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImage = data.getData();
            try {
                Bitmap photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                binding.saveImage.setImageBitmap(photoBitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                photoPath = stream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photoBitmap = (Bitmap) extras.get("data");
                binding.saveImage.setImageBitmap(photoBitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                photoPath = stream.toByteArray();
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DataInsertActivity.this, MainActivity.class));
    }
}
