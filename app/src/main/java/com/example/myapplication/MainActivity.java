package com.example.myapplication;

import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.Login;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

//    private Button uploadPhoto, selectPhoto;
    private Button detectEdge;
    private ImageView imageViewer;
    private Uri filepath;
    private Uri filepath2;
    private final int PICK_IMAGE_REQUEST = 22; // from geeksforgeeks to create a request code

    private Bitmap bitmap;
    private Bitmap bitmap2;

    FirebaseStorage storage;
    StorageReference storageReference;


//    private Button clickMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        clickMe = (Button) findViewById(R.id.click);

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

//        selectPhoto = findViewById(R.id.select);
//        uploadPhoto = findViewById(R.id.upload);
        imageViewer = findViewById(R.id.imgView);
        detectEdge = findViewById(R.id.detectEdge);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        binding.select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(
                                intent,
                                "Select Image from here..."),
                        PICK_IMAGE_REQUEST);
            }
        });

        detectEdge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filepath != null){
                    bitmap2 = bitmap.copy(Bitmap.Config.ARGB_8888,true);
                    int imageWidth = bitmap2.getWidth();
                    int imageHeight = bitmap2.getHeight();
                    double[][] grayMap = grayscale(bitmap2);
                    double[][] gradients = gradient(grayMap);
                    for(int i = 0; i < imageWidth; i++){
                        for(int j = 0; j < imageHeight; j++){
                            if(gradients[i][j] > 5) {
                                bitmap2.setPixel(i, j, Color.rgb(45, 127, 0));
                            }
                        }
                    }
                    imageViewer.setImageBitmap(bitmap2);
                    Snackbar.make(view, "Trace Successful" + Integer.toString(bitmap.getHeight()), Snackbar.LENGTH_LONG).
                            setAction("Action", null).show();
                }
//                Snackbar.make(view, "Detecting Edges!", Snackbar.LENGTH_LONG).
//                        setAction("Action", null).show();
            }
        });

        binding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filepath != null) {

                    // Code for showing progressDialog while uploading
                    ProgressDialog progressDialog
                            = new ProgressDialog(MainActivity.this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                    // Defining the child of storageReference
                    StorageReference ref
                            = storageReference
                            .child(
                                    "images/"
                                            + UUID.randomUUID().toString());

                    // adding listeners on upload
                    // or failure of image
                    ref.putFile(filepath)
                            .addOnSuccessListener(
                                    new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                        @Override
                                        public void onSuccess(
                                                UploadTask.TaskSnapshot taskSnapshot)
                                        {

                                            // Image uploaded successfully
                                            // Dismiss dialog
                                            progressDialog.dismiss();
                                            Toast
                                                    .makeText(MainActivity.this,
                                                            "Image Uploaded!!",
                                                            Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {

                                    // Error, Image not uploaded
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(MainActivity.this,
                                                    "Failed " + e.getMessage(),
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })
                            .addOnProgressListener(
                                    new OnProgressListener<UploadTask.TaskSnapshot>() {

                                        // Progress Listener for loading
                                        // percentage on the dialog box
                                        @Override
                                        public void onProgress(
                                                UploadTask.TaskSnapshot taskSnapshot)
                                        {
                                            double progress
                                                    = (100.0
                                                    * taskSnapshot.getBytesTransferred()
                                                    / taskSnapshot.getTotalByteCount());
                                            progressDialog.setMessage(
                                                    "Uploaded "
                                                            + (int)progress + "%");
                                        }
                                    });
                }
            }
        });

//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference("message");
//                myRef.setValue("Hello, World!");
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filepath = data.getData();
            try {

                // Setting image on image view using Bitmap
                bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filepath);
                imageViewer.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.welcome) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public double[][] grayscale(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        double array[][] = new double[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                int redComp;
                int greenComp;
                int blueComp;
                int color;
                color = bitmap.getPixel(i, j);
                redComp = red(color);
                greenComp = green(color);
                blueComp = blue(color);
                array[i][j] = 0.3 * redComp + 0.6 * greenComp + 0.1 * blueComp;
            }
        }
        return array;
    }

    public double[][] gradient(double[][] grayScaled){
        int width = grayScaled.length;
        int height = grayScaled[0].length;
        double magnitudes[][] = new double[width][height];
        for(int i = 0; i < width - 1; i++){
            for(int j = 0; j < height - 1; j++){
                double dx = grayScaled[i + 1][j] - grayScaled[i][j];
                double dy = grayScaled[i][j + 1] - grayScaled[i][j];
                magnitudes[i][j] = sqrt(pow(dx, 2) + pow(dy, 2));
            }
        }
        return magnitudes;
    }
}