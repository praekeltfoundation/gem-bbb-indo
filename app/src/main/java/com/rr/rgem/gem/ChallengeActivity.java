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

        Gson gson = new Gson();
        challenge = gson.fromJson(loadJsonFromResources(this), Challenge.class);
        questions = challenge.getQuestions().listIterator();
        if (!questions.hasNext()) {
            Utils.toast(this, "Challenge has no questions.");
            return;
        }
        currentQuestion = questions.next();
        displayQuestion();
        participantAnswerList = new ArrayList<>();
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
                                Intent intent = new Intent(ChallengeActivity.this,MainActivity.class);
                                startActivity(intent);
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
        LinearLayout questionOptionList = (LinearLayout)challengeMainLayout.findViewById(R.id.questionOptionList);
        questionOptionList.removeAllViews();
        for (final Answer answer: currentQuestion.getAnswers()) {
            Button option = new Button(this);
            option.setText(answer.getText());
            questionOptionList.addView(option);
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedOption = answer;
                }
            });
        }
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
