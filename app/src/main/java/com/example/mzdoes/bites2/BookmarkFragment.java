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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends Fragment {


    /**  BOOKMARKFRAGMENT INSTANCE VARIABLES  **/
    // ANDROID WIDGETS AND TOOLS
    private Article currentArticle;
    private String headline, description, urlImage;

    // FINAL VARIABLES
    private static final String URL_BLACK_IMAGE = "https://vignette.wikia.nocookie.net/joke-battles/images/5/5a/Black.jpg/revision/latest?cb=20161223050425";

    /** ---  METHODS AND STUFF  ---- **/
    // Required empty public constructor
    public BookmarkFragment() {}

    // MISCELLANEOUS
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        final ImageView background = view.findViewById(R.id.imageView_bookmarkFragmentBackground);
        TextView headlineView      = view.findViewById(R.id.textView_bookmarkHeadline);
        TextView descriptionView   = view.findViewById(R.id.textView_bookmarkDescription);

        // Fixes crash bug when scrolling
        while (this.getContext() == null) { Log.d("BookmarkFragment", "setup: WAITING FOR FRAGMENT CONTEXT"); }

        // For the background ImageView
        if (!(urlImage == null || urlImage.isEmpty())) { Picasso.with(getContext()).load(urlImage).fit().centerCrop().into(background); }
        else { Picasso.with(getContext()).load(URL_BLACK_IMAGE).fit().centerCrop().into(background); }

        background.setColorFilter(Color.rgb(123, 123, 123), PorterDuff.Mode.MULTIPLY);

        // Set other widgets
        headlineView.setText(headline); descriptionView.setText(description);
        view.setBackgroundColor((int) android.R.color.background_dark);

        // View.onClick to open article in a browser
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = currentArticle.getUrl();
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;

                Intent browserIntent = new Intent(getActivity(), BrowserActivity.class);
                browserIntent.putExtra(KeySettings.URL_KEY, url);
                startActivity(browserIntent);
            }
        });

        return view;
    }

    public static BookmarkFragment newInstance(Article article) {
        BookmarkFragment fragment = new BookmarkFragment();
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
    }

}
