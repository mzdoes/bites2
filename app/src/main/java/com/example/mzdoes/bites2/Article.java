package com.example.mzdoes.bites2;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Article implements Parcelable, Serializable {

    private Source source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;

    public Article() {
        source = new Source();
        author = "Rick Astley";
        title = "Never Gonna Give You Up";
        description = "A song used to Rick Roll";
        url = "youtube.com";
        urlToImage = "youtube.com";
        publishedAt = "YouTube";
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
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
            source = (Source) in.readValue(Source.class.getClassLoader());
            author = in.readString();
            title = in.readString();
            description = in.readString();
            url = in.readString();
            urlToImage = in.readString();
            publishedAt = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(source);
            dest.writeString(author);
            dest.writeString(title);
            dest.writeString(description);
            dest.writeString(url);
            dest.writeString(urlToImage);
            dest.writeString(publishedAt);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
            @Override
            public Article createFromParcel(Parcel in) {
                return new Article(in);
            }

            @Override
            public Article[] newArray(int size) {
                return new Article[size];
            }
        };

    @Override
    public String toString() {
        return "Article{" +
                "source=" + source +
                ", title='" + title + '\'' +
                '}';
    }
}