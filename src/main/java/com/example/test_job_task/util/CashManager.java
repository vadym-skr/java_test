package com.example.test_job_task.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CashManager {
    private final Map<String, Object> cache;

    public CashManager() {
        this.cache = new HashMap<>();
    }
    public CashManager(HashMap<String, Object> hashMap) {
        this.cache = hashMap;
    }

    public void addToCache(String key, Object value) {
        cache.put(key, value);
    }

    public Object getFromCache(String key) {
        return cache.get(key);
    }

    public boolean isInCache(String key) {
        return cache.containsKey(key);
    }

    public void removeFromCache(String key) {
        cache.remove(key);
    }

    public void clearCache() {
        cache.clear();
    }
}
