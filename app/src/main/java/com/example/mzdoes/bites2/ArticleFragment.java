package com.example.mzdoes.bites2;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.squareup.picasso.Target;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleFragment extends Fragment {

    private static final String URL_BLACK_IMAGE = "https://vignette.wikia.nocookie.net/joke-battles/images/5/5a/Black.jpg/revision/latest?cb=20161223050425";
    private Article currentArticle;
    private String headline, description;

    // Required empty public constructor
    public ArticleFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        final ImageView background = view.findViewById(R.id.imageView_articleFragmentBackground);
        TextView headlineView      = view.findViewById(R.id.textView_headline);
        TextView descriptionView   = view.findViewById(R.id.textView_description);

        // Fixes crash bug when scrolling
        while (this.getContext() == null) { Log.d("ArticleFragment", "setup: WAITING FOR FRAGMENT CONTEXT"); }

        // For the background ImageView
        String urlImage = currentArticle.getUrlToImage();
        if (!(urlImage == null || (urlImage.isEmpty()))) {
            Picasso.with(getContext()).load(urlImage).resize(container.getWidth(), container.getHeight()).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    BitmapDrawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
                    //drawable.setAlpha(135);
                    background.setImageDrawable(drawable);
                    background.setColorFilter(Color.rgb(123, 123, 123), PorterDuff.Mode.MULTIPLY);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else {
            Picasso.with(getContext()).load(URL_BLACK_IMAGE).resize(container.getWidth(), container.getHeight()).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    BitmapDrawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
                    //drawable.setAlpha(135);
                    background.setImageDrawable(drawable);
                    background.setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.MULTIPLY);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }

        // Set other widgets
        headlineView.setText(headline); descriptionView.setText(description);
        return view;
    }

    public static ArticleFragment newInstance(Article article) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putParcelable("currentArticle", article);
        args.putString("descString", "BY "+ article.getSource().getName() + ": " + article.getDescription());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentArticle = getArguments().getParcelable("currentArticle");
        description = getArguments().getString("descString");
        headline = currentArticle.getTitle();

        Log.d("ArticleFragment", "onCreate: " + description);
    }
}
