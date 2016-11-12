package com.nike.dooit.api.interfaces;

import com.nike.dooit.models.challenge.QuizChallengeEntry;
import com.nike.dooit.models.challenge.BaseChallenge;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by herman on 2016/11/05.
 */

public interface ChallengeAPI {

    @GET("/api/challenges/")
    Observable<List<BaseChallenge>> getChallenges();

    @GET("/api/challenges/{id}/")
    Observable<BaseChallenge> getChallenge(@Path("id") int id);

    @POST("/api/entries/")
    Observable<QuizChallengeEntry> postEntry(@Body QuizChallengeEntry quizChallengeEntry);


}
