package com.example.ocrdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lzy.okgo.OkGo;
import com.vondear.rxtools.RxTool;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RxTool.init(this);
        OkGo.getInstance().init(getApplication());
    }

}
