package com.panaceasoft.pswallpaper.ui.livewallpaper.detail;


import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
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
import com.panaceasoft.pswallpaper.databinding.FragmentVideoPlayBinding;
import com.panaceasoft.pswallpaper.ui.common.PSFragment;
import com.panaceasoft.pswallpaper.utils.AutoClearedValue;
import com.panaceasoft.pswallpaper.utils.Constants;
import com.panaceasoft.pswallpaper.utils.PSDialogMsg;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewmodel.favourite.FavouriteViewModel;
import com.panaceasoft.pswallpaper.viewmodel.point.PointViewModel;
import com.panaceasoft.pswallpaper.viewmodel.wallpaper.WallpaperViewModel;
import com.panaceasoft.pswallpaper.viewmodel.wallpaper.downloadcount.DownloadCountViewModel;
import com.panaceasoft.pswallpaper.viewmodel.wallpaper.touchcount.TouchCountViewModel;
import com.panaceasoft.pswallpaper.viewobject.Wallpaper;
import com.panaceasoft.pswallpaper.viewobject.common.Status;
import com.panaceasoft.pswallpaper.viewobject.holder.WallpaperParamsHolder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoPlayFragment extends PSFragment implements Player.EventListener {

    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    @VisibleForTesting
    private AutoClearedValue<FragmentVideoPlayBinding> binding;
    private AutoClearedValue<BottomSheetDialog> bottomSheetDialog;
    private AutoClearedValue<BottomSheetLayoutBinding> bottomSheetLiveWallpaperLayoutBinding;
    private AutoClearedValue<Drawable> drawable;

    private WallpaperViewModel wallpaperViewModel;
    private DownloadCountViewModel downloadCountViewModel;
    private FavouriteViewModel favouriteViewModel;
    private TouchCountViewModel touchCountViewModel;

    private List<String> tags;
    private int start = 0;
    private int end = 0;

    private PointViewModel pointViewModel;
    private InterstitialAd mInterstitialAd;

    private int itemPosition = 0;
    private PSDialogMsg psDialogMsg;
    private ProgressDialog progressDialog;
    private static ProgressDialog mProgressDialog;
    private int currentAdmobIntervalCount = 0;
    private int currentAdmobSwipeCount = 0;
    private String currentFunction;
    private SharedPreferences.Editor editor;
    private boolean isFirstOpen = true;
//    public String videoUri,videoId,videoPath;

    //endregion
    private SimpleExoPlayer player;
    private BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    private TrackSelection.Factory factory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
    private TrackSelector trackSelector = new DefaultTrackSelector(factory);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentVideoPlayBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_play, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);

        setHasOptionsMenu(true);

        prepareFullScreenAds();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //loop

        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        player.setPlayWhenReady(true);
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        binding.get().videoFullScreenPlayer.setPlayer(player);

        return binding.get().getRoot();
    }


    private void setUpVideo() {
        initializePlayer();
        if (wallpaperViewModel.videoUri == null) {
            return;
        }
        buildMediaSource(Uri.parse(wallpaperViewModel.videoUri));
    }

    private void initializePlayer() {
        if (player == null) {
            // 1. Create a default TrackSelector
            LoadControl loadControl = new DefaultLoadControl(
                    new DefaultAllocator(true, 16),
                    Config.MIN_BUFFER_DURATION,
                    Config.MAX_BUFFER_DURATION,
                    Config.MIN_PLAYBACK_START_BUFFER,
                    Config.MIN_PLAYBACK_RESUME_BUFFER, -1, true);

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            // 2. Create the player
            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext()), trackSelector, loadControl);
            binding.get().videoFullScreenPlayer.setPlayer(player);
        }


    }

    private void buildMediaSource(Uri mUri) {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), getString(R.string.app_name)), bandwidthMeter);
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mUri);
        // Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        player.addListener(this);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {

            case Player.STATE_BUFFERING:
                binding.get().spinnerVideoDetails.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_ENDED:
                // Activate the force enable
                break;
            case Player.STATE_IDLE:

                break;
            case Player.STATE_READY:
                if(binding != null && binding.get() != null && binding.get().spinnerVideoDetails != null) {
                    binding.get().spinnerVideoDetails.setVisibility(View.GONE);
                }
                break;
            default:
                // status = PlaybackStatus.IDLE;
                break;
        }
    }

    @Override
    protected void initUIAndActions() {

        getIntentData();

        onCreateDialog();

        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);

        if (getActivity() != null) {
            psDialogMsg = new PSDialogMsg(this.getActivity(), false);

            drawable = new AutoClearedValue<>(this, getActivity().getResources().getDrawable(R.drawable.circular_shape));

            tags = new ArrayList<>();

            BottomSheetDialog mBottomSheet = new BottomSheetDialog(getActivity());
            bottomSheetDialog = new AutoClearedValue<>(this, mBottomSheet);

            BottomSheetLayoutBinding bottomSheetLiveWallpaperLayoutBindingObject = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.bottom_sheet_layout, null, false, dataBindingComponent);
            bottomSheetLiveWallpaperLayoutBinding = new AutoClearedValue<>(this, bottomSheetLiveWallpaperLayoutBindingObject);

            bottomSheetDialog.get().setContentView(bottomSheetLiveWallpaperLayoutBinding.get().getRoot());
        }

        initBottomSheetUIAndAction();

        binding.get().fab.setOnClickListener(v -> {

            tags.clear();

//            if (wallpaperViewModel.wallpapers != null) {
//                if (itemPosition >= wallpaperViewModel.wallpapers.size()) {
//                    itemPosition = itemPosition % wallpaperViewModel.wallpapers.size();
//                    replaceWallpaperDetailData(wallpaperViewModel.wallpapers.get(itemPosition));
//
//
//                }
//            }

            bottomSheetDialog.get().show();

        });

        binding.get().backImageView.setOnClickListener(v -> Objects.requireNonNull(getActivity()).finish());


    }

    @Override
    protected void initViewModels() {
        wallpaperViewModel = new ViewModelProvider(this, viewModelFactory).get(WallpaperViewModel.class);
        downloadCountViewModel = new ViewModelProvider(this, viewModelFactory).get(DownloadCountViewModel.class);
        touchCountViewModel = new ViewModelProvider(this, viewModelFactory).get(TouchCountViewModel.class);
        favouriteViewModel = new ViewModelProvider(this, viewModelFactory).get(FavouriteViewModel.class);

    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

        // Load Control
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();
        editor.apply();

        hideWidthAndSize();

        // Get Current Touch Count
        currentAdmobIntervalCount = preferences.getInt(Constants.CURRENT_ADMOB_AT_DOWNLOAD_INTERVAL_COUNT_KEY, Config.REWARD_INIT);

        showFullScreenAdsForSwipe();

        setTouchCount(wallpaperViewModel.videoId);

//        wallpaperViewModel.setGetWallpaperListFromDatabaseObj(wallpaperViewModel.wallpaperParamsHolder);
        wallpaperViewModel.setWallpaperByIdObj(wallpaperViewModel.videoId, loginUserId);
        wallpaperViewModel.getGetWallpaperListFromDatabaseData().observe(this, result -> {

            if (result != null) {
                if (result.size() > 0) {

                    if (isFirstOpen) {

                        wallpaperViewModel.wallpapers.clear();

                        wallpaperViewModel.wallpapers.addAll(result);

                        for (int i = 0; i < wallpaperViewModel.wallpapers.size(); i++) {
                            if (wallpaperViewModel.wallpapers.get(i).wallpaper_id.equals(wallpaperViewModel.videoId)) {

                                itemPosition = i;

                                break;
                            }
                        }

                        tags.clear();
                        replaceWallpaperDetailData(result.get(itemPosition));
                        wallpaperViewModel.type = result.get(itemPosition).types;

                        for (int i = result.get(itemPosition).wallpaper_search_tags.indexOf(','); i > -1; i = result.get(itemPosition).wallpaper_search_tags.indexOf(',', i + 1)) {

                            tags.add(result.get(itemPosition).wallpaper_search_tags.substring(start, i - 1));

                            start = i + 1;
                        }
//                        replaceTags(tags);


//                        binding.get().wallpaperViewPager.setAdapter(new WallpaperDetailFragment.TouchImageAdapter());
//                        binding.get().wallpaperViewPager.setCurrentItem(itemPosition);
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

        //get wallpaper data
        wallpaperViewModel.getWallpaperById().observe(this, result -> {

            if (result != null) {

                switch (result.status) {

                    case SUCCESS:
                        if (result.data != null) {

                            if (isFirstOpen) {

                                wallpaperViewModel.wallpapers.clear();

                                wallpaperViewModel.wallpapers.add(result.data);

                                for (int i = 0; i < wallpaperViewModel.wallpapers.size(); i++) {
                                    if (wallpaperViewModel.wallpapers.get(i).wallpaper_id.equals(wallpaperViewModel.videoId)) {

                                        itemPosition = i;

                                        break;
                                    }
                                }

                                tags.clear();
                                replaceWallpaperDetailData(result.data);
                                wallpaperViewModel.type = result.data.types;

                                for (int i = result.data.wallpaper_search_tags.indexOf(','); i > -1; i = result.data.wallpaper_search_tags.indexOf(',', i + 1)) {

                                    tags.add(result.data.wallpaper_search_tags.substring(start, i - 1));

                                    start = i + 1;
                                }

                                isFirstOpen = false;
                            } else {
                                for (int i = 0; i < wallpaperViewModel.wallpapers.size(); i++) {

                                    if (wallpaperViewModel.wallpapers.get(i).wallpaper_id.equals(result.data.wallpaper_id)) {
                                        Collections.replaceAll(wallpaperViewModel.wallpapers, wallpaperViewModel.wallpapers.get(i), result.data);
                                    }

                                }
                            }
                        }
                        wallpaperViewModel.setLoadingState(false);

                        break;

                    case LOADING:

                        if (result.data != null) {
                            replaceWallpaperDetailData(result.data);
                        }
                        break;

                }

            }
        });

        //get touch count post method
        touchCountViewModel.getTouchCountPostData().observe(this, result -> {
            if (result != null) {
                if (result.data != null) {
                    if (this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }

                } else {
                    if (this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }
                }
            }
        });

        //get download count post method
        downloadCountViewModel.getDownloadCountPostData().observe(this, result -> {
            if (result != null) {
                if (result.data != null) {
                    if (this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                        bottomSheetLiveWallpaperLayoutBinding.get().downloadCountNumberTextView.setText(Utils.numberFormat(result.data.download_count));
                    }

                } else {
                    if (this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }
                }
            }
        });

        //get favourite post method
        favouriteViewModel.getFavouritePostData().observe(this, result -> {
            if (result != null) {
                if (result.status == Status.SUCCESS) {
                    if (this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }

                } else if (result.status == Status.ERROR) {
                    if (this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }
                }
            }
        });

        wallpaperViewModel.getRatingData().observe(this, result -> {

            if (result != null) {
                if (result.data != null) {
                    if (this.getActivity() != null) {

                        wallpaperViewModel.wallpaperContainer = result.data;

                        bottomSheetLiveWallpaperLayoutBinding.get().ratingBar.setRating(result.data.rating_count);
                        bottomSheetLiveWallpaperLayoutBinding.get().ratingBarTextView.setText(String.valueOf(result.data.rating_count));

                        wallpaperViewModel.setLoadingState(false);
                    }

                } else {
                    if (this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }
                    wallpaperViewModel.setLoadingState(false);
                }
            }
        });


    }

    private void hideWidthAndSize() {
        bottomSheetLiveWallpaperLayoutBinding.get().sizeCardView.setVisibility(View.GONE);
        bottomSheetLiveWallpaperLayoutBinding.get().widthHeightColorCardView.setVisibility(View.GONE);
    }

    private void getIntentData(){
        try {
            if (getActivity() != null) {
                if (getActivity().getIntent().getExtras() != null) {

                    wallpaperViewModel.videoId = getActivity().getIntent().getExtras().getString(Constants.INTENT__LIVE_WALLPAPER_ID);
                    wallpaperViewModel.videoPath = getActivity().getIntent().getExtras().getString(Constants.INTENT__LIVE_WALLPAPER_PATH);
                    wallpaperViewModel.videoUri = Config.APP_IMAGES_URL + wallpaperViewModel.videoPath;
                    wallpaperViewModel.wallpaperParamsHolder = (WallpaperParamsHolder) getActivity().getIntent().getSerializableExtra(Constants.INTENT__WALLPAPER_PARAM_HOLDER);

                    //bind video
                    setUpVideo();
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("", e);
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }


    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    //touch count
    private void setTouchCount(String videoId) {
        if (connectivity.isConnected()) {
            touchCountViewModel.setTouchCountPostDataObj(videoId, loginUserId);
        }
    }

    private void onCreateDialog() {

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.message__downloading));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMax(100);

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

                    pointViewModel.setSendClaimedPointToServerObj(loginUserId, Config.REWARD_POINT_1);

                    mInterstitialAd.loadAd(adRequest);
                }

                public void onAdLoaded() {

                }
            });

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

    private void replaceWallpaperDetailData(Wallpaper wallpaper) {

        if (wallpaper != null) {
            bottomSheetLiveWallpaperLayoutBinding.get().setWallpaper(wallpaper);

            bottomSheetLiveWallpaperLayoutBinding.get().viewCountNumberTextView.setText(Utils.numberFormat(wallpaper.touch_count));
            bottomSheetLiveWallpaperLayoutBinding.get().downloadCountNumberTextView.setText(Utils.numberFormat(wallpaper.download_count));
            bottomSheetLiveWallpaperLayoutBinding.get().favouriteCountNumberTextView.setText(Utils.numberFormat(wallpaper.favourite_count));

            if (wallpaper.is_favourited.equals(Constants.ONE)) {
                bottomSheetLiveWallpaperLayoutBinding.get().heartButton.setLiked(true);
            } else if (wallpaper.is_favourited.equals(Constants.ZERO)) {
                bottomSheetLiveWallpaperLayoutBinding.get().heartButton.setLiked(false);
            }

//            if (wallpaper.is_landscape.equals(Constants.ONE)) {
//                bottomSheetLiveWallpaperLayoutBinding.get().orientationValueTextView.setText(getString(R.string.Landscape));
//            } else if (wallpaper.is_portrait.equals(Constants.ONE)) {
//                bottomSheetLiveWallpaperLayoutBinding.get().orientationValueTextView.setText(getString(R.string.Portrait));
//            } else if (wallpaper.is_square.equals(Constants.ONE)) {
//                bottomSheetLiveWallpaperLayoutBinding.get().orientationValueTextView.setText(getString(R.string.Square));
//            }

//            if (!wallpaper.color.code.isEmpty()) {
//                drawable.get().setColorFilter(Color.parseColor(wallpaper.color.code), PorterDuff.Mode.SRC_ATOP);
//
//                bottomSheetLiveWallpaperLayoutBinding.get().view11.setBackground(drawable.get());
//            }
//
//            bottomSheetLiveWallpaperLayoutBinding.get().colorEditText.setText(wallpaper.color.name);

            if (wallpaperViewModel.wallpaperContainer != null) {

                bottomSheetLiveWallpaperLayoutBinding.get().ratingBar.setRating(wallpaperViewModel.wallpaperContainer.rating_count);
                bottomSheetLiveWallpaperLayoutBinding.get().ratingBarTextView.setText(String.valueOf(wallpaperViewModel.wallpaperContainer.rating_count));
            }else {

                bottomSheetLiveWallpaperLayoutBinding.get().ratingBar.setRating(wallpaper.rating_count);
                bottomSheetLiveWallpaperLayoutBinding.get().ratingBarTextView.setText(String.valueOf(wallpaper.rating_count));
            }
        }

    }

    private void replaceTags(List<String> wallpapers) {

//        if (wallpapers.size() > 0) {
//            bottomSheetLiveWallpaperLayoutBinding.get().tagRecyclerView.setAdapter(this.tagAdapter.get());
//            this.tagAdapter.get().replace(wallpapers);
//            binding.get().executePendingBindings();
//        }
    }

    private void initBottomSheetUIAndAction() {

        bottomSheetLiveWallpaperLayoutBinding.get().setAsCardView.setOnClickListener(v -> {

            String imgPath = wallpaperViewModel.wallpapers.get(itemPosition).default_video.img_path;
            startDownloadForWallpaper(Config.APP_IMAGES_URL + imgPath, imgPath);
            bottomSheetDialog.get().cancel();

        });

        bottomSheetLiveWallpaperLayoutBinding.get().heartButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                favFunction(wallpaperViewModel.wallpapers.get(itemPosition), bottomSheetLiveWallpaperLayoutBinding.get().heartButton);
            }

            @Override
            public void unLiked(LikeButton likeButton) {

                unFavFunction(wallpaperViewModel.wallpapers.get(itemPosition), bottomSheetLiveWallpaperLayoutBinding.get().heartButton);
            }
        });

        bottomSheetLiveWallpaperLayoutBinding.get().ratingCardView.setOnClickListener(v -> {

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

        bottomSheetLiveWallpaperLayoutBinding.get().shareCardView.setOnClickListener(v -> {

            String imgPath = wallpaperViewModel.wallpapers.get(itemPosition).default_video.img_path;
            startDownloadForShare(Config.APP_IMAGES_URL + imgPath, imgPath);
            bottomSheetDialog.get().cancel();

            if (Config.SHOW_ADMOB_AT_DOWNLOAD) {
                showFullScreenAds();
            }

        });

        bottomSheetLiveWallpaperLayoutBinding.get().downloadCardView.setOnClickListener(v -> {

            if (connectivity.isConnected()) {

                String imgPath = wallpaperViewModel.wallpapers.get(itemPosition).default_video.img_path;
                startDownload(Config.APP_IMAGES_URL + imgPath, imgPath);
                bottomSheetDialog.get().cancel();
            }

        });
    }

    private void startDownload(String serverUrl, String fileName) {
        Utils.psLog("Download File : " + serverUrl);
        if (Utils.isStoragePermissionGranted(getActivity())) {
            new DownloadFileAsync().execute(serverUrl, fileName);
        }
    }

    private void startDownloadForWallpaper(String serverUrl, String fileName) {
        Utils.psLog("Download File : " + serverUrl);
        if (Utils.isStoragePermissionGranted(getActivity())) {
            new DownloadFileForWallpaperAsync().execute(serverUrl, fileName);
        }
    }

    private void startDownloadForShare(String serverUrl, String fileName) {
        Utils.psLog("Download File : " + serverUrl);
        if (Utils.isStoragePermissionGranted(getActivity())) {
            new DownloadFileForShareAsync().execute(serverUrl, fileName);
        }
    }

    private void showDialog() {
        mProgressDialog.setProgress(0);
        mProgressDialog.show();
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
                File dir = new File(path, "/0_Live_Wallpapers/");

                if (!dir.exists()) {
                    boolean b = dir.mkdir();
                    int i = 0;
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

    class DownloadFileForWallpaperAsync extends AsyncTask<String, String, String> {

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
                String fileNameAndExtension = "temp.mp4";
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept-Encoding", "identity");
                urlConnection.connect();

                InputStream input = new BufferedInputStream(url.openStream());
                File path = Environment.getExternalStorageDirectory();
                File dir = new File(path, "/0_Live_Wallpapers/");

                if (!dir.exists()) {
                    boolean b = dir.mkdir();
                    int i = 0;
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

                //set wallpaper
                if(getActivity() != null) {
                    Intent intent = new Intent(
                            WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                            new ComponentName(getActivity(), VideoWallpaperService.class));
                    startActivity(intent);
                }
                //endregion
            }

        }
    }

    class DownloadFileForShareAsync extends AsyncTask<String, String, String> {

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
                File dir = new File(path, "/0_Live_Wallpapers/");

                if (!dir.exists()) {
                    boolean b = dir.mkdir();
                    int i = 0;
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

                //share
                shareVideoUri();
                //endregion
            }

        }
    }

    //download count
    private void setDownloadCount(String videoId) {
        if (connectivity.isConnected()) {
            downloadCountViewModel.setDownloadCountPostDataObj(videoId, loginUserId);
        }
    }

    private void dismissDialog() {
        mProgressDialog.setProgress(100);
        mProgressDialog.cancel();
    }

    private void shareVideoUri() {

        new Thread(() -> {
            try {
                File path = Environment.getExternalStorageDirectory();
                File dir = new File(path, "/0_Live_Wallpapers/");
                String imgPath = wallpaperViewModel.wallpapers.get(itemPosition).default_video.img_path;
                File file = new File(dir, imgPath);

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/mp4");
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));
                startActivity(Intent.createChooser(share, "Share Video"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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


    private void unFavFunction(Wallpaper wallpaper, LikeButton likeButton) {

        Utils.navigateOnUserVerificationActivityFromFav(userIdToVerify, loginUserId, psDialogMsg, getActivity(), navigationController,likeButton, () -> {

            if (!favouriteViewModel.isLoading) {
                favouriteViewModel.setFavouritePostDataObj(wallpaper.wallpaper_id, loginUserId);
                likeButton.setLikeDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.heart_off, null));
            }

        });
    }

    private void favFunction(Wallpaper wallpaper, LikeButton likeButton) {

        Utils.navigateOnUserVerificationActivityFromFav(userIdToVerify, loginUserId, psDialogMsg, getActivity(), navigationController,likeButton, () -> {

            if (!favouriteViewModel.isLoading) {
                favouriteViewModel.setFavouritePostDataObj(wallpaper.wallpaper_id, loginUserId);
                likeButton.setLikeDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.heart_on, null));
            }

        });
    }

    public class VideoSurfaceHolder implements SurfaceHolder {

        private SurfaceHolder surfaceHolder;

        public VideoSurfaceHolder(SurfaceHolder holder) {
            surfaceHolder = holder;
        }

        @Override
        public void addCallback(Callback callback) {
            surfaceHolder.addCallback(callback);
        }

        @Override
        public Surface getSurface() {
            return surfaceHolder.getSurface();
        }

        @Override
        public Rect getSurfaceFrame() {
            return surfaceHolder.getSurfaceFrame();
        }

        @Override
        public boolean isCreating() {
            return surfaceHolder.isCreating();
        }

        @Override
        public Canvas lockCanvas() {
            return surfaceHolder.lockCanvas();
        }

        @Override
        public Canvas lockCanvas(Rect dirty) {
            return surfaceHolder.lockCanvas(dirty);
        }

        @Override
        public void removeCallback(Callback callback) {
            surfaceHolder.removeCallback(callback);
        }

        @Override
        public void setFixedSize(int width, int height) {
//            surfaceHolder.getSurface().setSize(width, height);
            surfaceHolder.setFixedSize(width, height);
            surfaceHolder.setSizeFromLayout();
        }

        @Override
        public void setFormat(int format) {
            surfaceHolder.setFormat(format);
        }

        @Override
        public void setSizeFromLayout() {
            surfaceHolder.setSizeFromLayout();
        }

        @Override
        public void setType(int type) {
            surfaceHolder.setType(SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void setKeepScreenOn(boolean bool) {
            //do nothing
        }

        @Override
        public void unlockCanvasAndPost(Canvas canvas) {
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLoginUserId();

        wallpaperViewModel.setWallpaperByIdObj(wallpaperViewModel.videoId, loginUserId);
        setUpVideo();
    }
}
