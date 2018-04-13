package com.example.mzdoes.bites2;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by zeucudatcapua2 on 3/22/18.
 */

public class Article implements Parcelable, Serializable {

    private ShortSource source;
    private String author, title, description, url, urlToImage, publishedAt;

    public Article() {
        source = new ShortSource();
        author = "Rick Astley";
        title = "Never Gonna Give You Up";
        description = "A song used to Rick Roll";
        url = "youtube.com";
        urlToImage = "youtube.com";
        publishedAt = "YouTube";
    }

    public ShortSource getSource() {
        return source;
    }
    public void setSource(ShortSource source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }
    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    protected Article(Parcel in) {
        source = (ShortSource) in.readValue(ShortSource.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(source);
    }

    @SuppressWarnings("unused")
    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}