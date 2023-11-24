package com.example.supersoiree

import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener
import org.osmdroid.views.overlay.OverlayItem

class OverlayClass() : OnItemGestureListener<OverlayItem>{

    class OverlayClass(){}

    override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
        return(true)
    }

    override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
        return(false)
    }
}