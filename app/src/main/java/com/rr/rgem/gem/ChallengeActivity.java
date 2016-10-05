package com.rr.rgem.gem;

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

import com.rr.rgem.gem.answers.ChallengesAnswers;
import com.rr.rgem.gem.controllers.JSONConversation;
import com.rr.rgem.gem.controllers.common.Factory;
import com.rr.rgem.gem.controllers.common.JSONController;
import com.rr.rgem.gem.models.ConvoCallback;
import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.views.LeftRightConversation;
import com.rr.rgem.gem.views.Message;
import com.rr.rgem.gem.views.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = new GEMNavigation(this);
        Utils.toast(this, "starting current challenge activity");
        coachScreen = (LinearLayout) navigation.addLayout(R.layout.conversational_layout);
        contentLayout = (RelativeLayout) coachScreen.findViewById(R.id.container);
        coachView = new LeftRightConversation(contentLayout);
        //coachController = new JSONConversation(this, R.raw.challenges);
        this.coachController = Factory.createChallenges(this,coachView);
        coachController.setDoneCallback(new ConvoCallback() {
            @Override
            public String callback(Map<String, String> vars, Map<String, String> responses) {
                Message summary = new Message(1, "2016", true, Message.ResponseType.FreeForm, null);
                summary.setTitle("SUMMARY:");
                coachView.addFreeFormPlain(summary);

                for (String key: responses.keySet()) {
                    Message msg = new Message(0, "2016", true, Message.ResponseType.FreeForm, null);
                    msg.setTitle(key + ": " + responses.get(key));
                    coachView.addFreeFormPlain(msg);
                }

                Button button1 = new Button(contentLayout.getContext());
                button1.setText(R.string.challenge_next);
                coachView.addRightView(button1, "name");
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(ChallengeActivity.this,MainActivity.class);
                        startActivity(intent);

                    }
                });
                return null;

            }

        });
        //coachController.sendChallenges(this, coachView, null);
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
