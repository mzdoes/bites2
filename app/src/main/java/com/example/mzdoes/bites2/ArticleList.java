package com.example.mzdoes.bites2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeucudatcapua2 on 3/22/18.
 */

public class ArticleList implements Serializable {

    private String status; private int totalResults;
    private List<Article> articles;

    public ArticleList() {
        status = "ok";
        totalResults = 0;
        articles = new ArrayList<>();
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }
    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<Article> getArticles() {
        return articles;
    }
    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}
