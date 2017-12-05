package com.google.firebase.quickstart.database;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.quickstart.database.models.Post;
import com.google.firebase.quickstart.database.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NewPostActivity extends BaseActivity {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    private static final int MAX_LENGTH = 12;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 10 ;

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    // [START declare_database_ref]
    protected StorageReference mStorage;// Image storage reference.


    private EditText mTitleField;
    private EditText mBodyField;
    //protected Button mUpload; // Button for Image
    private ImageButton mSelectImage;

    private FloatingActionButton mSubmitButton;

    private ProgressDialog mProgressDialog;

    private Uri imageURI = null;
    //private Uri tempUriCameraDeviceNotSuported = null;
    private Uri downloadURL = null;

    // TODO:
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int CAMERA_REQUEST_CODE = 1;

    private static final String randomImageName = random();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mStorage = FirebaseStorage.getInstance().getReference();

        mTitleField = findViewById(R.id.field_title);
        mBodyField = findViewById(R.id.field_body);
        mSubmitButton = findViewById(R.id.fab_submit_post);
        mSelectImage = (ImageButton) findViewById(R.id.selectImage);
        mProgressDialog = new ProgressDialog(this);




        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check permissions at runtime
                setPermissionCheck();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager()) != null) {

                    ContentValues values = new ContentValues(1);

                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");

                    imageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    startActivityForResult(intent, CAMERA_REQUEST_CODE);

                }
                //Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

//                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                startActivityForResult(takePicture, CAMERA_REQUEST_CODE);
//
//


//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//
//                intent.setType("image/*");
//
//                startActivityForResult(intent, GALLERY_REQUEST_CODE);



            }
        });


        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }

    private void setPermissionCheck() {

        int permissionCheck = ContextCompat.checkSelfPermission(NewPostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(NewPostActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);

                // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL is an
                // app-defined int constant. The callback method gets the
                // result of the request.

            }



    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {







                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    return;
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void submitPost() {


        mProgressDialog.setMessage("Uploading Image ...");
        mProgressDialog.show();
        final String title = mTitleField.getText().toString();
        final String body = mBodyField.getText().toString();
        // Add Book required fields (XML screen before adding this fields)

        // Title is required
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED);
            return;
        }

        // TODO: Image check for empty uri done
        if (imageURI != null){

            //randomImageName = random();
            final String userId = getUid();
            final StorageReference filePath = mStorage.child("Exbook").child(randomImageName);


            filePath.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // TODO: Add download URl to UserPost and Posts for image retrieval
                    downloadURL = taskSnapshot.getDownloadUrl();
                    // Please add to user and user posts
                    Toast.makeText(NewPostActivity.this, "Upload Done.",Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG,"File could not be uploaded",new Exception());

                }
            });
        }else {
            Log.w(TAG,"imageUri is Null",new Exception());
        }


        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        // TODO: Authenticating User done
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, user.username, title, body);
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
        // [END single_value_read]



        // IMAGE to DATABASE
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //imageURI = data.getData();
        mSelectImage.setImageURI(imageURI);

    }

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String username, String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey(); //
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
    // [END write_fan_out]

    // START 

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog1() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(NewPostActivity.this);
            mProgressDialog.setMessage("Loading. Please wait...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }


}
