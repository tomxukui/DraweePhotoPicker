package com.ablingbling.library.draweephotopicker.adapter;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.ablingbling.library.draweephotopicker.R;
import com.ablingbling.library.salvage.RecyclingPagerAdapter;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

import me.relex.photodraweeview.PhotoDraweeView;

public class PhotoPagerAdapter extends RecyclingPagerAdapter {

    private List<String> mList;

    public PhotoPagerAdapter(List<String> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.picker_item_pager_photo, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        String path = mList.get(i);
        Uri uri = Uri.parse(path.startsWith("http") ? path : ("file://" + path));

        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        controller.setUri(uri);
        controller.setOldController(holder.iv_img.getController());
        controller.setControllerListener(new BaseControllerListener<ImageInfo>() {

            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo != null) {
                    holder.iv_img.update(imageInfo.getWidth(), imageInfo.getHeight());
                }
            }

        });

        holder.iv_img.setController(controller.build());
        return view;
    }

    static class ViewHolder {

        final PhotoDraweeView iv_img;

        public ViewHolder(View view) {
            iv_img = view.findViewById(R.id.iv_img);
        }
    }

}
