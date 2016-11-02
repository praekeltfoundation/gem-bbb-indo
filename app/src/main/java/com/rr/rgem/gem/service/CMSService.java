package com.rr.rgem.gem.service;

import com.rr.rgem.gem.models.Challenge;
import com.rr.rgem.gem.models.Entry;
import com.rr.rgem.gem.models.ParticipantAnswer;
import com.rr.rgem.gem.models.TipArticle;
import com.rr.rgem.gem.service.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Wimpie Victor on 2016/10/14.
 */
public interface CMSService {

    @GET("/api/users/{id}/")
    Call<User> retrieveUser(@Path("id") int id);

    @POST("/api/users/{user}/")
    Call<User> createUser(@Path("user") String user, @Body User data);

    @GET("/api/tips/")
    Call<List<TipArticle>> listTips();

    @GET("/api/challenges/")
    Call<List<Challenge>> listChallenges();

    @GET("/api/challenges/{id}/")
    Call<Challenge> retrieveChallenge(@Path("id") int id);

    @POST("/api/participantanswers/")
    Call<ParticipantAnswer> createParticipantAnswers(@Body ParticipantAnswer data);

    @POST("/api/participantanswers/")
    Call<List<ParticipantAnswer>> createParticipantAnswers(@Body List<ParticipantAnswer> data);

    @POST("/api/entries/")
    Call<Entry> createEntries(@Body Entry data);

    @POST("/api/entries/")
    Call<List<Entry>> createEntries(@Body List<Entry> data);
}
