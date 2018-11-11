package com.brijesh.webster.news.model;

import java.util.ArrayList;

/**
 * Created by debajyotibasak on 17/12/17.
 */

public class NewsResponse {
    private String status;
    private int totalResults;
    private int page;
    private ArrayList<ArticleStructure> articles;

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


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<ArticleStructure> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<ArticleStructure> articles) {
        this.articles = articles;
    }
}
