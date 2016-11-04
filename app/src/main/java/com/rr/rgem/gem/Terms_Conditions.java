package com.rr.rgem.gem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rr.rgem.gem.controllers.JSONConversation;
import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.views.LeftRightConversation;
import com.rr.rgem.gem.views.Message;

public class Terms_Conditions extends ApplicationActivity {

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms__conditions);

       Button agree=(Button)findViewById(R.id.agree);
       agree.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent intent = new Intent(Terms_Conditions.this,RegistrationCompleteActivity.class);
               startActivity(intent);
               Terms_Conditions.this.finish();
           }

           });

       Button disagree=(Button)findViewById(R.id.disagree);
       disagree.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent intent = new Intent(Terms_Conditions.this,Language_Selection.class);
               startActivity(intent);
               Terms_Conditions.this.finish();
           }

       });


}
}