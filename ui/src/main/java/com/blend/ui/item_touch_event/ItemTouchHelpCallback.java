package com.blend.ui.item_touch_event;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;

import com.blend.ui.item_touch_event.item_touch_helper.ItemTouchHelper;


public class ItemTouchHelpCallback extends ItemTouchHelper.Callback {


    /**
     * 针对swipe和drag状态，设置不同状态下支持的方向
     * （LEFT，RIGHT，START，END，UP，DOWN）
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlag = ItemTouchHelper.LEFT;

        return makeMovementFlags(dragFlag, swipeFlag);
    }

    /**
     * 针对drag状态，当前target对应的item是否允许move
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        MainRecyclerAdapter adapter = (MainRecyclerAdapter) recyclerView.getAdapter();
        adapter.move(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    /**
     * 针对swap和drag状态，整个过程中一直会调用这个函数，随手指移动的view就是在super里面做到的（和ItemDecoration里面的onDraw()函数相对应）
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (dY != 0 && dX == 0) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        MainRecyclerAdapter.ItemBaseViewHolder holder = (MainRecyclerAdapter.ItemBaseViewHolder) viewHolder;
        if (viewHolder instanceof MainRecyclerAdapter.ItemSwipeWithActionWidthNoSpringViewHolder) {
            if (dX < -holder.mActionContainer.getWidth()) {
                dX = -holder.mActionContainer.getWidth();
            }
            holder.mViewContent.setTranslationX(dX);
            return;
        }
        if (viewHolder instanceof MainRecyclerAdapter.ItemBaseViewHolder) {
            holder.mViewContent.setTranslationX(dX);
        }
    }

    /**
     * 针对swipe状态，swipe到达滑动消失的距离回调函数，一般在这个函数里面处理删除item的逻辑
     * 确切的来讲就是swipe item画出屏幕动画结束的时候调用
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    /**
     * 针对drag状态，当item长按的时候是否允许进入drag（拖动）状态
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
}
