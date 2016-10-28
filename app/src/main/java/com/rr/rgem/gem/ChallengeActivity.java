package com.rr.rgem.gem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rr.rgem.gem.models.Answer;
import com.rr.rgem.gem.models.Challenge;
import com.rr.rgem.gem.models.ParticipantAnswer;
import com.rr.rgem.gem.models.Question;
import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.service.CMSService;
import com.rr.rgem.gem.service.WebServiceApplication;
import com.rr.rgem.gem.service.WebServiceFactory;
import com.rr.rgem.gem.views.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import android.os.Handler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by chris on 9/14/2016.
 */
public class ChallengeActivity extends ApplicationActivity{

    private final static String TAG = "ChallengeActivity";

    private CMSService cmsService;

    private GEMNavigation navigation;
    private RelativeLayout contentLayout;
    private LinearLayout challengeMainLayout;

    private ImageView currentImage;
    private boolean done;
    private Challenge challenge;
    private ListIterator<Question> questions;
    private Question currentQuestion;
    private Answer selectedOption;
    private List<ParticipantAnswer> participantAnswerList;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_screen);
        challengeMainLayout = (LinearLayout)this.findViewById(R.id.challengeMainLayout);
        Utils.toast(this, "starting current challenge activity");

        WebServiceFactory factory = ((WebServiceApplication) getApplication()).getWebServiceFactory();
        cmsService = factory.createService(CMSService.class);
        cmsService.retrieveChallenge(1).enqueue(new Callback<Challenge>() {
            @Override
            public void onResponse(Call<Challenge> call, Response<Challenge> response) {
                challenge = response.body();
                initChallenge();
            }

            @Override
            public void onFailure(Call<Challenge> call, Throwable t) {
                Utils.toast(ChallengeActivity.this, "Challenge could not be loaded.");
            }
        });

//        Gson gson = new Gson();
//        challenge = gson.fromJson(loadJsonFromResources(this), Challenge.class);
    }

    void initChallenge()
    {
        questions = challenge.getQuestions().listIterator();
        if (!questions.hasNext()) {
            Utils.toast(this, "Challenge has no questions.");
            return;
        }
        currentQuestion = questions.next();
        participantAnswerList = new ArrayList<>();
        displayQuestion();
        Button submitAnswer = (Button)challengeMainLayout.findViewById(R.id.submitAnswer);
        submitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAnswer()) {
                    if (questions.hasNext()) {
                        currentQuestion = questions.next();
                        displayQuestion();
                    } else {
                        Utils.toast(ChallengeActivity.this, "Congratulations! Challenge complete.");
                        Handler handler = new Handler(getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ChallengeActivity.this.finish();
                            }
                        }, 2000); // Delay in milliseconds
                    }
                }
            }
        });
    }

    void displayQuestion()
    {
        selectedOption = null;
        TextView questionText = (TextView)challengeMainLayout.findViewById(R.id.questionText);
        questionText.setText(currentQuestion.getText());
        final LinearLayout questionOptionList = (LinearLayout)challengeMainLayout.findViewById(R.id.questionOptionList);
        questionOptionList.removeAllViews();
        for (final Answer answer: currentQuestion.getAnswers()) {
            final Button option = new Button(this);
            option.setText(answer.getText());
            option.setTextColor(getResources().getColor(R.color.colorTextDeselected));
            questionOptionList.addView(option);
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedOption = answer;
                    int count = questionOptionList.getChildCount();
                    for (int i = 0; i < count; i++) {
                        ((Button)questionOptionList.getChildAt(i))
                                .setTextColor(getResources().getColor(R.color.colorTextDeselected));
                    }
                    option.setTextColor(getResources().getColor(R.color.colorTextSelected));
                }
            });
        }
    }

    void submitAnswers()
    {
        cmsService.createParticipantAnswers(participantAnswerList).enqueue(new Callback<List<ParticipantAnswer>>() {
            @Override
            public void onResponse(Call<List<ParticipantAnswer>> call, Response<List<ParticipantAnswer>> response) {
                if (response.isSuccessful()){
                    Utils.toast(ChallengeActivity.this, "Answers response successfully sent.");
                } else {
                    Utils.toast(ChallengeActivity.this, "Answers sent, but unsuccessful.");
                }
            }

            @Override
            public void onFailure(Call<List<ParticipantAnswer>> call, Throwable t) {
                Utils.toast(ChallengeActivity.this, "Failed to send answers.");
            }
        });
    }

    String loadJsonFromResources(Context context)
    {
        InputStream is = context.getResources().openRawResource(R.raw.challenges);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }catch (Exception e){
        } finally {
            try {
                is.close();
            }catch(Exception e){
            }
        }

        return writer.toString();
    }

    private boolean checkAnswer()
    {
        if (selectedOption == null) {
            Utils.toast(ChallengeActivity.this, "No answer selected.");
        } else {
            participantAnswerList.add(new ParticipantAnswer(currentQuestion, selectedOption));
            if (selectedOption.getCorrect()) {
                Utils.toast(ChallengeActivity.this, "Answer correct.");
                return true;
            } else {
                Utils.toast(ChallengeActivity.this, "Answer incorrect.");
            }
        }
        return false;
    }

    public  void goToMain() {

        Button button1 = new Button(contentLayout.getContext());
        button1.setText(R.string.challenge_done);
//        coachView.addRightView(button1, "name");

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ChallengeActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


    }
}
