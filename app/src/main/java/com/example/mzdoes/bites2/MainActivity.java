package com.example.mzdoes.bites2;

import android.annotation.SuppressLint;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
    private TextView                totalBites, sourceSettings;

    //  IMPORTANT INSTANCE VARIABLES
    private List<Article>           searchedArticles, bookmarks;
    private List<Source>            sources;
    private int                     articleRefreshState;

    //  API TOOLS
    private NewsAPI                 newsAPI;
    private String                  currentTopic, currentLanguage, currentCountry;

    //  FINAL VARIABLES
    public static final String TAG = "MainActivity";   // for test logging purposes
    public static final int    BOOKMARKS_REQUEST = 10;
    public static final int    SETTINGS_REQUEST  = 25;
    public static final int    DEFAULT_ARTICLENUM_LOAD = 20;





    /** ---  METHODS AND STUFF  ---- **/
    // API METHODS
    private void setSources(String currentLanguage, final String currentCountry) {
        Call<SourceResponse> sourceResponseCall = newsAPI.getSourceList(currentLanguage, currentCountry, KeySettings.API_KEY);
        sourceResponseCall.enqueue(new Callback<SourceResponse>() {
            @Override
            public void onResponse(Call<SourceResponse> call, Response<SourceResponse> response) {
                SourceResponse sourceResponse = response.body();
                List<Source> sourceListResponse = sourceResponse.getSourceList();
                if (sourceListResponse == null || sourceListResponse.isEmpty())
                    { Toast.makeText(MainActivity.this, "No available sources for this language combination. Please change!", Toast.LENGTH_SHORT).show(); }
                else { sources.clear(); sources.addAll(sourceListResponse); }

                setSearchedArticles(currentTopic, currentCountry);

            }

            @Override
            public void onFailure(Call<SourceResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "onFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSearchedArticles(String currentTopic, String currentCountry) {
        Call<ArticleResponse> articleResponseCall = newsAPI.getArticleList(currentTopic, currentCountry,DEFAULT_ARTICLENUM_LOAD + (articleRefreshState * 10), KeySettings.API_KEY);
        articleResponseCall.enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                ArticleResponse articleResponse = response.body();
                List<Article> articleListResponse = articleResponse.getArticles();
                if (articleListResponse == null || articleListResponse.isEmpty())
                    { Toast.makeText(MainActivity.this, "No articles found", Toast.LENGTH_SHORT).show(); }
                else {
                    List<Article> toRemove = new ArrayList<>();

                    for (Article article : articleListResponse) {
                        boolean found = false;
                        for (Source source : sources) {
                            String articleSourceID = article.getSource().getId(); String sourceID = source.getId();
                            if (articleSourceID.equals(sourceID)) { found = true; }
                        }
                        if (!found) { toRemove.add(article); }
                    }

                    articleListResponse.removeAll(toRemove);
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


    // APP WIDGETS & TOOLS METHODS
    private void setup() {
        //SET INSTANCE/API VARIABLES
        searchedArticles = bookmarks = new ArrayList<>();
        sources = new ArrayList<>();
        currentTopic = "all";
        try {
            currentTopic = Utility.readString(getApplicationContext(), KeySettings.DEFAULT_TOPIC_KEY);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        updateInstances();



        //SET WIDGETS
        articleRefreshState = 0;
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
                int lastIdx = mPagerAdapter.getCount() - 1;
                int curItem = mPager.getCurrentItem();

                if (curItem == lastIdx && state == 1) {
                    lastPageChange = true;

                    if (articleRefreshState != 8) {
                        final AlertDialog refreshDialog = new AlertDialog.Builder(MainActivity.this).create();
                        refreshDialog.setTitle("Refresh for more articles?");


                        refreshDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        articleRefreshState += 1;
                                        setArticleView();
                                    }
                                });
                        refreshDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        refreshDialog.dismiss();
                                    }
                                });

                        refreshDialog.show();
                    } else {
                        Toast.makeText(MainActivity.this, "Cannot load any more articles!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    lastPageChange = false;
                }
            }
        });
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new ParallaxPageTransformer());


        searchButton       = findViewById(R.id.floatingActionButton_search);
        bookmarkListButton = findViewById(R.id.imageButton_bookmarkList);
        settingsButton     = findViewById(R.id.imageButton_settings);
        setButtons();


        totalBites     = findViewById(R.id.textView_totalBites);
        sourceSettings = findViewById(R.id.textView_sourceSettings);



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
                                articleRefreshState = 0;
                                mPager.setCurrentItem(0);
                                setArticleView();
                            }
                        });

                searchDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Headlines",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                currentTopic = "all";
                                articleRefreshState = 0;
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
                if (bookmarks.isEmpty() || bookmarks == null) {
                    Toast.makeText(MainActivity.this, "Empty bookmarks list.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Utility.saveList(getApplicationContext(), KeySettings.BOOKMARKS_KEY, bookmarks);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent i = new Intent(MainActivity.this, BookmarksActivity.class);
                    i.putParcelableArrayListExtra(KeySettings.BOOKMARKS_KEY, (ArrayList<Article>) bookmarks);

                    startActivityForResult(i, BOOKMARKS_REQUEST);
                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

                }
            }
        });

        // Button to open OptionsActivity
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(i, SETTINGS_REQUEST);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
    }

    private void setArticleView() { setSources(currentLanguage, currentCountry); }

    private void updateInstances() {
        try {
            bookmarks = Utility.readList(getApplicationContext(), KeySettings.BOOKMARKS_KEY);
            currentLanguage = Utility.readString(getApplicationContext(), KeySettings.LANGUAGE_KEY);
            currentCountry = Utility.readString(getApplicationContext(), KeySettings.COUNTRY_KEY);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void updateWidgets() {
        mPagerAdapter.notifyDataSetChanged();
        if (articleRefreshState == 0) { mPager.setCurrentItem(0); }

        int size = searchedArticles.size();
        String bitesText = size + "";
        String topicHolder; if (currentTopic.equals("all")) { topicHolder = "headlines"; } else { topicHolder = currentTopic; }
        if (size > 1) {bitesText += " bites about '" + topicHolder + "'"; mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) { return false; }
        });}
        else {bitesText += " bite about '" + topicHolder + "'"; mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) { return true; }
        });}
        totalBites.setText(bitesText);


        String languageHolder; String countryHolder; String sourceText;
        if (currentLanguage.equals("")) { languageHolder = "All"; } else { languageHolder = currentLanguage.toUpperCase(); }
        if (currentCountry.equals(""))  { countryHolder  = "All"; } else { countryHolder  = currentCountry.toUpperCase();  }
        sourceText = "Sources: " + languageHolder + ", " + countryHolder;
        sourceSettings.setText(sourceText);
    }

    public void bookmarkArticle(Article articleToBookmark) {
        boolean found = ifBookmarkExists(articleToBookmark);
        if (!found) {
            bookmarks.add(articleToBookmark);
            try {
                Utility.saveList(this.getApplicationContext(), KeySettings.BOOKMARKS_KEY, bookmarks);
                Toast.makeText(this, "Bookmarked article", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { Toast.makeText(this, "Article already bookmarked", Toast.LENGTH_SHORT).show(); }
    }

    public void removeBookmark(Article articleToRemove) {
        boolean found = ifBookmarkExists(articleToRemove);
        if (found) {
            bookmarks.remove(articleToRemove);
            try {
                Utility.saveList(this.getApplicationContext(), KeySettings.BOOKMARKS_KEY, bookmarks);
                Toast.makeText(this, "Removed bookmark", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { Toast.makeText(this, "Bookmark doesn't exist.", Toast.LENGTH_SHORT).show(); }
    }

    public boolean ifBookmarkExists (Article articleToCheck) {
        boolean found = false;
        for (Article article : bookmarks) { if (articleToCheck.equals(article)) found = true; }
        return found;
    }

    // MISCELLANEOUS
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


        @Override
        public int getItemPosition(Object object) {
            ((ArticleFragment) this.getItem(mPager.getCurrentItem())).updateBookmarkButton();
            return super.getItemPosition(object);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            Utility.saveList(this.getApplicationContext(), KeySettings.BOOKMARKS_KEY, bookmarks);
            Utility.saveString(this.getApplicationContext(), KeySettings.LANGUAGE_KEY, currentLanguage);
            Utility.saveString(this.getApplicationContext(), KeySettings.COUNTRY_KEY, currentCountry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateInstances();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == BOOKMARKS_REQUEST) {
            //clear the local bookmark list and resume with saved bookmarks
            bookmarks.clear();
            try {
                bookmarks = Utility.readList(getApplicationContext(), KeySettings.BOOKMARKS_KEY);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_FIRST_USER && requestCode == BOOKMARKS_REQUEST) {
            bookmarks.removeAll(data.getParcelableArrayListExtra("toRemove"));
        } else if (resultCode == RESULT_OK && requestCode == SETTINGS_REQUEST) {

            updateInstances();

            articleRefreshState = 0;
            setArticleView();
        }
    }
}
