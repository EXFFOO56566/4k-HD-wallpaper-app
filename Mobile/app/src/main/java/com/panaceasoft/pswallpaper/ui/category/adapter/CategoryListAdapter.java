package com.panaceasoft.pswallpaper.ui.category.adapter;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.databinding.ItemCategoryListAdapterBinding;
import com.panaceasoft.pswallpaper.ui.common.DataBoundListAdapter;
import com.panaceasoft.pswallpaper.utils.Objects;
import com.panaceasoft.pswallpaper.viewobject.Category;

import androidx.databinding.DataBindingUtil;

public class CategoryListAdapter extends DataBoundListAdapter<Category,ItemCategoryListAdapterBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    public CategoryListAdapter.AllWallpapersByCategoryClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface = null;


    public CategoryListAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                               CategoryListAdapter.AllWallpapersByCategoryClickCallback callback) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;

    }

    @Override
    protected ItemCategoryListAdapterBinding createBinding(ViewGroup parent) {
        ItemCategoryListAdapterBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_category_list_adapter, parent, false,
                dataBindingComponent);

        binding.getRoot().setOnClickListener(v -> {
            Category category = binding.getAllWallpaperByCategory();
            if (category != null && callback != null) {
                callback.onClick(category);
            }
        });

        return  binding;
    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemCategoryListAdapterBinding binding, Category category) {
        if(category != null) {
            binding.setAllWallpaperByCategory(category);
            binding.categoryNameTextView.setText(binding.getRoot().getContext().getString(R.string.category__wallpaperCount, category.image_count, category.cat_name));
        }
    }

    @Override
    protected boolean areItemsTheSame(Category oldItem, Category newItem) {
        return Objects.equals(oldItem.cat_id, newItem.cat_id) &&
                oldItem.cat_id.equals(newItem.cat_id);
    }

    @Override
    protected boolean areContentsTheSame(Category oldItem, Category newItem) {
        return Objects.equals(oldItem.cat_id, newItem.cat_id)
                && oldItem.cat_id.equals(newItem.cat_id);
    }

    public interface AllWallpapersByCategoryClickCallback {
        void onClick(Category itemLike);
    }
}
