package com.panaceasoft.pswallpaper.ui.wallpaper.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.like.LikeButton;
import com.panaceasoft.pswallpaper.Config;
import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.binding.FragmentDataBindingComponent;
import com.panaceasoft.pswallpaper.databinding.FragmentWallpaperListBinding;
import com.panaceasoft.pswallpaper.ui.common.PSFragment;
import com.panaceasoft.pswallpaper.ui.wallpaper.listwithfilter.adapter.WallpaperListAdapter;
import com.panaceasoft.pswallpaper.utils.AutoClearedValue;
import com.panaceasoft.pswallpaper.utils.Constants;
import com.panaceasoft.pswallpaper.utils.PSDialogMsg;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewmodel.favourite.FavouriteViewModel;
import com.panaceasoft.pswallpaper.viewmodel.wallpaper.WallpaperViewModel;
import com.panaceasoft.pswallpaper.viewobject.Wallpaper;
import com.panaceasoft.pswallpaper.viewobject.common.Status;
import com.panaceasoft.pswallpaper.viewobject.holder.WallpaperParamsHolder;

import java.util.List;


public class WallpaperListFragment extends PSFragment {

    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private FavouriteViewModel favouriteViewModel;
    private WallpaperViewModel wallpaperViewModel;
    public int count;
    private String premiumOrNot;
    private PSDialogMsg psDialogMsg;

    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private int[] firstVisibleItems = null;

    @VisibleForTesting
    private AutoClearedValue<FragmentWallpaperListBinding> binding;
    private AutoClearedValue<WallpaperListAdapter> latestAdapter1;

    //endregion

    //region Override Methods

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentWallpaperListBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallpaper_list, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);
        premiumOrNot = getArguments() != null ? getArguments().getString(Constants.PREMIUM) : "";

        if (Config.SHOW_ADMOB && connectivity.isConnected()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            binding.get().adView.loadAd(adRequest);
        } else {
            binding.get().adView.setVisibility(View.GONE);
        }
        binding.get().setLoadingMore(connectivity.isConnected());

        return binding.get().getRoot();

    }


    @Override
    protected void initUIAndActions() {

        psDialogMsg = new PSDialogMsg(getActivity(), false);

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
                }else{
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

        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            wallpaperViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset reservationViewModel.offset
            wallpaperViewModel.offset = 0;
            wallpaperViewModel.limit = Config.ALL_WALLPAPERS_COUNT;

            // reset reservationViewModel.forceEndLoading
            wallpaperViewModel.forceEndLoading = false;

            // update live data

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

    }

    @Override
    protected void initAdapters() {

//        if(APP_GRID.equals(Config.GRID1)) {
        WallpaperListAdapter latestWallpaperAdapter = new WallpaperListAdapter(dataBindingComponent, new WallpaperListAdapter.AllWallpapersClickCallback() {
            @Override
            public void onClick(Wallpaper wallpaper) {

                navigationController.navigateToWallpaperDetail(WallpaperListFragment.this.getActivity(), wallpaper, wallpaperViewModel.wallpaperParamsHolder);
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
        latestWallpaperAdapter.setPremiumOrNot(premiumOrNot);

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

        getDataFromFragment();

        resetLimitAndOffset();

        loadNextPageWallpaperList(String.valueOf(wallpaperViewModel.offset));
        loadWallpaperList(String.valueOf(wallpaperViewModel.limit));

        //get favourite post method
        favouriteViewModel.getFavouritePostData().observe(this, result -> {
            if (result != null) {
                if (result.status == Status.SUCCESS) {
                    if (WallpaperListFragment.this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }

                } else if (result.status == Status.ERROR) {
                    if (WallpaperListFragment.this.getActivity() != null) {
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

        wallpaperViewModel.getLoadingState().observe(this, loadingState -> {

            binding.get().setLoadingMore(wallpaperViewModel.isLoading);

            if (loadingState != null && !loadingState) {
                binding.get().swipeRefresh.setRefreshing(false);
            }

        });

    }

    private void getDataFromFragment() {
        try {
            Bundle bundle = getArguments();
            if (bundle != null) {
                wallpaperViewModel.wallpaperParamsHolder = (WallpaperParamsHolder) bundle.getSerializable(Constants.INTENT__WALLPAPER_PARAM_HOLDER);
            }
        } catch (Exception e) {
            Utils.psErrorLog("", e);
        }
    }

    private void loadWallpaperList(String limit) {
        wallpaperViewModel.setGetWallpaperListObj(wallpaperViewModel.wallpaperParamsHolder, limit, String.valueOf(Config.ZERO_COUNT), loginUserId);
    }

    private void loadNextPageWallpaperList(String offset) {
        wallpaperViewModel.setGetNextPageWallpaperList(loginUserId, wallpaperViewModel.wallpaperParamsHolder, String.valueOf(Config.ALL_WALLPAPERS_COUNT), offset);
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

    }

    private void resetLimitAndOffset() {
        wallpaperViewModel.offset = 0;
        wallpaperViewModel.limit = Config.ALL_WALLPAPERS_COUNT;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.REQUEST_TO_SEARCH && resultCode == Utils.RESULT_FROM_SEARCH) {
            if (data != null && data.getExtras() != null) {
                WallpaperParamsHolder wallpaperParamsHolderNew = wallpaperViewModel.wallpaperParamsHolder;

                wallpaperViewModel.wallpaperParamsHolder = (WallpaperParamsHolder) (data.getExtras()).getSerializable(Constants.INTENT__WALLPAPER_PARAM_HOLDER);

                if (wallpaperViewModel.wallpaperParamsHolder != null) {
                    wallpaperViewModel.wallpaperParamsHolder.orderBy = wallpaperParamsHolderNew.orderBy;
                }
                if (wallpaperViewModel.wallpaperParamsHolder != null) {
                    wallpaperViewModel.wallpaperParamsHolder.orderType = wallpaperParamsHolderNew.orderType;
                }

                resetLimitAndOffset();

                loadWallpaperList(String.valueOf(wallpaperViewModel.limit));

            }
        }
    }
}
