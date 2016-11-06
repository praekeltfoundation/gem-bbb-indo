package com.rr.rgem.gem.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Wimpie Victor on 2016/10/14.
 */
public class TipArticle {

    int id;

    String title;

    @SerializedName("article_url")
    String articleUrl;

    @SerializedName("cover_image_url")
    String coverImageUrl;

    List<String> tags;

    boolean favourite;

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
        return coverImageUrl != null && !coverImageUrl.isEmpty();
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setFavourite(boolean favourite) { this.favourite = favourite; }

    public boolean isFavourite() { return favourite; }

    @Override
    public String toString() {
        return String.format("TipArticle{%s}", title);
    }
}
