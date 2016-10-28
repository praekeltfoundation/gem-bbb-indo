package com.rr.rgem.gem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.rr.rgem.gem.models.Challenge;
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
    private int questionIdx;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_screen);
        Utils.toast(this, "starting current challenge activity");

        WebServiceFactory factory = ((WebServiceApplication) getApplication()).getWebServiceFactory();
        cmsService = factory.createService(CMSService.class);

        Gson gson = new Gson();
        challenge = gson.fromJson(loadJsonFromResources(this), Challenge.class);
        Question[] questions = challenge.getQuestions().toArray(new Question[0]);
        questionIdx = 0;
        displayQuestion(questionIdx);
    }

    void displayQuestion(int idx)
    {
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
