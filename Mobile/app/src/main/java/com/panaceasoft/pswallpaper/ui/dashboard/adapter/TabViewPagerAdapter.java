package com.panaceasoft.pswallpaper.ui.dashboard.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.panaceasoft.pswallpaper.ui.wallpaper.list.WallpaperListFragment;
import com.panaceasoft.pswallpaper.utils.Constants;
import com.panaceasoft.pswallpaper.viewobject.holder.WallpaperParamsHolder;

public class TabViewPagerAdapter extends FragmentStatePagerAdapter {

    private int numOfTab;
    private String premiumOrNot;
    private String isGifCheck;
    public WallpaperParamsHolder wallpaperParamsHolder;

    public TabViewPagerAdapter(FragmentManager fm, int numOfTab, String premiumOrNot, String isGifCheck) {
        super(fm);
        this.numOfTab = numOfTab;
        this.premiumOrNot = premiumOrNot;
        this.isGifCheck = isGifCheck;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        wallpaperParamsHolder = new WallpaperParamsHolder();

        bundle.putString(Constants.PREMIUM, premiumOrNot);
        bundle.putString(Constants.GIF, isGifCheck);
        switch (position) {

            case 0:
                WallpaperListFragment latestFragment1 = new WallpaperListFragment();

                WallpaperParamsHolder holder = wallpaperParamsHolder.getLatestHolder();

                if (premiumOrNot.equals(Constants.PREMIUM)) {
                    holder.type = Constants.PREMIUM;
                } else {
                    holder.type = Constants.FREE;
                }

                if (isGifCheck.equals(Constants.ONE)) {
                    holder.isGif = Constants.ONE;
                    holder.isWallpaper = Constants.ZERO;
                    holder.isLiveWallpaper = Constants.ZERO;
                } else {
                    holder.isGif = Constants.ZERO;
                }
                bundle.putSerializable(Constants.INTENT__WALLPAPER_PARAM_HOLDER, holder);

                latestFragment1.setArguments(bundle);

                return latestFragment1;
            case 1:
                WallpaperListFragment latestFragment2 = new WallpaperListFragment();

                WallpaperParamsHolder holder2 = wallpaperParamsHolder.getTrendingHolder();

                if (premiumOrNot.equals(Constants.PREMIUM)) {
                    holder2.type = Constants.PREMIUM;
                } else {
                    holder2.type = Constants.FREE;
                }
                if (isGifCheck.equals(Constants.GIF)) {
                    holder2.isGif = Constants.GIF;
                    holder2.isWallpaper = Constants.ZERO;
                    holder2.isLiveWallpaper = Constants.ZERO;
                } else {
                    holder2.isGif = Constants.NOGIF;
                }
                bundle.putSerializable(Constants.INTENT__WALLPAPER_PARAM_HOLDER, holder2);

                latestFragment2.setArguments(bundle);
                return latestFragment2;
            case 2:
                WallpaperListFragment latestFragment3 = new WallpaperListFragment();
                WallpaperParamsHolder holder3 = wallpaperParamsHolder.getRecommendedHolder();

                if (premiumOrNot.equals(Constants.PREMIUM)) {
                    holder3.type = Constants.PREMIUM;
                } else {
                    holder3.type = Constants.FREE;
                }
                if (isGifCheck.equals(Constants.GIF)) {
                    holder3.isGif = Constants.GIF;
                    holder3.isWallpaper = Constants.ZERO;
                    holder3.isLiveWallpaper = Constants.ZERO;
                } else {
                    holder3.isGif = Constants.NOGIF;
                }

                bundle.putSerializable(Constants.INTENT__WALLPAPER_PARAM_HOLDER, holder3);
                latestFragment3.setArguments(bundle);
                return latestFragment3;
            case 3:
                WallpaperListFragment latestFragment4 = new WallpaperListFragment();
                WallpaperParamsHolder holder4 = wallpaperParamsHolder.getDownloadHolder();

                if (premiumOrNot.equals(Constants.PREMIUM)) {
                    holder4.type = Constants.PREMIUM;
                } else {
                    holder4.type = Constants.FREE;
                }
                if (isGifCheck.equals(Constants.GIF)) {
                    holder4.isGif = Constants.GIF;
                    holder4.isWallpaper = Constants.ZERO;
                    holder4.isLiveWallpaper = Constants.ZERO;
                } else {
                    holder4.isGif = Constants.NOGIF;
                }
                bundle.putSerializable(Constants.INTENT__WALLPAPER_PARAM_HOLDER, holder4);
                latestFragment4.setArguments(bundle);
                return latestFragment4;

            default:
                WallpaperListFragment latestFragment5 = new WallpaperListFragment();

                WallpaperParamsHolder holder5 = wallpaperParamsHolder.getLatestHolder();

                if (premiumOrNot.equals(Constants.PREMIUM)) {
                    holder5.type = Constants.PREMIUM;
                } else {
                    holder5.type = Constants.FREE;
                }
                if (isGifCheck.equals(Constants.GIF)) {
                    holder5.isGif = Constants.GIF;
                    holder5.isWallpaper = Constants.ZERO;
                    holder5.isLiveWallpaper = Constants.ZERO;
                } else {
                    holder5.isGif = Constants.NOGIF;
                }
                bundle.putSerializable(Constants.INTENT__WALLPAPER_PARAM_HOLDER, holder5);

                latestFragment5.setArguments(bundle);

                return latestFragment5;
        }

    }


    @Override
    public int getCount() {
        return numOfTab;
    }
}
