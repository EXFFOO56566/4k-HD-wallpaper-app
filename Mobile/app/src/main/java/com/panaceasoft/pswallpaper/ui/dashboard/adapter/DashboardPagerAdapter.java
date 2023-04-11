package com.panaceasoft.pswallpaper.ui.dashboard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.databinding.ItemDashboardPagerAdapterBinding;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewobject.Wallpaper;

import java.util.List;

public class DashboardPagerAdapter extends PagerAdapter {
    private List<Wallpaper> wallpaper;

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private WallpaperClickCallback callback;

    public DashboardPagerAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                                 WallpaperClickCallback callback) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        if (wallpaper == null) {
            return 0;
        } else {
            return wallpaper.size();
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        ItemDashboardPagerAdapterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(container.getContext()),
                        R.layout.item_dashboard_pager_adapter, container, false,
                        dataBindingComponent);

        if(wallpaper != null) {
            if(position < wallpaper.size()) {

                binding.setWallpaper(wallpaper.get(position));

                if (wallpaper.get(position).point == 0) {
                    binding.premiumImageView.setVisibility(View.GONE);
                    binding.premiumTextView.setVisibility(View.GONE);
                    binding.premiumPriceTextView2.setVisibility(View.GONE);
                } else {
                    binding.premiumImageView.setVisibility(View.VISIBLE);
                    binding.premiumTextView.setVisibility(View.VISIBLE);
                    binding.premiumPriceTextView2.setVisibility(View.VISIBLE);
                    binding.premiumPriceTextView2.setText(binding.getRoot().getContext().getString(R.string.dashboard__pts, Utils.numberFormat(wallpaper.get(position).point)));
                }

                container.addView(binding.getRoot());

                binding.getRoot().setOnClickListener(view -> {
                    Wallpaper image = binding.getWallpaper();
                    if (image != null && callback != null) {
                        callback.onItemClick(view, image, position);
                    }
                });

                binding.placeImageView.setOnClickListener(v -> {
                    Wallpaper image = binding.getWallpaper();
                    if (image != null && callback != null) {
                        callback.onPagerClick(image);
                    }
                });
            }
        }

        return binding.getRoot();
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }

    public void setWallpaperList(List<Wallpaper> imageList) {
        this.wallpaper = imageList;
        this.notifyDataSetChanged();

    }

    public interface WallpaperClickCallback {
        void onItemClick(View view, Wallpaper obj, int position);
        void onPagerClick(Wallpaper image);
    }


}
