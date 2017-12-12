package com.example.davidg.exbook;

public class ImageUploadInfo {

    public String imageName;

    public String imageURL;

    public String imageUri;

    public String postId;

    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String name, String url, String uri, String postId) {

        this.imageName = name;
        this.imageURL= url;
        this.imageUri = uri;
        this.postId = postId;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getImageUri() { return imageUri; }

    public String getPostId() { return postId; }

}