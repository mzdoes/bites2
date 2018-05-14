package com.example.mzdoes.bites2;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
public class ArticleFragment extends Fragment {


    /**  ARTICLEFRAGMENT INSTANCE VARIABLES  **/
    // ANDROID WIDGETS AND TOOLS
    private Article currentArticle;
    private String headline, description, urlImage, sourceUrlImage;

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

        // For the source ImageView
        if (!(sourceUrlImage == null || sourceUrlImage.isEmpty())) { Picasso.with(getContext()).load(sourceUrlImage).fit().centerCrop().into(sourceImg); }
        else { Picasso.with(getContext()).load(URL_BLACK_IMAGE).fit().centerCrop().into(sourceImg); }


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

        // HeadlineView OnLongClick to Bookmark
        headlineView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final AlertDialog confirmBookmarkDialog = new AlertDialog.Builder(getContext()).create();
                confirmBookmarkDialog.setTitle("Bookmark this article?");

                confirmBookmarkDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((MainActivity) getActivity()).bookmarkArticle(currentArticle);
                            }
                        });
                confirmBookmarkDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                confirmBookmarkDialog.dismiss();
                            }
                        });

                confirmBookmarkDialog.show();

                return false;
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

//        Log.d("ArticleFragment", "onCreate: " + sourceUrlImage);
    }


}
