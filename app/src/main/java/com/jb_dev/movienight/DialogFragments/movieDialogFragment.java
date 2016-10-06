package com.jb_dev.movienight.DialogFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jb_dev.movienight.R;

/**
 * Created by Dad on 9/29/2016.
 */

public class movieDialogFragment extends DialogFragment {

    private Button mButton;
    TextView mTextView;
    TextView mTitleTextView;

    public movieDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static movieDialogFragment newInstance(String title, String overview) {
        movieDialogFragment frag = new movieDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("overview", overview);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.movie_dialog_layout, container);



    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "DEFAULT");
        String overview = getArguments().getString("overview", "DEFAULT");
        getDialog().setTitle(title);


        mButton = (Button) getView().findViewById(R.id.button2);
        mTextView = (TextView) getView().findViewById(R.id.movieDescriptionTextView);
        mTitleTextView = (TextView) getView().findViewById(R.id.dialogTitleTextView);
        mTitleTextView.setText(title);
        mTextView.setText(overview);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }
}



