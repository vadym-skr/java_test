package com.example.test_job_task.database.repository.impl;

import com.example.test_job_task.database.entity.BaseEntity;
import com.example.test_job_task.database.repository.BaseRepository;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseRepositoryImpl<T extends BaseEntity> implements BaseRepository<T> {

    protected static Long currentId = 0L;

    protected Map<Long, T> data;

    public BaseRepositoryImpl(Map<Long, T> data) {
        this.data = data;
    }

    @Override
    public T getById(Long id) {
        return data.get(id);
    }

    @Override
    public boolean existsById(Long id) {
        return data.containsKey(id);
    }

    @Override
    public boolean save(T entity) {
        if (data.containsKey(currentId)) {
            return false;
        }
        return data.put(currentId++, entity) != null;
    }

    @Override
    public boolean update(T entity) {
        if (!data.containsKey(entity.getId())) {
            return false;
        }
        return data.put(entity.getId(), entity) != null;
    }

    @Override
    public boolean remove(Long id) {
        return data.remove(id) != null;
    }
}
