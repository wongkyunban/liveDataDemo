package com.wong.livedatademo;

import java.util.HashMap;
import java.util.Map;

public class LiveDataManager {
    //　用作锁
    private static Object LOCK = new Object();
    // 单例　WnLiveData
    private static Object wnLiveData;
    // 单例　LiveDataManager
    private static LiveDataManager liveDataManager;
    private Map<String, Object> mLiveDatas = new HashMap<>();

    public static LiveDataManager getInstance() {
        if (liveDataManager == null) {
            synchronized (LOCK) {
                liveDataManager = new LiveDataManager();
            }
        }
        return liveDataManager;
    }

    private LiveDataManager() {
    }

    public void putLiveData(String key, Object object) {
        mLiveDatas.put(key, object);
    }

    public Object getLiveData(String key) {
        return mLiveDatas.get(key);
    }

    public static <T> WnLiveData<T> getLiveData(Class<T> t){
        if (wnLiveData == null) {
            synchronized (LOCK) {
                wnLiveData = new WnLiveData<T>();
            }
        }
        return (WnLiveData<T>)wnLiveData;
    }
}
