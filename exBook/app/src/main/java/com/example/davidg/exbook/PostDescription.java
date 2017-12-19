package com.example.davidg.exbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.davidg.exbook.models.Post;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class PostDescription extends AppCompatActivity {

    private Post post;
    private static final String TAG = "ViewPostActivit";

    public static final String EXTRA_POST_KEY = "post_key";

    private StorageReference mPostReference;

    private ValueEventListener mPostListener;

    private TextView mTitleView;
    private TextView mPriceView;
    private TextView mConditionView;
    private TextView mISBNView;
    private TextView mEditionView;
    private TextView mDescriptionView;
    private TextView mAuthorsView;
    private ImageView bookPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_description);

        // Enable back arrow action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        post = getIntent().getParcelableExtra("PostDescription");
        if (post == null) {
            throw new IllegalArgumentException("Post info not found");
        }

        //TODO: what happens if the title is too long?
        setTitle(post.title.trim());

        // Initialize Views
        mTitleView = (TextView) findViewById(R.id.tv_book_title);
        mPriceView = (TextView) findViewById(R.id.tv_book_price);
        mConditionView = (TextView) findViewById(R.id.tv_book_condition);
        mISBNView = (TextView) findViewById(R.id.tv_book_isbn);
        mEditionView = (TextView) findViewById(R.id.tv_book_version);
        mDescriptionView = (TextView) findViewById(R.id.tv_book_description);
        mAuthorsView = (TextView) findViewById(R.id.tv_book_authors);
        bookPhoto = (ImageView) findViewById(R.id.postImage);

        mTitleView.setText(post.title);
        if(post.currency == Post.Currency.USD){
            mPriceView.setText("$ "+Double.toString(post.price));
        }else{
            mPriceView.setText(Double.toString(post.price));
        }

        mConditionView.setText(Post.getCondition(post.condition));
        mISBNView.setText(post.isbn);
        mEditionView.setText(Integer.toString(post.version));
        mAuthorsView.setText(post.authors);
        if(post.description.isEmpty()){
            mDescriptionView.setText("No Description.");
        }else{
            mDescriptionView.setText(post.description);
        }


        Glide.with(this).load(post.coverPhotoUrl).fitCenter().into(bookPhoto);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        finishAffinity();
        startActivity(new Intent(this,MainActivity.class));
        return true;

    }
}
