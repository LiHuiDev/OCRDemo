package com.example.ocrdemo.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Recond extends DataSupport implements Serializable{

    private String flag;
    private String code;
    private String message;
    private String path;
    private String result;
    private Date creationTime;

    public Recond() {
    }

    public Recond(String flag, String code, String message, String path, String result, Date creationTime) {
        this.flag = flag;
        this.code = code;
        this.message = message;
        this.path = path;
        this.result = result;
        this.creationTime = creationTime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public String toString() {
        return "Recond{" +
                "flag='" + flag + '\'' +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", result='" + result + '\'' +
                ", creationTime=" + creationTime +
                '}';
    }
}
