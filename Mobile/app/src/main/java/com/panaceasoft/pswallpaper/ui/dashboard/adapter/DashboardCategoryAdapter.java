package com.panaceasoft.pswallpaper.ui.dashboard.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.databinding.ItemDashboardCategoryAdapterBinding;
import com.panaceasoft.pswallpaper.ui.common.DataBoundListAdapter;
import com.panaceasoft.pswallpaper.utils.Objects;
import com.panaceasoft.pswallpaper.viewobject.Category;

import androidx.databinding.DataBindingUtil;

public class DashboardCategoryAdapter extends DataBoundListAdapter<Category,ItemDashboardCategoryAdapterBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    public DashboardCategoryAdapter.CategoryClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface = null;

    public DashboardCategoryAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                                    DashboardCategoryAdapter.CategoryClickCallback callback) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;

    }

    @Override
    protected ItemDashboardCategoryAdapterBinding createBinding(ViewGroup parent) {
        ItemDashboardCategoryAdapterBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_dashboard_category_adapter, parent, false,
                dataBindingComponent);

        binding.getRoot().setOnClickListener(v -> {
            Category category = binding.getWallpaperByCategory();
            if (category != null && callback != null) {
                callback.onCategoryClick(category);
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
    protected void bind(ItemDashboardCategoryAdapterBinding binding, Category category) {
        if(category != null) {
            binding.setWallpaperByCategory(category);
        }
    }


    @Override
    protected boolean areItemsTheSame(Category oldItem, Category newItem) {
        return Objects.equals(oldItem.cat_id, newItem.cat_id);
    }

    @Override
    protected boolean areContentsTheSame(Category oldItem, Category newItem) {
        return Objects.equals(oldItem.cat_id, newItem.cat_id);
    }

    public interface CategoryClickCallback {
        void onCategoryClick(Category category);
    }
}
