package com.example.mzdoes.bites2;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by zeucudatcapua2 on 3/22/18.
 */

public interface NewsAPI {

    public String base_Url = "http://newsapi.org/v2/";

    @GET("top-headlines")
    Call<ArticleList> getArticleList(@Query("q") String topic, @Query("apiKey") String apiKey, @Query("pageSize") int articleNum);

    @GET("sources")
    Call<SourceList> getSourceList(@Query("language") String language, @Query("country") String country, @Query("apiKey") String apiKey);
}
