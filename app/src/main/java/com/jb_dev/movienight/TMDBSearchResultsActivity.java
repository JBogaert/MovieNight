package com.jb_dev.movienight;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jb_dev.movienight.DialogFragments.movieDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Dad on 10/3/2016.
 */

public class TMDBSearchResultsActivity extends AppCompatActivity  implements RecyclerViewAdapter.OnItemClickListener{


private RecyclerView myRecyclerView;
private LinearLayoutManager linearLayoutManager;
private RecyclerViewAdapter myRecyclerViewAdapter;
    String rateQuery;
    String toDateQuery;
    String fromDateQuery;
    String numberOfVotesQuery;
    static ArrayList<MovieResult> results;
    String pageQuery;
    ProgressBar mProgressBar;
    Button mNextPageButton;
    Button mLastPageButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imdbsearch_result);

        mNextPageButton = (Button) findViewById(R.id.nextPageButton);
        mLastPageButton = (Button) findViewById(R.id.lastPageButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        Intent intent = getIntent();
        rateQuery = intent.getStringExtra("RateQuery");
        toDateQuery = intent.getStringExtra("ToDateQuery");
        fromDateQuery = intent.getStringExtra("FromDateQuery");
        numberOfVotesQuery = intent.getStringExtra("NumberOfVotesQuery");
        pageQuery = "1";
        results = null;

        // Check if the NetworkConnection is active and connected.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new TMDBQueryManager().execute(pageQuery);
        } else {
            TextView textView = new TextView(this);
            textView.setText("No network connection.");
            setContentView(textView);
        }

        myRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(false);
        mProgressBar.setVisibility(View.INVISIBLE);
        myRecyclerViewAdapter = new RecyclerViewAdapter(this);
        myRecyclerViewAdapter.setOnItemClickListener(this);
        myRecyclerView.setAdapter(myRecyclerViewAdapter);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        mNextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int page = Integer.parseInt(pageQuery);
                page++;
                pageQuery = page + "";

                int count = myRecyclerViewAdapter.getItemCount();
                for (int i = 0; i < count; ++i) {
                    myRecyclerViewAdapter.remove(0);
                    results.remove(0);
                }

                new TMDBQueryManager().execute(pageQuery);

            }
        });

        mLastPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int page = Integer.parseInt(pageQuery);
                if(page == 1) {
                    return;
                }else{
                    page += (-1);
                    pageQuery = page + "";
                    int count = myRecyclerViewAdapter.getItemCount();
                    for (int i = 0; i < count; ++i) {
                        myRecyclerViewAdapter.remove(0);
                        results.remove(0);
                    }
                    new TMDBQueryManager().execute(pageQuery);
                }
            }
        });
    }

    public void backToSearchOnClick(View v) {
        finish();
    }

    @Override
    public void onItemClick(RecyclerViewAdapter.ItemHolder item, int position) {
   showMovieInfoDialog(getCurrentFocus(), results.get(position).getTitle(),
                                           results.get(position).getOverview());

    }

    public void showMovieInfoDialog(View v, String title, String overview) {
        DialogFragment newFragment = movieDialogFragment.newInstance(title, overview);
        newFragment.show(getSupportFragmentManager(), "Movie Info");
//        movieDialogFragment.newInstance("Title", "Overview");
    }
    /**
     * Updates the View with the results. This is called asynchronously
     * when the results are ready.
     * @param result The results to be presented to the user.
     */
    public void updateViewWithResults(ArrayList<MovieResult> result) {

        int i = -1;
        Log.d("updateViewWithResults", "");
        // Add results to listView.
        for (MovieResult m : result) {
            i++;
            myRecyclerViewAdapter.add(i, m.getTitle());
            }
    }

    private class TMDBQueryManager extends AsyncTask {

        private final String TMDB_API_KEY = "4bd70396df45732da791eecd387f4fd4";
        private static final String DEBUG_TAG = "TMDBQueryManager";

        @Override
        protected ArrayList<MovieResult> doInBackground(Object... params) {
            try {
                return searchIMDB((String) params[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            updateViewWithResults((ArrayList<MovieResult>) result);
        }

        /**
         * Searches IMDBs API for the given query
         * @param query The query to search.
         * @return A list of all hits.
         */
        public ArrayList<MovieResult> searchIMDB(String query) throws IOException {
            if(query == null) {
                query = "1";
            }
            // Build URL
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("https://api.themoviedb.org/3/discover/movie");
            stringBuilder.append("?vote_average.lte=10");
            stringBuilder.append("&sort_by=popularity.desc");
            stringBuilder.append("&vote_count.gte=" + numberOfVotesQuery);
                stringBuilder.append("&vote_average.gte=" + rateQuery);
                stringBuilder.append("&primary_release_date.lte=" + toDateQuery);
                stringBuilder.append("&primary_release_date.gte=" + fromDateQuery + "&language=en-US");
                stringBuilder.append("&page=" + query + "");
                stringBuilder.append("&api_key=4bd70396df45732da791eecd387f4fd4");
            URL url = new URL(stringBuilder.toString());




            InputStream stream = null;
            try {
                // Establish a connection
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.addRequestProperty("Accept", "application/json"); // Required to get TMDB to play nicely.
                conn.setDoInput(true);
                conn.connect();

                int responseCode = conn.getResponseCode();
                Log.d(DEBUG_TAG, "The response code is: " + responseCode + " " + conn.getResponseMessage());

                stream = conn.getInputStream();
                return parseResult(stringify(stream));
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }

        private ArrayList<MovieResult> parseResult(String result) {
            String streamAsString = result;
            results = new ArrayList<MovieResult>();



            try {
                JSONObject jsonObject = new JSONObject(streamAsString);
                JSONArray array = (JSONArray) jsonObject.get("results");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonMovieObject = array.getJSONObject(i);
                    MovieResult.Builder movieBuilder = MovieResult.newBuilder(
                            Integer.parseInt(jsonMovieObject.getString("id")),
                            jsonMovieObject.getString("title"))
                            .setBackdropPath(jsonMovieObject.getString("backdrop_path"))
                            .setOriginalTitle(jsonMovieObject.getString("original_title"))
                            .setPopularity(jsonMovieObject.getString("popularity"))
                            .setPosterPath(jsonMovieObject.getString("poster_path"))
                            .setReleaseDate(jsonMovieObject.getString("release_date"))
                            .setOverview(jsonMovieObject.getString("overview"));
                    results.add(movieBuilder.build());
                }
            } catch (JSONException e) {
                System.err.println(e);
                Log.d(DEBUG_TAG, "Error parsing JSON. String was: " + streamAsString);
            }
            return results;
        }

        public String stringify(InputStream stream) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            return bufferedReader.readLine();
        }
    }

}
