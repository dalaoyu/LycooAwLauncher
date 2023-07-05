package com.lycoo.commons.helper;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * xxx
 *
 * Created by lancy on 2017/12/20
 */
public class RxBus {
    private static final String TAG = RxBus.class.getSimpleName();

    private Map<String, CompositeDisposable> mDisposableMap;
    private static volatile RxBus mRxBus;
    private final Subject<Object> mSubject;

    public static RxBus getInstance() {
        if (mRxBus == null) {
            synchronized (RxBus.class) {
                if (mRxBus == null) {
                    mRxBus = new RxBus();
                }
            }
        }
        return mRxBus;
    }

    private RxBus() {
        mSubject = PublishSubject.create().toSerialized();
    }

    public void post(Object o) {
        mSubject.onNext(o);
    }

    public boolean hasObservers() {
        return mSubject.hasObservers();
    }

    public void addDisposable(Object obj, Disposable disposable) {
        if (mDisposableMap == null) {
            mDisposableMap = new HashMap<>();
        }
        String key = obj.getClass().getName();
        if (mDisposableMap.get(key) != null) {
            mDisposableMap.get(key).add(disposable);
        } else {
            //一次性容器,可以持有多个并提供 添加和移除。
            CompositeDisposable disposables = new CompositeDisposable();
            disposables.add(disposable);
            mDisposableMap.put(key, disposables);
        }
    }

    public <T> Flowable<T> getObservable(Class<T> type) {
        return mSubject
                .toFlowable(BackpressureStrategy.BUFFER)
                .ofType(type);
    }

    public <T> Disposable registerSubscribe(Class<T> type, Consumer<T> next, Consumer<Throwable> error) {
        return getObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error);
    }

    public void unRegisterSubscribe(Object o) {
        if (mDisposableMap == null) {
            return;
        }

        String key = o.getClass().getName();
        if (!mDisposableMap.containsKey(key)) {
            return;
        }
        if (mDisposableMap.get(key) != null) {
            mDisposableMap.get(key).dispose();
        }

        mDisposableMap.remove(key);
    }

}
