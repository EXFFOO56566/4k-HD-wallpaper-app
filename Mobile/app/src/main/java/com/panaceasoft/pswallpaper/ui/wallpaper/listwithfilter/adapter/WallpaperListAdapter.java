package com.panaceasoft.pswallpaper.ui.wallpaper.listwithfilter.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.panaceasoft.pswallpaper.Config;
import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.databinding.ItemWallpaperListAdapterBinding;
import com.panaceasoft.pswallpaper.ui.common.DataBoundListAdapter;
import com.panaceasoft.pswallpaper.utils.Constants;
import com.panaceasoft.pswallpaper.utils.Objects;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewobject.Wallpaper;

import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import okhttp3.internal.Util;

public class WallpaperListAdapter extends DataBoundListAdapter<Wallpaper, ItemWallpaperListAdapterBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    public WallpaperListAdapter.AllWallpapersClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface = null;
    private String premiumOrNot;
    private int a = 0;


    public WallpaperListAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                                WallpaperListAdapter.AllWallpapersClickCallback callback) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;

    }

    public void setPremiumOrNot(String premiumOrNot) {
        this.premiumOrNot = premiumOrNot;
    }

    @Override
    protected ItemWallpaperListAdapterBinding createBinding(ViewGroup parent) {
        ItemWallpaperListAdapterBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_wallpaper_list_adapter, parent, false,
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
    protected void bind(ItemWallpaperListAdapterBinding binding, Wallpaper item) {
        binding.setAllWallpapers(item);

//        android:text="@{Integer.toString(allWallpapers.touch_count)}"


        if (Config.APP_GRID_LAYOUT == Constants.LAYOUT_TYPE.GRID_LAYOUT) {
            binding.latestCategoryImageView.getLayoutParams().height = Utils.dpToPx(binding.getRoot().getContext(), Config.GRID_CELL_HEIGHT);
        }

        binding.count1TextView.setText(Utils.numberFormat(item.favourite_count));
        binding.touchCountTextView.setText(Utils.numberFormat(item.touch_count));
        binding.premiumPriceTextView.setText(binding.getRoot().getContext().getString(R.string.premium__point, Utils.numberFormat(item.point)));

        if (item.is_favourited.equals(Constants.ONE)) {
            binding.heartButton.setLiked(true);
            binding.heartButton.setLikeDrawable(ResourcesCompat.getDrawable(binding.getRoot().getResources(), R.drawable.heart_on, null));
        } else if (item.is_favourited.equals(Constants.ZERO)) {
            binding.heartButton.setLiked(false);
            binding.heartButton.setLikeDrawable(ResourcesCompat.getDrawable(binding.getRoot().getResources(), R.drawable.heart_off, null));
        }

        if (item.point == 0) {
            binding.premiumImageView.setVisibility(View.GONE);
            binding.premiumTextView.setVisibility(View.GONE);
            binding.premiumPriceTextView.setVisibility(View.GONE);
        } else {
            binding.premiumImageView.setVisibility(View.VISIBLE);
            binding.premiumTextView.setVisibility(View.VISIBLE);
            binding.premiumPriceTextView.setVisibility(View.VISIBLE);
            binding.premiumPriceTextView.setText(binding.getRoot().getContext().getString(R.string.dashboard__pts, String.valueOf(item.point)));
        }

        if (premiumOrNot != null) {
            if (premiumOrNot.equals(Constants.PREMIUM)) {
                binding.premiumPriceTextView.setVisibility(View.VISIBLE);
                binding.premiumTextView.setVisibility(View.VISIBLE);
                binding.premiumImageView.setVisibility(View.VISIBLE);
            } else {

                binding.premiumImageView.setVisibility(View.GONE);
                binding.premiumPriceTextView.setVisibility(View.GONE);
                binding.premiumTextView.setVisibility(View.GONE);
            }
        }

        if (item.is_gif.equals(Constants.GIF)) {
            binding.gifImageView.setVisibility(View.VISIBLE);
        } else {
            binding.gifImageView.setVisibility(View.GONE);
        }
    }

    @Override
    protected boolean areItemsTheSame(Wallpaper oldItem, Wallpaper newItem) {
        return Objects.equals(oldItem.wallpaper_id, newItem.wallpaper_id)
                && oldItem.touch_count == newItem.touch_count
                && oldItem.favourite_count == newItem.favourite_count
                && oldItem.is_favourited.equals(newItem.is_favourited);
    }

    @Override
    protected boolean areContentsTheSame(Wallpaper oldItem, Wallpaper newItem) {
        return Objects.equals(oldItem.wallpaper_id, newItem.wallpaper_id)
                && oldItem.touch_count == newItem.touch_count
                && oldItem.favourite_count == newItem.favourite_count
                && oldItem.is_favourited.equals(newItem.is_favourited);
    }

    public interface AllWallpapersClickCallback {
        void onClick(Wallpaper wallpaper);

        void onFavLikeClick(Wallpaper wallpaper, LikeButton likeButton);

        void onFavUnlikeClick(Wallpaper wallpaper, LikeButton likeButton);

    }


}
