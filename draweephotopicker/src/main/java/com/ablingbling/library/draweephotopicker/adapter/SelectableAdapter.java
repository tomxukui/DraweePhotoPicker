package com.ablingbling.library.draweephotopicker.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.ablingbling.library.draweephotopicker.entity.Photo;
import com.ablingbling.library.draweephotopicker.entity.PhotoDirectory;
import com.ablingbling.library.draweephotopicker.event.Selectable;

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements Selectable {

    protected List<PhotoDirectory> mPhotoDirectories;
    protected List<String> mSelectedPhotos;
    protected int mCurrentDirectoryIndex = 0;

    public SelectableAdapter() {
        mPhotoDirectories = new ArrayList<>();
        mSelectedPhotos = new ArrayList<>();
    }

    /**
     * Indicates if the item at position where is selected
     *
     * @param photo Photo of the item to check
     * @return true if the item is selected, false otherwise
     */
    @Override
    public boolean isSelected(Photo photo) {
        return getSelectedPhotos().contains(photo.getPath());
    }

    /**
     * Toggle the selection status of the item at a given position
     *
     * @param photo Photo of the item to toggle the selection status for
     */
    @Override
    public void toggleSelection(Photo photo) {
        if (mSelectedPhotos.contains(photo.getPath())) {
            mSelectedPhotos.remove(photo.getPath());
        } else {
            mSelectedPhotos.add(photo.getPath());
        }
    }

    /**
     * Clear the selection status for all items
     */
    @Override
    public void clearSelection() {
        mSelectedPhotos.clear();
    }

    /**
     * Count the selected items
     *
     * @return Selected items count
     */
    @Override
    public int getSelectedItemCount() {
        return mSelectedPhotos.size();
    }

    public void setCurrentDirectoryIndex(int currentDirectoryIndex) {
        mCurrentDirectoryIndex = currentDirectoryIndex;
    }

    public List<Photo> getCurrentPhotos() {
        return mPhotoDirectories.get(mCurrentDirectoryIndex).getPhotos();
    }

    public List<String> getCurrentPhotoPaths() {
        List<String> currentPhotoPaths = new ArrayList<>();
        for (Photo photo : getCurrentPhotos()) {
            currentPhotoPaths.add(photo.getPath());
        }
        return currentPhotoPaths;
    }

    public List<String> getSelectedPhotos() {
        return mSelectedPhotos;
    }

}