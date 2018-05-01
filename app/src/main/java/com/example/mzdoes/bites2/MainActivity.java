package com.example.mzdoes.bites2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

    /**  MAINACTIVITY INSTANCE VARIABLES  **/
    //  ANDROID WIDGETS/TOOLS
    private ViewPager               mPager;
    private PagerAdapter            mPagerAdapter;
    private FloatingActionButton    searchButton;
    private ImageButton             bookmarkListButton, settingsButton;

    //  IMPORTANT INSTANCE VARIABLES
    private List<Article>           searchedArticles, bookmarks;
    private List<Source>            sources;


    //  API TOOLS
    private NewsAPI                 newsAPI;
    private String                  currentTopic, currentLanguage, currentCountry;

    //  FINAL VARIABLES
    public static final String TAG = "MainActivity";
    public static final int    BOOKMARKS_REQUEST = 10;

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
                    { Toast.makeText(MainActivity.this, "No available sources for this language/country combination. Please change!", Toast.LENGTH_SHORT).show(); }
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
                        for (Source source : sources) { if ((article.getSource().getId()).equals(source.getId())) { found = true; } }
                        if (!found) { toRemove.add(article); }
                        found = false;
                    }
                    articleListResponse.remove(toRemove);
                    searchedArticles.clear(); searchedArticles.addAll(articleListResponse);

                    if (response.isSuccessful()) { updateWidgets(); }

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
        sources = new ArrayList<>(); currentTopic = "trump";
        try {
            bookmarks       = Utility.readList(this.getApplicationContext(), KeySettings.BOOKMARKS_KEY);
//            currentLanguage = Utility.readString(this.getApplicationContext(), "languageSetting");
//            currentCountry  = Utility.readString(this.getApplicationContext(), "countrySetting");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            currentLanguage = ""; currentCountry = "";
        }

        Log.d(TAG, "setup: " + bookmarks.toString());

        //SET WIDGETS
        mPager = findViewById(R.id.pager);
//        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            boolean lastPageChange = false;
//
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        }); //---  UPDATE FOR ADDING ARTICLES AT END THING!  ---
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new ParallaxPageTransformer());

        searchButton       = findViewById(R.id.floatingActionButton_search);
        bookmarkListButton = findViewById(R.id.imageButton_bookmarkList);
        settingsButton     = findViewById(R.id.imageButton_settings);
        setButtons();


        //SET API
        Retrofit retrofit = new Retrofit.Builder().baseUrl(NewsAPI.base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        newsAPI = retrofit.create(NewsAPI.class);
        setArticleView();
    }

    private void setButtons() {
        // Button to open AlertDialog to search articles
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog searchDialog = new AlertDialog.Builder(MainActivity.this).create();
                LayoutInflater inflater = (MainActivity.this).getLayoutInflater();
                View theView = inflater.inflate(R.layout.dialog_search, null);
                theView.setBackgroundColor(getResources().getColor(R.color.colorDialog));
                searchDialog.setView(theView);
                searchDialog.setTitle(null);

                final EditText searchEditText = theView.findViewById(R.id.editText_searchTopic);

                searchDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Search",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                currentTopic = searchEditText.getText().toString();
                                setArticleView();
                            }
                        });

                searchDialog.show();
            }
        });

        // Button to open BookmarkActivity
        bookmarkListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Utility.saveList(getApplicationContext(), KeySettings.BOOKMARKS_KEY, bookmarks);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(MainActivity.this, BookmarksActivity.class);
                i.putParcelableArrayListExtra(KeySettings.BOOKMARKS_KEY, (ArrayList<Article>) bookmarks);
                startActivityForResult(i, BOOKMARKS_REQUEST);
            }
        });

        // Button to open OptionsActivity
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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

    public void bookmarkArticle(Article articleToBookmark) {
        boolean found = false;
        for (Article article : bookmarks) { if (articleToBookmark.equals(article)) found = true; }
        if (!found) {
            bookmarks.add(articleToBookmark);
            try {
                Utility.saveList(this.getApplicationContext(), KeySettings.BOOKMARKS_KEY, bookmarks);
                Toast.makeText(this, "Bookmarked article!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { Toast.makeText(this, "Article already bookmarked.", Toast.LENGTH_SHORT).show(); }
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
            Utility.saveList(this.getApplicationContext(), KeySettings.BOOKMARKS_KEY, bookmarks);
//            Utility.saveString(this.getApplicationContext(), "languageSetting", currentLanguage);
//            Utility.saveString(this.getApplicationContext(), "countrySetting", currentCountry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == BOOKMARKS_REQUEST) {
            //clear the local bookmark list and resume with saved bookmarks
            bookmarks.clear();
            try {
                bookmarks = Utility.readList(getApplicationContext(), "bookmarks");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_FIRST_USER && requestCode == BOOKMARKS_REQUEST) {
            bookmarks.removeAll(data.getParcelableArrayListExtra("toRemove"));
        }
    }
}
