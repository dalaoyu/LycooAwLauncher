package com.lycoo.commons.http;

import com.lycoo.commons.domain.ErrorCode;
import com.lycoo.commons.util.LogUtils;

import io.reactivex.Observable;
import io.reactivex.observers.DefaultObserver;

/**
 * xxx
 *
 * Created by lancy on 2017/12/22
 */
public class DownloadObserver<T> extends DefaultObserver<T> implements ProgressListener {
    private static final String TAG = DownloadObserver.class.getSimpleName();

    private DownloadCallBack mDownloadCallBack;
    private int mOldProgress;

    public DownloadObserver(DownloadCallBack downloadCallBack) {
        this.mDownloadCallBack = downloadCallBack;
    }


    @Override
    public void onNext(T t) {
        if (mDownloadCallBack != null) {
            LogUtils.debug(TAG, "download task status = " + mDownloadCallBack.getStatus());
            switch (mDownloadCallBack.getStatus()) {
                case DownloadCallBack.STATUS_STOP:
                    mDownloadCallBack.onStop();
                    break;
                case DownloadCallBack.STATUS_SUCCESS:
                    mDownloadCallBack.onSuccess(t);
                    break;
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        if (mDownloadCallBack != null) {
            mDownloadCallBack.onError(ErrorCode.UNKNOW, e);
        }
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onProgress(final long current, final long total) {
        LogUtils.debug(TAG, "onProgress(), current/total : " + current + "/" + total);
        /*
        if (mDownloadCallBack != null) {
            int progress = (int) (100 * current / total);
            Observable.just(progress)
                    .filter(new Predicate<Integer>() {
                        @Override
                        public boolean test(Integer integer) throws Exception {
                            return integer != mOldProgress;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            mOldProgress = integer;
                            mDownloadCallBack.onProgress(current, total);
                        }
                    });
        }
        */
        if (mDownloadCallBack != null) {
            Observable.just((int) (100 * current / total))
                    .subscribe(progress -> {
                        mDownloadCallBack.onProgress(current, total);
                        if (mOldProgress != progress) {
                            mOldProgress = progress;
                            mDownloadCallBack.onProgress(progress);
                        }
                    });
        }
    }
}
