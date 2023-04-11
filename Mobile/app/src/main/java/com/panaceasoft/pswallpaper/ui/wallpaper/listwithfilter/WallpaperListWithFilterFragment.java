package com.panaceasoft.pswallpaper.ui.wallpaper.listwithfilter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.like.LikeButton;
import com.panaceasoft.pswallpaper.Config;
import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.binding.FragmentDataBindingComponent;
import com.panaceasoft.pswallpaper.databinding.BottomBoxLayoutBinding;
import com.panaceasoft.pswallpaper.databinding.FragmentWallpaperListWithFilterBinding;
import com.panaceasoft.pswallpaper.ui.common.PSFragment;
import com.panaceasoft.pswallpaper.ui.wallpaper.listwithfilter.adapter.WallpaperListAdapter;
import com.panaceasoft.pswallpaper.utils.AutoClearedValue;
import com.panaceasoft.pswallpaper.utils.Constants;
import com.panaceasoft.pswallpaper.utils.PSDialogMsg;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewmodel.favourite.FavouriteViewModel;
import com.panaceasoft.pswallpaper.viewmodel.user.UserViewModel;
import com.panaceasoft.pswallpaper.viewmodel.wallpaper.WallpaperViewModel;
import com.panaceasoft.pswallpaper.viewobject.Wallpaper;
import com.panaceasoft.pswallpaper.viewobject.common.Status;
import com.panaceasoft.pswallpaper.viewobject.holder.WallpaperParamsHolder;

import java.util.ArrayList;
import java.util.List;

public class WallpaperListWithFilterFragment extends PSFragment {

    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private FavouriteViewModel favouriteViewModel;
    private WallpaperViewModel wallpaperViewModel;
    public int count;
    //private String premiumOrNot;
    private MenuItem pointMenuItem;
    private UserViewModel userViewModel;
    private PSDialogMsg psDialogMsg;

    private List<Wallpaper> clearRecyclerView = new ArrayList<>();

    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private int[] firstVisibleItems = null;

    @VisibleForTesting
    private AutoClearedValue<FragmentWallpaperListWithFilterBinding> binding;
    private AutoClearedValue<WallpaperListAdapter> latestAdapter1;
    private AutoClearedValue<BottomSheetDialog> mBottomSheetDialog;
    private AutoClearedValue<BottomBoxLayoutBinding> bottomBoxLayoutBinding;

    //endregion

    //region Override Methods

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentWallpaperListWithFilterBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallpaper_list_with_filter, container, false, dataBindingComponent);

        setHasOptionsMenu(true);

        binding = new AutoClearedValue<>(this, dataBinding);

        if (Config.SHOW_ADMOB && connectivity.isConnected()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            binding.get().adView.loadAd(adRequest);
        } else {
            binding.get().adView.setVisibility(View.GONE);
        }
        binding.get().setLoadingMore(connectivity.isConnected());

        // = getArguments() != null ? getArguments().getString(Config.PREMIUM) : null;
        return binding.get().getRoot();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.pointItem) {
            // Open Claim Activity

            navigationController.navigateToClaimPointActivity(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initUIAndActions() {

        psDialogMsg = new PSDialogMsg(getActivity(),false);

        if (getContext() != null) {

            BottomSheetDialog mBottomSheetDialog2 = new BottomSheetDialog(getContext());
            mBottomSheetDialog = new AutoClearedValue<>(this, mBottomSheetDialog2);

            BottomBoxLayoutBinding bottomBoxLayoutBinding2 = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.bottom_box_layout, null, false);
            bottomBoxLayoutBinding = new AutoClearedValue<>(this, bottomBoxLayoutBinding2);
            mBottomSheetDialog.get().setContentView(bottomBoxLayoutBinding.get().getRoot());

            bottomBoxLayoutBinding.get().recentButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baesline_access_time_black_24), null, getResources().getDrawable(R.drawable.baseline_check_green_24), null);

        }

        bottomBoxLayoutBinding.get().sortButton.setText(R.string.dialog__title_m_mun);
        bottomBoxLayoutBinding.get().trendingButton.setText(R.string.popular__m_mun);
        bottomBoxLayoutBinding.get().recentButton.setText(R.string.recent__m_mun);
        bottomBoxLayoutBinding.get().mostDownloadButton.setText(R.string.most_download__m_mun);
        bottomBoxLayoutBinding.get().lowestButton.setText(R.string.low_price__m_mun);
        bottomBoxLayoutBinding.get().highestButton.setText(R.string.high_price__m_mun);

        refreshButtonSheetIcon(LATEST_POSITION);

        binding.get().scrollFloatingButton.setOnClickListener(v -> binding.get().latestWallpaperView.smoothScrollToPosition(0));

        if(Config.APP_GRID_LAYOUT == Constants.LAYOUT_TYPE.GRID_LAYOUT) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), Config.MIN_COLUMN_COUNT, RecyclerView.VERTICAL, false);
            binding.get().latestWallpaperView.setLayoutManager(gridLayoutManager);
        }else {
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(Config.MIN_COLUMN_COUNT, RecyclerView.VERTICAL);
            binding.get().latestWallpaperView.setLayoutManager(staggeredGridLayoutManager);
        }
        binding.get().latestWallpaperView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager mLayoutManager = (StaggeredGridLayoutManager)
                            recyclerView.getLayoutManager();

                    if (mLayoutManager != null) {

                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();

                        firstVisibleItems = mLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);

                        if (firstVisibleItems != null && firstVisibleItems.length > 0) {
                            pastVisibleItems = firstVisibleItems[0];
                        }


                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            //                if (firstVisibleItems[firstVisibleItems.length - 1] == count - 1) {
                            Utils.psLog("Status : " + binding.get().getLoadingMore());
                            if (!binding.get().getLoadingMore() && !wallpaperViewModel.forceEndLoading) {

                                wallpaperViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                                int limit = Config.ALL_WALLPAPERS_COUNT;
                                wallpaperViewModel.offset = wallpaperViewModel.offset + limit;

                                wallpaperViewModel.limit = wallpaperViewModel.offset + limit;

                                loadNextPageWallpaperList(String.valueOf(wallpaperViewModel.offset));
                            }
                        }
                    }

                }else {
                    GridLayoutManager layoutManager = (GridLayoutManager)
                            recyclerView.getLayoutManager();

                    if (layoutManager != null) {

                        int lastPosition = layoutManager
                                .findLastVisibleItemPosition();

                        if (lastPosition == latestAdapter1.get().getItemCount() - 1) {

                            if (!binding.get().getLoadingMore() && !wallpaperViewModel.forceEndLoading) {

                                wallpaperViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                                int limit = Config.ALL_WALLPAPERS_COUNT;

                                wallpaperViewModel.offset = wallpaperViewModel.offset + limit;

                                loadNextPageWallpaperList(String.valueOf(wallpaperViewModel.offset));

                            }
                        }

                    }
                }
            }
        });

        binding.get().sortButton.setOnClickListener(v -> {
            mBottomSheetDialog.get().show();
            ButtonSheetClick();
        });

        binding.get().tuneButton.setOnClickListener(v -> {
            if (wallpaperViewModel.wallpaperParamsHolder != null) {

                navigationController.navigateToSearchActivity(getActivity(), wallpaperViewModel.wallpaperParamsHolder);
            }
        });

        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            wallpaperViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset reservationViewModel.offset
            resetLimitAndOffset();

            // reset reservationViewModel.forceEndLoading`
            wallpaperViewModel.forceEndLoading = false;

            // update live data
            wallpaperViewModel.limit = Config.ALL_WALLPAPERS_COUNT;

            loadWallpaperList(String.valueOf(wallpaperViewModel.limit));
        });


        wallpaperViewModel.getLoadingState().observe(this, loadingState -> {

            binding.get().setLoadingMore(wallpaperViewModel.isLoading);

            if (loadingState != null && !loadingState) {
                binding.get().swipeRefresh.setRefreshing(false);
            }

        });

    }


    @Override
    protected void initViewModels() {
        favouriteViewModel = new ViewModelProvider(this, viewModelFactory).get(FavouriteViewModel.class);
        wallpaperViewModel = new ViewModelProvider(this, viewModelFactory).get(WallpaperViewModel.class);
        userViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);

    }

    @Override
    protected void initAdapters() {

//        if(APP_GRID.equals(Config.GRID1)) {
        WallpaperListAdapter latestWallpaperAdapter = new WallpaperListAdapter(dataBindingComponent, new WallpaperListAdapter.AllWallpapersClickCallback() {
            @Override
            public void onClick(Wallpaper wallpaper) {
                if (wallpaper.is_video_wallpaper.equals(Constants.ONE)) {
                    navigationController.navigateToLiveWallpaperDetail(getActivity(), wallpaper, wallpaperViewModel.wallpaperParamsHolder);
                } else {//wallpaper and gif
                    navigationController.navigateToWallpaperDetail(WallpaperListWithFilterFragment.this.getActivity(), wallpaper, wallpaperViewModel.wallpaperParamsHolder);
                }
            }

            @Override
            public void onFavLikeClick(Wallpaper wallpaper, LikeButton likeButton) {
                favFunction(wallpaper, likeButton);
            }

            @Override
            public void onFavUnlikeClick(Wallpaper wallpaper, LikeButton likeButton) {
                unFavFunction(wallpaper, likeButton);
            }
        });

        this.latestAdapter1 = new AutoClearedValue<>(this, latestWallpaperAdapter);
        binding.get().latestWallpaperView.setAdapter(latestWallpaperAdapter);

        showHideWhenScroll();
    }

    private void showHideWhenScroll() {
        binding.get().latestWallpaperView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) binding.get().scrollFloatingButton.show();
                else binding.get().scrollFloatingButton.hide();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
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

    @Override
    protected void initData() {
        // get local user from database
        userViewModel.getLocalUser(loginUserId).observe(this, localUserData -> {

            if (localUserData != null) {

                if (pointMenuItem != null && getContext() != null) {
                    pointMenuItem.setTitle(getContext().getString(R.string.dashboard__pts, localUserData.total_point));
                }

            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

            }
        });

        try {
            if (getActivity() != null) {
                if (getActivity().getIntent().getExtras() != null) {
                    wallpaperViewModel.wallpaperParamsHolder = (WallpaperParamsHolder) (getActivity()).getIntent().getExtras().getSerializable(Constants.INTENT__WALLPAPER_PARAM_HOLDER);

                    Utils.psLog("TEst" + wallpaperViewModel.wallpaperParamsHolder.isSquare);
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("", e);
        }

        resetLimitAndOffset();

        loadWallpaperList(String.valueOf(wallpaperViewModel.limit));


        //get favourite post method
        favouriteViewModel.getFavouritePostData().observe(this, result -> {
            if (result != null) {
                if (result.status == Status.SUCCESS) {
                    if (WallpaperListWithFilterFragment.this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }

                } else if (result.status == Status.ERROR) {
                    if (WallpaperListWithFilterFragment.this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }
                }
            }
        });

        wallpaperViewModel.getWallpaperListLiveData().observe(this, result -> {

            if (result.data != null) {
                if (result.data.size() > 0) {
                    binding.get().noItemConstraintLayout.setVisibility(View.INVISIBLE);
                    wallpaperViewModel.setLoadingState(false);
                    replaceData(result.data);
                } else {
                    result.data.size();
                    binding.get().noItemConstraintLayout.setVisibility(View.VISIBLE);
                    replaceData(result.data);
                }
            }
        });

        wallpaperViewModel.getNextPageWallpaperListLiveData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {

                    case ERROR:

                        wallpaperViewModel.setLoadingState(false);
                        wallpaperViewModel.forceEndLoading = true;

                        break;
                }
            }
        });

        hasFilterData();

    }

    private void hasFilterData() {

        if ((!wallpaperViewModel.wallpaperParamsHolder.catId.equals(""))
                || (!wallpaperViewModel.wallpaperParamsHolder.wallpaperName.equals(""))
                || (!wallpaperViewModel.wallpaperParamsHolder.keyword.equals(""))
                || (!wallpaperViewModel.wallpaperParamsHolder.catName.equals(""))
                || (!wallpaperViewModel.wallpaperParamsHolder.type.equals(""))
                || (!wallpaperViewModel.wallpaperParamsHolder.isRecommended.equals(""))
                || (!wallpaperViewModel.wallpaperParamsHolder.isPortrait.equals(""))
                || (!wallpaperViewModel.wallpaperParamsHolder.isLandscape.equals(""))
                || (!wallpaperViewModel.wallpaperParamsHolder.isSquare.equals(""))
                || (!wallpaperViewModel.wallpaperParamsHolder.colorId.equals(""))
                || (!wallpaperViewModel.wallpaperParamsHolder.colorName.equals(""))
                || (!wallpaperViewModel.wallpaperParamsHolder.rating_max.equals(""))
                || (!wallpaperViewModel.wallpaperParamsHolder.rating_min.equals(""))
                || (!wallpaperViewModel.wallpaperParamsHolder.point_max.equals(""))
                || (!wallpaperViewModel.wallpaperParamsHolder.point_min.equals(""))
                || (!wallpaperViewModel.wallpaperParamsHolder.isGif.equals(""))
        ) {
            binding.get().tuneButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baseline_tune_with_check_orange_24), null, null);
        }

    }

    private void replaceData(List<Wallpaper> wallpapers) {

        latestAdapter1.get().replace(wallpapers);
        binding.get().executePendingBindings();

    }


    @Override
    public void onResume() {
        super.onResume();
        Utils.psLog("On Resume");

        loadLoginUserId();

        if (userViewModel != null) {
            userViewModel.setLocalUser(loginUserId);
        }

    }

    private void ButtonSheetClick() {

        bottomBoxLayoutBinding.get().trendingButton.setOnClickListener(view -> {

            wallpaperViewModel.loadingDirection = Utils.LoadingDirection.top;

            binding.get().sortButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baseline_sort_with_check_orange_24), null, null);

            refreshButtonSheetIcon(TRENDING_POSITION);

            wallpaperViewModel.wallpaperParamsHolder = wallpaperViewModel.wallpaperParamsHolder.getTrendingHolderForSorting();

            replaceData(clearRecyclerView);

            loadWallpaperList(String.valueOf(wallpaperViewModel.limit));

            mBottomSheetDialog.get().dismiss();

        });

        bottomBoxLayoutBinding.get().recentButton.setOnClickListener(view -> {
            wallpaperViewModel.loadingDirection = Utils.LoadingDirection.top;
            binding.get().sortButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baseline_sort_with_check_orange_24), null, null);

            refreshButtonSheetIcon(LATEST_POSITION);
            wallpaperViewModel.wallpaperParamsHolder = wallpaperViewModel.wallpaperParamsHolder.getLatestHolderForSorting();

            replaceData(clearRecyclerView);

            resetLimitAndOffset();
            loadWallpaperList(String.valueOf(wallpaperViewModel.limit));

            mBottomSheetDialog.get().dismiss();

        });


        bottomBoxLayoutBinding.get().lowestButton.setOnClickListener(view -> {
            wallpaperViewModel.loadingDirection = Utils.LoadingDirection.top;
            binding.get().sortButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baseline_sort_with_check_orange_24), null, null);

            refreshButtonSheetIcon(LOWEST_POSITION);

            wallpaperViewModel.wallpaperParamsHolder = wallpaperViewModel.wallpaperParamsHolder.getLowestRatingHolderForSorting();
            replaceData(clearRecyclerView);

            resetLimitAndOffset();
            loadWallpaperList(String.valueOf(wallpaperViewModel.limit));

            mBottomSheetDialog.get().dismiss();

        });

        bottomBoxLayoutBinding.get().highestButton.setOnClickListener(view -> {

            wallpaperViewModel.loadingDirection = Utils.LoadingDirection.top;
            binding.get().sortButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baseline_sort_with_check_orange_24), null, null);

            refreshButtonSheetIcon(HIGHEST_POSITION);
            wallpaperViewModel.wallpaperParamsHolder = wallpaperViewModel.wallpaperParamsHolder.getHighestRatingHolderForSorting();

            replaceData(clearRecyclerView);

            resetLimitAndOffset();
            loadWallpaperList(String.valueOf(wallpaperViewModel.limit));

            mBottomSheetDialog.get().dismiss();

        });

        bottomBoxLayoutBinding.get().mostDownloadButton.setOnClickListener(v -> {
            wallpaperViewModel.loadingDirection = Utils.LoadingDirection.top;
            binding.get().sortButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baseline_sort_with_check_orange_24), null, null);

            refreshButtonSheetIcon(MOST_DOWNLOAD_POSITION);

            wallpaperViewModel.wallpaperParamsHolder = wallpaperViewModel.wallpaperParamsHolder.getDownloadHolderForSorting();

            replaceData(clearRecyclerView);

            resetLimitAndOffset();
            loadWallpaperList(String.valueOf(wallpaperViewModel.limit));

            mBottomSheetDialog.get().dismiss();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.REQUEST_TO_SEARCH && resultCode == Utils.RESULT_FROM_SEARCH) {
            if (data != null) {
                WallpaperParamsHolder wallpaperParamsHolderNew = wallpaperViewModel.wallpaperParamsHolder;

                if (data.getExtras() != null) {

                    wallpaperViewModel.wallpaperParamsHolder = (WallpaperParamsHolder) (data.getExtras()).getSerializable(Constants.INTENT__WALLPAPER_PARAM_HOLDER);

                    if (wallpaperViewModel.wallpaperParamsHolder != null) {
                        wallpaperViewModel.wallpaperParamsHolder.orderBy = wallpaperParamsHolderNew.orderBy;
                    }
                    if (wallpaperViewModel.wallpaperParamsHolder != null) {
                        wallpaperViewModel.wallpaperParamsHolder.orderType = wallpaperParamsHolderNew.orderType;
                    }

                    binding.get().tuneButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baseline_tune_with_check_orange_24), null, null);

                    resetLimitAndOffset();
                    loadWallpaperList(String.valueOf(wallpaperViewModel.limit));
                }

            } else {

                binding.get().tuneButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baseline_tune_orange_24), null, null);
            }
        }
    }

    private int LATEST_POSITION = 0;
    private int TRENDING_POSITION = 1;
    private int MOST_DOWNLOAD_POSITION = 2;
    private int LOWEST_POSITION = 3;
    private int HIGHEST_POSITION = 4;

    private void refreshButtonSheetIcon(int position) {
        if (position == LATEST_POSITION) {
            bottomBoxLayoutBinding.get().recentButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baesline_access_time_black_24), null, getResources().getDrawable(R.drawable.baseline_check_green_24), null);
        } else {
            bottomBoxLayoutBinding.get().recentButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baesline_access_time_black_24), null, null, null);
        }

        if (position == TRENDING_POSITION) {
            bottomBoxLayoutBinding.get().trendingButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_trending_black_24), null, getResources().getDrawable(R.drawable.baseline_check_green_24), null);
        } else {
            bottomBoxLayoutBinding.get().trendingButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_trending_black_24), null, null, null);
        }

        if (position == MOST_DOWNLOAD_POSITION) {
            bottomBoxLayoutBinding.get().mostDownloadButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_most_download_grey_24), null, getResources().getDrawable(R.drawable.baseline_check_green_24), null);
        } else {
            bottomBoxLayoutBinding.get().mostDownloadButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_most_download_grey_24), null, null, null);
        }

        if (position == LOWEST_POSITION) {
            bottomBoxLayoutBinding.get().lowestButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_lowest_rating_black_24), null, getResources().getDrawable(R.drawable.baseline_check_green_24), null);
        } else {
            bottomBoxLayoutBinding.get().lowestButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_lowest_rating_black_24), null, null, null);
        }

        if (position == HIGHEST_POSITION) {
            bottomBoxLayoutBinding.get().highestButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_highest_rating_black_24), null, getResources().getDrawable(R.drawable.baseline_check_green_24), null);
        } else {
            bottomBoxLayoutBinding.get().highestButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_highest_rating_black_24), null, null, null);
        }
    }

    private void loadWallpaperList(String limit) {
        wallpaperViewModel.setGetWallpaperListObj(wallpaperViewModel.wallpaperParamsHolder, limit, String.valueOf(Config.ZERO_COUNT), loginUserId);
    }

    private void loadNextPageWallpaperList(String offset) {
        wallpaperViewModel.setGetNextPageWallpaperList(loginUserId, wallpaperViewModel.wallpaperParamsHolder, String.valueOf(Config.ALL_WALLPAPERS_COUNT), offset);
    }

    private void resetLimitAndOffset() {
        wallpaperViewModel.limit = Config.ALL_WALLPAPERS_COUNT;
        wallpaperViewModel.offset = 0;
    }
}
