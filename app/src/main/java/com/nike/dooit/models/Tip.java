package com.nike.dooit.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by herman on 2016/11/05.
 */

public class Tip {

    private int id;
    private String title;
    @SerializedName("article_url") private String articleUrl;
    @SerializedName("cover_image_url") private String coverImageUrl;
    private List<String> tags;

    public Tip() {
        // Empty constructor
    }

    public Tip(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public boolean hasCoverImageUrl() {
        return coverImageUrl != null && coverImageUrl != "";
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return title;
    }
}
