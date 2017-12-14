package com.example.davidg.exbook;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.example.davidg.exbook.helpers.BottomNavigationViewHelper;
import com.example.davidg.exbook.models.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostActivity2 extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Button nextButton;
    private ImageButton selectPhotoImgButton;
    private ImageButton takePhotoImgButton;
    private ImageButton coverPhotoImgButton;
    private Post post;
    private static final int GALLERY_INTENT = 2;
    private static final String TAG = "PostActivity_2";

    //TODO: to be removed START

    protected StorageReference mStorage;// Image storage reference.

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    //TODO: to be removed END

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post2);

        // Enable back arrow action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Disable shifting mode and displays all titles of navigation options
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        nextButton = (Button) findViewById(R.id.bt_post_next);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(post.coverPhotoUri == null || post.coverPhotoUri.isEmpty()){ // get the photo before continuing
                    return;
                }
                nextActivity(view);
            }
        });

        selectPhotoImgButton = findViewById(R.id.ib_select_photo);
        takePhotoImgButton = findViewById(R.id.ib_take_photo);
        coverPhotoImgButton = findViewById(R.id.ib_cover_image);

        post = getIntent().getParcelableExtra("MyPost");

        selectPhotoImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);

                intent.setType("image/*");

                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        //TODO: to be removed START

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        // [START initialize_database_ref]
        mStorage = FirebaseStorage.getInstance().getReference();
        // [END initialize_database_ref]

        //TODO: to be removed END


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;

    }

    private void nextActivity(View view){
        Context context = view.getContext();
        Class postActivity3 = PostActivity3.class;

        Intent startNextPostActivity = new Intent(context,postActivity3);

        startNextPostActivity.putExtra("MyPost",post);

        //TODO: pass editText info to next activity to submit post in the end

        startActivity(startNextPostActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){

            Uri uri = data.getData();
            Glide.with(this).load(uri).fitCenter().into(coverPhotoImgButton);
            //Picasso.with(this).load(uri).fit().centerInside().into(coverPhotoImgButton);
            coverPhotoImgButton.setBackground(null);
            post.coverPhotoUri = uri.toString();

        }
    }
}
