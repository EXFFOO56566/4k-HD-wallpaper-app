package com.panaceasoft.pswallpaper.viewmodel.wallpaper.trending;

import com.panaceasoft.pswallpaper.Config;
import com.panaceasoft.pswallpaper.repository.wallpaper.WallpaperRepository;
import com.panaceasoft.pswallpaper.utils.AbsentLiveData;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewmodel.common.PSViewModel;
import com.panaceasoft.pswallpaper.viewobject.Wallpaper;
import com.panaceasoft.pswallpaper.viewobject.common.Resource;
import com.panaceasoft.pswallpaper.viewobject.holder.WallpaperParamsHolder;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class TrendingViewModel extends PSViewModel implements Serializable {
    private LiveData<Resource<List<Wallpaper>>> allTrendingWallpaperData;
    private MutableLiveData<TrendingViewModel.TmpDataHolder> allTrendingWallpapersObj = new MutableLiveData<>();

    private LiveData<Resource<Boolean>> allTrendingWallpaperNetworkData;
    private MutableLiveData<TrendingViewModel.TmpDataHolder> allTrendingWallpaperNetworkObj = new MutableLiveData<>();

    public WallpaperParamsHolder wallpaperParamsHolder = new WallpaperParamsHolder().getTrendingHolder();

    @Inject
    TrendingViewModel(WallpaperRepository repository) {
        Utils.psLog("DashBoard ViewModel...");


        allTrendingWallpaperData = Transformations.switchMap(allTrendingWallpapersObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.getWallpaperListByKey(obj.wallpaperParamsHolder, obj.limit, obj.offset, obj.loginUserId);
        });

        allTrendingWallpaperNetworkData = Transformations.switchMap(allTrendingWallpaperNetworkObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.getNextWallpaperListByKey(Config.API_KEY, obj.loginUserId, obj.wallpaperParamsHolder, obj.limit, obj.offset);
        });
    }


    // region all trending wallpapers from pager
    public void setAllTrendingWallpaperObj(WallpaperParamsHolder paramsHolder, String limit, String offset, String loginUserId) {

        TrendingViewModel.TmpDataHolder tmpDataHolder = new TrendingViewModel.TmpDataHolder();
        tmpDataHolder.wallpaperParamsHolder = paramsHolder;
        tmpDataHolder.limit = limit;
        tmpDataHolder.offset = offset;
        tmpDataHolder.loginUserId = loginUserId;

        allTrendingWallpapersObj.setValue(tmpDataHolder);

    }

    public LiveData<Resource<List<Wallpaper>>> getAllTrendingWallpaperData() {
        return allTrendingWallpaperData;
    }
    //endregion


    // region all trending wallpapers add network data

    public void setAllTrendingWallpaperNetworkObj(String loginUserId, WallpaperParamsHolder wallpaperParamsHolder, String limit, String offset) {

        if (!isLoading) {
            TrendingViewModel.TmpDataHolder tmpDataHolder = new TrendingViewModel.TmpDataHolder();
            tmpDataHolder.loginUserId = loginUserId;
            tmpDataHolder.wallpaperParamsHolder = wallpaperParamsHolder;
            tmpDataHolder.limit = limit;
            tmpDataHolder.offset = offset;

            allTrendingWallpaperNetworkObj.setValue(tmpDataHolder);

            setLoadingState(true);
        }
    }

    public LiveData<Resource<Boolean>> getAllTrendingWallpaperNetworkData() {
        return allTrendingWallpaperNetworkData;
    }
    //endregion

    class TmpDataHolder {
        WallpaperParamsHolder wallpaperParamsHolder = new WallpaperParamsHolder();
        public String loginUserId = "";
        public String limit = "";
        public String offset = "";
        public String wallpaperName = "";
        public String catId = "";

    }

}
