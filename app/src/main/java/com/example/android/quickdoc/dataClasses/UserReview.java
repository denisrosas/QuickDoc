package com.example.android.quickdoc.dataClasses;

/**
 * Created by Denis on 22/04/2018.
 */

public class UserReview {

    private String reviewTitle;
    private String reviewText;
    private String date;
    private int time;
    private float review;

    public UserReview(String reviewTitle, String reviewText, String date, int time) {
        this.reviewTitle = reviewTitle;
        this.reviewText = reviewText;
        this.date = date;
        this.time = time;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public String getReviewText() {
        return reviewText;
    }

    public String getDate() {
        return date;
    }

    public int getTime() {
        return time;
    }
}
