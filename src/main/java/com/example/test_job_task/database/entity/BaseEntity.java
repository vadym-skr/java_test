package com.example.test_job_task.database.entity;

public abstract class BaseEntity {

    protected Long Id;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }
}
