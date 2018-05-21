package com.example.mzdoes.bites2;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleFragment extends Fragment {


    /**  ARTICLEFRAGMENT INSTANCE VARIABLES  **/
    // ANDROID WIDGETS AND TOOLS
    private Article currentArticle;
    private String headline, description, urlImage, sourceUrlImage;
    private boolean aBookmark;
    private ImageView bookmarkButton;


    // FINAL VARIABLES
    private static final String URL_BLACK_IMAGE = "https://vignette.wikia.nocookie.net/joke-battles/images/5/5a/Black.jpg/revision/latest?cb=20161223050425";

    /** ---  METHODS AND STUFF  ---- **/
    // Required empty public constructor
    public ArticleFragment() {}

    //MISCELLANEOUS
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        final ImageView background = view.findViewById(R.id.imageView_articleFragmentBackground);
        final ImageView sourceImg  = view.findViewById(R.id.imageView_source);
        TextView headlineView      = view.findViewById(R.id.textView_headline);
        TextView descriptionView   = view.findViewById(R.id.textView_description);

        // Fixes crash bug when scrolling
        while (this.getContext() == null) { Log.d("ArticleFragment", "setup: WAITING FOR FRAGMENT CONTEXT"); }

        // For the background ImageView
        if (!(urlImage == null || urlImage.isEmpty())) { Picasso.with(getContext()).load(urlImage).fit().centerCrop().into(background); }
        else { Picasso.with(getContext()).load(URL_BLACK_IMAGE).fit().centerCrop().into(background); }

        background.setColorFilter(Color.rgb(123, 123, 123), PorterDuff.Mode.MULTIPLY);

        // For the source ImageView STILL NEEDS TO BE IMPLEMENTED
        if (!(sourceUrlImage == null || sourceUrlImage.isEmpty())) { Picasso.with(getContext()).load(sourceUrlImage).fit().centerCrop().into(sourceImg); }
        else { Picasso.with(getContext()).load(URL_BLACK_IMAGE).fit().centerCrop().into(sourceImg); }


        // Set other widgets
        headlineView.setText(headline); descriptionView.setText(description);
        view.setBackgroundColor((int) android.R.color.background_dark);

        // View.onClick or swipe up to open article in a browser
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openURL();
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_UP) { openURL(); }
                return true;
            }
        });

        // Set Buttons
        bookmarkButton = view.findViewById(R.id.imageButton_bookmarkArticle);
        ImageButton shareButton = view.findViewById(R.id.imageButton_shareArticle);

        if (aBookmark) { bookmarkButton.setImageResource(R.drawable.ic_bookmark_black_24dp); }
        else { bookmarkButton.setImageResource(R.drawable.ic_bookmark_border_black_24dp); }

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aBookmark) {
                    ((MainActivity) getActivity()).removeBookmark(currentArticle);
                    aBookmark = false;
                    bookmarkButton.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                }
                else {
                    ((MainActivity) getActivity()).bookmarkArticle(currentArticle);
                    aBookmark = true;
                    bookmarkButton.setImageResource(R.drawable.ic_bookmark_black_24dp);
                }
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check this article out: " + currentArticle.getUrl());
                shareIntent.setType("text/plain"); startActivity(shareIntent);
            }
        });

        return view;
    }

    public static ArticleFragment newInstance(Article article) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putParcelable("currentArticle", article);
        args.putString("urlToImage", article.getUrlToImage());
        args.putString(    "descString",     "BY "+ article.getSource().getName() + ": " + article.getDescription());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentArticle = getArguments().getParcelable("currentArticle");
        urlImage = getArguments().getString("urlToImage");
        description = getArguments().getString("descString");
        headline = currentArticle.getTitle();
        sourceUrlImage = "logo.clearbit.com/" + currentArticle.getSource().getUrl();

        aBookmark = ((MainActivity) getActivity()).ifBookmarkExists(currentArticle);

//        Log.d("ArticleFragment", "onCreate: " + sourceUrlImage);
    }

    private void openURL() {
        String url = currentArticle.getUrl();
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

        Intent browserIntent = new Intent(getActivity(), BrowserActivity.class);
        browserIntent.putExtra(KeySettings.URL_KEY, url);
        startActivity(browserIntent);
    }

    public void updateBookmarkButton() {
        if (currentArticle != null) { aBookmark = ((MainActivity) getActivity()).ifBookmarkExists(currentArticle); }

        if (bookmarkButton != null) {
            if (aBookmark) { bookmarkButton.setImageResource(R.drawable.ic_bookmark_black_24dp); }
            else { bookmarkButton.setImageResource(R.drawable.ic_bookmark_border_black_24dp); }
        }
    }
}
