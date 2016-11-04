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

import com.rr.rgem.gem.controllers.JSONConversation;
import com.rr.rgem.gem.models.ConvoCallback;
import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.views.LeftRightConversation;
import com.rr.rgem.gem.views.Message;
import com.rr.rgem.gem.views.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by sjj98 on 9/23/2016.
 */
public class TipsActivity extends ApplicationActivity {

    private GEMNavigation navigation;
    private RelativeLayout contentLayout;
    private LinearLayout coachScreen;

    private LeftRightConversation coachView;
    private JSONConversation coachController;
    private ImageView currentImage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = new GEMNavigation(this);
        Utils.toast(this, getString(R.string.startingTipsActivity));
        coachScreen = (LinearLayout) navigation.addLayout(R.layout.conversational_layout);
        contentLayout = (RelativeLayout) coachScreen.findViewById(R.id.container);
        coachView = new LeftRightConversation(contentLayout);
        Message start = new Message(1, "2016", true, Message.ResponseType.FreeForm, null);
        start.setTitle(getString(R.string.shortTips));
        coachView.addFreeFormPlain(start);
        coachController = new JSONConversation(this, R.raw.tips);

        Map<String, String> vars = coachController.getVars();
        vars.put("Tone", getString(R.string.Tone));
        vars.put("Ttwo",getString(R.string.Ttwo));
        vars.put("Tthree", getString(R.string.Tthree));
        vars.put("Tfour",getString(R.string.Tfour));
        vars.put("infoTipsEnd", getString(R.string.infoTipsEnd));
        //Button
        vars.put("ToneY", getString(R.string.ToneY));
        vars.put("ToneN", getString(R.string.ToneN));
        vars.put("TtwoY", getString(R.string.TtwoY));
        vars.put("TtwoN", getString(R.string.TtwoN));
        vars.put("TthreeY", getString(R.string.TthreeY));
        vars.put("TthreeN", getString(R.string.TthreeN));
        vars.put("TfourY", getString(R.string.TfourY));


        coachController.setDoneCallback(new ConvoCallback() {


            @Override
            public String callback(Map<String, String> vars, Map<String, String> responses) {

              /*  Message summary = new Message(1, "2016", true, Message.ResponseType.FreeForm, null);
                summary.setTitle("SUMMARY:");
                coachView.addFreeFormPlain(summary);

                for (String key: responses.keySet()) {
                    Message msg = new Message(0, "2016", true, Message.ResponseType.FreeForm, null);
                    msg.setTitle(key + ": " + responses.get(key));
                    coachView.addFreeFormPlain(msg);
                }*/
                Button button1 = new Button(contentLayout.getContext());
                button1.setText(R.string.challenge_next);
                coachView.addRightView(button1, "name");
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(TipsActivity.this,MainActivity.class);
                        startActivity(intent);

                    }
                });
                return null;
            }
        });
        coachController.sendChallenges(this,coachView, null);



    }

    public void goToMain() {

        Button button1 = new Button(contentLayout.getContext());
        button1.setText(R.string.challenge_done);
        coachView.addRightView(button1, "name");

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TipsActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }
}

