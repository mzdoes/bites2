package com.example.mzdoes.bites2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    /**  ANDROID WIDGETS/TOOLS  **/
    private ViewPager       mPager;
    private PagerAdapter    mPagerAdapter;


    /**  IMPORTANT INSTANCE VARIABLES  **/
    private List<Article>   searchedArticlesList, bookmarkedArticlesList, shownArticlesList;


    /**  API TOOLS  **/
    private NewsAPI newsAPI;


    /**  FINAL VARIABLES  **/
    public static final String TAG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchedArticlesList = bookmarkedArticlesList = new ArrayList<>();

        mPager = findViewById(R.id.pager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            boolean lastPageChange = false;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        }); //UPDATE FOR ADDING ARTICLES AT END THING!
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new ParallaxPageTransformer());

        Retrofit retrofit = new Retrofit.Builder().baseUrl(NewsAPI.base_Url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

        newsAPI = retrofit.create(NewsAPI.class);
    }





    //MISCELLANEOUS
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ArticleFragment.newInstance(shownArticlesList.get(position));
        }

        @Override
        public int getCount() {
            return shownArticlesList.size();
        }
    }
}
