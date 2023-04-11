package com.panaceasoft.pswallpaper.ui.search.selection.colorselection;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.panaceasoft.pswallpaper.Config;
import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.binding.FragmentDataBindingComponent;
import com.panaceasoft.pswallpaper.databinding.FragmentCategorySelectionListBinding;
import com.panaceasoft.pswallpaper.ui.common.DataBoundListAdapter;
import com.panaceasoft.pswallpaper.ui.common.PSFragment;
import com.panaceasoft.pswallpaper.ui.search.adapter.ColorSelectionAdapter;
import com.panaceasoft.pswallpaper.utils.AutoClearedValue;
import com.panaceasoft.pswallpaper.utils.Constants;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewmodel.color.ColorViewModel;
import com.panaceasoft.pswallpaper.viewobject.Color;
import com.panaceasoft.pswallpaper.viewobject.common.Status;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ColorSelectionListFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {

    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ColorViewModel colorViewModel;
    private String color_id = "";

    @VisibleForTesting
    private AutoClearedValue<FragmentCategorySelectionListBinding> binding;
    private AutoClearedValue<ColorSelectionAdapter> adapter;

    //endregion


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentCategorySelectionListBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_selection_list, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        binding.get().setLoadingMore(connectivity.isConnected());
        setHasOptionsMenu(true);

        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            this.color_id = intent.getStringExtra(Constants.INTENT__COLOR_ID);
        }

        return binding.get().getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.clear_button, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.clear) {
            this.color_id = "";
            String colorName = "";

            initAdapters();

            initData();

            navigationController.navigateBackToSearchFragmentFromColor(ColorSelectionListFragment.this.getActivity(), this.color_id, colorName, "");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initUIAndActions() {
        binding.get().scrollFloatingButton.setOnClickListener(v -> binding.get().categoryList.smoothScrollToPosition(0));

        binding.get().categoryList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int lastPosition = layoutManager
                            .findLastVisibleItemPosition();
                    if (lastPosition == adapter.get().getItemCount() - 1) {

                        if (!binding.get().getLoadingMore() && !colorViewModel.forceEndLoading) {

                            if (connectivity.isConnected()) {

                                colorViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                                int limit = Config.ALL_WALLPAPERS_COUNT_BY_CATEGORY;
                                colorViewModel.offset = colorViewModel.offset + limit;

                                colorViewModel.setNextPageColorObj(String.valueOf(Config.COLOR_COUNT), String.valueOf(colorViewModel.offset));
                            }
                        }
                    }
                }
            }
        });

        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            colorViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset reservationViewModel.offset
            colorViewModel.offset = 0;

            // reset reservationViewModel.forceEndLoading
            colorViewModel.forceEndLoading = false;

            // update live data
            colorViewModel.setAllColorObj(String.valueOf(Config.COLOR_COUNT), Config.ZERO_COUNT);

        });
    }

    @Override
    protected void initViewModels() {

        colorViewModel = new ViewModelProvider(this, viewModelFactory).get(ColorViewModel.class);
    }

    @Override
    protected void initAdapters() {
        ColorSelectionAdapter nvadapter = new ColorSelectionAdapter(dataBindingComponent,
                new ColorSelectionAdapter.NewsClickCallback() {
                    @Override
                    public void onClick(Color color, String id) {

                        navigationController.navigateBackToSearchFragmentFromColor(ColorSelectionListFragment.this.getActivity(), color.id, color.name, color.code);

                        if (ColorSelectionListFragment.this.getActivity() != null) {
                            ColorSelectionListFragment.this.getActivity().finish();
                        }
                    }
                }, this.color_id);
        this.adapter = new AutoClearedValue<>(this, nvadapter);
        binding.get().categoryList.setAdapter(this.adapter.get());

        showHideWhenScroll();
    }

    private void showHideWhenScroll() {
        binding.get().categoryList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) binding.get().scrollFloatingButton.show();
                else binding.get().scrollFloatingButton.hide();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void initData() {

        colorViewModel.setAllColorObj(String.valueOf(Config.COLOR_COUNT), Config.ZERO_COUNT);
        colorViewModel.getAllColorData().observe(this, resource -> {

            if (resource != null) {

                Utils.psLog("Got Data" + resource.message + resource.toString());

                switch (resource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        if (resource.data != null) {

                            fadeIn(binding.get().getRoot());

                            replaceDataByCategories(resource.data);
                        }
                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (resource.data != null) {

                            replaceDataByCategories(resource.data);
                        }

                        colorViewModel.setLoadingState(false);
                        break;
                    case ERROR:
                        // Error State
                        colorViewModel.setLoadingState(false);
                        break;
                    default:
                        // Default

                        break;
                }

            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

            }


            // we don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (resource != null) {
                Utils.psLog("Got Data Of all wallpapers.");


            } else {

                Utils.psLog("No Data of all wallpapers.");
            }
        });


        colorViewModel.getNextPageColorData().observe(this, state -> {
            if (state != null) {
                if (state.status == Status.ERROR) {
                    Utils.psLog("Next Page State : " + state.data);

                    colorViewModel.setLoadingState(false);//hide
                    colorViewModel.forceEndLoading = true;//stop
                }
            }
        });

        colorViewModel.getLoadingState().observe(this, loadingState -> {

            binding.get().setLoadingMore(colorViewModel.isLoading);

            if (loadingState != null && !loadingState) {
                binding.get().swipeRefresh.setRefreshing(false);
            }

        });

    }
    //endregion

    private void replaceDataByCategories(List<Color> colors) {
        adapter.get().replace(colors);
        binding.get().executePendingBindings();


    }

    @Override
    public void onDispatched() {

    }
}
