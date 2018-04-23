package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_main, parent, false);
        }

        News currentItem = getItem(position);
        TextView newsTitle = listItemView.findViewById(R.id.newsTitle);
        assert currentItem != null;
        newsTitle.setText(currentItem.getNewsTitle());

        TextView newsCategoryTextView = listItemView.findViewById(R.id.newsCategory);
        newsCategoryTextView.setText(currentItem.getNewsCategory());

        TextView newsAuthorTextView = listItemView.findViewById(R.id.newsAuthor);
        newsAuthorTextView.setText(currentItem.getNewsAuthor());

        TextView newsDateTextView = listItemView.findViewById(R.id.newsDate);

        SimpleDateFormat dateFormatJSON = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("EE dd MMM yyyy", Locale.ENGLISH);

        // catch the error in case anything unexpected happens
        try {
            Date dateNews = dateFormatJSON.parse(currentItem.getNewsDate());

            String date = dateFormat2.format(dateNews);
            newsDateTextView.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return listItemView;
    }
}
