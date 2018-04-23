package com.example.android.newsapp;

public class News {
    // all the strings necessary for this class
    private final String newsTitle;
    private final String newsCategory;
    private final String newsAuthor;
    private final String newsDate;
    private final String newsUrl;

    /**
     * Constructs a new object.
     *
     * @param title    the title
     * @param category the specified section
     * @param author   name of the author
     * @param date     date of the news
     * @param url      URL of the news
     */

    public News(String title, String category, String author, String date, String url) {
        newsTitle = title;
        newsCategory = category;
        newsAuthor = author;
        newsDate = date;
        newsUrl = url;
    }

    // all the returned data
    public String getNewsTitle() {
        return newsTitle;
    }
    public String getNewsCategory() {
        return newsCategory;
    }
    public String getNewsAuthor() {
        return newsAuthor;
    }
    public String getNewsDate() {
        return newsDate;
    }
    public String getUrl() {
        return newsUrl;
    }
}