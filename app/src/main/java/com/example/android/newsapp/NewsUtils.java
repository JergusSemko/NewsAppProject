package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NewsUtils {
    private static final String LOG_TAG = NewsUtils.class.getSimpleName();
    private static final String response = "response";
    private static final String results = "results";
    private static final String section = "sectionName";
    private static final String date = "webPublicationDate";
    private static final String title = "webTitle";
    private static final String url = "webUrl";
    private static final String tags = "tags";
    private static final String author = "webTitle";

    /**
     * Create a private constructor.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name NewsUtils.
     */
    private NewsUtils() {
    }

    public static List<News> fetchNewsData(String requestUrl) throws InterruptedException {
        URL url = returnUrl(requestUrl);
        // Perform HTTP request to URL and receive a JSON response
        String jsonResponse = null;
        try {
            // Try to create a HTTP request with the request URL by makeHttpRequest
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            // In case that request failed, print the error message into log
            Log.e(LOG_TAG, "HTTP request failed.", e);
        }
        // Extract relevant fields from the JSON response and create a list of Education News
        List<News> news = extractFeatureFromJson(jsonResponse);
        // Return the list of news
        return news;
    }

    private static URL returnUrl(String stringUrl) {
        URL url = null;
        try {
             url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            // In case that request failed, print the error message into log
            Log.e(LOG_TAG, "URL building problem.", e);
        }
        return url;
    }

    /*
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful, then read the Input Stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                // If the response failed, print it to the Log
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {

            // If the connection was not established, print it to the log
            Log.e(LOG_TAG, "Connection was not established. Problem retrieving JSON News results.", e);
        } finally {

            // Disconnect the HTTP connection if it is not disconnected yet
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            // Close the Input Stream if it is not closed yet
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /*
    / Convert the InputStream into a String which contains the whole JSON response from the server.
    */
    private static String readFromStream(InputStream inputStream) throws IOException {

        // Create a new StringBuilder
        StringBuilder output = new StringBuilder();

        // If the InputStream exists, create an InputStreamReader from it and a BufferedReader from the InputStreamReader
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // Append the data of the BufferedReader line by line to the StringBuilder
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return list of {@link News} objects that has been built up from parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<News> newsList = new ArrayList<>();

        try {
            JSONObject baseJsonNewsResponse = new JSONObject(newsJSON);
            JSONObject responseJsonNews = baseJsonNewsResponse.getJSONObject(response);
            JSONArray newsArray = responseJsonNews.getJSONArray(results);

            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject currentNews = newsArray.getJSONObject(i);
                String newsSection = currentNews.getString(section);
                String newsDate = "N/A";
                if (currentNews.has(date)) {
                    newsDate = currentNews.getString(date);
                }

                String newsTitle = currentNews.getString(title);
                String newsUrl = currentNews.getString(url);
                JSONArray currentNewsAuthorArray = currentNews.getJSONArray(tags);

                String newsAuthor = "N/A";
                int tagsLenght = currentNewsAuthorArray.length();

                if (tagsLenght == 1) {
                    JSONObject currentNewsAuthor = currentNewsAuthorArray.getJSONObject(0);
                    String newsAuthor1 = currentNewsAuthor.getString(author);
                    newsAuthor = "written by: " + newsAuthor1;
                }

                // Create a new News object with the title, category, author, date, url ,
                // from the JSON response.
                News newNews = new News(newsTitle, newsSection, newsAuthor, newsDate, newsUrl);
                newsList.add(newNews);
            }

        } catch (JSONException e) {
            Log.e("NewsUtils", "JSON results parsing problem.");
        }
        return newsList;
    }
}