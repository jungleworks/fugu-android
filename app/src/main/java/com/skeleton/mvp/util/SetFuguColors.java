package com.skeleton.mvp.util;

import com.skeleton.mvp.FuguColorConfig;
import com.skeleton.mvp.FuguConfig;

/**
 * Created by rajatdhamija on 19/02/18.
 */

public final class SetFuguColors {
    public static void setFuguColors() {
        FuguColorConfig fuguColorConfig = new FuguColorConfig.Builder()
                .fuguActionBarBg("#2496ff")
                .fuguPrimaryTextMsgYou("#000000")
                .fuguSecondaryTextMsgYou("#5c5c5c")
                .fuguSecondaryTextMsgFrom("#a7a5b5")
                .fuguMessageColor("#9E9E9E")
                .fuguMessageRead("#2496ff")
                .fuguBgMessageYou("#d0d8f1")
                .fuguBgMessageFrom("#ffffff")
                .build();
        FuguConfig.getInstance().setColorConfig(fuguColorConfig);
    }
}
