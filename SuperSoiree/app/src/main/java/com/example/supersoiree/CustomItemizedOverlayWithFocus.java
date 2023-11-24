package com.example.supersoiree;

import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;

import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;

import java.util.List;

public class CustomItemizedOverlayWithFocus extends ItemizedOverlayWithFocus {


    public CustomItemizedOverlayWithFocus(Context pContext, List aList, OnItemGestureListener aOnItemTapListener) {
        super(pContext, aList, aOnItemTapListener);
    }

    public CustomItemizedOverlayWithFocus(List aList, OnItemGestureListener aOnItemTapListener, Context pContext) {

        this(aList,
                pContext.getDrawable(R.drawable.beer_icon)
                , null, NOT_SET,
                aOnItemTapListener, pContext);
    }

    public CustomItemizedOverlayWithFocus(List aList, Drawable pMarker, Drawable pMarkerFocused, int pFocusedBackgroundColor, OnItemGestureListener aOnItemTapListener, Context pContext) {
        super(aList, pMarker, pMarkerFocused, pFocusedBackgroundColor, aOnItemTapListener, pContext);
    }
}
