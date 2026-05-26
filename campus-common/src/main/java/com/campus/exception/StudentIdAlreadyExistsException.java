package com.campus.exception;

/**
 * 学号已存在异常
 */
public class StudentIdAlreadyExistsException extends BaseException {

    public StudentIdAlreadyExistsException() {
    }

    public StudentIdAlreadyExistsException(String msg) {
        super(msg);
    }
}
