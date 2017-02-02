package org.gem.indo.dooit.models.enums;

import org.gem.indo.dooit.R;

/**
 * Created by Wimpie Victor on 2017/02/02.
 */

public enum BotQuickAnswerBackground {
    PRIMARY(R.drawable.ic_d_answer_dialogue_bkg_blue, R.drawable.ic_d_answer_dialogue_tail_blue),
    DANGER(R.drawable.ic_d_answer_dialogue_bkg_red, R.drawable.ic_d_answer_dialogue_tail_red);

    private int bkgRes;
    private int tailRes;

    BotQuickAnswerBackground(int bkgRes, int tailRes) {
        this.bkgRes = bkgRes;
        this.tailRes = tailRes;
    }

    public int getBackgroundResource() {
        return bkgRes;
    }

    public int getTailRes() {
        return tailRes;
    }
}
