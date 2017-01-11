package org.gem.indo.dooit.helpers.auth;

import android.content.Context;

import dagger.Component;

/**
 * Created by Reinhardt on 2017/01/05.
 */

@Component
public interface InvalidTokenHandler {
    void handle(Context context);
}
