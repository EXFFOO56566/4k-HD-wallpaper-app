package com.panaceasoft.pswallpaper.ui.dashboard.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.panaceasoft.pswallpaper.ui.livewallpaper.detail.LiveWallpaperListFragment;
import com.panaceasoft.pswallpaper.utils.Constants;
import com.panaceasoft.pswallpaper.viewobject.holder.WallpaperParamsHolder;

public class TabViewPagerLiveWallpaperAdapter extends FragmentStatePagerAdapter {

    private int numOfTab;
    public WallpaperParamsHolder wallpaperParamsHolder;

    public TabViewPagerLiveWallpaperAdapter(FragmentManager fm, int numOfTab) {
        super(fm);
        this.numOfTab = numOfTab;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        wallpaperParamsHolder = new WallpaperParamsHolder();

//        bundle.putString(Constants.PREMIUM, premiumOrNot);
//        bundle.putString(Constants.GIF, gifOrNot);
        switch (position) {

            case 0:
                LiveWallpaperListFragment latestFragment1 = new LiveWallpaperListFragment();

                WallpaperParamsHolder holder = wallpaperParamsHolder.getLatestLiveWallpaperHolder();

                bundle.putSerializable(Constants.INTENT__WALLPAPER_PARAM_HOLDER, holder);

                latestFragment1.setArguments(bundle);

                return latestFragment1;
            case 1:
                LiveWallpaperListFragment latestFragment2 = new LiveWallpaperListFragment();

                WallpaperParamsHolder holder2 = wallpaperParamsHolder.getTrendingLiveWallpaperHolder();

                bundle.putSerializable(Constants.INTENT__WALLPAPER_PARAM_HOLDER, holder2);

                latestFragment2.setArguments(bundle);
                return latestFragment2;
            case 2:
                LiveWallpaperListFragment latestFragment3 = new LiveWallpaperListFragment();
                WallpaperParamsHolder holder3 = wallpaperParamsHolder.getRecommendedLiveWallpaperHolder();

                bundle.putSerializable(Constants.INTENT__WALLPAPER_PARAM_HOLDER, holder3);
                latestFragment3.setArguments(bundle);
                return latestFragment3;
            case 3:
                LiveWallpaperListFragment latestFragment4 = new LiveWallpaperListFragment();
                WallpaperParamsHolder holder4 = wallpaperParamsHolder.getDownloadLiveWallpaperHolder();

                bundle.putSerializable(Constants.INTENT__WALLPAPER_PARAM_HOLDER, holder4);
                latestFragment4.setArguments(bundle);
                return latestFragment4;

            default:
                LiveWallpaperListFragment latestFragment5 = new LiveWallpaperListFragment();

                WallpaperParamsHolder holder5 = wallpaperParamsHolder.getLatestLiveWallpaperHolder();

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
