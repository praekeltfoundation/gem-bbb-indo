package org.gem.indo.dooit.api.serializers;

import com.google.gson.TypeAdapterFactory;

import org.gem.indo.dooit.adapter.RuntimeTypeAdapterFactory;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.challenge.FreeformChallenge;
import org.gem.indo.dooit.models.challenge.QuizChallenge;

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
