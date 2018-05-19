package com.example.android.quickdoc.dataClasses;

/**
 * Created by Denis on 22/04/2018.
 */

public class UserReview {

    private String reviewTitle;
    private String reviewText;
    private String date;
    private int time;
    private float reviewGrade;

    public UserReview(String reviewTitle, String reviewText, String date, int time, float reviewGrade) {
        this.reviewTitle = reviewTitle;
        this.reviewText = reviewText;
        this.date = date;
        this.time = time;
        this.reviewGrade = reviewGrade;
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

    public float getReviewGrade() {
        return reviewGrade;
    }

}
