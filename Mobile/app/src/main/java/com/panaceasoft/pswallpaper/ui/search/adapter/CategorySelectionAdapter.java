package com.panaceasoft.pswallpaper.ui.search.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.databinding.ItemCategorySelectionAdapterBinding;
import com.panaceasoft.pswallpaper.ui.common.DataBoundListAdapter;
import com.panaceasoft.pswallpaper.ui.common.DataBoundViewHolder;
import com.panaceasoft.pswallpaper.utils.Objects;
import com.panaceasoft.pswallpaper.viewobject.Category;

import androidx.databinding.DataBindingUtil;

public class CategorySelectionAdapter extends DataBoundListAdapter<Category, ItemCategorySelectionAdapterBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private final CategorySelectionAdapter.NewsClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface = null;
    public String cat_id ;

    public CategorySelectionAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                                    CategorySelectionAdapter.NewsClickCallback callback, String cat_id) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.cat_id = cat_id;
    }

    @Override
    protected ItemCategorySelectionAdapterBinding createBinding(ViewGroup parent) {
        ItemCategorySelectionAdapterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_category_selection_adapter, parent, false,
                        dataBindingComponent);

        binding.getRoot().setOnClickListener(v -> {

            Category category = binding.getCategory();

            if (category != null && callback != null) {

                binding.groupview.setBackgroundColor(parent.getResources().getColor(R.color.md_green_50));

                callback.onClick(category,category.cat_id);
            }
        });
        return binding;
    }

    // For general animation
    @Override
    public void bindView(DataBoundViewHolder<ItemCategorySelectionAdapterBinding> holder, int position) {
        super.bindView(holder, position);


        // setAnimation(holder.itemView, position);
    }

    @Override
    protected void dispatched() {
        if(diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemCategorySelectionAdapterBinding binding, Category category) {

        if(category != null) {
            binding.setCategory(category);

            if (cat_id != null) {
                if (category.cat_id.equals(cat_id)) {
                    binding.groupview.setBackgroundColor(binding.groupview.getResources().getColor((R.color.md_green_50)));
                }
            }
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

    public interface NewsClickCallback {
        void onClick(Category category, String id);
    }

}
