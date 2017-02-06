package org.gem.indo.dooit.models.challenge;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.models.enums.ChallengeType;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by Rudolph Jacobs on 2016-11-11.
 */

public abstract class BaseChallenge implements Parcelable, Serializable {
    protected ChallengeType type;
    /*** Variables ***/
    private long id;
    private String name;
    private String subtitle;
    private String intro;
    private String outro;

    @SerializedName("image_url")
    private String imageURL;
    @SerializedName("terms_url")
    private String termsUrl;
    @SerializedName("activation_date")
    private DateTime activationDate;
    @SerializedName("deactivation_date")
    private DateTime deactivationDate;
    @SerializedName("is_active")
    private boolean isActive;
    private String instruction;
    @SerializedName("call_to_action")
    private String callToAction;
    @SerializedName("prize")
    private String prize;

    /*** Constructors ***/
    BaseChallenge() {
        // Mandatory empty constructor
    }

    /*** Parcelable methods ***/

    protected BaseChallenge(Parcel in) {
        id = in.readLong();
        name = in.readString();
        activationDate = (DateTime) in.readValue(DateTime.class.getClassLoader());
        deactivationDate = (DateTime) in.readValue(DateTime.class.getClassLoader());
        type = (ChallengeType) in.readValue(ChallengeType.class.getClassLoader());
        prize = in.readString();
    }

    /*** Getters/Setters ***/
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DateTime getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(DateTime activationDate) {
        this.activationDate = activationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public void setImageURL(String url) {
        this.imageURL = url;
    }

    public String getTermsUrl() {
        return termsUrl;
    }

    public ChallengeType getType() {
        return type;
    }

    public void setType(ChallengeType type) {
        this.type = type;
    }

    public DateTime getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(DateTime deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getIntro() {
        return intro;
    }

    public String getOutro() {
        return outro;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeValue(activationDate);
        dest.writeValue(deactivationDate);
        dest.writeValue(type);
        dest.writeString(prize);
    }
}
