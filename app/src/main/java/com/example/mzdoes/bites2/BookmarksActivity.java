package com.example.mzdoes.bites2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity {


    private ViewPager               mPager;
    private PagerAdapter            mPagerAdapter;
    private FloatingActionButton    backButton;
    private ImageButton             clearButton, deleteButton;

    private List<Article>           currentBookmarks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        setup();
    }

    private void setup() {
        currentBookmarks = new ArrayList<>();
        try {
            currentBookmarks = Utility.readList(this.getApplicationContext(), KeySettings.BOOKMARKS_KEY);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "" + currentBookmarks.toString(), Toast.LENGTH_SHORT).show();

        // SET WIDGETS
        mPager = findViewById(R.id.pager_bookmarks);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter); mPager.setPageTransformer(true, new ParallaxPageTransformer());

        backButton   = findViewById(R.id.floatingActionButton_back);
        clearButton  = findViewById(R.id.imageButton_clear);
        deleteButton = findViewById(R.id.imageButton_delete);

        setButtons();
    }

    private void setButtons() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Utility.saveList(getApplicationContext(), "bookmarks", currentBookmarks);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                finish();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog confirmClearBookmarksDialog = new AlertDialog.Builder(BookmarksActivity.this).create();
                confirmClearBookmarksDialog.setTitle("Clear all bookmarks?");

                confirmClearBookmarksDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                currentBookmarks.clear();
                                try {
                                    Utility.saveList(getApplicationContext(), "bookmarks", currentBookmarks);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(BookmarksActivity.this, "Cleared bookmarks.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                confirmClearBookmarksDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                confirmClearBookmarksDialog.dismiss();
                            }
                        });

                confirmClearBookmarksDialog.show();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog confirmDeleteBookmarkDialog = new AlertDialog.Builder(BookmarksActivity.this).create();
                confirmDeleteBookmarkDialog.setTitle("Delete this bookmark?");

                confirmDeleteBookmarkDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                currentBookmarks.remove(mPager.getCurrentItem());
                                Toast.makeText(BookmarksActivity.this, "Deleted bookmark.", Toast.LENGTH_SHORT).show();
                                updateWidgets();
                            }
                        });

                confirmDeleteBookmarkDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                confirmDeleteBookmarkDialog.dismiss();
                            }
                        });

                confirmDeleteBookmarkDialog.show();
            }
        });
    }

    private void updateWidgets() {
        mPagerAdapter.notifyDataSetChanged();
        mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ArticleFragment.newInstance(currentBookmarks.get(position));
        }

        @Override
        public int getCount() {
            return currentBookmarks.size();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            Utility.saveList(this.getApplicationContext(), KeySettings.BOOKMARKS_KEY, currentBookmarks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
