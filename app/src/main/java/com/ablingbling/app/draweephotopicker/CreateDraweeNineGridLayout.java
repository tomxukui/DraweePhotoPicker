package com.ablingbling.app.draweephotopicker;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.ablingbling.library.ninegridlayout.CreateNineGridLayout;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by xukui on 2018/5/20.
 */
public class CreateDraweeNineGridLayout extends CreateNineGridLayout<SimpleDraweeView> {

    private Context mContext;

    public CreateDraweeNineGridLayout(Context context) {
        super(context);
        mContext = context;
    }

    public CreateDraweeNineGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public CreateDraweeNineGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CreateDraweeNineGridLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    @Override
    protected SimpleDraweeView createItemView() {
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setPlaceholderImage(R.mipmap.ic_launcher)
                .setPlaceholderImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .setFailureImage(R.mipmap.ic_launcher)
                .setFailureImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .setRetryImage(R.mipmap.ic_launcher)
                .setRetryImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .build();

        SimpleDraweeView simpleDraweeView = new SimpleDraweeView(mContext);
        simpleDraweeView.setHierarchy(hierarchy);
        return simpleDraweeView;
    }

    @Override
    protected SimpleDraweeView setItemView(SimpleDraweeView view, int viewWidth, int viewHeight, String imgPath) {
        Uri uri = Uri.parse("file://" + imgPath);

        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(viewWidth, viewHeight))
                .build();

        DraweeController draweeController = Fresco
                .newDraweeControllerBuilder()
                .setOldController(view.getController())
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .build();

        view.setController(draweeController);
        return view;
    }

}
