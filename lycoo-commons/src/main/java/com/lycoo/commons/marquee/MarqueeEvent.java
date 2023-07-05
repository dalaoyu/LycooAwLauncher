package com.lycoo.commons.marquee;

import com.lycoo.commons.entity.MarqueeInfo;

/**
 * 跑马灯事件
 *
 * Created by lancy on 2018/1/2
 */
public class MarqueeEvent {

    private MarqueeInfo mMarqueeInfo;

    public MarqueeEvent(MarqueeInfo marqueeInfo) {
        this.mMarqueeInfo = marqueeInfo;
    }

    public MarqueeInfo getMarqueeInfo() {
        return mMarqueeInfo;
    }
}
