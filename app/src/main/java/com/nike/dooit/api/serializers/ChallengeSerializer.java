package com.nike.dooit.api.serializers;

import com.google.gson.TypeAdapterFactory;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.nike.dooit.models.challenge.BaseChallenge;
import com.nike.dooit.models.challenge.FreeformChallenge;
import com.nike.dooit.models.challenge.QuizChallenge;

/**
 * Created by Rudolph Jacobs on 2016-11-11.
 */

public class ChallengeSerializer {
    public static TypeAdapterFactory getChallengeAdapterFactory() {
        RuntimeTypeAdapterFactory<BaseChallenge> challengeAdapterFactory = RuntimeTypeAdapterFactory.of(BaseChallenge.class, "type");
        challengeAdapterFactory.registerSubtype(QuizChallenge.class, "quiz");
        challengeAdapterFactory.registerSubtype(FreeformChallenge.class, "freeform");
        return challengeAdapterFactory;
    }
}
