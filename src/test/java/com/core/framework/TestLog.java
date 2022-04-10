package com.core.framework;

import com.aventstack.extentreports.Status;

public class TestLog {
    public Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TestLog{" +
                "status=" + status +
                '}';
    }
}
