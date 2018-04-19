package com.example.mzdoes.bites2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    /**  ANDROID WIDGETS/TOOLS  **/
    private ViewPager       mPager;
    private PagerAdapter    mPagerAdapter;


    /**  IMPORTANT INSTANCE VARIABLES  **/
    private List<Article>   searchedArticles, bookmarks;
    private List<Source>    sources;


    /**  API TOOLS  **/
    private NewsAPI         newsAPI;
    private String          currentTopic, currentLanguage, currentCountry;

    /**  FINAL VARIABLES  **/
    public static final String TAG = "MainActivity";


    //---  METHODS AND STUFF  ----


    /**  API METHODS  **/
    private void setSources(String currentLanguage) {
        Call<SourceResponse> sourceResponseCall = newsAPI.getSourceList(currentLanguage, KeySettings.API_KEY);
        sourceResponseCall.enqueue(new Callback<SourceResponse>() {
            @Override
            public void onResponse(Call<SourceResponse> call, Response<SourceResponse> response) {
                SourceResponse sourceResponse = response.body();
                List<Source> sourceListResponse = sourceResponse.getSourceList();
                if (sourceListResponse == null || sourceListResponse.isEmpty())
                    { Toast.makeText(MainActivity.this, "No available sources for this language. Please change!", Toast.LENGTH_SHORT).show(); }
                else { sources.clear(); sources.addAll(sourceListResponse); }
            }

            @Override
            public void onFailure(Call<SourceResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "onFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSearchedArticles(String currentTopic, String currentCountry) {
        Call<ArticleResponse> articleResponseCall = newsAPI.getArticleList(currentTopic, currentCountry, 20, KeySettings.API_KEY);
        articleResponseCall.enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                ArticleResponse articleResponse = response.body();
                List<Article> articleListResponse = articleResponse.getArticles();
                if (articleListResponse == null || articleListResponse.isEmpty())
                    { Toast.makeText(MainActivity.this, "No articles found", Toast.LENGTH_SHORT).show(); }
                else {
                    List<Article> toRemove = new ArrayList<>();
                    boolean found = false;
                    for (Article article : articleListResponse) {
                        for (Source source : sources) { if ((article.getSource().getId().toString()).equals(source.getId().toString())) { found = true; } }
                        if (!found) { toRemove.add(article); }
                        found = false;
                    }
                    articleListResponse.remove(toRemove);
                    searchedArticles.clear(); searchedArticles.addAll(articleListResponse);
                    updateWidgets();
                }
            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "onFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    /**  APP WIDGETS & TOOLS METHODS  **/
    private void setup() {
        //SET INSTANCE/API VARIABLES
        searchedArticles = bookmarks = new ArrayList<>();
        sources = new ArrayList<>(); currentTopic = "";
        try {
            currentLanguage = Utility.readString(this.getApplicationContext(), "languageSetting");
            currentCountry  = Utility.readString(this.getApplicationContext(), "countrySetting");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            currentLanguage = ""; currentCountry = "";
        }

        //SET WIDGETS
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


        //SET API
        Retrofit retrofit = new Retrofit.Builder().baseUrl(NewsAPI.base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        newsAPI = retrofit.create(NewsAPI.class);
        setArticleView();
    }

    private void setArticleView() {
        setSources(currentLanguage);
        while (sources == null) { Log.d(TAG, "setArticleView: WAITING FOR SOURCES"); }
        setSearchedArticles(currentTopic, currentCountry);
    }

    private void updateWidgets() {
        mPagerAdapter.notifyDataSetChanged();
        mPager.setCurrentItem(0);
    }

    private void bookmarkArticle(Article articleToBookmark) {
        boolean found = false;
        for (Article article : bookmarks) { if (article.equals(articleToBookmark)) found = true; }
        if (!found) {
            bookmarks.add(articleToBookmark);
            try {
                Utility.saveList(this.getApplicationContext(), "bookmarks", bookmarks);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { Toast.makeText(this, "This article has already been saved", Toast.LENGTH_SHORT).show(); }
    }


    /**  MISCELLANEOUS  **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ArticleFragment.newInstance(searchedArticles.get(position));
        }

        @Override
        public int getCount() {
            return searchedArticles.size();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            Utility.saveList(this.getApplicationContext(), "bookmarks", bookmarks);
            Utility.saveString(this.getApplicationContext(), "languageSetting", currentLanguage);
            Utility.saveString(this.getApplicationContext(), "countrySetting", currentCountry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
