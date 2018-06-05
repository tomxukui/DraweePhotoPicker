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
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;

import me.relex.photodraweeview.PhotoDraweeView;

public class PhotoPagerAdapter extends RecyclingPagerAdapter {

    private ViewHolder vh;

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
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.picker_item_pager_photo, viewGroup, false);
            vh = new ViewHolder(view);
            view.setTag(vh);

        } else {
            vh = (ViewHolder) view.getTag();
        }

        String path = mList.get(i);
        Uri uri = Uri.parse(path.startsWith("http") ? path : ("file://" + path));
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setOldController(vh.iv_img.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>() {

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if (imageInfo != null) {
                            vh.iv_img.update(imageInfo.getWidth(), imageInfo.getHeight());
                        }
                    }

                })
                .build();
        vh.iv_img.setController(controller);
        return view;
    }

    class ViewHolder {

        PhotoDraweeView iv_img;

        public ViewHolder(View view) {
            iv_img = view.findViewById(R.id.iv_img);
        }

    }

}
