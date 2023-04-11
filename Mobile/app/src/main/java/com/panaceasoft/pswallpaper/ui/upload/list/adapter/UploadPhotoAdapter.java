package com.panaceasoft.pswallpaper.ui.upload.list.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.databinding.ItemUploadPhotoBinding;
import com.panaceasoft.pswallpaper.ui.common.DataBoundListAdapter;
import com.panaceasoft.pswallpaper.utils.Objects;
import com.panaceasoft.pswallpaper.viewobject.Wallpaper;

import androidx.databinding.DataBindingUtil;

public class UploadPhotoAdapter extends DataBoundListAdapter<Wallpaper, ItemUploadPhotoBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    public UploadPhotoAdapter.AllUploadPhotoClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface;


    public UploadPhotoAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                              UploadPhotoAdapter.AllUploadPhotoClickCallback callback,
                              DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.diffUtilDispatchedInterface = diffUtilDispatchedInterface;
    }

    @Override
    protected ItemUploadPhotoBinding createBinding(ViewGroup parent) {
        ItemUploadPhotoBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_upload_photo, parent, false,
                dataBindingComponent);

        binding.getRoot().setOnClickListener(v -> {
            Wallpaper wallpaper = binding.getUploadPhoto();
            if (wallpaper != null && callback != null) {
                callback.onClick(wallpaper);
            }
        });

        binding.deleteImageView.setOnClickListener(v -> callback.onDeleteClick(binding.getUploadPhoto()));

        return binding;
    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemUploadPhotoBinding binding, Wallpaper item) {
        binding.setUploadPhoto(item);

    }

    @Override
    protected boolean areItemsTheSame(Wallpaper oldItem, Wallpaper newItem) {
        return Objects.equals(oldItem.wallpaper_name, newItem.wallpaper_name)
                && Objects.equals(oldItem.category.cat_name, newItem.category.cat_name)
                && oldItem.added_date.equals(newItem.added_date)
                && oldItem.default_photo.img_path.equals(newItem.default_photo.img_path);
    }

    @Override
    protected boolean areContentsTheSame(Wallpaper oldItem, Wallpaper newItem) {
        return Objects.equals(oldItem.wallpaper_name, newItem.wallpaper_name)
                && Objects.equals(oldItem.category.cat_name, newItem.category.cat_name)
                && oldItem.added_date.equals(newItem.added_date)
                && oldItem.default_photo.img_path.equals(newItem.default_photo.img_path);
    }

    public interface AllUploadPhotoClickCallback {
        void onClick(Wallpaper wallpaper);

        void onDeleteClick(Wallpaper wallpaper);
    }


}
