package com.panaceasoft.pswallpaper.ui.search.selection.wallpapertypesselection;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.databinding.ActivityGeneralBinding;
import com.panaceasoft.pswallpaper.ui.common.PSAppCompactActivity;
import com.panaceasoft.pswallpaper.utils.Utils;

public class WallpaperTypesSelectionListActivity extends PSAppCompactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityGeneralBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_general);

        initUI(binding);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.psLog("Inside Result MainActivity");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void initUI(ActivityGeneralBinding binding) {
        // Toolbar
        initToolbar(binding.toolbar, getString(R.string.menu__imageTypes));

        // setup Fragment
        setupFragment(new WallpaperTypesSelectionListFragment());

    }

}
