package com.ablingbling.library.draweephotopicker.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

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
public class PhotoGridAdapter extends SelectableAdapter<PhotoGridAdapter.PhotoViewHolder> {

    public final static int ITEM_TYPE_CAMERA = 100;
    public final static int ITEM_TYPE_PHOTO = 101;
    private final static int COL_NUMBER_DEFAULT = 3;

    private boolean mHasCamera = true;
    private boolean mPreviewEnable = true;
    private int mImageSize;
    private int mColumnNumber = COL_NUMBER_DEFAULT;

    private OnItemCheckListener onItemCheckListener = null;
    private OnPhotoClickListener onPhotoClickListener = null;
    private View.OnClickListener onCameraClickListener = null;

    public PhotoGridAdapter(Context context, List<PhotoDirectory> photoDirectories) {
        this.photoDirectories = photoDirectories;
        setColumnNumber(context, mColumnNumber);
    }

    public PhotoGridAdapter(Context context, List<PhotoDirectory> photoDirectories, ArrayList<String> orginalPhotos, int colNum) {
        this(context, photoDirectories);
        setColumnNumber(context, colNum);
        selectedPhotos = new ArrayList<>();
        if (orginalPhotos != null) {
            selectedPhotos.addAll(orginalPhotos);
        }
    }

    private void setColumnNumber(Context context, int columnNumber) {
        mColumnNumber = columnNumber;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        mImageSize = widthPixels / columnNumber;
    }

    @Override
    public int getItemViewType(int position) {
        return (showCamera() && position == 0) ? ITEM_TYPE_CAMERA : ITEM_TYPE_PHOTO;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.picker_item_grid_photo, parent, false);
        final PhotoViewHolder holder = new PhotoViewHolder(itemView);
        if (viewType == ITEM_TYPE_CAMERA) {
            holder.vSelected.setVisibility(View.GONE);
            holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCameraClickListener != null) {
                        onCameraClickListener.onClick(view);
                    }
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_TYPE_PHOTO) {
            List<Photo> photos = getCurrentPhotos();
            final Photo photo = photos.get(showCamera() ? (position - 1) : position);
            Uri uri = Uri.parse("file://" + photo.getPath());

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(mImageSize, mImageSize))
                    .build();

            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(holder.ivPhoto.getController())
                    .setImageRequest(request)
                    .build();

            holder.ivPhoto.setController(controller);

            boolean isChecked = isSelected(photo);

            holder.vSelected.setSelected(isChecked);
            holder.ivPhoto.setSelected(isChecked);
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (onPhotoClickListener != null) {
                        int pos = holder.getAdapterPosition();
                        if (mPreviewEnable) {
                            onPhotoClickListener.onClick(view, pos, showCamera());
                        } else {
                            holder.vSelected.performClick();
                        }
                    }
                }

            });
            holder.vSelected.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int pos = holder.getAdapterPosition();
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

        } else {
            holder.ivPhoto.setImageResource(R.mipmap.picker_ic_camera);
        }
    }

    @Override
    public int getItemCount() {
        int photosCount = (photoDirectories.size() == 0 ? 0 : getCurrentPhotos().size());
        if (showCamera()) {
            return photosCount + 1;
        }
        return photosCount;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView ivPhoto;
        private View vSelected;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            vSelected = itemView.findViewById(R.id.v_selected);
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

        for (String photo : selectedPhotos) {
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
        return (mHasCamera && currentDirectoryIndex == MediaStoreHelper.INDEX_ALL_PHOTOS);
    }

}
