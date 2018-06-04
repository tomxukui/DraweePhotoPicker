package com.ablingbling.library.draweephotopicker.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ablingbling.library.draweephotopicker.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import com.ablingbling.library.draweephotopicker.utils.AndroidLifecycleUtils;
import com.ablingbling.library.draweephotopicker.widget.TouchImageView;

/**
 * Created by xukui on 2018/5/18.
 */
public class PhotoZoomActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTED_PHOTO = "EXTRA_SELECTED_PHOTO";

    private TouchImageView iv_img;

    private String mImgPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_activity_photo_zoom);
        initData();
        initView();
    }

    private void initData() {
        mImgPath = getIntent().getStringExtra(EXTRA_SELECTED_PHOTO);
    }

    private void initView() {
        iv_img = findViewById(R.id.iv_img);

        if (AndroidLifecycleUtils.canLoadImage(this)) {
            Uri uri = (mImgPath.startsWith("http") ? Uri.parse(mImgPath) : Uri.fromFile(new File(mImgPath)));

            RequestOptions options = new RequestOptions()
                    .dontAnimate()
                    .dontTransform()
                    .override(800, 800)
                    .error(R.mipmap.picker_ic_broken_img);

            Glide.with(this)
                    .setDefaultRequestOptions(options)
                    .load(uri)
                    .thumbnail(0.1f)
                    .into(iv_img);
        }
    }

}