package com.example.davidg.exbook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.davidg.exbook.models.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {

    private final ListingAdapterOnClickHandler mClickHandler;
    Context context;
    List<Post> MainImageUploadInfoList;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListingAdapterOnClickHandler {
        void onClick(Post clickedPost);
    }


    /**
     * Creates a ListingAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
//    public ListingAdapter(ListingAdapterOnClickHandler clickHandler) {
//        mClickHandler = clickHandler;
//    }

    public ListingAdapter(Context context, ListingAdapterOnClickHandler clickHandler, List<Post> TempList) {

        this.MainImageUploadInfoList = TempList;
        this.mClickHandler = clickHandler;
        this.context = context;

    }


    /**
     * Cache of the children views for a forecast list item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public ImageView imageView;
        public TextView imageNameTextView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageNameTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView);
            view.setOnClickListener(this);
        }
        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Post post = MainImageUploadInfoList.get(adapterPosition);
            mClickHandler.onClick(post);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ViewHolder that holds the View for each list item
     */


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recyclerview_items;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ViewHolder(view);
    }


/*

    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }
 */

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = MainImageUploadInfoList.get(position);
        Picasso.with(context).load(post.coverPhotoUrl).into(holder.imageView);
        holder.imageNameTextView.setText(post.title.trim());
        //Loading image from Glide library.
        //Glide.with(context).load(UploadInfo.getImageURL()).into(holder.imageView);
        //Picasso.with(context).load(UploadInfo.getImageUri()).fit().centerInside().into(holder.imageView);

    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        if (null == MainImageUploadInfoList) return 0;
        return MainImageUploadInfoList.size();
    }

    /**
     * This method is used to set the weather forecast on a ForecastAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * @param posts The new weather data to be displayed.
     */
    //TODO: create a method to add new images to the adapter list
    public void setPostData(List<Post> posts) {
        for (Post post: posts) {
            MainImageUploadInfoList.add(post);
        }
        notifyDataSetChanged();
    }
}