package com.panaceasoft.pswallpaper.ui.wallpaper.detail;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.panaceasoft.pswallpaper.Config;
import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.binding.FragmentDataBindingComponent;
import com.panaceasoft.pswallpaper.databinding.BottomSheetLayoutBinding;
import com.panaceasoft.pswallpaper.databinding.FragmentWallpaperDetailBinding;
import com.panaceasoft.pswallpaper.ui.common.PSFragment;
import com.panaceasoft.pswallpaper.ui.dashboard.adapter.TagAdapter;
import com.panaceasoft.pswallpaper.utils.AutoClearedValue;
import com.panaceasoft.pswallpaper.utils.Constants;
import com.panaceasoft.pswallpaper.utils.PSDialogMsg;
import com.panaceasoft.pswallpaper.utils.TouchImageView;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewmodel.favourite.FavouriteViewModel;
import com.panaceasoft.pswallpaper.viewmodel.point.PointViewModel;
import com.panaceasoft.pswallpaper.viewmodel.user.UserViewModel;
import com.panaceasoft.pswallpaper.viewmodel.wallpaper.WallpaperViewModel;
import com.panaceasoft.pswallpaper.viewmodel.wallpaper.downloadcount.DownloadCountViewModel;
import com.panaceasoft.pswallpaper.viewmodel.wallpaper.touchcount.TouchCountViewModel;
import com.panaceasoft.pswallpaper.viewobject.Wallpaper;
import com.panaceasoft.pswallpaper.viewobject.common.Status;
import com.panaceasoft.pswallpaper.viewobject.holder.WallpaperParamsHolder;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.panaceasoft.pswallpaper.utils.Utils.RESULT_OK;
import static com.panaceasoft.pswallpaper.utils.Utils.psLog;

public class WallpaperDetailFragment extends PSFragment implements UCropFragmentCallback {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "CropImage";
    private TouchCountViewModel touchCountViewModel;
    private DownloadCountViewModel downloadCountViewModel;
    private FavouriteViewModel favouriteViewModel;
    private WallpaperViewModel wallpaperViewModel;
    private UserViewModel userViewModel;

    private String wallpaperId;
    private List<String> tags;
    private int start = 0;
    private int end = 0;

    private PointViewModel pointViewModel;
    private InterstitialAd mInterstitialAd;

    private int itemPosition;
    private PSDialogMsg psDialogMsg;
    private Dialog setAsDialog;
    private ProgressDialog progressDialog;
    private static ProgressDialog mProgressDialog;
    private int currentAdmobIntervalCount = 0;
    private int currentAdmobSwipeCount = 0;
    public String currentFunction;
    private SharedPreferences.Editor editor;
    private WallpaperParamsHolder wallpaperParamsHolder;

    @VisibleForTesting
    private AutoClearedValue<FragmentWallpaperDetailBinding> binding;
    private AutoClearedValue<BottomSheetDialog> bottomSheetDialog;
    private AutoClearedValue<BottomSheetLayoutBinding> bottomSheetLayoutBinding;
    private AutoClearedValue<TagAdapter> tagAdapter;
    private AutoClearedValue<Drawable> drawable;

    //endregion

    //region Override Methods
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentWallpaperDetailBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallpaper_detail, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);

        initUIAndActions();

        setHasOptionsMenu(true);

        prepareFullScreenAds();

        return binding.get().getRoot();
    }

    @Override
    protected void initUIAndActions() {

        onCreateDialog();

        prepareSetAsWallpaperDialog();

        wallpaperParamsHolder = new WallpaperParamsHolder();
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);

        if (getActivity() != null) {
            psDialogMsg = new PSDialogMsg(this.getActivity(), false);

            drawable = new AutoClearedValue<>(this, getActivity().getResources().getDrawable(R.drawable.circular_shape));

            tags = new ArrayList<>();

            BottomSheetDialog mBottomSheet = new BottomSheetDialog(getActivity());
            bottomSheetDialog = new AutoClearedValue<>(this, mBottomSheet);

            BottomSheetLayoutBinding bottomSheetLayoutBindingObject = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.bottom_sheet_layout, null, false, dataBindingComponent);
            bottomSheetLayoutBinding = new AutoClearedValue<>(this, bottomSheetLayoutBindingObject);

            bottomSheetDialog.get().setContentView(bottomSheetLayoutBinding.get().getRoot());
        }

        initBottomSheetUIAndAction();

        binding.get().wallpaperViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageScrollStateChanged(int arg0) {

            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            public void onPageSelected(int currentPage) {

                if (wallpaperViewModel.wallpapers != null) {
                    if (currentPage >= wallpaperViewModel.wallpapers.size()) {
                        currentPage = currentPage % wallpaperViewModel.wallpapers.size();
                        itemPosition = currentPage;
                        if (!wallpaperId.equals(wallpaperViewModel.wallpapers.get(itemPosition).wallpaper_id)) {
                            wallpaperId = wallpaperViewModel.wallpapers.get(itemPosition).wallpaper_id;
                            checkPremium(wallpaperViewModel.wallpapers.get(itemPosition));
                            checkIsGif(wallpaperViewModel.wallpapers.get(itemPosition));
                            setTouchCount(wallpaperId);
                        }
                    } else {
                        itemPosition = currentPage;
                        if (!wallpaperId.equals(wallpaperViewModel.wallpapers.get(itemPosition).wallpaper_id)) {
                            wallpaperId = wallpaperViewModel.wallpapers.get(itemPosition).wallpaper_id;
                            checkPremium(wallpaperViewModel.wallpapers.get(itemPosition));
                            checkIsGif(wallpaperViewModel.wallpapers.get(itemPosition));
                            setTouchCount(wallpaperId);

                            if (Config.SHOW_ADMOB_AT_SWIPE) {
                                showFullScreenAdsForSwipe();
                            }
                        }
                    }
                }
            }

        });

        binding.get().premiumPriceTextView.setOnClickListener(v -> {
            if (wallpaperViewModel.wallpapers != null) {
                if (itemPosition < wallpaperViewModel.wallpapers.size()) {

                    Wallpaper wallpaper = wallpaperViewModel.wallpapers.get(itemPosition);

                    if (wallpaper.is_buy.equals(Constants.ZERO)) {

                        if(wallpaperViewModel.type.equals(Constants.TWO)) {

                            Utils.navigateOnUserVerificationActivity(userIdToVerify, loginUserId, psDialogMsg, getActivity(), navigationController, new Utils.NavigateOnUserVerificationActivityCallback() {
                                @Override
                                public void onSuccess() {
                                    psDialogMsg.showConfirmDialog(getString(R.string.buy_first), getString(R.string.app__ok), getString(R.string.confirm_message__cancel));
                                    psDialogMsg.show();

                                    psDialogMsg.okButton.setOnClickListener(v15 -> {

                                        buyWallpaper(wallpaperViewModel.wallpapers.get(itemPosition).point);

                                        psDialogMsg.cancel();
                                    });

                                    psDialogMsg.cancelButton.setOnClickListener(v14 -> psDialogMsg.cancel());
                                }
                            });

                        }
                    }
                }
            }
        });

        binding.get().fab.setOnClickListener(v -> {

            tags.clear();

            if (wallpaperViewModel.wallpapers != null) {
                if (itemPosition >= wallpaperViewModel.wallpapers.size()) {
                    itemPosition = itemPosition % wallpaperViewModel.wallpapers.size();

                    replaceWallpaperDetailData(wallpaperViewModel.wallpapers.get(itemPosition));

                    Wallpaper wallpaper = wallpaperViewModel.wallpapers.get(itemPosition);

                    start = 0;
                    end = 0;
                    tags.clear();

                    if (wallpaper.wallpaper_search_tags.contains(",")) {

                        for (end = wallpaper.wallpaper_search_tags.indexOf(','); end > -1; end = wallpaper.wallpaper_search_tags.indexOf(',', end + 1)) {

                            tags.add(wallpaper.wallpaper_search_tags.substring(start, end - 1));

                            start = end + 1;
                        }

                        tags.add(wallpaper.wallpaper_search_tags.substring(start));

                    } else {
                        tags.add(wallpaper.wallpaper_search_tags);
                    }

                    replaceTags(tags);

                } else {

                    start = 0;
                    end = 0;
                    tags.clear();

                    replaceWallpaperDetailData(wallpaperViewModel.wallpapers.get(itemPosition));

                    Wallpaper wallpaper = wallpaperViewModel.wallpapers.get(itemPosition);

                    if (wallpaper.wallpaper_search_tags.indexOf(",") > 0) {
                        for (end = wallpaper.wallpaper_search_tags.indexOf(','); end > -1; end = wallpaper.wallpaper_search_tags.indexOf(',', end + 1)) {

                            tags.add(wallpaper.wallpaper_search_tags.substring(start, end - 1));

                            start = end + 1;
                        }

                        tags.add(wallpaper.wallpaper_search_tags.substring(start));
                    } else {
                        tags.add(wallpaper.wallpaper_search_tags);
                    }

                    replaceTags(tags);
                }
            }

            bottomSheetDialog.get().show();

        });

        binding.get().backImageView.setOnClickListener(v -> Objects.requireNonNull(getActivity()).finish());


    }

    private void showFullScreenAds() {
        if (currentAdmobIntervalCount >= Config.SHOW_ADMOB_AT_DOWNLOAD_INTERVAL_COUNT) {

            Utils.psLog("******Get Advertising Full Ad*****");

            if (mInterstitialAd.isLoaded()) {

                //reset control
                currentAdmobIntervalCount = 0;
                editor.putInt(Constants.CURRENT_ADMOB_AT_DOWNLOAD_INTERVAL_COUNT_KEY, currentAdmobIntervalCount);
                editor.apply();

                // show admob
                mInterstitialAd.show();
            }


        } else {
            currentAdmobIntervalCount++;
            editor.putInt(Constants.CURRENT_ADMOB_AT_DOWNLOAD_INTERVAL_COUNT_KEY, currentAdmobIntervalCount);
            editor.apply();
        }

    }

    private void showFullScreenAdsForSwipe() {
        if (currentAdmobSwipeCount >= Config.SHOW_ADMOB_AT_SWIPE_COUNT) {

            Utils.psLog("******Get Advertising Full Ad*****");

            if (mInterstitialAd.isLoaded()) {

                //reset control
                currentAdmobSwipeCount = 0;
                editor.putInt(Constants.CURRENT_ADMOB_AT_SWIPE_COUNT_KEY, currentAdmobSwipeCount);
                editor.apply();

                // show admob
                mInterstitialAd.show();
            }


        } else {
            currentAdmobSwipeCount++;
            editor.putInt(Constants.CURRENT_ADMOB_AT_SWIPE_COUNT_KEY, currentAdmobSwipeCount);
            editor.apply();
        }

    }

    private void checkVisibilityForSetAs(String gifOrNot) {
        if (gifOrNot.equals(Constants.GIF)) {
            bottomSheetLayoutBinding.get().setAsCardView.setVisibility(View.GONE);
        } else {
            bottomSheetLayoutBinding.get().setAsCardView.setVisibility(View.VISIBLE);
        }
    }

    private Uri saveImageExternal(Bitmap image) {
        Uri uri = null;
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            File file = new File(Objects.requireNonNull(getContext()).getExternalFilesDir(Environment.DIRECTORY_PICTURES), "to-share.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.close();
            uri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    @Override
    protected void initViewModels() {
        touchCountViewModel = new ViewModelProvider(this, viewModelFactory).get(TouchCountViewModel.class);
        downloadCountViewModel = new ViewModelProvider(this, viewModelFactory).get(DownloadCountViewModel.class);
        favouriteViewModel = new ViewModelProvider(this, viewModelFactory).get(FavouriteViewModel.class);
        wallpaperViewModel = new ViewModelProvider(this, viewModelFactory).get(WallpaperViewModel.class);
        userViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);
        pointViewModel = new ViewModelProvider(this, viewModelFactory).get(PointViewModel.class);
    }

    @Override
    protected void initAdapters() {

        TagAdapter nvAdapter = new TagAdapter(dataBindingComponent, tag -> {
//          Toast.makeText(getContext(),tag,Toast.LENGTH_SHORT).show();
            wallpaperParamsHolder.wallpaperName = tag;
            navigationController.navigateToLatestWallpaperList(getActivity(), wallpaperParamsHolder, Constants.WALLPAPER);
        });

        tagAdapter = new AutoClearedValue<>(this, nvAdapter);
        bottomSheetLayoutBinding.get().tagRecyclerView.setAdapter(tagAdapter.get());
    }

    private boolean isFirstOpen = true;

    @Override
    protected void initData() {


        // Load Control
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();
        editor.apply();

        // Get Current Touch Count
        currentAdmobIntervalCount = preferences.getInt(Constants.CURRENT_ADMOB_AT_DOWNLOAD_INTERVAL_COUNT_KEY, Config.REWARD_INIT);

        try {
            if (getActivity() != null) {
                if (getActivity().getIntent().getExtras() != null) {

                    wallpaperId = getActivity().getIntent().getExtras().getString(Constants.INTENT__WALLPAPER_ID);
                    wallpaperViewModel.wallpaperParamsHolder = (WallpaperParamsHolder) getActivity().getIntent().getSerializableExtra(Constants.INTENT__WALLPAPER_PARAM_HOLDER);

                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("", e);
        }

        setTouchCount(wallpaperId);

        wallpaperViewModel.setGetWallpaperListFromDatabaseObj(wallpaperViewModel.wallpaperParamsHolder);

        wallpaperViewModel.getGetWallpaperListFromDatabaseData().observe(this, result -> {

            if (result != null) {
                if (result.size() > 0) {

                    if (isFirstOpen) {
                        String gifOrNot = wallpaperViewModel.wallpaperParamsHolder.isGif;
                        checkVisibilityForSetAs(gifOrNot);
                        wallpaperViewModel.wallpapers.clear();

                        wallpaperViewModel.wallpapers.addAll(result);

                        for (int i = 0; i < wallpaperViewModel.wallpapers.size(); i++) {
                            if (wallpaperViewModel.wallpapers.get(i).wallpaper_id.equals(wallpaperId)) {

                                itemPosition = i;

                                break;
                            }
                        }

                        tags.clear();
                        replaceWallpaperDetailData(result.get(itemPosition));
                        wallpaperViewModel.type = result.get(itemPosition).types;
                        checkPremium(result.get(itemPosition));
                        checkIsGif(result.get(itemPosition));

                        for (int i = result.get(itemPosition).wallpaper_search_tags.indexOf(','); i > -1; i = result.get(itemPosition).wallpaper_search_tags.indexOf(',', i + 1)) {

                            tags.add(result.get(itemPosition).wallpaper_search_tags.substring(start, i - 1));

                            start = i + 1;
                        }
                        replaceTags(tags);


                        binding.get().wallpaperViewPager.setAdapter(new TouchImageAdapter());
                        binding.get().wallpaperViewPager.setCurrentItem(itemPosition);
                        isFirstOpen = false;
                    } else {
                        for (int i = 0; i < wallpaperViewModel.wallpapers.size(); i++) {
                            for (int j = 0; j < result.size(); j++) {
                                if (wallpaperViewModel.wallpapers.get(i).wallpaper_id.equals(result.get(j).wallpaper_id)) {
                                    Collections.replaceAll(wallpaperViewModel.wallpapers, wallpaperViewModel.wallpapers.get(i), result.get(j));
                                }
                            }
                        }
                    }
                } else {
                    wallpaperViewModel.wallpapers.addAll(result);
                }
            }
        });
        ///getDataByParameterHolderFROMDATABASE

        //get touch count post method
        touchCountViewModel.getTouchCountPostData().observe(this, result -> {
            if (result != null) {
                if (result.data != null) {
                    if (WallpaperDetailFragment.this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }

                } else {
                    if (WallpaperDetailFragment.this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }
                }
            }
        });

        //get wallpaper list
        wallpaperViewModel.getWallpaperListLiveData().observe(this, result -> {

            if (result.data != null) {
                if(result.status == Status.SUCCESS) {
                    if (wallpaperViewModel.callFromDBWallpaper) {
                        wallpaperViewModel.setLoadingState(false);
                        replaceWallpaperDetailData(result.data.get(itemPosition));
                        wallpaperViewModel.setGetWallpaperListFromDatabaseObj(wallpaperViewModel.wallpaperParamsHolder);
                        wallpaperViewModel.callFromDBWallpaper = false;
                    }
                }
            }
        });

        //get download count post method
        downloadCountViewModel.getDownloadCountPostData().observe(this, result -> {
            if (result != null) {
                if (result.data != null) {
                    if (WallpaperDetailFragment.this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                        bottomSheetLayoutBinding.get().downloadCountNumberTextView.setText(Utils.numberFormat(result.data.download_count));
                    }

                } else {
                    if (WallpaperDetailFragment.this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }
                }
            }
        });

        //get favourite post method
        favouriteViewModel.getFavouritePostData().observe(this, result -> {
            if (result != null) {
                if (result.status == Status.SUCCESS) {
                    if (WallpaperDetailFragment.this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }

                } else if (result.status == Status.ERROR) {
                    if (WallpaperDetailFragment.this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }
                }
            }
        });

        wallpaperViewModel.getRatingData().observe(this, result -> {

            if (result != null) {
                if (result.data != null) {
                    if (WallpaperDetailFragment.this.getActivity() != null) {

                        wallpaperViewModel.wallpaperContainer = result.data;
                        bottomSheetLayoutBinding.get().ratingBar.setRating(result.data.rating_count);
                        bottomSheetLayoutBinding.get().ratingBarTextView.setText(String.valueOf(result.data.rating_count));

                        wallpaperViewModel.setLoadingState(false);
                    }

                } else {
                    if (WallpaperDetailFragment.this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }
                    wallpaperViewModel.setLoadingState(false);
                }
            }

        });

        wallpaperViewModel.getBuyWallpaperData().observe(this, result -> {

            switch (result.status) {
                case SUCCESS:

                    progressDialog.cancel();

                    checkPremium(wallpaperViewModel.wallpapers.get(itemPosition));
                    checkIsGif(wallpaperViewModel.wallpapers.get(itemPosition));

                    psDialogMsg.showInfoDialog(getString(R.string.purchase_successful), getString(R.string.app__ok));
                    psDialogMsg.show();

                    psDialogMsg.okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            psDialogMsg.cancel();

//                            if (currentFunction != null) {
//
//                                Utils.psLog("The current function is " + currentFunction);
//                                switch (WallpaperDetailFragment.this.currentFunction) {
//                                    case Constants.downloadFunction:
//
//                                        String imgPath = wallpaperViewModel.wallpapers.get(itemPosition).default_photo.img_path;
//                                        WallpaperDetailFragment.this.startDownload(Config.APP_IMAGES_URL + imgPath, imgPath);
//
//                                        break;
//                                    case Constants.setAsFunction:

                            WallpaperDetailFragment.this.doActionToSetAsWallpaper();

//                                        break;
//                                    case Constants.shareFunction:
//                                        Bitmap bitmap = WallpaperDetailFragment.this.getBitmapFromView(WallpaperDetailFragment.this.getCurrentImageView());
//                                        WallpaperDetailFragment.this.shareImageUri(WallpaperDetailFragment.this.saveImageExternal(bitmap));
//                                        break;
//                                }

//                                WallpaperDetailFragment.this.currentFunction = "";
//                            }

                        }
                    });

                    break;

                case ERROR:

                    progressDialog.cancel();

                    psDialogMsg.showErrorDialog(result.message, getString(R.string.app__ok));
                    psDialogMsg.show();

                    this.currentFunction = "";

                    break;

            }
        });


    }

    private void checkIsGif(Wallpaper wallpaper) {
        if (wallpaper.is_gif.equals(Constants.GIF)) {
            binding.get().gifImageView.setVisibility(View.VISIBLE);
        } else {
            binding.get().gifImageView.setVisibility(View.GONE);
        }
    }

    private void checkPremium(Wallpaper wallpaper) {

        if (wallpaper.types.equals(Constants.TWO)) {

            binding.get().premiumPriceTextView.setVisibility(View.VISIBLE);
            binding.get().premiumImageView.setVisibility(View.VISIBLE);
            binding.get().premiumTextView.setVisibility(View.VISIBLE);

            if (wallpaper.is_buy.equals(Constants.ZERO)) {
                binding.get().premiumPriceTextView.setText(getString(R.string.premium_count, String.valueOf(wallpaper.point)));
            } else {
                binding.get().premiumPriceTextView.setText(getString(R.string.premium_count_purchased, String.valueOf(wallpaper.point)));
            }
        } else {
            binding.get().premiumPriceTextView.setVisibility(View.GONE);
            binding.get().premiumImageView.setVisibility(View.GONE);
            binding.get().premiumTextView.setVisibility(View.GONE);
        }

    }

    //touch count
    private void setTouchCount(String wallpaperId) {
        if (connectivity.isConnected()) {
            touchCountViewModel.setTouchCountPostDataObj(wallpaperId, loginUserId);
        }
    }

    //download count
    private void setDownloadCount(String wallpaperId) {
        if (connectivity.isConnected()) {
            downloadCountViewModel.setDownloadCountPostDataObj(wallpaperId, loginUserId);
        }
    }

    private void replaceWallpaperDetailData(Wallpaper wallpaper) {

        if (wallpaper != null) {
            bottomSheetLayoutBinding.get().setWallpaper(wallpaper);

            checkVisibilityForSetAs(wallpaper.is_gif);

            bottomSheetLayoutBinding.get().viewCountNumberTextView.setText(Utils.numberFormat(wallpaper.touch_count));
            bottomSheetLayoutBinding.get().downloadCountNumberTextView.setText(Utils.numberFormat(wallpaper.download_count));
            bottomSheetLayoutBinding.get().favouriteCountNumberTextView.setText(Utils.numberFormat(wallpaper.favourite_count));

            if (wallpaper.is_favourited.equals(Constants.ONE)) {
                bottomSheetLayoutBinding.get().heartButton.setLiked(true);
            } else if (wallpaper.is_favourited.equals(Constants.ZERO)) {
                bottomSheetLayoutBinding.get().heartButton.setLiked(false);
            }

            if (wallpaper.is_landscape.equals(Constants.ONE)) {
                bottomSheetLayoutBinding.get().orientationValueTextView.setText(getString(R.string.Landscape));
            } else if (wallpaper.is_portrait.equals(Constants.ONE)) {
                bottomSheetLayoutBinding.get().orientationValueTextView.setText(getString(R.string.Portrait));
            } else if (wallpaper.is_square.equals(Constants.ONE)) {
                bottomSheetLayoutBinding.get().orientationValueTextView.setText(getString(R.string.Square));
            }

            if (!wallpaper.color.code.isEmpty()) {
                drawable.get().setColorFilter(Color.parseColor(wallpaper.color.code), PorterDuff.Mode.SRC_ATOP);

                bottomSheetLayoutBinding.get().view11.setBackground(drawable.get());
            }

            bottomSheetLayoutBinding.get().colorEditText.setText(wallpaper.color.name);

            if (wallpaperViewModel.wallpaperContainer != null) {

                bottomSheetLayoutBinding.get().ratingBar.setRating(wallpaperViewModel.wallpaperContainer.rating_count);
                bottomSheetLayoutBinding.get().ratingBarTextView.setText(String.valueOf(wallpaperViewModel.wallpaperContainer.rating_count));
            }else {

                bottomSheetLayoutBinding.get().ratingBar.setRating(wallpaper.rating_count);
                bottomSheetLayoutBinding.get().ratingBarTextView.setText(String.valueOf(wallpaper.rating_count));
            }
        }

    }

    private void replaceTags(List<String> wallpapers) {

        if (wallpapers.size() > 0) {
            bottomSheetLayoutBinding.get().tagRecyclerView.setAdapter(this.tagAdapter.get());
            this.tagAdapter.get().replace(wallpapers);
            binding.get().executePendingBindings();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.psLog("On Resume");

        loadLoginUserId();

        if(!loginUserId.isEmpty()){
            userViewModel.setGetUserPointByIdObj(loginUserId);
            wallpaperViewModel.setGetWallpaperListObj(wallpaperViewModel.wallpaperParamsHolder, "", "", loginUserId);
        }

    }

    class TouchImageAdapter extends PagerAdapter {

        private TouchImageAdapter() {
        }

        @Override
        public int getCount() {
            if (wallpaperViewModel.wallpapers != null) {
                return wallpaperViewModel.wallpapers.size();
            } else {
                return 0;
            }
        }

        @Override
        @NonNull
        public ImageView instantiateItem(@NonNull ViewGroup container, int position) {

            TouchImageView imgView = new TouchImageView(container.getContext());
            imgView.setBackgroundColor(Color.BLACK);
            if (wallpaperViewModel.wallpapers != null) {
                if (position >= wallpaperViewModel.wallpapers.size()) {
                    position = position % wallpaperViewModel.wallpapers.size();
                } else if (position < 0) {
                    position = 0;
                }

                if (getActivity() != null) {
//                    if (wallpaperViewModel.wallpaperParamsHolder.isGif.equals(Constants.GIF)) {
                    // For Detail Image Loading

//                    } else {
//                        dataBindingComponent.getFragmentBindingAdapters().bindFullImage(imgView, wallpaperViewModel.wallpapers.get(position).default_photo.img_path);
//                    }

                    if (wallpaperViewModel.wallpapers.get(position).is_gif.equals(Constants.GIF)) {
                        dataBindingComponent.getFragmentBindingAdapters().bindGif(imgView, wallpaperViewModel.wallpapers.get(position).default_photo.image_path_thumb, wallpaperViewModel.wallpapers.get(position).default_photo.img_path);
                    } else {
                        dataBindingComponent.getFragmentBindingAdapters().bindFullImage(imgView, wallpaperViewModel.wallpapers.get(position).default_photo.img_path);
                    }

                    container.addView(imgView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                }
            }

            return imgView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

    }

    private void prepareSetAsWallpaperDialog() {

        if (getContext() != null) {

            setAsDialog = new Dialog(getContext());
            setAsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setAsDialog.setContentView(R.layout.item_detail_select_set_as);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            if (setAsDialog.getWindow() != null) {

                lp.copyFrom(setAsDialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                Button homeScreenButton = setAsDialog.findViewById(R.id.homeScreenButton);
                Button lockScreenButton = setAsDialog.findViewById(R.id.lockScreenButton);
                Button homeAndLockScreenButton = setAsDialog.findViewById(R.id.homeAndLockScreenButton);

                homeScreenButton.setOnClickListener((View v) -> {
                    setAsDialog.cancel();
                    startSetAsWallpaper(Utils.setToHome);
                });

                lockScreenButton.setOnClickListener((View v) -> {
                    setAsDialog.cancel();
                    startSetAsWallpaper(Utils.setToLock);
                });

                homeAndLockScreenButton.setOnClickListener((View v) -> {
                    setAsDialog.cancel();
                    startSetAsWallpaper(Utils.setToBoth);
                });


                setAsDialog.getWindow().setAttributes(lp);
            }
        }
    }

    private Uri uri;

    private void startSetAsWallpaper(String action) {

        new setWallpaper().execute(uri, action);

    }

    private void initBottomSheetUIAndAction() {

        bottomSheetLayoutBinding.get().setAsCardView.setOnClickListener(v -> {

            if (checkAvailabilityOfWallpaper().equals(Utils.FREE)) {

                doActionToSetAsWallpaper();

            } else {

                if(wallpaperViewModel.type.equals(Constants.TWO)){

                    Utils.navigateOnUserVerificationActivity(userIdToVerify, loginUserId, psDialogMsg, getActivity(), navigationController, new Utils.NavigateOnUserVerificationActivityCallback() {
                        @Override
                        public void onSuccess() {

                            if (checkAvailabilityOfWallpaper().equals(Utils.PREMIUM_PURCHASED)) {

                                doActionToSetAsWallpaper();

                            } else if (checkAvailabilityOfWallpaper().equals(Utils.PREMIUM_NOT_PURCHASED)) {

                                currentFunction = Constants.setAsFunction;

                                psDialogMsg.showConfirmDialog(getString(R.string.buy_first), getString(R.string.app__ok), getString(R.string.confirm_message__cancel));
                                psDialogMsg.show();

                                psDialogMsg.okButton.setOnClickListener(v15 -> {


                                    buyWallpaper(wallpaperViewModel.wallpapers.get(itemPosition).point);

                                    psDialogMsg.cancel();

                                });

                                psDialogMsg.cancelButton.setOnClickListener(v14 -> psDialogMsg.cancel());
                            }
                        }
                    });

                }
            }
        });

        bottomSheetLayoutBinding.get().heartButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                favFunction(wallpaperViewModel.wallpapers.get(itemPosition), bottomSheetLayoutBinding.get().heartButton);
            }

            @Override
            public void unLiked(LikeButton likeButton) {

                unFavFunction(wallpaperViewModel.wallpapers.get(itemPosition), bottomSheetLayoutBinding.get().heartButton);
            }
        });

        bottomSheetLayoutBinding.get().ratingCardView.setOnClickListener(v -> {

            Utils.navigateOnUserVerificationActivity(userIdToVerify, loginUserId, psDialogMsg, getActivity(), navigationController, new Utils.NavigateOnUserVerificationActivityCallback() {
                @Override
                public void onSuccess() {

                    psDialogMsg.showRatingDialog(getString(R.string.your_rating), getString(R.string.button_submit), getString(R.string.confirm_message__cancel), 0);
                    psDialogMsg.show();

                    psDialogMsg.okButton.setOnClickListener(v1 -> {

                        psDialogMsg.cancel();

                        wallpaperViewModel.setRatingObj(wallpaperViewModel.wallpapers.get(itemPosition).wallpaper_id, loginUserId, psDialogMsg.newRating);

                    });
                }
            });

        });

        bottomSheetLayoutBinding.get().shareCardView.setOnClickListener(v -> {

            if (checkAvailabilityOfWallpaper().equals(Utils.FREE)) {
                Bitmap bitmap = getBitmapFromView(getCurrentImageView());
                shareImageUri(saveImageExternal(bitmap));
                if (Config.SHOW_ADMOB_AT_DOWNLOAD) {
                    showFullScreenAds();
                }
            } else {

                if(wallpaperViewModel.type.equals(Constants.TWO)) {

                    Utils.navigateOnUserVerificationActivity(userIdToVerify, loginUserId, psDialogMsg, getActivity(), navigationController, new Utils.NavigateOnUserVerificationActivityCallback() {
                        @Override
                        public void onSuccess() {
                            if (checkAvailabilityOfWallpaper().equals(Utils.PREMIUM_PURCHASED)) {

                                Bitmap bitmap = getBitmapFromView(getCurrentImageView());
                                shareImageUri(saveImageExternal(bitmap));
                                if (Config.SHOW_ADMOB_AT_DOWNLOAD) {
                                    showFullScreenAds();
                                }

                            } else if (checkAvailabilityOfWallpaper().equals(Utils.PREMIUM_NOT_PURCHASED)) {

                                currentFunction = Constants.shareFunction;

                                psDialogMsg.showConfirmDialog(getString(R.string.buy_first), getString(R.string.app__ok), getString(R.string.confirm_message__cancel));
                                psDialogMsg.show();

                                psDialogMsg.okButton.setOnClickListener(v15 -> {

                                    buyWallpaper(wallpaperViewModel.wallpapers.get(itemPosition).point);

                                    psDialogMsg.cancel();
                                });

                                psDialogMsg.cancelButton.setOnClickListener(v14 -> psDialogMsg.cancel());
                            }
                        }
                    });

                }
            }
        });

        bottomSheetLayoutBinding.get().downloadCardView.setOnClickListener(v -> {

            if (connectivity.isConnected()) {

                if (checkAvailabilityOfWallpaper().equals(Utils.FREE)) {
                    String imgPath = wallpaperViewModel.wallpapers.get(itemPosition).default_photo.img_path;
                    startDownload(Config.APP_IMAGES_URL + imgPath, imgPath);
                    bottomSheetDialog.get().cancel();
                } else {
                    if(wallpaperViewModel.type.equals(Constants.TWO)) {

                        Utils.navigateOnUserVerificationActivity(userIdToVerify, loginUserId, psDialogMsg, getActivity(), navigationController, new Utils.NavigateOnUserVerificationActivityCallback() {
                            @Override
                            public void onSuccess() {
                                if (checkAvailabilityOfWallpaper().equals(Utils.FREE) || checkAvailabilityOfWallpaper().equals(Utils.PREMIUM_PURCHASED)) {
                                    if (bottomSheetDialog.get().isShowing()) {
                                        bottomSheetDialog.get().cancel();
                                    }

                                    progressDialog.show();

                                    Utils.psLog("Download Wallpaper Id " + wallpaperViewModel.wallpapers.get(itemPosition).wallpaper_id);
                                    String imgPath = wallpaperViewModel.wallpapers.get(itemPosition).default_photo.img_path;
                                    startDownload(Config.APP_IMAGES_URL + imgPath, imgPath);

                                } else if (checkAvailabilityOfWallpaper().equals(Utils.PREMIUM_NOT_PURCHASED)) {

                                    currentFunction = Constants.downloadFunction;

                                    psDialogMsg.showConfirmDialog(getString(R.string.buy_first), getString(R.string.app__ok), getString(R.string.confirm_message__cancel));
                                    psDialogMsg.show();

                                    psDialogMsg.okButton.setOnClickListener(v15 -> {

                                        buyWallpaper(wallpaperViewModel.wallpapers.get(itemPosition).point);

                                        psDialogMsg.cancel();
                                    });

                                    psDialogMsg.cancelButton.setOnClickListener(v14 -> psDialogMsg.cancel());
                                }
                            }
                        });
                    }
                }
            } else {

                psDialogMsg.showErrorDialog(getString(R.string.error_message__no_internet), getString(R.string.app__ok));
                psDialogMsg.show();
            }

        });
    }


    private void prepareFullScreenAds() {
        if (getContext() != null) {

            // load AD
            mInterstitialAd = new InterstitialAd(getContext());

            // set the ad unit ID
            mInterstitialAd.setAdUnitId(getString(R.string.adview_interstitial_ad_key));

            AdRequest adRequest = new AdRequest.Builder().build();

            // Load ads into Interstitial Ads
            mInterstitialAd.loadAd(adRequest);

            mInterstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdFailedToLoad(int i) {
                    Log.d("TEAMPS", "Failed to load." + i);
                    super.onAdFailedToLoad(i);
                }

                @Override
                public void onAdClosed() {

                    super.onAdClosed();

                    pointViewModel.setSendClaimedPointToServerObj(loginUserId, String.valueOf(Config.REWARD_POINT_1));

                    mInterstitialAd.loadAd(adRequest);
                }

                public void onAdLoaded() {

                }
            });

        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Condition Checking and taking action Section

    private void doActionToSetAsWallpaper() {

        psDialogMsg.showConfirmDialog(getResources().getString(R.string.message__crop_image), getString(R.string.app__ok), getString(R.string.confirm_message__cancel));
        psDialogMsg.show();
        psDialogMsg.okButton.setOnClickListener(v13 -> {

            psDialogMsg.cancel();
            bottomSheetDialog.get().cancel();

            startCrop(changeUri(getCurrentImageView()));
        });

        psDialogMsg.cancelButton.setOnClickListener((View v12) -> {

            uri = null;
            setAsDialog.show();

            psDialogMsg.cancel();

            if (bottomSheetDialog.get().isShowing()) {
                bottomSheetDialog.get().cancel();
            }

        });
    }

    private String checkAvailabilityOfWallpaper() {

        Wallpaper wallpaper = wallpaperViewModel.wallpapers.get(itemPosition);
        String status = null;

        if (wallpaper.types.equals(Constants.ONE)) {
            status = Utils.FREE;

        } else if (wallpaper.types.equals(Constants.TWO)) {

            if (wallpaper.is_buy.equals(Constants.ONE)) {
                status = Utils.PREMIUM_PURCHASED;

            } else {
                status = Utils.PREMIUM_NOT_PURCHASED;
            }
        }

        return status;
    }

    private void buyWallpaper(int wallpaperPoint) {
        wallpaperViewModel.setBuyWallpaperObj(loginUserId, wallpaperPoint, "$", wallpaperId);
        progressDialog.show();
    }

    private void startDownload(String serverUrl, String fileName) {
        Utils.psLog("Download File : " + serverUrl);
        if (Utils.isStoragePermissionGranted(getActivity())) {
            new DownloadFileAsync().execute(serverUrl, fileName);
        }

        // Get the bitmap from drawable object
//        Bitmap bitmap = null;
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // Save image to gallery
//        String savedImageURL = MediaStore.Images.Media.insertImage(
//                getContext().getContentResolver(),
//                bitmap,
//                "Bird",
//                "Image of bird"
//        );
//
//        // Parse the gallery image url to uri
//        Uri savedImageURI = Uri.parse(savedImageURL);
//
//        // Display the saved image to ImageView
//        iv_saved.setImageURI(savedImageURI);
//
//        // Display saved image url to TextView
//        tv_saved.setText("Image saved to gallery.\n" + savedImageURL);
    }

    private void showDialog() {
        mProgressDialog.setProgress(0);
        mProgressDialog.show();
    }

    private void dismissDialog() {
        mProgressDialog.setProgress(100);
        mProgressDialog.cancel();
    }

    private void onCreateDialog() {

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.message__downloading));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMax(100);

    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

        private boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showDialog();
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {
                URL url = new URL(aurl[0]);
                String fileNameAndExtension = aurl[1];
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept-Encoding", "identity");
                urlConnection.connect();

                InputStream input = new BufferedInputStream(url.openStream());
                File path = Environment.getExternalStorageDirectory();
                File dir = new File(path, "/0_Wallpapers/");

                if (!dir.exists()) {
                    boolean b = dir.mkdir();
                }
                File file = new File(dir, fileNameAndExtension);

                try {
                    OutputStream output = new FileOutputStream(file);

                    byte data[] = new byte[1024];

                    long total = 0;

                    int lenghtOfFile = urlConnection.getContentLength();
                    Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();

                    // Tell the media scanner about the new file so that it is
                    // immediately available to the user.
                    MediaScannerConnection.scanFile(getContext(),new String[] { file.getAbsolutePath() }, null,new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

                    isSuccess = true;
                } catch (Exception e) {
                    isSuccess = false;
                    Utils.psErrorLog("", e);
                }
            } catch (Exception e) {
                isSuccess = false;
                Utils.psErrorLog("", e);
            }
            return null;

        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            dismissDialog();

            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }

            if (isSuccess) {
                setDownloadCount(wallpaperViewModel.wallpapers.get(itemPosition).wallpaper_id);
                Toast.makeText(getContext(), getString(R.string.message__download_success), Toast.LENGTH_SHORT).show();
                if (Config.SHOW_ADMOB_AT_DOWNLOAD) {
                    showFullScreenAds();
                }
            }

        }
    }

    //Condition Checking and taking action Section
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void shareImageUri(Uri uri) {

        new Thread(() -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/*");
                Objects.requireNonNull(getContext()).startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Cropping Section

    private void startCrop(@NonNull Uri uri) {
        if (getActivity() != null) {
            AutoClearedValue<UCrop> uCrop = new AutoClearedValue<>(this, UCrop.of(uri, Uri.fromFile(new File(this.getActivity().getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME))));
            uCrop.get().start(this.getActivity());
        }
    }

    private ImageView getCurrentImageView() {

        return (ImageView) Utils.getCurrentView(binding.get().wallpaperViewPager);
    }

    private Bitmap getBitmapFromView(ImageView view) {
        Drawable drawable = view.getDrawable();

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private Uri changeUri(ImageView imageView) {

        Bitmap bitmap = this.getBitmapFromView(imageView);
        try {
            wallpaperViewModel.imageUri = saveImageExternal(bitmap).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Uri.parse(wallpaperViewModel.imageUri);
    }

    @Override
    public void loadingProgress(boolean showLoader) {

    }

    @Override
    public void onCropFinish(UCropFragment.UCropResult result) {
        switch (result.mResultCode) {
            case RESULT_OK:
                try {
                    handleCropResult(result.mResultData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case UCrop.RESULT_ERROR:
                handleCropError(result.mResultData);
                break;
        }
//        removeFragmentFromScreen();
    }

    private void handleCropResult(@NonNull Intent result) throws IOException {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {

            if (bottomSheetDialog.get().isShowing()) {
                bottomSheetDialog.get().cancel();
            }

//            progressDialog.show();

            uri = resultUri;
            setAsDialog.show();

        } else {
            Toast.makeText(this.getActivity(), R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Toast.makeText(this.getActivity(), cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this.getActivity(), R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Utils.psLog("Inside Activity Result.");
        if (resultCode == RESULT_OK) {
            int requestMode = -1;
            if (requestCode == requestMode) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCrop(selectedUri);
                    binding.get().toolbar.setVisibility(View.GONE);
                } else {
                    binding.get().toolbar.setVisibility(View.GONE);
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                try {
                    handleCropResult(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.get().toolbar.setVisibility(View.GONE);
            }
        }

        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
            binding.get().toolbar.setVisibility(View.GONE);
        }
    }

    private class setWallpaper extends AsyncTask<Object, Object, Object> {

        Uri uri;
        boolean result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            if (uri == null) {
                uri = changeUri(getCurrentImageView());
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            if (objects[0] != null) {
                uri = (Uri) objects[0];
            }

            if (uri != null) {
                String actionStr = (String) objects[1];

                if (getActivity() != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    WallpaperManager manager = WallpaperManager.getInstance(getContext());


                    try {

                        switch (actionStr) {
                            case Utils.setToHome:
                                // For Main Screen
                                manager.setBitmap(bitmap);
                                break;
                            case Utils.setToLock:
                                // For Lock Screen
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                                }
                                break;
                            case Utils.setToBoth:
                                manager.setBitmap(bitmap);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                                }
                                break;
                            default:
                                progressDialog.cancel();
                        }

                        result = true;

                    } catch (IOException e) {
                        progressDialog.cancel();

                        result = false;
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (result) {
                if (Config.SHOW_ADMOB_AT_DOWNLOAD) {
                    showFullScreenAds();
                }

                progressDialog.cancel();

                Toast.makeText(getActivity(), "Set As Wallpaper", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void unFavFunction(Wallpaper wallpaper, LikeButton likeButton) {

        Utils.navigateOnUserVerificationActivityFromFav(userIdToVerify, loginUserId, psDialogMsg, getActivity(), navigationController,likeButton,() -> {

            if (!favouriteViewModel.isLoading) {
                favouriteViewModel.setFavouritePostDataObj(wallpaper.wallpaper_id, loginUserId);
                likeButton.setLikeDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.heart_off, null));
            }

        });
    }

    private void favFunction(Wallpaper wallpaper, LikeButton likeButton) {

        Utils.navigateOnUserVerificationActivityFromFav(userIdToVerify, loginUserId, psDialogMsg, getActivity(), navigationController,likeButton,() -> {

            if (!favouriteViewModel.isLoading) {
                favouriteViewModel.setFavouritePostDataObj(wallpaper.wallpaper_id, loginUserId);
                likeButton.setLikeDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.heart_on, null));
            }

        });
    }
}
