/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 * <p>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.geezer.nativerefreshcontrol.swiperefresh;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.geezer.nativerefreshcontrol.R;

import java.util.Map;

import javax.annotation.Nullable;

import static com.geezer.nativerefreshcontrol.swiperefresh.SuperSwipeRefreshLayoutManager.REACT_CLASS;


/**
 * ViewManager for {@link ReactSuperSwipeRefreshLayout} which allows the user to "pull to refresh" a
 * child view. Emits an {@code onRefresh} event when this happens.
 */
@ReactModule(name = REACT_CLASS)
public class SuperSwipeRefreshLayoutManager extends ViewGroupManager<ReactSuperSwipeRefreshLayout> {

    protected static final String REACT_CLASS = "AndroidSuperSwipeRefreshLayout";
    // Header View
    private View mHeadView;
    private ProgressBar progressBar;
    private TextView textView;
    private ImageView imageView;

    @Override
    protected ReactSuperSwipeRefreshLayout createViewInstance(ThemedReactContext reactContext) {
        ReactSuperSwipeRefreshLayout swipeRefreshLayout = new ReactSuperSwipeRefreshLayout(reactContext);
        return swipeRefreshLayout;
    }

    private View createHeaderView(Context context) {
        View headerView = LayoutInflater.from(context)
                .inflate(R.layout.layout_head, null);
        progressBar = (ProgressBar) headerView.findViewById(R.id.pb_view);
        textView = (TextView) headerView.findViewById(R.id.text_view);
        textView.setText("下拉刷新");
        imageView = (ImageView) headerView.findViewById(R.id.image_view);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.down_arrow);
        progressBar.setVisibility(View.GONE);
        return headerView;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactProp(name = ViewProps.ENABLED, defaultBoolean = true)
    public void setEnabled(ReactSuperSwipeRefreshLayout view, boolean enabled) {
        view.setEnabled(enabled);
    }

    @ReactProp(name = "refreshing")
    public void setRefreshing(ReactSuperSwipeRefreshLayout view, boolean refreshing) {
        view.setRefreshing(refreshing);
    }


    @ReactProp(name = "colors", customType = "ColorArray")
    public void setColors(ReactSuperSwipeRefreshLayout view, @Nullable ReadableArray colors) {
        if (colors != null) {
            int[] colorValues = new int[colors.size()];
            for (int i = 0; i < colors.size(); i++) {
                colorValues[i] = colors.getInt(i);
            }
            view.setColorSchemeColors(colorValues);
        } else {
            view.setColorSchemeColors();
        }
    }

    @ReactProp(name = "progressBackgroundColor", defaultInt = Color.TRANSPARENT, customType = "Color")
    public void setProgressBackgroundColor(ReactSuperSwipeRefreshLayout view, int color) {
        view.setProgressBackgroundColorSchemeColor(color);
    }

    @ReactProp(name = "size", defaultInt = SwipeRefreshLayout.DEFAULT)
    public void setSize(ReactSuperSwipeRefreshLayout view, int size) {
        view.setSize(size);
    }

//    TODO: 有问题
//    @ReactProp(name = "progressViewOffset", defaultFloat = 0)
//    public void setProgressViewOffset(final ReactSuperSwipeRefreshLayout view, final float offset) {
//        view.setProgressViewOffset(offset);
//    }


    @ReactProp(name = "changeDefaultHead")
    public void setHeaderView(ReactSuperSwipeRefreshLayout view, String type) {
        switch (type) {
            case "test":
                mHeadView = createHeaderView(view.getContext());
                view.setHeaderView(mHeadView);// add headerView
                Log.d("TAG", "changeDefaultHead " + type);
                break;
            default:
                break;
        }
    }

    @ReactProp(name = "distance")
    public void setDistanceToTriggerSync(ReactSuperSwipeRefreshLayout view, Integer distance) {
        if (distance != null) {
            view.setDistanceToTriggerSync(distance);
        }
    }


    @Override
    protected void addEventEmitters(
            final ThemedReactContext reactContext,
            final ReactSuperSwipeRefreshLayout view) {
        view.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO: 需要改善
                if (mHeadView != null) {
                    textView.setText("正在刷新");
                    imageView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher()
                                .dispatchEvent(new RefreshEvent(view.getId()));
                    }
                }, 3000);

            }

            @Override
            public void onPullDistance(int distance) {
                PullDistanceEvent distanceEvent = new PullDistanceEvent(view.getId());
                WritableMap event = distanceEvent.getEvent();
                event.putInt("distance", distance);
                reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher()
                        .dispatchEvent(distanceEvent);
            }

            @Override
            public void onPullEnable(boolean enable) {
                PullEnableEvent enableEvent = new PullEnableEvent(view.getId());
                WritableMap event = enableEvent.getEvent();
                event.putBoolean("enable", enable);
                reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher()
                        .dispatchEvent(enableEvent);
                if (mHeadView != null) {
                    textView.setText(enable ? "松开刷新" : "下拉刷新");
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setRotation(enable ? 180 : 0);
                }
            }
        });
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedViewConstants() {
        return MapBuilder.<String, Object>of(
                "SIZE",
                MapBuilder.of("DEFAULT", SwipeRefreshLayout.DEFAULT, "LARGE", SwipeRefreshLayout.LARGE));
    }

    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put("topRefresh", MapBuilder.of("registrationName", "onRefresh"))
                .put("topRefreshPullDistance", MapBuilder.of("registrationName", "onPullDistance"))
                .put("topRefreshPullEnable", MapBuilder.of("registrationName", "onPullEnable"))
                .build();
    }
}
