package com.example.davidg.exbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post implements Parcelable{

    public String userId;
    public String isbn;
    public String title;
    public int version;
    public String authors;
    public double price;
    public Currency currency;
    public boolean negotiable;
    public boolean free;
    public Condition condition;
    public String description;
    //TODO: add cover picture field

    //public int rating = 0;
    //public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        currency = Currency.USD;
        condition = Condition.OTHER;
    }

    public Post(Parcel in){
        String []data =  new String[11];

        in.readStringArray(data);
        userId = data[0];
        isbn = data[1];
        title = data[2];
        version = Integer.parseInt(data[3]);
        authors = data[4];
        price = Double.parseDouble(data[5]);
        currency = Currency.valueOf(data[6]);
        negotiable = Boolean.parseBoolean(data[7]);
        free = Boolean.parseBoolean(data[8]);
        condition = Condition.valueOf(data[9]);
        description = data[10];

    }

//    public Post(String userId, String isbn, String title, int version, String authors) {
//        this.userId = userId;
//        this.isbn = isbn;
//        this.title = title;
//        this.version = version;
//        this.authors = authors;
//    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("isbn", isbn);
        result.put("title", title);
        result.put("version", version);
        result.put("authors", authors);
        result.put("price",price);
        result.put("currency",currency.name());
        result.put("negotiable",Boolean.toString(negotiable));
        result.put("free",Boolean.toString(free));
        result.put("condition",condition.name());
        result.put("description",description);
//        result.put("stars", stars);

        return result;
    }
    // [END post_to_map]

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        String []postFields = new String[]{
                userId,
                isbn,
                title,
                Integer.toString(version),
                authors,
                Double.toString(price),
                currency.name(),
                Boolean.toString(negotiable),
                Boolean.toString(free),
                condition.name(),
                description
        };
        parcel.writeStringArray(postFields);
    }

    public static final Parcelable.Creator<Post> CREATOR= new Parcelable.Creator<Post>() {

        @Override
        public Post createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new Post(source);  //using parcelable constructor
        }

        @Override
        public Post[] newArray(int size) {
            // TODO Auto-generated method stub
            return new Post[size];
        }
    };




    public enum Currency{
        USD,
        EUR
    }

    public enum Condition{
        OTHER,
        DAMAGED,
        SOME_TEAR,
        USED,
        LIKE_NEW,
        NEW
    }

    //TODO: unhardcode string cases in switch stamt and values.array.xml
    public static Currency getCurrency(String currency){
        switch(currency){
            case "EUR":
                return Currency.EUR;
            default:
                return Currency.USD;
        }
    }

    //TODO: unhardcode string cases in switch stamt and values.array.xml
    public static Condition getCondition(String condition){
        switch(condition){
            case "Damaged (pages missing)":
                return Condition.DAMAGED;
            case "Some Tear (highlighted, cover scratches)":
                return Condition.SOME_TEAR;
            case "Used (normal wear)":
                return Condition.USED;
            case "Like New":
                return Condition.LIKE_NEW;
            case "New":
                return Condition.NEW;
            default:
                return Condition.OTHER;
        }
    }

}
// [END post_class]
