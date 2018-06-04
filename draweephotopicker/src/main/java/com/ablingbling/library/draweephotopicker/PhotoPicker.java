package com.ablingbling.library.draweephotopicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.ablingbling.library.draweephotopicker.ui.PhotoPickerActivity;

import java.util.ArrayList;

public class PhotoPicker {

    public final static int DEFAULT_MAX_COUNT = 9;
    public final static int DEFAULT_COLUMN_NUMBER = 3;

    public final static String EXTRA_MAX_COUNT = "EXTRA_MAX_COUNT";
    public final static String EXTRA_SHOW_CAMERA = "EXTRA_SHOW_CAMERA";
    public final static String EXTRA_SHOW_GIF = "EXTRA_SHOW_GIF";
    public final static String EXTRA_GRID_COLUMN = "EXTRA_GRID_COLUMN";
    public final static String EXTRA_ORIGINAL_PHOTOS = "EXTRA_ORIGINAL_PHOTOS";
    public final static String EXTRA_PREVIEW_ENABLED = "EXTRA_PREVIEW_ENABLED";
    public final static String EXTRA_SELECTED_PHOTOS = "EXTRA_SELECTED_PHOTOS";

    public static PhotoPickerBuilder builder() {
        return new PhotoPickerBuilder();
    }

    public static class PhotoPickerBuilder {

        private Bundle mBundle;

        public PhotoPickerBuilder() {
            mBundle = new Bundle();
        }

        public Intent getIntent(@NonNull Context context) {
            Intent intent = new Intent(context, PhotoPickerActivity.class);
            intent.putExtras(mBundle);
            return intent;
        }

        public PhotoPickerBuilder setPhotoCount(int photoCount) {
            mBundle.putInt(EXTRA_MAX_COUNT, photoCount);
            return this;
        }

        public PhotoPickerBuilder setGridColumnCount(int columnCount) {
            mBundle.putInt(EXTRA_GRID_COLUMN, columnCount);
            return this;
        }

        public PhotoPickerBuilder setShowGif(boolean showGif) {
            mBundle.putBoolean(EXTRA_SHOW_GIF, showGif);
            return this;
        }

        public PhotoPickerBuilder setShowCamera(boolean showCamera) {
            mBundle.putBoolean(EXTRA_SHOW_CAMERA, showCamera);
            return this;
        }

        public PhotoPickerBuilder setSelected(ArrayList<String> imagesUri) {
            mBundle.putStringArrayList(EXTRA_ORIGINAL_PHOTOS, imagesUri);
            return this;
        }

        public PhotoPickerBuilder setPreviewEnabled(boolean previewEnabled) {
            mBundle.putBoolean(EXTRA_PREVIEW_ENABLED, previewEnabled);
            return this;
        }

        public void start(@NonNull Activity activity, int requestCode) {
            activity.startActivityForResult(getIntent(activity), requestCode);
        }

        public void start(@NonNull Context context, @NonNull Fragment fragment, int requestCode) {
            fragment.startActivityForResult(getIntent(context), requestCode);
        }

    }

}
