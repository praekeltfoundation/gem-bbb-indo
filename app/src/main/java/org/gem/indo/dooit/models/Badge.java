package org.gem.indo.dooit.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.helpers.strings.StringHelper;
import org.joda.time.DateTime;

/**
 * Created by Wimpie Victor on 2016/12/04.
 */

public class Badge implements Parcelable {

    private String name;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("earned_on")
    private DateTime earnedOn;
    @SerializedName("social_url")
    private String socialUrl;
    private String intro;

    public Badge(){ }

    public Badge copy(){
        Badge badge = new Badge();
        badge.setName(StringHelper.newString(this.name));
        badge.imageUrl = StringHelper.newString(this.imageUrl);
        badge.earnedOn = new DateTime(this.earnedOn);
        badge.socialUrl = StringHelper.newString(this.socialUrl);
        badge.intro = StringHelper.newString(this.intro);
        return badge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Helper to create name consistent with other dialogue graph names.
     *
     * @return
     */
    public String getGraphName() {
        return getName().toLowerCase().replace(' ', '_');
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public DateTime getEarnedOn() {
        return earnedOn;
    }

    public String getSocialUrl() {
        return socialUrl;
    }

    public boolean hasSocialUrl() {
        return !TextUtils.isEmpty(socialUrl);
    }

    public String getIntro() {
        return intro;
    }

    public boolean hasIntro() {
        return !TextUtils.isEmpty(intro);
    }

    protected Badge(Parcel in) {
        name = in.readString();
        imageUrl = in.readString();
        earnedOn = (DateTime) in.readValue(DateTime.class.getClassLoader());
        socialUrl = in.readString();
        intro = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeValue(earnedOn);
        dest.writeString(socialUrl);
        dest.writeString(intro);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Badge> CREATOR = new Parcelable.Creator<Badge>() {
        @Override
        public Badge createFromParcel(Parcel in) {
            return new Badge(in);
        }

        @Override
        public Badge[] newArray(int size) {
            return new Badge[size];
        }
    };


}