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
    Call<ArticleResponse> getArticleList(@Query("q") String topic, @Query("country") String country, @Query("pageSize") int articleNum, @Query("apiKey") String apiKey);

    @GET("sources")
    Call<SourceResponse> getSourceList(@Query("language") String language, @Query("country") String country, @Query("apiKey") String apiKey);
}
