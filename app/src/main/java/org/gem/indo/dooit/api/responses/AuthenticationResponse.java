package org.gem.indo.dooit.api.responses;

import org.gem.indo.dooit.models.Token;
import org.gem.indo.dooit.models.User;

/**
 * Created by herman on 2016/11/05.
 */

public class AuthenticationResponse {

    private Token token;
    private User user;

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
