package com.panaceasoft.pswallpaper.ui.upload.list;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.panaceasoft.pswallpaper.Config;
import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.binding.FragmentDataBindingComponent;
import com.panaceasoft.pswallpaper.databinding.FragmentUploadedWallpaperListBinding;
import com.panaceasoft.pswallpaper.ui.common.DataBoundListAdapter;
import com.panaceasoft.pswallpaper.ui.common.PSFragment;
import com.panaceasoft.pswallpaper.ui.upload.list.adapter.UploadPhotoAdapter;
import com.panaceasoft.pswallpaper.utils.AutoClearedValue;
import com.panaceasoft.pswallpaper.utils.Constants;
import com.panaceasoft.pswallpaper.utils.PSDialogMsg;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewmodel.user.UserViewModel;
import com.panaceasoft.pswallpaper.viewmodel.wallpaper.WallpaperViewModel;
import com.panaceasoft.pswallpaper.viewobject.Wallpaper;
import com.panaceasoft.pswallpaper.viewobject.common.Status;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadedWallpaperListFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {
    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private WallpaperViewModel wallpaperViewModel;

    private AutoClearedValue<FragmentUploadedWallpaperListBinding> binding;
    private AutoClearedValue<UploadPhotoAdapter> adapter;
    private MenuItem pointMenuItem;
    private UserViewModel userViewModel;
    private PSDialogMsg psDialogMsg;
    //endregion

    //region Override Methods
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentUploadedWallpaperListBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_uploaded_wallpaper_list, container, false, dataBindingComponent);

        setHasOptionsMenu(true);
        binding = new AutoClearedValue<>(this, dataBinding);

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
    protected void initUIAndActions() {

        if (Config.ENABLE_UPLOAD_WALLPAPER) {
            binding.get().scrollFloatingButton.setVisibility(View.VISIBLE);

        } else if (!Config.ENABLE_UPLOAD_WALLPAPER) {
            binding.get().scrollFloatingButton.setVisibility(View.GONE);

            showHideWhenScroll();
        }

        psDialogMsg = new PSDialogMsg(getActivity(), false);

        binding.get().scrollFloatingButton.setOnClickListener(v -> {
            Utils.navigateOnUserVerificationActivity(userIdToVerify, loginUserId, psDialogMsg, getActivity(), navigationController, () -> {
                navigationController.navigateToImageUploadActivity(getActivity());
            });
        });

        binding.get().recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager mLayoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();

                if (mLayoutManager != null) {
                    int lastPosition = mLayoutManager
                            .findLastVisibleItemPosition();
                    if (lastPosition == adapter.get().getItemCount() - 1) {

                        if (!binding.get().getLoadingMore() && !wallpaperViewModel.forceEndLoading) {

                            if (connectivity.isConnected()) {

                                wallpaperViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                                int limit = Config.UPLOAD_PHOTO_COUNT;
                                wallpaperViewModel.offset = wallpaperViewModel.offset + limit;

                                wallpaperViewModel.setNextPageUploadedLoadingStateObj(loginUserId, String.valueOf(Config.UPLOAD_PHOTO_COUNT), String.valueOf(wallpaperViewModel.offset),Constants.WALLPAPER);
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

            // reset reservationViewModel.forceEndLoading
            wallpaperViewModel.forceEndLoading = false;

            // update live data
            wallpaperViewModel.setAllUploadedWallpaperObj(loginUserId, String.valueOf(Config.UPLOAD_PHOTO_COUNT), String.valueOf(wallpaperViewModel.offset));

        });
    }

    @Override
    protected void initViewModels() {
        wallpaperViewModel = new ViewModelProvider(this, viewModelFactory).get(WallpaperViewModel.class);
        userViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);
    }

    @Override
    protected void initAdapters() {

        UploadPhotoAdapter latestWallpaperAdapter = new UploadPhotoAdapter(dataBindingComponent, new UploadPhotoAdapter.AllUploadPhotoClickCallback() {
            @Override
            public void onClick(Wallpaper wallpaper) {
                navigationController.navigateToImageUploadActivityForEdit(getActivity(), wallpaper.wallpaper_id);
            }

            @Override
            public void onDeleteClick(Wallpaper wallpaper) {
                psDialogMsg.showConfirmDialog(getString(R.string.upload_photo__delete), getString(R.string.app__ok), getString(R.string.message__cancel_close));
                psDialogMsg.show();

                psDialogMsg.okButton.setOnClickListener(v -> {
                    wallpaperViewModel.loadingDirection = Utils.LoadingDirection.none;
                    wallpaperViewModel.setDeleteUserWallpaperByIdObj(wallpaper.wallpaper_id, loginUserId);
                    psDialogMsg.cancel();
                });

                psDialogMsg.cancelButton.setOnClickListener(v -> psDialogMsg.cancel());
            }
        }, this);

        this.adapter = new AutoClearedValue<>(this, latestWallpaperAdapter);
        binding.get().recyclerView.setAdapter(latestWallpaperAdapter);

        showHideWhenScroll();
    }

    private void showHideWhenScroll() {
        binding.get().recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy >= 0) binding.get().scrollFloatingButton.show();
                else binding.get().scrollFloatingButton.hide();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (wallpaperViewModel.getAllUploadedWallpaperData().getValue() != null) {
            if (wallpaperViewModel.getAllUploadedWallpaperData().getValue().data != null) {
                if (wallpaperViewModel.getAllUploadedWallpaperData().getValue().data.size() <= 1) {
                    wallpaperViewModel.setAllUploadedWallpaperObj(loginUserId, String.valueOf(Config.UPLOAD_PHOTO_COUNT), Constants.ZERO);
                }
            }
        }
    }

    @Override
    protected void initData() {

        ////Delete Wallpaper

        wallpaperViewModel.getDeleteUserWallpaperByIdData().observe(this, result -> {

            if (result != null) {
                if (result.data != null) {
                    switch (result.status) {

                        case ERROR:
                            Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                            break;

                        case SUCCESS:
                            Toast.makeText(getActivity(), getString(R.string.upload_photo__delete_status), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });

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

        //wallpaperViewModel.loadingDirection = Utils.LoadingDirection.top;
        wallpaperViewModel.setAllUploadedWallpaperObj(loginUserId, String.valueOf(Config.UPLOAD_PHOTO_COUNT), String.valueOf(wallpaperViewModel.offset));
        wallpaperViewModel.getAllUploadedWallpaperData().observe(this, resource -> {

            if (resource != null) {

                Utils.psLog("Got Data" + resource.message + resource.toString());

                switch (resource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        if (resource.data != null) {

                            //fadeIn(binding.get().getRoot());

                            if (resource.data.size() == 0) {

                                if (!binding.get().getLoadingMore()) {
                                    binding.get().noItemConstraintLayout.setVisibility(View.VISIBLE);
                                }

                            } else {
                                binding.get().noItemConstraintLayout.setVisibility(View.INVISIBLE);

                            }

                            replaceGrid1Data(resource.data);

                        }

                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (resource.data != null) {

                            if (resource.data.size() == 0) {

                                if (!binding.get().getLoadingMore()) {
                                    binding.get().noItemConstraintLayout.setVisibility(View.VISIBLE);
                                }

                            } else {
                                binding.get().noItemConstraintLayout.setVisibility(View.INVISIBLE);

                            }

                            replaceGrid1Data(resource.data);

                        }

                        wallpaperViewModel.setLoadingState(false);
                        break;
                    case ERROR:
                        // Error State
                        wallpaperViewModel.setLoadingState(false);

                        if (wallpaperViewModel.getAllUploadedWallpaperData() != null) {
                            if (wallpaperViewModel.getAllUploadedWallpaperData().getValue() != null) {
                                if (wallpaperViewModel.getAllUploadedWallpaperData().getValue().data != null) {
                                    if (!binding.get().getLoadingMore() && wallpaperViewModel.getAllUploadedWallpaperData().getValue().data.size() == 0) {
                                        binding.get().noItemConstraintLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }

                        break;
                    default:
                        // Default
                        break;
                }
            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

            }

            // we don't need any null checks here for the adapterGrid2 since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (resource != null) {
                Utils.psLog("Got Data Of all wallpapers.");

            } else {

                Utils.psLog("No Data of all wallpapers.");
            }
        });

        wallpaperViewModel.getLoadingState().observe(this, loadingState -> {

            binding.get().setLoadingMore(wallpaperViewModel.isLoading);

            if (loadingState != null && !loadingState) {
                binding.get().swipeRefresh.setRefreshing(false);
            }

        });

        wallpaperViewModel.getNextPageUploadedLoadingStateData().observe(this, state -> {
            if (state != null) {
                if (state.status == Status.ERROR) {
                    Utils.psLog("Next Page State : " + state.data);

                    wallpaperViewModel.setLoadingState(false);//hide
                    wallpaperViewModel.forceEndLoading = true;//stop
                }
            }
        });


    }
    //endregion

    private void replaceGrid1Data(List<Wallpaper> wallpapers) {
        adapter.get().replace(wallpapers);
        binding.get().executePendingBindings();

    }

    @Override
    public void onDispatched() {
        if (wallpaperViewModel.loadingDirection == Utils.LoadingDirection.top) {

            if (binding.get().recyclerView != null) {

                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        binding.get().recyclerView.getLayoutManager();

                if (layoutManager != null) {
                    layoutManager.scrollToPosition(0);
                }
            }
        }
    }

    //endregion

}