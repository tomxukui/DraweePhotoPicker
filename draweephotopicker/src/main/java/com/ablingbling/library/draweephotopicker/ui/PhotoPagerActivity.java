package com.ablingbling.library.draweephotopicker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ablingbling.library.draweephotopicker.PhotoPreview;
import com.ablingbling.library.draweephotopicker.R;

import java.util.ArrayList;

import com.ablingbling.library.draweephotopicker.adapter.PhotoPagerAdapter;

/**
 * Created by donglua on 15/6/24.
 */
public class PhotoPagerActivity extends AppCompatActivity {

    private TextView tv_title;
    private ViewPager viewPager;

    private PhotoPagerAdapter mPagerAdapter;

    private int mCurrentIndex;
    private ArrayList<String> mPhotoPaths;
    private boolean mShowDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_activity_photo_pager);
        initData();
        initActionBar();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPager.removeOnPageChangeListener(mOnPageChangeListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picker_menu_preview, menu);
        menu.findItem(R.id.action_delete).setVisible(mShowDelete);
        return true;
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(PhotoPreview.EXTRA_PHOTO_PATHS, mPhotoPaths);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.action_delete) {
            final int index = viewPager.getCurrentItem();

            if (mPhotoPaths.size() <= 1) {
                mPhotoPaths.remove(index);
                mPagerAdapter.notifyDataSetChanged();

                finish();

            } else {
                Snackbar.make(viewPager, "删除了一张图片", Snackbar.LENGTH_SHORT).show();

                mPhotoPaths.remove(index);
                mPagerAdapter.notifyDataSetChanged();

                setTitleView(viewPager.getCurrentItem());
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        mCurrentIndex = getIntent().getIntExtra(PhotoPreview.EXTRA_CURRENT_ITEM, 0);
        mPhotoPaths = getIntent().getStringArrayListExtra(PhotoPreview.EXTRA_PHOTO_PATHS);
        mShowDelete = getIntent().getBooleanExtra(PhotoPreview.EXTRA_SHOW_DELETE, true);
        if (mPhotoPaths == null) {
            mPhotoPaths = new ArrayList<>();
        }

        mPagerAdapter = new PhotoPagerAdapter(mPhotoPaths);
    }

    private void initActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv_title = findViewById(R.id.tv_title);
        setTitleView(mCurrentIndex);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示默认标题
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回键
        }
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(mCurrentIndex);
        viewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    private void setTitleView(int pos) {
        tv_title.setText(String.format("%d/%d", pos + 1, mPhotoPaths.size()));
    }

    private final ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            setTitleView(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

    };

}
