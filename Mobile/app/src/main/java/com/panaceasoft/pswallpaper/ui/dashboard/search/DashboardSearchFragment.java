package com.panaceasoft.pswallpaper.ui.dashboard.search;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdRequest;
import com.panaceasoft.pswallpaper.Config;
import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.binding.FragmentDataBindingComponent;
import com.panaceasoft.pswallpaper.databinding.FragmentDashboardSearchBinding;
import com.panaceasoft.pswallpaper.ui.common.PSFragment;
import com.panaceasoft.pswallpaper.utils.AutoClearedValue;
import com.panaceasoft.pswallpaper.utils.Constants;
import com.panaceasoft.pswallpaper.utils.RangeSeekBar;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewmodel.user.UserViewModel;
import com.panaceasoft.pswallpaper.viewobject.holder.WallpaperParamsHolder;

import java.util.Objects;

public class DashboardSearchFragment extends PSFragment {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private WallpaperParamsHolder wallpaperParamsHolder;
    private Drawable drawable;
    private MenuItem pointMenuItem;
    private UserViewModel userViewModel;

    @VisibleForTesting
    private AutoClearedValue<FragmentDashboardSearchBinding> binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentDashboardSearchBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard_search, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        drawable = Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.circular_shape);

        setHasOptionsMenu(true);

        if (Config.SHOW_ADMOB && connectivity.isConnected()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            binding.get().adView.loadAd(adRequest);
        } else {
            binding.get().adView.setVisibility(View.GONE);
        }
        return binding.get().getRoot();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (Config.ENABLE_PREMIUM) {
            inflater.inflate(R.menu.point_menu, menu);
            pointMenuItem = menu.findItem(R.id.pointItem);
            super.onCreateOptionsMenu(menu, inflater);
        }
        if (userViewModel != null) {
            userViewModel.setLocalUser(loginUserId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.pointItem) {
            // Open Claim Activity

            navigationController.navigateToClaimPointActivity(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE__SEARCH_FRAGMENT && resultCode == Constants.RESULT_CODE__SEARCH_WITH_CATEGORY) {
            wallpaperParamsHolder.catId = data.getStringExtra(Constants.INTENT__CAT_ID);

            binding.get().setCategoryEditText.setText(data.getStringExtra(Constants.INTENT__CAT_NAME));
        } else if (requestCode == Constants.REQUEST_CODE__SEARCH_FRAGMENT && resultCode == Constants.RESULT_CODE__SEARCH_WITH_COLOR) {
            wallpaperParamsHolder.colorId = data.getStringExtra(Constants.INTENT__COLOR_ID);
            wallpaperParamsHolder.colorName = data.getStringExtra(Constants.INTENT__COLOR_NAME);
            wallpaperParamsHolder.colorCode = data.getStringExtra(Constants.INTENT__COLOR_CODE);

            drawable.setColorFilter(Color.parseColor(data.getStringExtra(Constants.INTENT__COLOR_CODE)), PorterDuff.Mode.SRC_ATOP);

            binding.get().view11.setBackground(drawable);
            binding.get().colorEditText.setText(data.getStringExtra(Constants.INTENT__COLOR_NAME));
        }
    }

    @Override
    protected void initUIAndActions() {

        RangeSeekBar seekBar = new RangeSeekBar<>(0, 5, this.getContext());

        LinearLayout linearLayout = binding.get().priceRangeBarContainer;
        linearLayout.addView(seekBar);

        seekBar.setSelectedMinValue(0);
        seekBar.setSelectedMaxValue(5);

        wallpaperParamsHolder = new WallpaperParamsHolder();

        seekBar.onStartTrackingTouch(binding.get().setKeyWord);

        seekBar.setOnRangeSeekBarChangeListener((bar, minValue, maxValue) -> {

            wallpaperParamsHolder.rating_max = maxValue.toString();
            wallpaperParamsHolder.rating_min = minValue.toString();

            binding.get().minRatingTextView.setText(wallpaperParamsHolder.rating_min);
            binding.get().maxRatingTextView.setText(wallpaperParamsHolder.rating_max);

        });

        binding.get().setCategoryEditText.setOnClickListener(v -> navigationController.navigateToCategorySelectionActivity(getActivity(), wallpaperParamsHolder.catId));

        binding.get().colorEditText.setOnClickListener(v -> navigationController.navigateToColorSelectionActivity(getActivity(), wallpaperParamsHolder.colorId));

        binding.get().view11.setOnClickListener(v -> navigationController.navigateToColorSelectionActivity(getActivity(), wallpaperParamsHolder.colorId));

        binding.get().filter.setOnClickListener(v -> {

            wallpaperParamsHolder.wallpaperName = binding.get().setKeyWord.getText().toString();
            wallpaperParamsHolder.keyword = binding.get().setKeyWord.getText().toString();

            wallpaperParamsHolder.catName = binding.get().setCategoryEditText.getText().toString();

            if (binding.get().freeSwitch.isChecked()) {
                wallpaperParamsHolder.type = Constants.ONE;
            }

            if (binding.get().premiumSwitch.isChecked()) {
                wallpaperParamsHolder.type = Constants.TWO;
            }

            if (binding.get().freeSwitch.isChecked() && binding.get().premiumSwitch.isChecked()) {
                wallpaperParamsHolder.type = Constants.THREE;
            } else if (!binding.get().freeSwitch.isChecked() && !binding.get().premiumSwitch.isChecked()) {
                wallpaperParamsHolder.type = "";
            }

            if (binding.get().minimumEditText.getText().toString().equals(getResources().getString(R.string.sf__notSet))) {
                wallpaperParamsHolder.point_min = "";
            } else {
                wallpaperParamsHolder.point_min = binding.get().minimumEditText.getText().toString();
            }

            if (binding.get().maximumEditText.getText().toString().equals(getResources().getString(R.string.sf__notSet))) {
                wallpaperParamsHolder.point_max = "";
            } else {
                wallpaperParamsHolder.point_max = binding.get().maximumEditText.getText().toString();
            }

            if (binding.get().isRecommendSwitch.isChecked()) {
                wallpaperParamsHolder.isRecommended = Constants.ONE;
            } else {
                wallpaperParamsHolder.isRecommended = "";
            }

            Utils.psLog(wallpaperParamsHolder.isRecommended + "recommended");

            if (binding.get().isPortraitSwitch.isChecked()) {
                wallpaperParamsHolder.isPortrait = Constants.ONE;
            } else {
                wallpaperParamsHolder.isPortrait = "";
            }

            if (binding.get().isLandScapeSwitch.isChecked()) {
                wallpaperParamsHolder.isLandscape = Constants.ONE;
            } else {
                wallpaperParamsHolder.isLandscape = "";
            }

            if (binding.get().isSquareSwitch.isChecked()) {
                wallpaperParamsHolder.isSquare = Constants.ONE;
            } else {
                wallpaperParamsHolder.isSquare = "";
            }

            if (binding.get().isGifSwitch.isChecked()) {
                wallpaperParamsHolder.isGif = Constants.ONE;
                wallpaperParamsHolder.isWallpaper = Constants.ZERO;
                wallpaperParamsHolder.isLiveWallpaper = Constants.ZERO;
            } else {
                wallpaperParamsHolder.isGif = "";
            }

            if (binding.get().isLiveWallpaperSwitch.isChecked()) {
                wallpaperParamsHolder.isLiveWallpaper = Constants.ONE;
                wallpaperParamsHolder.isWallpaper = Constants.ZERO;
                wallpaperParamsHolder.isGif = Constants.ZERO;
            } else {
                wallpaperParamsHolder.isLiveWallpaper = "";
            }

            if(!binding.get().isGifSwitch.isChecked() && !binding.get().isLiveWallpaperSwitch.isChecked()){
                wallpaperParamsHolder.isWallpaper = Constants.ONE;
            }

            Utils.psLog(wallpaperParamsHolder.isRecommended + "recommended");

            navigationController.navigateToLatestWallpaperList(getActivity(), wallpaperParamsHolder, Constants.WALLPAPER);

        });

        binding.get().isGifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.get().isRecommendSwitch.setChecked(false);
                binding.get().isPortraitSwitch.setChecked(false);
                binding.get().isLandScapeSwitch.setChecked(false);
                binding.get().isSquareSwitch.setChecked(false);
                binding.get().isLiveWallpaperSwitch.setChecked(false);
            }
        });

        binding.get().isLiveWallpaperSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.get().isRecommendSwitch.setChecked(false);
                binding.get().isPortraitSwitch.setChecked(false);
                binding.get().isLandScapeSwitch.setChecked(false);
                binding.get().isSquareSwitch.setChecked(false);
                binding.get().isGifSwitch.setChecked(false);
            }
        });

        binding.get().isRecommendSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.get().isGifSwitch.setChecked(false);
                binding.get().isLiveWallpaperSwitch.setChecked(false);
            }
        });

        binding.get().isPortraitSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.get().isGifSwitch.setChecked(false);
                binding.get().isLiveWallpaperSwitch.setChecked(false);
            }
        });

        binding.get().isLandScapeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.get().isGifSwitch.setChecked(false);
                binding.get().isLiveWallpaperSwitch.setChecked(false);
            }
        });

        binding.get().isSquareSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.get().isGifSwitch.setChecked(false);
                binding.get().isLiveWallpaperSwitch.setChecked(false);
            }
        });

        if (Config.ENABLE_GIF) {
            binding.get().isGifButton.setVisibility(View.VISIBLE);
            binding.get().isGifSwitch.setVisibility(View.VISIBLE);
        } else {
            binding.get().isGifButton.setVisibility(View.GONE);
            binding.get().isGifSwitch.setVisibility(View.GONE);
        }

        if(Config.ENABLE_LIVE_WALLPAPER){
            binding.get().isLiveWallpaperButton.setVisibility(View.VISIBLE);
            binding.get().isLiveWallpaperSwitch.setVisibility(View.VISIBLE);
        }else{
            binding.get().isLiveWallpaperButton.setVisibility(View.GONE);
            binding.get().isLiveWallpaperSwitch.setVisibility(View.GONE);
        }

        if (Config.ENABLE_PREMIUM){
            binding.get().premiumButton.setVisibility(View.VISIBLE);
            binding.get().premiumSwitch.setVisibility(View.VISIBLE);
            binding.get().pointTextView.setVisibility(View.VISIBLE);
            binding.get().pointRangeConstraintLayout.setVisibility(View.VISIBLE);
        }else{
            binding.get().premiumButton.setVisibility(View.GONE);
            binding.get().premiumSwitch.setVisibility(View.GONE);
            binding.get().pointTextView.setVisibility(View.GONE);
            binding.get().pointRangeConstraintLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initViewModels() {
        userViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);
    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

        // get local user from database
        userViewModel.getLocalUser(loginUserId).observe(this, localUserData -> {

            if (localUserData != null) {

                if (pointMenuItem != null && getContext() != null) {
                    pointMenuItem.setTitle(getContext().getString(R.string.dashboard__pts, Utils.numberFormat(Long.parseLong(localUserData.total_point))));
                }


            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

            }
        });

    }
}
