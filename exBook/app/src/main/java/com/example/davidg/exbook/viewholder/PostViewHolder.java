package com.example.davidg.exbook.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.davidg.exbook.R;
import com.example.davidg.exbook.models.Post;

import org.w3c.dom.Text;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView priceView;
    public TextView conditionView;
    public TextView descriptionView;
    public ImageView bookImage;
    public String uid;

    // Author header
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;


    public PostViewHolder(View itemView) {
        super(itemView);

        bookImage = itemView.findViewById(R.id.postImage);
        titleView = itemView.findViewById(R.id.bookTitleView);
        priceView = itemView.findViewById(R.id.priceTextView);
        conditionView = itemView.findViewById(R.id.bookConditionView);
        descriptionView = itemView.findViewById(R.id.bookDescriptionView);

        // Author view
        authorView = itemView.findViewById(R.id.postAuthor);
        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.post_num_stars);
    }

    public void bindToPost(Post post, View.OnClickListener starClickListener) {
        titleView.setText(post.title);
        priceView.setText((int) post.price);
        conditionView.setText(post.condition.toString());
        descriptionView.setText(post.description);


        authorView.setText(post.authors);
        numStarsView.setText(String.valueOf(post.starCount));
        starView.setOnClickListener(starClickListener);
    }
}
