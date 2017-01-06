package org.gem.indo.dooit.helpers.InvalidTokenHandler;

import android.content.Context;
import android.content.Intent;
import org.gem.indo.dooit.views.onboarding.LoginActivity;

/**
 * Created by Reinhardt on 2017/01/05.
 */

public class OpenLoginHandler implements InvalidTokenHandler {


    @Override
    public void handle(final Context context) {
        LoginActivity.Builder.create(context).startActivityClearTop();
    }
}
