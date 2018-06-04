package com.ablingbling.app.draweephotopicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.ablingbling.app.draweephotopicker.permission.DefaultRationale;
import com.ablingbling.app.draweephotopicker.permission.PermissionSetting;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;

import java.util.ArrayList;
import java.util.List;

import com.ablingbling.library.draweephotopicker.PhotoPicker;
import com.ablingbling.library.draweephotopicker.PhotoPreview;

public class MainActivity extends AppCompatActivity {

    private static final int EXTRA_PICK = 1;
    private static final int EXTRA_PREVIEW = 2;

    private PhotoAdapter photoAdapter;

    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private Rationale mRationale;
    private PermissionSetting mPermissionSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRationale = new DefaultRationale();
        mPermissionSetting = new PermissionSetting(this);

        AndPermission.with(this)
                .permission(Permission.Group.STORAGE, Permission.Group.CAMERA)
                .rationale(mRationale)
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

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        photoAdapter = new PhotoAdapter(this, selectedPhotos);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setGridColumnCount(4)
                        .start(MainActivity.this, EXTRA_PICK);
            }

        });

        findViewById(R.id.button_no_camera).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(7)
                        .setShowCamera(false)
                        .setPreviewEnabled(false)
                        .start(MainActivity.this, EXTRA_PICK);
            }

        });

        findViewById(R.id.button_one_photo).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .start(MainActivity.this, EXTRA_PICK);
            }

        });

        findViewById(R.id.button_photo_gif).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setShowCamera(true)
                        .setShowGif(true)
                        .start(MainActivity.this, EXTRA_PICK);
            }

        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (photoAdapter.getItemViewType(position) == PhotoAdapter.TYPE_ADD) {
                    PhotoPicker.builder()
                            .setPhotoCount(PhotoAdapter.MAX)
                            .setShowCamera(true)
                            .setPreviewEnabled(false)
                            .setSelected(selectedPhotos)
                            .start(MainActivity.this, EXTRA_PICK);

                } else {
                    PhotoPreview.builder()
                            .setPhotos(selectedPhotos)
                            .setCurrentItem(position)
                            .start(MainActivity.this, EXTRA_PREVIEW);
                }
            }

        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case EXTRA_PICK: {
                if (resultCode == RESULT_OK) {
                    List<String> photos = data.getStringArrayListExtra(PhotoPicker.EXTRA_SELECTED_PHOTOS);
                    selectedPhotos.clear();
                    if (photos != null) {
                        selectedPhotos.addAll(photos);
                    }
                    photoAdapter.notifyDataSetChanged();
                }
            }
            break;

            case EXTRA_PREVIEW: {
                if (resultCode == RESULT_OK) {
                    List<String> photos = data.getStringArrayListExtra(PhotoPreview.EXTRA_PHOTO_PATHS);
                    selectedPhotos.clear();
                    if (photos != null) {
                        selectedPhotos.addAll(photos);
                    }
                    photoAdapter.notifyDataSetChanged();
                }
            }
            break;

            default:
                break;

        }
    }

}
