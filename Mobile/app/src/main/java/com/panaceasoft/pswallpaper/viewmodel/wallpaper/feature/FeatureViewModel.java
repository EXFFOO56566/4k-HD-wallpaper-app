package com.panaceasoft.pswallpaper.viewmodel.wallpaper.feature;

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

public class FeatureViewModel extends PSViewModel implements Serializable {
    private LiveData<Resource<List<Wallpaper>>> allFeatureWallpaperData;
    private MutableLiveData<FeatureViewModel.TmpDataHolder> allFeatureWallpapersObj = new MutableLiveData<>();

    private LiveData<Resource<Boolean>> allFeatureWallpaperNetworkData;
    private MutableLiveData<FeatureViewModel.TmpDataHolder> allFeatureWallpaperNetworkObj = new MutableLiveData<>();

    public WallpaperParamsHolder wallpaperParamsHolder = new WallpaperParamsHolder();

    @Inject
    FeatureViewModel(WallpaperRepository repository) {
        Utils.psLog("DashBoard ViewModel...");


        allFeatureWallpaperData = Transformations.switchMap(allFeatureWallpapersObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.getWallpaperListByKey(obj.wallpaperParamsHolder, obj.limit, obj.offset, obj.loginUserId);
        });

        allFeatureWallpaperNetworkData = Transformations.switchMap(allFeatureWallpaperNetworkObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.getNextWallpaperListByKey(Config.API_KEY, obj.loginUserId, obj.wallpaperParamsHolder, obj.limit, obj.offset);
        });
    }


    // region all feature wallpapers from pager
    public void setAllFeatureWallpaperObj(WallpaperParamsHolder paramsHolder, String limit, String offset, String loginUserId) {

        FeatureViewModel.TmpDataHolder tmpDataHolder = new FeatureViewModel.TmpDataHolder();
        tmpDataHolder.wallpaperParamsHolder = paramsHolder;
        tmpDataHolder.limit = limit;
        tmpDataHolder.offset = offset;
        tmpDataHolder.loginUserId = loginUserId;

        allFeatureWallpapersObj.setValue(tmpDataHolder);

    }

    public LiveData<Resource<List<Wallpaper>>> getAllFeatureWallpaperData() {
        return allFeatureWallpaperData;
    }
    //endregion


    // region all feature wallpapers add network data

    public void setAllFeatureWallpaperNetworkObj(String loginUserId, WallpaperParamsHolder wallpaperParamsHolder, String limit, String offset) {

        if (!isLoading) {
            FeatureViewModel.TmpDataHolder tmpDataHolder = new FeatureViewModel.TmpDataHolder();
            tmpDataHolder.loginUserId = loginUserId;
            tmpDataHolder.wallpaperParamsHolder = wallpaperParamsHolder;
            tmpDataHolder.limit = limit;
            tmpDataHolder.offset = offset;

            allFeatureWallpaperNetworkObj.setValue(tmpDataHolder);

            setLoadingState(true);
        }
    }

    public LiveData<Resource<Boolean>> getAllFeatureWallpaperNetworkData() {
        return allFeatureWallpaperNetworkData;
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
