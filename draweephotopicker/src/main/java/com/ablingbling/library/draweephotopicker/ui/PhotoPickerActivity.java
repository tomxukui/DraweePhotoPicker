package com.ablingbling.library.draweephotopicker.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.ablingbling.library.draweephotopicker.PhotoPicker;
import com.ablingbling.library.draweephotopicker.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ablingbling.library.draweephotopicker.adapter.PhotoPickerAdapter;
import com.ablingbling.library.draweephotopicker.adapter.PopupDirectoryListAdapter;
import com.ablingbling.library.draweephotopicker.entity.Photo;
import com.ablingbling.library.draweephotopicker.entity.PhotoDirectory;
import com.ablingbling.library.draweephotopicker.event.OnItemCheckListener;
import com.ablingbling.library.draweephotopicker.event.OnPhotoClickListener;
import com.ablingbling.library.draweephotopicker.utils.ImageCaptureManager;
import com.ablingbling.library.draweephotopicker.utils.MediaStoreHelper;
import com.facebook.drawee.backends.pipeline.Fresco;

public class PhotoPickerActivity extends AppCompatActivity {

    private final static int MAX_COUNT = 4;
    private final static int COLUMN_MAX_NUMBER = 4;
    private static final int POPUP_MAX_COUNT = 4;//目录弹出框的一次最多显示的目录数目

    private TextView tv_title;
    private RecyclerView recyclerView;
    private TextView tv_dir;

    private PhotoPickerAdapter mPhotoPickerAdapter;
    private PopupDirectoryListAdapter mDirPopupListAdapter;
    private ListPopupWindow mListPopupWindow;
    private RequestManager mGlideRequestManager;
    private ImageCaptureManager mCaptureManager;

    private List<PhotoDirectory> mDirectories;
    private ArrayList<String> mOriginalPhotos;

    private boolean mShowCamera;
    private boolean mShowGif;
    private boolean mPreviewEnabled;
    private int mMaxCount;
    private int mColumnNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_activity_photo_picker);
        initData();
        initActionBar();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picker_menu_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.action_done) {
            ArrayList<String> selectedPhotos = mPhotoPickerAdapter.getSelectedPhotoPaths();
            if (selectedPhotos == null) {
                selectedPhotos = new ArrayList<>();
            }

            Intent intent = new Intent();
            intent.putStringArrayListExtra(PhotoPicker.EXTRA_SELECTED_PHOTOS, selectedPhotos);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case ImageCaptureManager.REQUEST_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    mCaptureManager.galleryAddPic();
                    if (mDirectories.size() > 0) {
                        String path = mCaptureManager.getCurrentPhotoPath();
                        PhotoDirectory directory = mDirectories.get(MediaStoreHelper.INDEX_ALL_PHOTOS);
                        directory.getPhotos().add(MediaStoreHelper.INDEX_ALL_PHOTOS, new Photo(path.hashCode(), path));
                        directory.setCoverPath(path);
                        mPhotoPickerAdapter.notifyDataSetChanged();
                    }
                }
            }
            break;

            default:
                break;

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mCaptureManager.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCaptureManager.onRestoreInstanceState(savedInstanceState);
    }

    private void initData() {
        mShowCamera = getIntent().getBooleanExtra(PhotoPicker.EXTRA_SHOW_CAMERA, true);
        mShowGif = getIntent().getBooleanExtra(PhotoPicker.EXTRA_SHOW_GIF, false);
        mPreviewEnabled = getIntent().getBooleanExtra(PhotoPicker.EXTRA_PREVIEW_ENABLED, true);
        mMaxCount = getIntent().getIntExtra(PhotoPicker.EXTRA_MAX_COUNT, MAX_COUNT);
        mColumnNumber = getIntent().getIntExtra(PhotoPicker.EXTRA_GRID_COLUMN, COLUMN_MAX_NUMBER);
        mOriginalPhotos = getIntent().getStringArrayListExtra(PhotoPicker.EXTRA_ORIGINAL_PHOTOS);

        mGlideRequestManager = Glide.with(this);
        mDirectories = new ArrayList<>();
        if (mOriginalPhotos == null) {
            mOriginalPhotos = new ArrayList<>();
        }

        mPhotoPickerAdapter = new PhotoPickerAdapter(this, mDirectories, mOriginalPhotos, mColumnNumber);
        mPhotoPickerAdapter.setShowCamera(mShowCamera);
        mPhotoPickerAdapter.setPreviewEnable(mPreviewEnabled);
        mPhotoPickerAdapter.setOnItemCheckListener(new OnItemCheckListener() {

            @Override
            public boolean onItemCheck(int position, Photo photo, int selectedItemCount) {
                if (mMaxCount <= 1) {
                    List<String> photos = mPhotoPickerAdapter.getSelectedPhotos();
                    if (!photos.contains(photo.getPath())) {
                        photos.clear();
                        mPhotoPickerAdapter.notifyDataSetChanged();
                    }
                    return true;
                }

                if (selectedItemCount > mMaxCount) {
                    Toast.makeText(PhotoPickerActivity.this, String.format("最多可以选择%d张", mMaxCount), Toast.LENGTH_LONG).show();
                    return false;
                }

                setTitleView(selectedItemCount);
                return true;
            }

        });
        mPhotoPickerAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {

            @Override
            public void onClick(View v, int position, boolean showCamera) {
                int index = showCamera ? position - 1 : position;
                List<String> photoPaths = mPhotoPickerAdapter.getCurrentPhotoPaths();
                String photoPath = photoPaths.get(index);

                Intent intent = new Intent(PhotoPickerActivity.this, PhotoZoomActivity.class);
                intent.putExtra(PhotoZoomActivity.EXTRA_SELECTED_PHOTO, photoPath);
                startActivity(intent);
            }

        });
        mPhotoPickerAdapter.setOnCameraClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                openCamera();
            }

        });

        mDirPopupListAdapter = new PopupDirectoryListAdapter(mGlideRequestManager, mDirectories);

        mCaptureManager = new ImageCaptureManager(this);

        Bundle mediaStoreArgs = new Bundle();
        mediaStoreArgs.putBoolean(PhotoPicker.EXTRA_SHOW_GIF, mShowGif);
        MediaStoreHelper.getPhotoDirs(this, mediaStoreArgs, new MediaStoreHelper.PhotosResultCallback() {

            @Override
            public void onResultCallback(List<PhotoDirectory> dirs) {
                mDirectories.clear();
                mDirectories.addAll(dirs);
                mPhotoPickerAdapter.notifyDataSetChanged();
                mDirPopupListAdapter.notifyDataSetChanged();
                adjustHeight();
            }

        });
    }

    private void initActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv_title = findViewById(R.id.tv_title);
        setTitleView(mOriginalPhotos.size());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示默认标题
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回键
        }
    }

    private void initView() {
        tv_dir = findViewById(R.id.tv_dir);
        tv_dir.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListPopupWindow.isShowing()) {
                    mListPopupWindow.dismiss();

                } else if (!isFinishing()) {
                    adjustHeight();
                    mListPopupWindow.show();
                }
            }

        });

        recyclerView = findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(mColumnNumber, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mPhotoPickerAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int preScrollState;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {

                    case RecyclerView.SCROLL_STATE_IDLE: {//停止滑动
                        if (Fresco.getImagePipeline().isPaused()) {
                            Fresco.getImagePipeline().resume();
                        }
                    }
                    break;

                    case RecyclerView.SCROLL_STATE_DRAGGING: {
                        if (preScrollState == RecyclerView.SCROLL_STATE_SETTLING) {
                            Fresco.getImagePipeline().pause();//触摸滑动不需要加载

                        } else {
                            if (Fresco.getImagePipeline().isPaused()) {//触摸滑动需要加载
                                Fresco.getImagePipeline().resume();
                            }
                        }
                    }
                    break;

                    case RecyclerView.SCROLL_STATE_SETTLING: {//惯性滑动
                        Fresco.getImagePipeline().pause();
                    }
                    break;

                    default:
                        break;

                }

                preScrollState = newState;
            }

        });

        mListPopupWindow = new ListPopupWindow(this);
        mListPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        mListPopupWindow.setAnchorView(tv_dir);
        mListPopupWindow.setAdapter(mDirPopupListAdapter);
        mListPopupWindow.setModal(true);
        mListPopupWindow.setDropDownGravity(Gravity.BOTTOM);
        mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListPopupWindow.dismiss();
                PhotoDirectory directory = mDirectories.get(position);
                tv_dir.setText(directory.getName());
                mPhotoPickerAdapter.setCurrentDirectoryIndex(position);
                mPhotoPickerAdapter.notifyDataSetChanged();
            }

        });
    }

    private void setTitleView(int count) {
        if (mMaxCount > 1) {
            tv_title.setText(String.format("选择图片(%d/%d)", count, mMaxCount));

        } else {
            tv_title.setText("选择图片");
        }
    }

    private void openCamera() {
        try {
            Intent intent = mCaptureManager.dispatchTakePictureIntent();
            startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void adjustHeight() {
        if (mDirPopupListAdapter == null) {
            return;
        }

        int count = Math.min(mDirPopupListAdapter.getCount(), POPUP_MAX_COUNT);

        if (mListPopupWindow != null) {
            mListPopupWindow.setHeight(count * getResources().getDimensionPixelOffset(R.dimen.picker_item_directory_height));
        }
    }

}
