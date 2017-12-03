package com.example.davidg.exbook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.davidg.exbook.helpers.BottomNavigationViewHelper;
import com.example.davidg.exbook.models.Post;

public class PostActivity2 extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Button nextButton;
    private ImageButton selectPhotoImgButton;
    private ImageButton takePhotoImgButton;
    private ImageButton coverPhotoImgButton;
    private Post post;

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
                nextActivity(view);
            }
        });

        selectPhotoImgButton = findViewById(R.id.ib_select_photo);
        takePhotoImgButton = findViewById(R.id.ib_take_photo);
        coverPhotoImgButton = findViewById(R.id.ib_cover_image);

        post = getIntent().getParcelableExtra("MyPost");
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
}
