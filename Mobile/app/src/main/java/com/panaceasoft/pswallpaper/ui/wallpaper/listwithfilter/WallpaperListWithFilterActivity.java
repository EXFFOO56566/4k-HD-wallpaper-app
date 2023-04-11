package com.panaceasoft.pswallpaper.ui.wallpaper.listwithfilter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.panaceasoft.pswallpaper.Config;
import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.databinding.ActivityWallpaperListWithFilterBinding;
import com.panaceasoft.pswallpaper.ui.common.PSAppCompactActivity;
import com.panaceasoft.pswallpaper.utils.Constants;
import com.panaceasoft.pswallpaper.utils.MyContextWrapper;
import com.panaceasoft.pswallpaper.utils.Utils;

public class WallpaperListWithFilterActivity extends PSAppCompactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityWallpaperListWithFilterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_wallpaper_list_with_filter);

        initUI(binding);
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String LANG_CURRENT = preferences.getString(Constants.LANGUAGE_CODE, Config.DEFAULT_LANGUAGE);

        String CURRENT_LANG_COUNTRY_CODE = preferences.getString(Constants.LANGUAGE_COUNTRY_CODE, Config.DEFAULT_LANGUAGE_COUNTRY_CODE);

        super.attachBaseContext(MyContextWrapper.wrap(newBase, LANG_CURRENT, CURRENT_LANG_COUNTRY_CODE, true));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Utils.psLog("Inside Result MainActivity");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initUI(ActivityWallpaperListWithFilterBinding binding) {
        // setup Fragment

        //toolbar
        if (getIntent().getStringExtra(Constants.INTENT__FLAG).equals(Constants.WALLPAPER)) {
            initToolbar(binding.toolbar, getResources().getString(R.string.menu__wallpapers_list));
        } else {
            initToolbar(binding.toolbar, getResources().getString(R.string.menu__live_wallpapers));
        }
        setupFragment(new WallpaperListWithFilterFragment());
    }

}
