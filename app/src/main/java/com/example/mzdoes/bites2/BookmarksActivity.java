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
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity {


    private ViewPager               mPager;
    private PagerAdapter            mPagerAdapter;
    private FloatingActionButton    backButton;
    private ImageButton             clearButton, deleteButton;
    private TextView                totalBookmarksView;

    private List<Article> currentBookmarks, toRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        setup();
    }

    private void setup() {
        currentBookmarks = toRemove = new ArrayList<>();

        Intent i = getIntent();
        currentBookmarks = i.getParcelableArrayListExtra(KeySettings.BOOKMARKS_KEY);

        // SET WIDGETS
        mPager = findViewById(R.id.pager_bookmarks);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter); mPager.setPageTransformer(true, new ParallaxPageTransformer());

        backButton   = findViewById(R.id.floatingActionButton_back);
        clearButton  = findViewById(R.id.imageButton_clear);
        deleteButton = findViewById(R.id.imageButton_delete);
        totalBookmarksView = findViewById(R.id.textView_totalBookmarks);

        int size = currentBookmarks.size();
        String totalString = size + " total bookmark";
        if (size > 1) {totalString += "s";}
        totalBookmarksView.setText(totalString);

        setButtons();
    }

    private void setButtons() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBookmarksAndEnd();
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
                                Toast.makeText(BookmarksActivity.this, "Cleared bookmarks.", Toast.LENGTH_SHORT).show();
                                
                                updateBookmarksAndEnd();
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
                                if (currentBookmarks.size() == 1) {
                                    currentBookmarks.clear();
                                    Toast.makeText(BookmarksActivity.this, "Deleted bookmark.", Toast.LENGTH_SHORT).show();
                                    updateBookmarksAndEnd();
                                } else {
                                    toRemove.add(currentBookmarks.get(mPager.getCurrentItem()));
                                    currentBookmarks.remove(mPager.getCurrentItem());
                                    Toast.makeText(BookmarksActivity.this, "Deleted bookmark.", Toast.LENGTH_SHORT).show();
                                    updateWidgets(mPager.getCurrentItem());
                                }
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

    private void updateWidgets(int pos) {
        mPagerAdapter.notifyDataSetChanged();
        if (pos == 0) { mPager.setCurrentItem(1);}
        else { mPager.setCurrentItem(pos - 1); }
        int size = currentBookmarks.size();
        String totalString = size + " total bookmark";
        if (size > 1) {totalString += "s";}
        totalBookmarksView.setText(totalString);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return BookmarkFragment.newInstance(currentBookmarks.get(position));
        }

        @Override
        public int getCount() {
            return currentBookmarks.size();
        }
    }

    private void updateBookmarksAndEnd() {
        updateBookmarks();

        Intent intent = new Intent(BookmarksActivity.this, MainActivity.class);
        intent.putParcelableArrayListExtra("toRemove", (ArrayList<Article>) toRemove);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    private void updateBookmarks() {
        try {
            Utility.saveList(getApplicationContext(), "bookmarks", currentBookmarks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateBookmarks();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        updateBookmarksAndEnd();
    }
}
