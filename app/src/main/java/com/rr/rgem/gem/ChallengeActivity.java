package com.rr.rgem.gem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.rr.rgem.gem.answers.ChallengesAnswers;
import com.rr.rgem.gem.controllers.JSONConversation;
import com.rr.rgem.gem.controllers.common.Factory;
import com.rr.rgem.gem.controllers.common.JSONController;
import com.rr.rgem.gem.models.Challenge;
import com.rr.rgem.gem.models.ConversationNode;
import com.rr.rgem.gem.models.ConvoCallback;
import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.views.LeftRightConversation;
import com.rr.rgem.gem.views.Message;
import com.rr.rgem.gem.views.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Created by chris on 9/14/2016.
 */
public class ChallengeActivity extends ApplicationActivity{

    //public void goToMain(){};

    private GEMNavigation navigation;
    private RelativeLayout contentLayout;
    private LinearLayout coachScreen;

    private LeftRightConversation coachView;
    //JSONConversation coachController;
    private ChallengesAnswers coachController;
    private ImageView currentImage;
    private boolean done;
    private Challenge challenge;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = new GEMNavigation(this);
        Utils.toast(this, "starting current challenge activity");
        Gson gson = new Gson();
        challenge = gson.fromJson(loadJsonFromResources(this), Challenge.class);
    }

    String loadJsonFromResources(Context context){
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
        coachView.addRightView(button1, "name");

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ChallengeActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


    }
}
