package com.panaceasoft.pswallpaper.ui.dashboard.adapter.wallpaper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.databinding.ItemWallpaperAdapterBinding;
import com.panaceasoft.pswallpaper.databinding.ItemWallpaperAdapterBinding;
import com.panaceasoft.pswallpaper.ui.common.DataBoundListAdapter;
import com.panaceasoft.pswallpaper.utils.Constants;
import com.panaceasoft.pswallpaper.utils.Objects;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewobject.Wallpaper;

import androidx.databinding.DataBindingUtil;

public class WallpaperAdapter extends DataBoundListAdapter<Wallpaper, ItemWallpaperAdapterBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    public WallpaperAdapter.AllWallpapersClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface = null;

    public WallpaperAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                            WallpaperAdapter.AllWallpapersClickCallback callback) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
    }

    @Override
    protected ItemWallpaperAdapterBinding createBinding(ViewGroup parent) {
        ItemWallpaperAdapterBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_wallpaper_adapter, parent, false,
                dataBindingComponent);

        binding.getRoot().setOnClickListener(v -> {
            Wallpaper wallpaper = binding.getAllWallpapers();
            if (wallpaper != null && callback != null) {
                callback.onClick(wallpaper);
            }
        });

        binding.heartButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                Wallpaper wallpaper = binding.getAllWallpapers();
                if (wallpaper != null && callback != null) {
                    callback.onFavLikeClick(wallpaper, binding.heartButton);
                }

            }

            @Override
            public void unLiked(LikeButton likeButton) {

                Wallpaper wallpaper = binding.getAllWallpapers();
                if (wallpaper != null && callback != null) {
                    callback.onFavUnlikeClick(wallpaper, binding.heartButton);
                }
            }
        });

        return binding;
    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemWallpaperAdapterBinding binding, Wallpaper wallpaper) {
        if (wallpaper != null) {
            binding.setAllWallpapers(wallpaper);

            if (wallpaper.is_favourited.equals(Constants.ONE)) {
                binding.heartButton.setLiked(true);
            } else {
                binding.heartButton.setLiked(false);
            }

            binding.count1TextView.setText(Utils.numberFormat(wallpaper.favourite_count));
            binding.count2TextView.setText(Utils.numberFormat(wallpaper.touch_count));

            if (wallpaper.point == 0) {
                binding.premiumImageView.setVisibility(View.GONE);
                binding.premiumTextView.setVisibility(View.GONE);
                binding.premiumPriceTextView.setVisibility(View.GONE);
            } else {
                binding.premiumImageView.setVisibility(View.VISIBLE);
                binding.premiumTextView.setVisibility(View.VISIBLE);
                binding.premiumPriceTextView.setVisibility(View.VISIBLE);
                binding.premiumPriceTextView.setText(binding.getRoot().getContext().getString(R.string.dashboard__pts, Utils.numberFormat(wallpaper.point)));
            }

            if (wallpaper.is_gif.equals(Constants.GIF)) {
                binding.gifImageView.setVisibility(View.VISIBLE);
            } else {
                binding.gifImageView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected boolean areItemsTheSame(Wallpaper oldItem, Wallpaper newItem) {
        return Objects.equals(oldItem.wallpaper_id, newItem.wallpaper_id) &&
                oldItem.touch_count == newItem.touch_count &&
                oldItem.is_favourited.equals(newItem.is_favourited);
    }

    @Override
    protected boolean areContentsTheSame(Wallpaper oldItem, Wallpaper newItem) {
        return Objects.equals(oldItem.wallpaper_id, newItem.wallpaper_id) &&
                oldItem.touch_count == newItem.touch_count &&
                oldItem.is_favourited.equals(newItem.is_favourited);
    }

    public interface AllWallpapersClickCallback {
        void onClick(Wallpaper wallpaper);

        void onFavLikeClick(Wallpaper wallpaper, LikeButton likeButton);

        void onFavUnlikeClick(Wallpaper wallpaper, LikeButton likeButton);
    }

}
