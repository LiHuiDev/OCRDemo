package com.example.ocrdemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ocrdemo.model.Recond;
import com.example.ocrdemo.model.ResponseJSON;
import com.example.ocrdemo.utils.Util;

import static com.example.ocrdemo.utils.Util.showToast;

public class ShowResult extends AppCompatActivity {

    //    private final String URL = "http://192.168.232.220:8000";
//    private final String URL = "http://192.168.232.219:8000/";
        private final String URL = "http://192.168.43.10:8000/";

    private Toolbar toolbar;
    private TextView complete;
    private Recond recond;

    private EditText info;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        recond = (Recond) getIntent().getSerializableExtra("recond");

        initView();
        actionBar();

    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        complete = (TextView)findViewById(R.id.complete);
        info = (EditText) findViewById(R.id.info);
        imageView = (ImageView) findViewById(R.id.img);
    }

    private void actionBar(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("识别结果");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        StringBuilder responseInfoString = new StringBuilder();
//        for(int i = 0; i < recond.getResult().size(); i++){
//            responseInfoString.append(recond.getResult().get(i)+"\n");
//        }
        info.setText(recond.getResult());
        String path = URL+recond.getPath();
        Glide.with(this).load(path).fitCenter().into(imageView);

        //监听
        info.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                complete.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = info.getText().toString().trim();
                if(result.isEmpty()){
                    info.setError("此处不为空");
                }else{
                    Recond newRecond = new Recond();
                    newRecond.setResult(info.getText().toString());
                    int flag = newRecond.updateAll("code=? and message=? and path=? and result=?", recond.getCode(), recond.getMessage(), recond.getPath(), recond.getResult());
                    if(flag > 0){
                        showToast(ShowResult.this, "保存成功");
                        finish();
                    } else {
                        showToast(ShowResult.this, "保存失败，请重试！");
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
