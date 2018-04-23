package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<com.example.android.newsapp.News>> {

    private static final String GUARDIAN_REQUEST_URL = "http://content.guardianapis.com/search?";
    private static final String API_KEY = "api-key";
    private static final String KEY = "f1dfc1ea-9071-49cc-b586-005ed71ac92c";
    private static final String ORDER = "order-by";
    private static final String DATE = "newest";
    private static final String TAGS = "show-tags";
    private static final String AUTHOR = "contributor";
    private static final String PAGE = "page-size";
    private static final String PAGES = "15";
    private static final String SECTION = "section";
    private static final int NEWS_LOADER_ID = 1;
    SwipeRefreshLayout swipeRefreshLayout;
    private NewsAdapter newsAdapter;
    private String displayedMessage;
    private TextView EmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);

        ListView newsListView = findViewById(R.id.newsList);

        // no news
        EmptyTextView = findViewById(R.id.noNews);
        newsListView.setEmptyView(EmptyTextView);

        newsAdapter = new NewsAdapter(this, new ArrayList<News>());
        newsListView.setAdapter(newsAdapter);

        // swipe to refresh news
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                restartLoader();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // set onItemClick listener and redirect to specified web news
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                News clickedNews = newsAdapter.getItem(position);
                assert clickedNews != null;
                Uri newsURI = Uri.parse(clickedNews.getUrl());
                Intent webNewsIntent = new Intent(Intent.ACTION_VIEW, newsURI);

                // in case there is no available browser, the app will display this toast message
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(webNewsIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);

                boolean isIntentSafe = activities.size() > 0;
                if (isIntentSafe) {
                    startActivity(webNewsIntent);
                } else {
                    String message = getString(R.string.no_browser);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });

        // check the network connectivity
        ConnectivityManager connectivityMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityMgr != null;

        NetworkInfo networkInfo = connectivityMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_info);
            loadingIndicator.setVisibility(View.GONE);

            // display this message in case of no connection
            displayedMessage = (String) getText(R.string.no_connection);
            warningMessage(displayedMessage);
        }
    }

    // a loader will be created according to the URL request
    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String searchCategory = sharedPreferences.getString(
                getString(R.string.chosen_cat1),
                getString(R.string.everything));

        String[] categorySearch = searchCategory.split(" ");
        String catInCapitals = categorySearch[0];
        String category = catInCapitals.toLowerCase();

        // create a URI
        Uri uri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = uri.buildUpon();

        // search parameters for URL
        if (!category.equals("all")) {
            uriBuilder.appendQueryParameter(SECTION, category);
        }
        if (category.equals(getString(R.string.usChosen))) {
            category = category + getString(R.string.defaultTag);
            uriBuilder.appendQueryParameter(SECTION, category);
        }
         if (category.equals(getString(R.string.ukChosen))) {
            category = category + getString(R.string.defaultTag);
            uriBuilder.appendQueryParameter(SECTION, category);
        }

        uriBuilder.appendQueryParameter(ORDER, DATE);
        uriBuilder.appendQueryParameter(TAGS, AUTHOR);
        uriBuilder.appendQueryParameter(PAGE, PAGES);
        uriBuilder.appendQueryParameter(API_KEY, KEY);

        return new NewsLoader(this, uriBuilder.toString());
    }

    // refresh the adapter and hide the loading indicator upon inflating the ListView
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        View loadingIndicator = findViewById(R.id.loading_info);
        loadingIndicator.setVisibility(View.GONE);
        newsAdapter.clear();
        if (news != null && !news.isEmpty()) {
            newsAdapter.addAll(news);
            if (news.isEmpty()) {
                // display this toast in case something went wrong
                displayedMessage = (String) getText(R.string.nothing_to_display);
                warningMessage(displayedMessage);
            }
        }
    }

    @Override
    // reset the loader
    public void onLoaderReset(Loader<List<News>> loader) {
        newsAdapter.clear();
    }

    private void warningMessage(String messageForUser) {
        View loadingIndicator = findViewById(R.id.loading_info);
        loadingIndicator.setVisibility(View.GONE);
        EmptyTextView.setVisibility(View.VISIBLE);
        EmptyTextView.setText(messageForUser);
    }

    @Override
    // initializing the contents of the options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // an item in the options menu is selected
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.setting) {
            Intent settingsIntent = new Intent(this, NewsSettings.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // send a reference to the LoaderManager and restart it
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(NEWS_LOADER_ID, null, this);
    }

    // refresh the news
    public void restartLoader() {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(1, null, this);
    }
}