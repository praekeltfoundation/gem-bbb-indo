package org.gem.indo.dooit.models.enums;

import org.gem.indo.dooit.R;

/**
 * Created by Wimpie Victor on 2017/02/02.
 */

public enum BotQuickAnswerBackground {
    PRIMARY(R.drawable.ic_d_answer_dialogue_bkg_blue),
    DANGER(R.drawable.ic_d_answer_dialogue_bkg_red);

    private int bkgRes;

    BotQuickAnswerBackground(int bkgRes) {
        this.bkgRes = bkgRes;
    }

    public int getBackgroundResource() {
        return bkgRes;
    }
}
