package com.example.ocrdemo.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

public class ResponseJSON extends DataSupport implements Serializable{

    private String code;
    private String message;
    private String path;
    private List<String> result;

    public ResponseJSON() {
    }

    public ResponseJSON(String code, String message, String path, List<String> result) {
        this.code = code;
        this.message = message;
        this.path = path;
        this.result = result;
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

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }
}
