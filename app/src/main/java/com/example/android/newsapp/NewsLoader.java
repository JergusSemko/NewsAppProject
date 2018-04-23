package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String url;

    /**
     * @param context of the activity
     * @param url     url for
     */
    public NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (url == null) {
            return null;
        }

        // the network request and response
        List<News> News = null;
        try {
            News = NewsUtils.fetchNewsData(url);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return News;
    }
}