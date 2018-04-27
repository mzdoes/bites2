package com.example.mzdoes.bites2;

import android.os.Parcel;
import android.os.Parcelable;

public class Source implements Parcelable {
    private String id;
    private String name;
    private String description;
    private String url;
    private String category;
    private String language;
    private String country;

    public Source() {
        id = "cnn";
        name = "CNN";
        description = "some major US news media company";
        url = "cnn.com";
        category = "politics";
        language = "en";
        country = "us";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    protected Source(Parcel in) {
            id = in.readString();
            name = in.readString();
            description = in.readString();
            url = in.readString();
            category = in.readString();
            language = in.readString();
            country = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeString(description);
            dest.writeString(url);
            dest.writeString(category);
            dest.writeString(language);
            dest.writeString(country);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Source> CREATOR = new Parcelable.Creator<Source>() {
            @Override
            public Source createFromParcel(Parcel in) {
                return new Source(in);
            }

            @Override
            public Source[] newArray(int size) {
                return new Source[size];
            }
        };
    }