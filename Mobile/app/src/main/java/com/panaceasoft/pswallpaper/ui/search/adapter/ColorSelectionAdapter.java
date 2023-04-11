package com.panaceasoft.pswallpaper.ui.search.adapter;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.databinding.ItemColorSelectionAdapterBinding;
import com.panaceasoft.pswallpaper.ui.common.DataBoundListAdapter;
import com.panaceasoft.pswallpaper.ui.common.DataBoundViewHolder;
import com.panaceasoft.pswallpaper.utils.Objects;
import com.panaceasoft.pswallpaper.viewobject.Color;

import androidx.databinding.DataBindingUtil;

public class ColorSelectionAdapter extends DataBoundListAdapter<Color, ItemColorSelectionAdapterBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private final ColorSelectionAdapter.NewsClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface = null;
    public String cat_id;

    public ColorSelectionAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                                 ColorSelectionAdapter.NewsClickCallback callback, String cat_id) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.cat_id = cat_id;
    }

    @Override
    protected ItemColorSelectionAdapterBinding createBinding(ViewGroup parent) {
        ItemColorSelectionAdapterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_color_selection_adapter, parent, false,
                        dataBindingComponent);

        binding.getRoot().setOnClickListener(v -> {

            Color color = binding.getColor();

            if (color != null && callback != null) {

                binding.groupview.setBackgroundColor(parent.getResources().getColor(R.color.md_green_50));

                callback.onClick(color, color.id);
            }
        });
        return binding;
    }

    // For general animation
    @Override
    public void bindView(DataBoundViewHolder<ItemColorSelectionAdapterBinding> holder, int position) {
        super.bindView(holder, position);

    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemColorSelectionAdapterBinding binding, Color color) {

        if(color != null) {
            binding.setColor(color);

            if (cat_id != null) {
                if (color.id.equals(cat_id)) {
                    binding.groupview.setBackgroundColor(binding.groupview.getResources().getColor((R.color.md_green_50)));
                }
            }

            Drawable drawable = binding.getRoot().getResources().getDrawable(R.drawable.circular_shape);

            drawable.setColorFilter(android.graphics.Color.parseColor(color.code), PorterDuff.Mode.SRC_ATOP);

            binding.colorView.setBackground(drawable);
        }
    }

    @Override
    protected boolean areItemsTheSame(Color oldItem, Color newItem) {
        return Objects.equals(oldItem.id, newItem.id);
    }

    @Override
    protected boolean areContentsTheSame(Color oldItem, Color newItem) {
        return Objects.equals(oldItem.id, newItem.id);
    }

    public interface NewsClickCallback {
        void onClick(Color color, String id);
    }


//    private void setAnimation(View viewToAnimate, int position) {
//        if (position > lastPosition) {
//            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.slide_in_bottom);
//            viewToAnimate.startAnimation(animation);
//            lastPosition = position;
//        } else {
//            lastPosition = position;
//        }
//    }
}
