package com.swg_games_lab.nanicki.artguide.ui;

import com.swg_games_lab.nanicki.artguide.R;

public enum AnimationSetting {
    LEFT(R.anim.slide_in_right, R.anim.slide_out_left),
    RIGHT(R.anim.slide_in_left, R.anim.slide_out_right),
    UP(R.anim.slide_in_up, R.anim.slide_out_down),
    DOWN(R.anim.slide_in_down, R.anim.slide_out_up);

    private final int start;
    private final int end;

    AnimationSetting(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
