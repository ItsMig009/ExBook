package com.example.davidg.exbook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.davidg.exbook.helpers.BottomNavigationViewHelper;
import com.example.davidg.exbook.models.Post;
import com.example.davidg.exbook.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class PostActivity3 extends AppCompatActivity  implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private BottomNavigationView bottomNavigationView;
    private EditText priceEditText;
    private Spinner currencySpinner;
    private Spinner conditionSpinner;
    private Switch negotiableSwitch;
    private Switch freeSwitch;
    private EditText descriptionEditText;
    private Button postButton;
    private FirebaseAuth mAuth;
    //TODO: to be removed
    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END initialize_database_ref]
    private Post post;
    private static final String REQUIRED = "Required"; //TODO: make it @string
    private static final String TAG = "PostActivity_3";

    // [START declare_database_ref]
    protected StorageReference mStorage;// Image storage reference.
    // [END declare_database_ref]

    private Toast toast;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post3);

        // Enable back arrow action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        // Disable shifting mode and displays all titles of navigation options
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        currencySpinner = findViewById(R.id.sp_currency);
        conditionSpinner = findViewById(R.id.sp_condition);
        priceEditText = (EditText) findViewById(R.id.et_price);
        negotiableSwitch = (Switch) findViewById(R.id.sw_negotiable);
        freeSwitch = (Switch) findViewById(R.id.sw_free);
        descriptionEditText = (EditText) findViewById(R.id.et_description);
        postButton = (Button) findViewById(R.id.bt_post_now);

        //populate currency drop-down menu
        populateSpinner(currencySpinner, R.array.currencies);
        //populate currency drop-down menu
        populateSpinner(conditionSpinner, R.array.conditions);

        // Set up listeners
        currencySpinner.setOnItemSelectedListener(this);
        conditionSpinner.setOnItemSelectedListener(this);
        negotiableSwitch.setOnCheckedChangeListener(this);
        freeSwitch.setOnCheckedChangeListener(this);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPost(view);
            }
        });

        post = getIntent().getParcelableExtra("MyPost");

        // [START initialize_database_ref]
        mStorage = FirebaseStorage.getInstance().getReference();
        // [END initialize_database_ref]

        final View activityRootView = findViewById(R.id.post3);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();

                if (heightDiff > 250) {
                    // keyboard is up
                    bottomNavigationView.setVisibility(View.INVISIBLE);
                } else {
                    // keyboard is down
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        String selectedOption = parent.getSelectedItem().toString();
        switch(parent.getId()){
            case R.id.sp_currency:
                post.currency = Post.getCurrency(selectedOption);
                break;
            case R.id.sp_condition:
                post.condition = Post.getCondition(selectedOption);
                break;
            default:
                // unexpected error
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void populateSpinner(Spinner spinner, int resorceId ){

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                resorceId , android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void submitPost(View view) {

        final String price = priceEditText.getText().toString();

        // Description field is optional, so it does not need to be validated
        if(validateFields(price) == false){
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);

        // [START single_value_read]
        final String userId = BaseActivity.getUid();

        //TODO: show proper dialog box instead of a toast message
        if(!userId.equals(post.userId)){
            showToast("User ids are not compatible. Unexpected exception occurred.",Toast.LENGTH_SHORT);
            return;
        }

        mDatabase.child("users");
        mDatabase.child(userId);
        mDatabase.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            showToast("Error: could not fetch user.", Toast.LENGTH_SHORT);
                        } else {
                            // Write new post
                            //user.username is the user who made the post. Un-use for now
                            //First uplodad the photo to get the download url that is going to be used later to get post photos
                            //TODO: replace by loading spinner
                            showToast("Posting...",Toast.LENGTH_LONG);
                            submitPhoto();
                        }
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

//        // go back to main page

        // [END single_value_read]
    }

    //TODO: working on. Fix parameters passed to the database
    // [START write_fan_out]
    private void writeNewPost(String price, String description) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        post.postId = key;
        post.price = Double.parseDouble(price);
        post.description = description;

        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + post.userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isComplete()){
                    showToast("Post Successfully Completed.", Toast.LENGTH_SHORT);
                    // Finish this Activity, back to the stream
                    setEditingEnabled(true);
                    finishAffinity();
                    startActivity(new Intent(PostActivity3.this,MainActivity.class));
                }
                else{
                    showToast("task not completed",Toast.LENGTH_LONG);
                }

            }
        });



    }
    // [END write_fan_out]

    private void submitPhoto(){
        Uri uri = Uri.parse(post.coverPhotoUri);
        final String price = priceEditText.getText().toString();
        final String description = descriptionEditText.getText().toString();

        StorageReference filePath = mStorage.child("uploaded_by_user").child(uri.getLastPathSegment());

        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //set up post download Url here
                post.coverPhotoUrl = taskSnapshot.getDownloadUrl().toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("An error occurred while uploading the image.",Toast.LENGTH_LONG);
                Log.w(TAG,"File could not be uploaded",new Exception());

            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isComplete()){
                    //showToast("Photo Upload Done.",Toast.LENGTH_SHORT);
                    //After phot have finished uploading. Complete writing the post
                    writeNewPost(price, description);
                }else{
                    showToast("Waiting to upload photo. Post info won't be uploaded if you receive this message.",Toast.LENGTH_SHORT);
                }


            }
        });
    }

    private boolean validateFields(String price){
        // price is required
        if (TextUtils.isEmpty(price)) {
            priceEditText.setError(REQUIRED);
            priceEditText.requestFocus();
            return false;
        }
        return true;

    }

    private void setEditingEnabled(boolean enabled) {
        priceEditText.setEnabled(enabled);
        currencySpinner.setEnabled(enabled);
        conditionSpinner.setEnabled(enabled);
        negotiableSwitch.setEnabled(enabled);
        freeSwitch.setEnabled(enabled);
        descriptionEditText.setEnabled(enabled);

        if (enabled) {
            postButton.setVisibility(View.VISIBLE);
        } else {
            postButton.setVisibility(View.GONE);
        }
    }

    //TODO: to be removed
    public void checkLogInStatus(){
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){ //if user is logged on already
            //showLogOut();
        }
        else{
            //showLogIn();
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch(compoundButton.getId()){
            case R.id.sw_negotiable:
                post.negotiable = isChecked;
                break;
            case R.id.sw_free:
                post.free = isChecked;
                break;
            default:
                //unexpected error occurred
        }
    }

    void showToast(String text, int duration)
    {
        if(toast != null)
        {
            toast.cancel();
        }
        toast = Toast.makeText(this, text, duration);
        toast.show();

    }
}
