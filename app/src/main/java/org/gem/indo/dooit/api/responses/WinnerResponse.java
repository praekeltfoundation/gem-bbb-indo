package org.gem.indo.dooit.api.responses;

import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.challenge.BaseChallenge;

/**
 * Created by frede on 2017/01/26.
 */

public class WinnerResponse {
    private boolean available;
    private Badge badge;
    private BaseChallenge challenge;

    public boolean isAvailable() {
        return available;
    }

    public Badge getBadge() {
        return badge;
    }

    public BaseChallenge getChallenge() {
        return challenge;
    }
}
