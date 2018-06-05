package com.ablingbling.library.draweephotopicker.ui;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ablingbling.library.draweephotopicker.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;

import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by xukui on 2018/5/18.
 */
public class PhotoZoomActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTED_PHOTO = "EXTRA_SELECTED_PHOTO";

    private PhotoDraweeView iv_img;

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

        Uri uri = Uri.parse(mImgPath.startsWith("http") ? mImgPath : ("file://" + mImgPath));
        DraweeController controller = Fresco
                .newDraweeControllerBuilder()
                .setUri(uri)
                .setOldController(iv_img.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>() {

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if (imageInfo != null) {
                            iv_img.update(imageInfo.getWidth(), imageInfo.getHeight());
                        }
                    }

                })
                .build();
        iv_img.setController(controller);
    }

}