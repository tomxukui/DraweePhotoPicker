package com.ablingbling.app.draweephotopicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ablingbling.app.draweephotopicker.permission.DefaultRationale;
import com.ablingbling.app.draweephotopicker.permission.PermissionSetting;
import com.ablingbling.library.ninegridlayout.CreateNineGridLayout;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;

import java.util.ArrayList;
import java.util.List;

import com.ablingbling.library.draweephotopicker.PhotoPicker;
import com.ablingbling.library.draweephotopicker.PhotoPreview;

public class MainActivity extends AppCompatActivity {

    private static final int SPAN_COUNT = 3;
    private static final int MAX_COUNT = 9;
    private static final int REQUEST_PICK_IMG = 1;
    private static final int REQUEST_PREVIEW_IMG = 2;

    private CreateDraweeNineGridLayout grid_create;

    private Rationale mRationale;
    private PermissionSetting mPermissionSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_PICK_IMG: {
                if (resultCode == RESULT_OK) {
                    List<String> imgPaths = data.getStringArrayListExtra(PhotoPicker.EXTRA_SELECTED_PHOTOS);
                    grid_create.setNewData(imgPaths);
                }
            }
            break;

            case REQUEST_PREVIEW_IMG: {
                if (resultCode == RESULT_OK) {
                    List<String> imgPaths = data.getStringArrayListExtra(PhotoPreview.EXTRA_PHOTO_PATHS);
                    grid_create.setNewData(imgPaths);
                }
            }
            break;

            default:
                break;

        }
    }

    private void initData() {
        mRationale = new DefaultRationale();
        mPermissionSetting = new PermissionSetting(this);
    }

    private void initView() {
        grid_create = findViewById(R.id.grid_create);

        grid_create.setMaxCount(MAX_COUNT);
        grid_create.setMaxColumn(SPAN_COUNT);
        grid_create.setOnAddClickListener(new CreateNineGridLayout.OnAddClickListener() {

            @Override
            public void onAddClick(CreateNineGridLayout createNineGridLayout, ImageView imageView) {
                pickImgsWithPermission();
            }

        });
        grid_create.setOnItemClickListener(new CreateNineGridLayout.OnItemClickListener() {

            @Override
            public void onItemClick(CreateNineGridLayout createNineGridLayout, View view, int i, String s) {
                previewImgs(i);
            }

        });
        grid_create.notifyDataSetChanged();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setGridColumnCount(3)
                        .start(MainActivity.this, REQUEST_PICK_IMG);
            }

        });

        findViewById(R.id.button_no_camera).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(7)
                        .setShowCamera(false)
                        .setPreviewEnabled(false)
                        .start(MainActivity.this, REQUEST_PICK_IMG);
            }

        });

        findViewById(R.id.button_one_photo).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .start(MainActivity.this, REQUEST_PICK_IMG);
            }

        });

        findViewById(R.id.button_photo_gif).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setShowCamera(true)
                        .setShowGif(true)
                        .start(MainActivity.this, REQUEST_PICK_IMG);
            }

        });
    }

    private void pickImgs() {
        PhotoPicker.builder()
                .setPhotoCount(MAX_COUNT)
                .setShowGif(false)
                .setPreviewEnabled(true)
                .setGridColumnCount(4)
                .setShowCamera(true)
                .setSelected((ArrayList<String>) grid_create.getImages())
                .start(this, REQUEST_PICK_IMG);
    }

    private void pickImgsWithPermission() {
        AndPermission.with(this)
                .permission(Permission.Group.STORAGE, Permission.Group.CAMERA)
                .rationale(mRationale)
                .onGranted(new Action() {

                    @Override
                    public void onAction(List<String> permissions) {
                        pickImgs();
                    }

                })
                .onDenied(new Action() {

                    @Override
                    public void onAction(List<String> permissions) {
                        Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT).show();

                        if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, permissions)) {
                            mPermissionSetting.showSetting(permissions);
                        }
                    }

                })
                .start();
    }

    private void previewImgs(int position) {
        PhotoPreview.builder()
                .setPhotos((ArrayList<String>) grid_create.getImages())
                .setCurrentItem(position)
                .setShowDeleteButton(true)
                .start(this, REQUEST_PREVIEW_IMG);
    }

}
