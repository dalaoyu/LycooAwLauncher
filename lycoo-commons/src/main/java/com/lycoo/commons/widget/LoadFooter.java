package com.lycoo.commons.widget;

import android.view.View;

/**
 * Define load footer interface
 *
 * Created by lancy on 2018/3/15
 */
public interface LoadFooter {

    void onNormal();

    void onLoading();

    void onComplete();

    void onEnd();

    void onError();

    View getFooterView();
}
