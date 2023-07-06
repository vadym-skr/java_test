package com.example.test_job_task.database.repository;

import com.example.test_job_task.database.entity.BaseEntity;

public interface BaseRepository<T extends BaseEntity> {

    T getById(Long id);

    boolean existsById(Long id);

    boolean save(T entity);

    boolean update(T entity);

    boolean remove(Long id);
}
