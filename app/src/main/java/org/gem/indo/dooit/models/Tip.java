package org.gem.indo.dooit.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by herman on 2016/11/05.
 */

public class Tip {

    private int id;
    private String title;
    private String intro;

    @SerializedName("article_url")
    private String articleUrl;

    @SerializedName("cover_image_url")
    private String coverImageUrl;

    @SerializedName("is_favourite")
    private boolean isFavourite;

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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
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

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    @Override
    public String toString() {
        return title;
    }
}
