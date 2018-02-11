package com.jijith.barchart.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * An abstract adapter which can be extended for Recyclerview
 */
public abstract class BarChartViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    /**
     * Called by RecyclerView when it stops observing this Adapter.
     *
     * @param recyclerView The RecyclerView instance which stopped observing this adapter.
     * @see #onAttachedToRecyclerView(RecyclerView)
     */
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    /**
     * the basic view holder creation
     *
     * @param parent   coming from the bottom api
     * @param viewType coming the bottom api as well
     * @return expected a typed view holder
     */
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        return onCreateViewHolder(parent);
    }

    /**
     * for all NORMAL type holder
     *
     * @param parent view group parent
     * @return vh
     */
    public abstract VH onCreateViewHolder(ViewGroup parent);

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * retrieve the amount of the total items in the urv for display that will be including all data items as well as the decorative items
     *
     * @return the int
     */
    @Override
    public int getItemCount() {
        return getAdapterItemCount();
    }

    /**
     * Returns the number of items in the adapter bound to the parent RecyclerView.
     *
     * @return The number of data items in the bound adapter
     */
    public abstract int getAdapterItemCount();

    /**
     * Enumerator for recycler view item animation
     */
    protected enum AdapterAnimationType {
        AlphaIn,
        SlideInBottom,
        ScaleIn,
        SlideInLeft,
        SlideInRight,
    }

    /**
     * Animations when loading the adapter
     *
     * @param view the view
     * @param type the type of the animation
     * @return the animator in array
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected Animator[] getAdapterAnimations(View view, AdapterAnimationType type) {
        if (type == AdapterAnimationType.ScaleIn) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", .5f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", .5f, 1f);
            return new ObjectAnimator[]{scaleX, scaleY};
        } else if (type == AdapterAnimationType.AlphaIn) {
            return new Animator[]{ObjectAnimator.ofFloat(view, "alpha", .5f, 1f)};
        } else if (type == AdapterAnimationType.SlideInBottom) {
            return new Animator[]{
                    ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight(), 0)
            };
        } else if (type == AdapterAnimationType.SlideInLeft) {
            return new Animator[]{
                    ObjectAnimator.ofFloat(view, "translationX", -view.getRootView().getWidth(), 0)
            };
        } else if (type == AdapterAnimationType.SlideInRight) {
            return new Animator[]{
                    ObjectAnimator.ofFloat(view, "translationX", view.getRootView().getWidth(), 0)
            };
        }
        return null;
    }
}
