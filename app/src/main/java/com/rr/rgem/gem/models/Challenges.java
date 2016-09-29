package com.rr.rgem.gem.models;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.rr.rgem.gem.ChallengeActivity;
import com.rr.rgem.gem.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 9/14/2016.
 */
public class Challenges {
    private List<Challenge> challenges = new ArrayList<Challenge>();

    public List<Challenge> getChallenges() {
        return challenges;
    }
    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }
}
