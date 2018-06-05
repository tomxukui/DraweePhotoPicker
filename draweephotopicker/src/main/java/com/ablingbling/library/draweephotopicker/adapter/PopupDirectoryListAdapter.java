package com.ablingbling.library.draweephotopicker.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import com.ablingbling.library.draweephotopicker.R;
import com.ablingbling.library.draweephotopicker.entity.PhotoDirectory;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by donglua on 15/6/28.
 */
public class PopupDirectoryListAdapter extends BaseAdapter {

    private ViewHolder vh;

    private List<PhotoDirectory> mDirectories;
    private float mImgWidth;
    private float mImgHeight;

    public PopupDirectoryListAdapter(Context context, List<PhotoDirectory> directories) {
        mDirectories = (directories == null ? new ArrayList<PhotoDirectory>() : directories);
        mImgWidth = context.getResources().getDimension(R.dimen.picker_item_directory_img_width);
        mImgHeight = context.getResources().getDimension(R.dimen.picker_item_directory_img_height);
    }

    @Override
    public int getCount() {
        return mDirectories.size();
    }

    @Override
    public PhotoDirectory getItem(int position) {
        return mDirectories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDirectories.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.picker_item_list_popup_directory, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        PhotoDirectory item = getItem(position);

        Uri uri = Uri.parse("file://" + item.getCoverPath());
        setDraweeView(uri, vh.iv_dir_cover);
        vh.tv_dir_name.setText(item.getName());
        vh.tv_dir_count.setText(String.format("%d张", item.getPhotos().size()));

        return convertView;
    }

    private void setDraweeView(Uri uri, SimpleDraweeView iv) {
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions((int) mImgWidth, (int) mImgHeight))
                .build();

        DraweeController controller = Fresco
                .newDraweeControllerBuilder()
                .setOldController(iv.getController())
                .setImageRequest(request)
                .build();

        iv.setController(controller);
    }

    class ViewHolder {

        SimpleDraweeView iv_dir_cover;
        TextView tv_dir_name;
        TextView tv_dir_count;

        public ViewHolder(View view) {
            iv_dir_cover = view.findViewById(R.id.iv_dir_cover);
            tv_dir_name = view.findViewById(R.id.tv_dir_name);
            tv_dir_count = view.findViewById(R.id.tv_dir_count);
        }

    }

}
