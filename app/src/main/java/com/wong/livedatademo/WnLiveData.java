package com.wong.livedatademo;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.internal.SafeIterableMap;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import static androidx.lifecycle.Lifecycle.State.DESTROYED;
import static androidx.lifecycle.Lifecycle.State.STARTED;

public class WnLiveData<T> {
    private Object NOT_SET = new Object();
    // 存入的数据
    private T mData;
    // 生命周期
    private LifecycleOwner owner;
    // 存放观察者对象
    private Map<Observer<? super T>, WnLifecycleObserver> mObservers = new HashMap<>();
    private int mVersion;
    volatile Object mPendingData = new Object();
    final Object mDataLock = new Object();

    private final Runnable mPostValueRunnable = new Runnable() {
        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            Object newValue;
            synchronized (mDataLock) {
                newValue = mPendingData;
                mPendingData = NOT_SET;
            }
            setValue((T) newValue);
        }
    };

    // 非主线程中使用
    @SuppressLint("RestrictedApi")
    public void postValue(T value) {
        synchronized (mDataLock) {
            mPendingData = value;
        }
        ArchTaskExecutor.getInstance().postToMainThread(mPostValueRunnable);
    }
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        if (owner.getLifecycle().getCurrentState() == DESTROYED) {
            return;
        }
        WnLifecycleObserver wrapper = new WnLifecycleObserver(owner, observer);
        mObservers.put(observer,wrapper);
        owner.getLifecycle().addObserver(wrapper);
    }


    // 在主线程中使用
    public void setValue(T value) {
        mVersion++;
        mData = value;
        dispatchingValue(null);
    }




    private class WnLifecycleObserver implements LifecycleEventObserver {

        final Observer<? super T> mObserver;
        boolean mActive;
        int mLastVersion = -1;

        @NonNull
        final LifecycleOwner mOwner;

        private WnLifecycleObserver(@NonNull LifecycleOwner mOwner,Observer<? super T> mObserver) {
            this.mObserver = mObserver;
            this.mOwner = mOwner;
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (mOwner.getLifecycle().getCurrentState() == DESTROYED) {
                WnLifecycleObserver removed = mObservers.remove(mObserver);
                return;
            }
            boolean newActive = mOwner.getLifecycle().getCurrentState().isAtLeast(STARTED);
            if (newActive == mActive) {
                return;
            }
            mActive = newActive;
            if (mActive) {
                dispatchingValue(this);
            }
        }
    }

    private void dispatchingValue(WnLifecycleObserver observer) {
        if (observer != null) {
            notify(observer);
        } else {
            for (Map.Entry<Observer<? super T>, WnLifecycleObserver> observerWnLifecycleObserverEntry : mObservers.entrySet()) {
                notify(observerWnLifecycleObserverEntry.getValue());
            }
        }
    }

    private void notify(WnLifecycleObserver observer) {
        if (!observer.mActive) {
            return;
        }
        if (observer.mLastVersion >= mVersion) {
            return;
        }
        observer.mLastVersion = mVersion;
        observer.mObserver.onChanged((T) mData);
    }
}
