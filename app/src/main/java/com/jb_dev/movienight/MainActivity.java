package com.jb_dev.movienight;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.jb_dev.movienight.DialogFragments.DatePickerDialogFragment;
import com.jb_dev.movienight.DialogFragments.ToDatePickerDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button datePickButton;
    Button toDatePickButton;
    Button searchButton;
    RatingBar mRatingBar;
    TextView mRatingTextView;
    TextView mFromDateTextView;
    TextView mToDateTextView;
    Spinner mSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRatingTextView = (TextView) findViewById(R.id.ratingTextView);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar2);
        datePickButton = (Button) findViewById(R.id.DateButton);
        toDatePickButton = (Button) findViewById(R.id.toDateButton);
        mFromDateTextView = (TextView) findViewById(R.id.fromDateTextView);
        mToDateTextView = (TextView) findViewById(R.id.toDateTextView);
        searchButton = (Button) findViewById(R.id.button);

        if ( !isNetworkAvailable() ){
            showNoInternetAlertDialog();
        }

        updateRatingTextView();






        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        String currentDateString = year + "-0" + month + "-0" + date;
        String oneMonthAgoString = year + "-0" + (month - 1) + "-0" + date;
        mToDateTextView.setText(currentDateString);
        mFromDateTextView.setText(oneMonthAgoString);

        final List<String> list=new ArrayList<String>();
        list.add("0");
        list.add("100");
        list.add("500");
        list.add("1000");
        list.add("2000");
        list.add("5000");

        mSpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adp= new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,list);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adp);

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                updateRatingTextView();
            }
        });



        datePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        toDatePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToDatePickerDialog(v);
            }
        });
    }

    private void showNoInternetAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("No Internet Connection! Please check your settings and restart App.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void updateRatingTextView() {
        mRatingTextView.setText(mRatingBar.getProgress() + "");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showToDatePickerDialog(View v) {
        DialogFragment newFragment = new ToDatePickerDialogFragment();
        newFragment.show(getSupportFragmentManager(), "toDatePicker");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }



    public void queryTMDB(View view) {
        Intent intent = new Intent(this, TMDBSearchResultsActivity.class);
        String fromDateQuery = mFromDateTextView.getText().toString();
        String toDateQuery = mToDateTextView.getText().toString();
        String rateQuery = mRatingTextView.getText().toString();
        String numberOfVotesQuery = mSpinner.getSelectedItem().toString();


//        if(fromDateQuery.contains("Date")) {
//            fromDateQuery = "2016-01-01";
//            Log.v("TAG", fromDateQuery);
//        }
//        if(toDateQuery.contains("Date")) {
//            toDateQuery = "2020-01-01";
//            Log.v("TAG", toDateQuery);
//        }

            intent.putExtra("FromDateQuery", fromDateQuery);
            intent.putExtra("ToDateQuery", toDateQuery);
            intent.putExtra("RateQuery", rateQuery);
            intent.putExtra("NumberOfVotesQuery", numberOfVotesQuery);


        if ( !isNetworkAvailable() ){
            showNoInternetAlertDialog();
            return;
        }
            startActivity(intent);

        }
}
