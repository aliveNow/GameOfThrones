package com.zys.brokenview;

import android.graphics.Paint;
import android.graphics.Region;
import android.view.View;

public class BrokenConfig {
    int complexity = 12;
    int breakDuration = 700;
    int fallDuration = 2000;
    int circleRiftsRadius = 66;
    int maxWidth;
    int maxHeight;
    Region region = null;
    View childView = null;
    Paint paint = null;

    public BrokenConfig(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

}
