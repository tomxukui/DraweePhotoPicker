package com.ablingbling.library.draweephotopicker.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.ablingbling.library.draweephotopicker.R;
import com.ablingbling.library.draweephotopicker.entity.Photo;
import com.ablingbling.library.draweephotopicker.entity.PhotoDirectory;
import com.ablingbling.library.draweephotopicker.event.OnItemCheckListener;
import com.ablingbling.library.draweephotopicker.event.OnPhotoClickListener;
import com.ablingbling.library.draweephotopicker.utils.MediaStoreHelper;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by donglua on 15/5/31.
 */
public class PhotoPickerAdapter extends SelectableAdapter<PhotoPickerAdapter.ViewHolder> {

    private final static int TYPE_CAMERA = 0;
    private final static int TYPE_PHOTO = 1;
    private final static int COLUMN_MAX_NUMBER = 4;

    private boolean mHasCamera;
    private boolean mPreviewEnable;
    private int mImageSize;
    private int mColumnNumber;
    private String mPackageName;

    private OnItemCheckListener onItemCheckListener;
    private OnPhotoClickListener onPhotoClickListener;
    private View.OnClickListener onCameraClickListener;

    public PhotoPickerAdapter(Context context, List<PhotoDirectory> photoDirectories, ArrayList<String> orginalPhotos, int colNum) {
        mPackageName = context.getPackageName();
        mHasCamera = true;
        mPreviewEnable = true;
        mColumnNumber = colNum;
        int widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
        mImageSize = widthPixels / mColumnNumber;

        mPhotoDirectories = (photoDirectories == null ? new ArrayList<PhotoDirectory>() : photoDirectories);
        mSelectedPhotos.clear();
        if (orginalPhotos != null) {
            mSelectedPhotos.addAll(orginalPhotos);
        }
    }

    public PhotoPickerAdapter(Context context, List<PhotoDirectory> photoDirectories) {
        this(context, photoDirectories, null, COLUMN_MAX_NUMBER);
    }

    @Override
    public int getItemViewType(int position) {
        return (showCamera() && position == 0) ? TYPE_CAMERA : TYPE_PHOTO;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picker_item_photo_picker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder vh, int position) {
        int type = getItemViewType(position);

        switch (type) {

            case TYPE_CAMERA: {
                vh.iv_selector.setVisibility(View.GONE);

                Uri uri = Uri.parse("res://" + mPackageName + "/" + R.mipmap.picker_ic_camera);
                vh.iv_photo.setImageURI(uri);
                vh.iv_photo.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (onCameraClickListener != null) {
                            onCameraClickListener.onClick(view);
                        }
                    }

                });
            }
            break;

            case TYPE_PHOTO: {
                final Photo photo = getCurrentPhotos().get(showCamera() ? (position - 1) : position);
                boolean isChecked = isSelected(photo);

                Uri uri = Uri.parse("file://" + photo.getPath());
                setDraweeView(uri, vh.iv_photo);
                vh.iv_photo.setSelected(isChecked);
                vh.iv_photo.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (onPhotoClickListener != null) {
                            int pos = vh.getAdapterPosition();

                            if (mPreviewEnable) {
                                onPhotoClickListener.onClick(view, pos, showCamera());

                            } else {
                                vh.iv_selector.performClick();
                            }
                        }
                    }

                });

                vh.iv_selector.setSelected(isChecked);
                vh.iv_selector.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        int pos = vh.getAdapterPosition();
                        boolean isEnable = true;

                        if (onItemCheckListener != null) {
                            isEnable = onItemCheckListener.onItemCheck(pos, photo,
                                    getSelectedPhotos().size() + (isSelected(photo) ? -1 : 1));
                        }

                        if (isEnable) {
                            toggleSelection(photo);
                            notifyItemChanged(pos);
                        }
                    }

                });
            }
            break;

            default:
                break;

        }
    }

    @Override
    public int getItemCount() {
        int photosCount = (mPhotoDirectories.size() == 0 ? 0 : getCurrentPhotos().size());
        return showCamera() ? (photosCount + 1) : photosCount;
    }

    private void setDraweeView(Uri uri, SimpleDraweeView iv) {
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(mImageSize, mImageSize))
                .build();

        PipelineDraweeController controller = (PipelineDraweeController) Fresco
                .newDraweeControllerBuilder()
                .setOldController(iv.getController())
                .setImageRequest(request)
                .build();

        iv.setController(controller);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView iv_photo;
        public View iv_selector;

        public ViewHolder(View view) {
            super(view);
            iv_photo = itemView.findViewById(R.id.iv_photo);
            iv_selector = itemView.findViewById(R.id.iv_selector);
        }

    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }

    public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }

    public ArrayList<String> getSelectedPhotoPaths() {
        ArrayList<String> selectedPhotoPaths = new ArrayList<>(getSelectedItemCount());

        for (String photo : mSelectedPhotos) {
            selectedPhotoPaths.add(photo);
        }

        return selectedPhotoPaths;
    }

    public void setShowCamera(boolean hasCamera) {
        mHasCamera = hasCamera;
    }

    public void setPreviewEnable(boolean previewEnable) {
        mPreviewEnable = previewEnable;
    }

    public boolean showCamera() {
        return (mHasCamera && mCurrentDirectoryIndex == MediaStoreHelper.INDEX_ALL_PHOTOS);
    }

}