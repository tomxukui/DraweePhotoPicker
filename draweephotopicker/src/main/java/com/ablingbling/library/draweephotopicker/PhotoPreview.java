package com.ablingbling.library.draweephotopicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ablingbling.library.draweephotopicker.ui.PhotoPagerActivity;

import java.util.ArrayList;

public class PhotoPreview {

    public final static String EXTRA_CURRENT_ITEM = "EXTRA_CURRENT_ITEM";
    public final static String EXTRA_PHOTO_PATHS = "EXTRA_PHOTO_PATHS";
    public final static String EXTRA_SHOW_DELETE = "EXTRA_SHOW_DELETE";

    public static PhotoPreviewBuilder builder() {
        return new PhotoPreviewBuilder();
    }

    public static class PhotoPreviewBuilder {

        private Bundle mBundle;

        public PhotoPreviewBuilder() {
            mBundle = new Bundle();
        }

        public PhotoPreviewBuilder setPhotos(ArrayList<String> photoPaths) {
            mBundle.putStringArrayList(EXTRA_PHOTO_PATHS, photoPaths);
            return this;
        }

        public PhotoPreviewBuilder setCurrentItem(int currentItem) {
            mBundle.putInt(EXTRA_CURRENT_ITEM, currentItem);
            return this;
        }

        public PhotoPreviewBuilder setShowDeleteButton(boolean showDeleteButton) {
            mBundle.putBoolean(EXTRA_SHOW_DELETE, showDeleteButton);
            return this;
        }

        public Intent getIntent(Context context) {
            Intent intent = new Intent(context, PhotoPagerActivity.class);
            intent.putExtras(mBundle);
            return intent;
        }

        public void start(Activity activity) {
            if (activity != null) {
                activity.startActivity(getIntent(activity));
            }
        }

        public void start(Activity activity, int requestCode) {
            if (activity != null) {
                activity.startActivityForResult(getIntent(activity), requestCode);
            }
        }

        public void start(Fragment fragment) {
            if (fragment != null && fragment.getContext() != null) {
                fragment.startActivity(getIntent(fragment.getContext()));
            }
        }

        public void start(Fragment fragment, int requestCode) {
            if (fragment != null && fragment.getContext() != null) {
                fragment.startActivityForResult(getIntent(fragment.getContext()), requestCode);
            }
        }

    }

}
