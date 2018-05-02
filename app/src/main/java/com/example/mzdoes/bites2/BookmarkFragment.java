package com.example.mzdoes.bites2;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
public class BookmarkFragment extends Fragment {


    private static final String URL_BLACK_IMAGE = "https://vignette.wikia.nocookie.net/joke-battles/images/5/5a/Black.jpg/revision/latest?cb=20161223050425";
    private Article currentArticle;
    private String headline, description, urlImage;
    private TextView headlineView;
    private ImageView background;

    // Required empty public constructor
    public BookmarkFragment() {}


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        background = view.findViewById(R.id.imageView_bookmarkFragmentBackground);
        headlineView      = view.findViewById(R.id.textView_bookmarkHeadline);
        TextView descriptionView   = view.findViewById(R.id.textView_bookmarkDescription);

        // Fixes crash bug when scrolling
        while (this.getContext() == null) { Log.d("BookmarkFragment", "setup: WAITING FOR FRAGMENT CONTEXT"); }

        // For the background ImageView
        if (!(urlImage.isEmpty())) {
            Picasso.with(getContext())
                    .load(urlImage)
                    //.resize(view.getWidth(), view.getHeight())
                    .fit()
                    .centerCrop()
                    .into(background);
        } else {
            Picasso.with(getContext()).load(URL_BLACK_IMAGE)
                    //.resize(view.getWidth(), view.getHeight())
                    .fit()
                    .centerCrop()
                    .into(background);
        }

        background.setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.MULTIPLY);

        // Set other widgets
        headlineView.setText(headline); descriptionView.setText(description);
        view.setBackgroundColor((int) android.R.color.background_dark);

        // View.onClick to open article in a browser
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //---  UPDATE TO ALLOW USER CHOICE FOR DIALOG TO POP UP IN SETTINGS  ---
                final AlertDialog confirmOpenDialog = new AlertDialog.Builder(getContext()).create();
                confirmOpenDialog.setTitle("Open in browser?");

                confirmOpenDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = currentArticle.getUrl();
                                if (!url.startsWith("http://") && !url.startsWith("https://"))
                                    url = "http://" + url;

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(browserIntent);
                            }
                        });

                confirmOpenDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                confirmOpenDialog.dismiss();
                            }
                        });

                confirmOpenDialog.show();
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
