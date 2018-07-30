package com.example.ocrdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.ocrdemo.model.ResponseJSON;
import com.example.ocrdemo.model.Recond;
import com.example.ocrdemo.utils.GlideImageLoader;
import com.example.ocrdemo.utils.NumberProgressBar;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.vondear.rxtools.view.dialog.RxDialogScaleView;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.example.ocrdemo.utils.Util.showToast;

public class MainFragment extends Fragment {

//    private final String URL = "http://192.168.232.220:8000";
//    private final String URL = "http://192.168.232.219:8000/";
    private final String URL = "http://192.168.43.10:8000/";

    private View rootView;
    private Context context;

    private Button btnSelectImage;
    private Button btnFormUpload;
    private TextView tvProgress;
    private TextView tvNetSpeed;
    private TextView tvDownloadSize;
    private NumberProgressBar pbProgress;
    private LinearLayout imageLinearLayout;
    private TextView imageInfo;
    private TextView compressImageInfo;
    private ImageView imageView;
    private ImageView compressImageView;
    private TextView responseInfo;
    private ImageView cropImageView;

    private NumberFormat numberFormat;
    private ProgressDialog progres;

    private ArrayList<String> imagesPath = new ArrayList<>();
    private ArrayList<String> compressImageList = new ArrayList<>();

    private ResponseJSON responseJSON;
    private Recond recond = new Recond();
    private StringBuilder string;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        context = inflater.getContext();

        initView();
        btnListener();
        imageViewListener();

        numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(2);

        return rootView;
    }

    public void initView(){
        btnSelectImage = (Button) rootView.findViewById(R.id.selectImage);
        btnFormUpload = (Button) rootView.findViewById(R.id.formUpload);
        tvDownloadSize = (TextView) rootView.findViewById(R.id.downloadSize);
        tvNetSpeed = (TextView) rootView.findViewById(R.id.netSpeed);
        tvProgress = (TextView) rootView.findViewById(R.id.tvProgress);
        pbProgress = (NumberProgressBar) rootView.findViewById(R.id.pbProgress);
        imageLinearLayout = (LinearLayout)rootView.findViewById(R.id.image_linearLayout);
        imageInfo = (TextView)rootView.findViewById(R.id.image_info);
        compressImageInfo = (TextView)rootView.findViewById(R.id.compress_image_info);
        imageView = (ImageView) rootView.findViewById(R.id.image);
        compressImageView = (ImageView)rootView.findViewById(R.id.compress_image);
        responseInfo = (TextView) rootView.findViewById(R.id.response_text);
        cropImageView = (ImageView) rootView.findViewById(R.id.crop_img);
    }

    public void btnListener(){
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker imagePicker = ImagePicker.getInstance();
                imagePicker.setImageLoader(new GlideImageLoader());
                imagePicker.setMultiMode(false);   //单选
                imagePicker.setShowCamera(true);  //显示拍照按钮
                imagePicker.setCrop(false);       //不进行裁剪
                Intent intent = new Intent(context, ImageGridActivity.class);
                startActivityForResult(intent, 100);

                btnFormUpload.setText("上传识别");
                tvDownloadSize.setText("--M/--M");
                tvNetSpeed.setText("---K/s");
                tvProgress.setText("--.--%");
                pbProgress.setProgress(0);
                responseInfo.setText("");
            }
        });

        btnFormUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(compressImageList.size() > 0){   //有压缩图片
                    if(isNetworkConnected(context)){    //有网

                        //网络请求
                        OkGo.<String>post(URL+"index/")
                                .tag(this)
                                .params("image", new File(imagesPath.get(0)))   //图片路径
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        btnFormUpload.setText("上传成功");

                                        responseJSON = JSON.parseObject(response.body(), ResponseJSON.class);
                                        if(!response.isFromCache()){
                                            StringBuilder responseInfoString = new StringBuilder();
                                            responseInfoString.append("返回信息");
                                            responseInfoString.append("\n状态码：" + responseJSON.getCode());
                                            responseInfoString.append("\n描述信息：" + responseJSON.getMessage());
                                            if(responseJSON.getCode().equals("1")){ //识别到图片
                                                responseInfoString.append("\n返回数据：\n");

                                                string = new StringBuilder();
                                                for(int i = 0; i < responseJSON.getResult().size(); i++){
                                                    string.append(responseJSON.getResult().get(i)+"\n");
                                                }
                                                responseInfoString.append(string.toString());
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //保存到数据库
                                                        recond.setFlag(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                                                        recond.setCode(responseJSON.getCode());
                                                        recond.setMessage(responseJSON.getMessage());
                                                        recond.setPath(responseJSON.getPath());
                                                        recond.setResult(string.toString());
                                                        recond.setCreationTime(new Date());
                                                        showToast(context, recond.toString());
                                                        if(recond.save()){
                                                            showToast(context, "保存成功");
                                                        }else{
                                                            showToast(context, "保存失败");
                                                        }

                                                        String path = URL+responseJSON.getPath();
                                                        cropImageView.setVisibility(View.VISIBLE);
                                                        Glide.with(context).load(path).fitCenter().into(cropImageView);
                                                        progres.dismiss();
                                                    }
                                                });
                                            }
                                            responseInfo.setText(responseInfoString);
                                        }else{
                                            showToast(context,"数据来自缓存");
                                        }
                                    }

                                    @Override
                                    public void onStart(Request<String, ? extends Request> request) {
                                        super.onStart(request);
                                        btnFormUpload.setText("开始处理");
                                    }

                                    @Override
                                    public void onError(Response<String> response) {
                                        super.onError(response);
                                        btnFormUpload.setText("处理错误");
                                        if(!response.isFromCache()){
                                            StringBuilder responseInfoString = new StringBuilder();
                                            responseInfoString.append("错误信息");
                                            responseInfoString.append("\n状态码：" + response.code());
                                            responseInfoString.append("\n描述信息：" + response.message());
                                            responseInfoString.append("\n异常信息：" + response.getException().getMessage());
                                            responseInfo.setText(responseInfoString);
                                        }else{
                                            showToast(context,"数据来自缓存");
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        super.onFinish();
                                        btnFormUpload.setText("处理完成");
                                        progres.dismiss();
                                    }

                                    @Override
                                    public void uploadProgress(Progress progress) {
                                        super.uploadProgress(progress);
                                        btnFormUpload.setText("正在处理中...");
                                        String downloadLength = Formatter.formatFileSize(context, progress.currentSize);
                                        String totalLength = Formatter.formatFileSize(context, progress.totalSize);
                                        tvDownloadSize.setText(downloadLength + "/" + totalLength);
                                        String speed = Formatter.formatFileSize(context, progress.speed);
                                        tvNetSpeed.setText(String.format("%s/s", speed));
                                        tvProgress.setText(numberFormat.format(progress.fraction));
                                        pbProgress.setMax(10000);
                                        pbProgress.setProgress((int) (progress.fraction * 10000));

                                        if(progress.fraction == 1){
                                            progres = new ProgressDialog(context);
                                            progres.setMessage("正在拼命处理中...");
                                            progres.setCancelable(false);
                                            progres.show();
                                        }
                                    }
                                });
                    }else{
                        showToast(context, "没有网络呀");
                    }
                }else{
                    showToast(context, "没有选择图片呀");
                }
            }
        });
    }

    public void imageViewListener(){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxDialogScaleView rxDialogScaleView = new RxDialogScaleView(context);
                rxDialogScaleView.setImagePath(imagesPath.get(0));
                rxDialogScaleView.show();
            }
        });
        compressImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxDialogScaleView rxDialogScaleView = new RxDialogScaleView(context);
                rxDialogScaleView.setImagePath(compressImageList.get(0));
                rxDialogScaleView.show();
            }
        });
        cropImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowResult.class);
                intent.putExtra("recond", recond);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 100:
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    if (data != null) {
                        ArrayList<ImageItem>imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        if (imageItems != null && imageItems.size() > 0) {
                            imageLinearLayout.setVisibility(View.VISIBLE);
                            imagesPath.clear();
                            compressImageList.clear();
                            for (int i = 0; i < imageItems.size(); i++) {
                                imagesPath.add(imageItems.get(i).path);
                            }
                            File image = new File(imagesPath.get(0));
                            Glide.with(this).load(image).fitCenter().into(imageView);
                            int[] imageSize = computeSize(image.getAbsolutePath());
                            StringBuilder imageString = new StringBuilder();
                            imageString.append("原图信息");
                            imageString.append("\n名称： " + image.getName());
                            imageString.append("\n大小： " + Formatter.formatFileSize(context, image.length()));
                            imageString.append("\n尺寸： " + imageSize[0] + "*" + imageSize[1]);
//                            imageString.append("\n类型： " + image.mimeType);
                            imageString.append("\n路径： " + image.getAbsolutePath());
                            imageInfo.setText(imageString.toString());

                            Luban.with(context)
                                    .load(image.getAbsolutePath())
                                    .ignoreBy(100)
                                    .putGear(80)
                                    .setTargetDir(getPath())
                                    .setCompressListener(new OnCompressListener() {
                                        @Override
                                        public void onStart() {

                                        }

                                        @Override
                                        public void onSuccess(File file) {
                                            compressImageList.add(file.getAbsolutePath());

                                            Glide.with(context).load(file).fitCenter().into(compressImageView);
                                            int[] thumbSize = computeSize(file.getAbsolutePath());
                                            StringBuilder compressImageString = new StringBuilder();
                                            compressImageString.append("压缩后图片信息");
                                            compressImageString.append("\n名称： " + file.getName());
                                            compressImageString.append("\n大小： " + Formatter.formatFileSize(context, file.length()));
                                            compressImageString.append("\n尺寸： " + thumbSize[0] + "*" + thumbSize[1]);
//                                            compressImageString.append("\n类型： " + file.mimeType);
                                            compressImageString.append("\n路径： " + file.getAbsolutePath());
                                            compressImageInfo.setText(compressImageString.toString());
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }
                                    }).launch();
                        } else {
                            showToast(context, "没有选择图片");
                        }
                    } else {
                        showToast(context, "没有选择图片");
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }

    //压缩后图片保存路径
    private String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/Luban/images/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }

    //计算图片尺寸
    private int[] computeSize(String srcImg) {
        int[] size = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;

        BitmapFactory.decodeFile(srcImg, options);
        size[0] = options.outWidth;
        size[1] = options.outHeight;

        return size;
    }

    //判断有无网络
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
