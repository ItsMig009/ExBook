package com.example.davidg.exbook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.davidg.exbook.helpers.BottomNavigationViewHelper;
import com.example.davidg.exbook.models.Post;

public class PostActivity1 extends AppCompatActivity  {

    private BottomNavigationView bottomNavigationView;
    private Button nextButton;
    private Button barcodeScannerButton;
    private EditText isbnEditText;
    private EditText titleEditText;
    private EditText versionEditText;
    private EditText authorsEditText;
    private static final String REQUIRED = "Required"; //TODO: make it @string
    private static final String TAG = "PostActivity_1";

    private Post post;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post1);

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

        barcodeScannerButton = (Button) findViewById(R.id.bt_barcode_scanner);

        barcodeScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcodeScannerButtonClicked(view);
            }
        });

        isbnEditText = (EditText) findViewById(R.id.et_isbn);
        titleEditText = (EditText) findViewById(R.id.et_book_title);
        versionEditText = (EditText) findViewById(R.id.et_version_number);
        authorsEditText = (EditText) findViewById(R.id.et_authors);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;

    }

    private void nextActivity(View view){
        Context context = view.getContext();
        Class postActivity2 = PostActivity2.class;

        Intent startNextPostActivity = new Intent(context,postActivity2);

        //TODO: to be removed. BEGIGN
        final String userId = BaseActivity.getUid();
        final String isbn = isbnEditText.getText().toString();
        final String title = titleEditText.getText().toString();
        final String version = versionEditText.getText().toString();
        final String authors = authorsEditText.getText().toString(); // maybe parse into String []


        //check that all fields have been completed
        if(validateFields(isbn,title,version,authors) == false){
            return;
        }

        //Populate post object with fields from PostActivity1
        post = new Post();
        post.userId = userId;
        post.isbn = isbn;
        post.title = title;
        post.version = Integer.parseInt(version);
        post.authors = authors;

        //TODO: pass editText info to next activity to submit post in the end
        startNextPostActivity.putExtra("MyPost", post);

        startActivity(startNextPostActivity);
    }

    private void barcodeScannerButtonClicked(View view){
        //TODO: complete
    }

    //TODO: make a class containing all the post fields from Post1, Post2, and Post3 and populate fields accordingly
    private boolean validateFields(String isbn, String title, String version, String authors){
        // isbn is required
        if (TextUtils.isEmpty(isbn)) {
            isbnEditText.setError(REQUIRED);
            return false;
        }

        // title is required
        if (TextUtils.isEmpty(title)) {
            titleEditText.setError(REQUIRED);
            return false;
        }

        // isbn is required
        if (TextUtils.isEmpty(version)) {
            versionEditText.setError(REQUIRED);
            return false;
        }

        // title is required
        if (TextUtils.isEmpty(authors)) {
            authorsEditText.setError(REQUIRED);
            return false;
        }
        return true;

    }


    private void setEditingEnabled(boolean enabled) {
        isbnEditText.setEnabled(enabled);
        titleEditText.setEnabled(enabled);
        versionEditText.setEnabled(enabled);
        authorsEditText.setEnabled(enabled);

        if (enabled) {
            nextButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.GONE);
        }
    }

}
