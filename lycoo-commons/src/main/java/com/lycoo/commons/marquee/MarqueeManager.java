package com.lycoo.commons.marquee;

import android.content.Context;
import android.content.SharedPreferences;

import com.lycoo.commons.base.BaseService;
import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.entity.MarqueeInfo;
import com.lycoo.commons.entity.MarqueeResponse;
import com.lycoo.commons.helper.RxBus;
import com.lycoo.commons.http.HttpHelper;
import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 跑马灯辅助类
 *
 * Created by lancy on 2018/1/2
 */
public class MarqueeManager {
    private static final String TAG = MarqueeManager.class.getSimpleName();

    private Context mContext;
    private SharedPreferences mMarqueeSp;
    private static MarqueeManager mInstance;
    private CompositeDisposable mCompositeDisposable;

    private MarqueeManager(Context context) {
        mContext = context;
        mMarqueeSp = context.getSharedPreferences(MarqueeConstants.SP_MARQUEE, Context.MODE_PRIVATE);
        mCompositeDisposable = new CompositeDisposable();
    }

    public static MarqueeManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (MarqueeManager.class) {
                if (mInstance == null) {
                    mInstance = new MarqueeManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 更新跑马灯信息
     *
     * Created by lancy on 2018/1/2 19:00
     */
    public void getMarqueeInfo() {
        mCompositeDisposable.add(
                HttpHelper
                        .getInstance(mContext)
                        .getService(BaseService.class)
                        .getMarqueeInfo(ApplicationUtils.getApplicationMetaData(mContext, CommonConstants.APP_KEY), DeviceUtils.getEthernetMacBySeparator(""), DeviceUtils.getCustomerCode())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(this::parseMarqueeInfo, throwable -> {
                            LogUtils.error(TAG, "update marquee info failed, error message : " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));

    }

    /**
     * 解析跑马灯信息
     *
     * @param response 服务端返回信息
     * @throws ParseException 异常
     *
     *                        Created by lancy on 2018/1/2 19:00
     */
    private void parseMarqueeInfo(MarqueeResponse response) throws ParseException {
        LogUtils.debug(TAG, "response : " + response);
        if (response == null) {
            LogUtils.error(TAG, "response is null......");
            return;
        }

        if (response.getStatusCode() == CommonConstants.STATUS_CODE_ERROR) {
            LogUtils.error(TAG, "responseMessage = " + response.getMessage());
            return;
        }

        MarqueeInfo marqueeInfo = response.getData();
        if (marqueeInfo == null) {
            LogUtils.error(TAG, "marqueeInfo is null ......");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String localUpdateTime = mMarqueeSp.getString(MarqueeConstants.UPDATETIME, MarqueeConstants.DEF_UPDATETIME);
        if (!dateFormat.parse(localUpdateTime).before(dateFormat.parse(marqueeInfo.getUpdateTime()))) {
            LogUtils.info(TAG, "marquee info is the latest version......");
            return;
        }

        updateMarqueeInfo(marqueeInfo);
    }

    /**
     * 更新跑马灯信息
     *
     * @param marqueeInfo 跑马灯信息
     *
     *                    Created by lancy on 2018/1/2 19:01
     */
    private void updateMarqueeInfo(MarqueeInfo marqueeInfo) {
        mMarqueeSp
                .edit()
                .putBoolean(MarqueeConstants.SHOW, marqueeInfo.isShow())
                .putString(MarqueeConstants.NAME, marqueeInfo.getName())
                .putInt(MarqueeConstants.COUNT, marqueeInfo.getCount())
                .putInt(MarqueeConstants.PERIOD, marqueeInfo.getPeriod())
                .putString(MarqueeConstants.DATA, marqueeInfo.getData())
                .putString(MarqueeConstants.UPDATETIME, marqueeInfo.getUpdateTime())
                .apply();
        RxBus.getInstance().post(new MarqueeEvent(marqueeInfo));
    }


    /**
     * 获取跑马灯信息
     *
     * @return 跑马灯信息
     *
     * Created by lancy on 2018/1/2 19:00
     */
    public MarqueeInfo getMargueeInfo() {
        MarqueeInfo marqueeInfo = new MarqueeInfo();
        marqueeInfo.setName(mMarqueeSp.getString(MarqueeConstants.NAME, ""));
        marqueeInfo.setShow(mMarqueeSp.getBoolean(MarqueeConstants.SHOW, false));
        marqueeInfo.setCount(mMarqueeSp.getInt(MarqueeConstants.COUNT, MarqueeConstants.DEF_COUNT));
        marqueeInfo.setPeriod(mMarqueeSp.getInt(MarqueeConstants.PERIOD, MarqueeConstants.DEF_PERIOD));
        marqueeInfo.setData(mMarqueeSp.getString(MarqueeConstants.DATA, ""));
        marqueeInfo.setUpdateTime(mMarqueeSp.getString(MarqueeConstants.UPDATETIME, MarqueeConstants.DEF_UPDATETIME));

        return marqueeInfo;
    }

    public void onDestroy() {
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
        }

        mInstance = null;
    }

}
