package com.example.mzdoes.bites2;

/**
 * Created by per6 on 4/13/18.
 */
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

public class ParallaxPageTransformer implements ViewPager.PageTransformer {

    public void transformPage(View view, float position) {

        int pageWidth = view.getWidth();
        ImageView imageView = view.findViewById(R.id.imageView_articleFragmentBackground);

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(1);

        } else if (position <= 1) { // [-1,1]

            imageView.setTranslationX(-position * (pageWidth / 2)); //Half the normal speed

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(1);
        }


    }
}