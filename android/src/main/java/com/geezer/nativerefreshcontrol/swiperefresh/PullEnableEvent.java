/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.geezer.nativerefreshcontrol.swiperefresh;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class PullEnableEvent extends Event<PullEnableEvent> {

    private WritableMap event = Arguments.createMap();

    protected PullEnableEvent(int viewTag) {
        super(viewTag);
    }

    @Override
    public String getEventName() {
        return "topRefreshPullEnable";
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), event);
    }

    public WritableMap getEvent() {
       return this.event;
    }
}
