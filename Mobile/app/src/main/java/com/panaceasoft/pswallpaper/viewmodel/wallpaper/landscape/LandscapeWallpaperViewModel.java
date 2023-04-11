package com.panaceasoft.pswallpaper.viewmodel.wallpaper.landscape;

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

public class LandscapeWallpaperViewModel extends PSViewModel implements Serializable {

    private LiveData<Resource<List<Wallpaper>>> allLandscapeWallpaperData;
    private MutableLiveData<LandscapeWallpaperViewModel.TmpDataHolder> allLandscapeWallpapersObj = new MutableLiveData<>();

    private LiveData<Resource<Boolean>> allLandscapeWallpaperNetworkData;
    private MutableLiveData<LandscapeWallpaperViewModel.TmpDataHolder> allLandscapeWallpaperNetworkObj = new MutableLiveData<>();
    public WallpaperParamsHolder wallpaperParamsHolder = new WallpaperParamsHolder().getLandscapeHolder();

    @Inject
    LandscapeWallpaperViewModel(WallpaperRepository repository) {
        Utils.psLog("DashBoard ViewModel...");


        allLandscapeWallpaperData = Transformations.switchMap(allLandscapeWallpapersObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.getWallpaperListByKey(obj.wallpaperParamsHolder, obj.limit, obj.offset, obj.loginUserId);
        });

        allLandscapeWallpaperNetworkData = Transformations.switchMap(allLandscapeWallpaperNetworkObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.getNextWallpaperListByKey(Config.API_KEY, obj.loginUserId, obj.wallpaperParamsHolder, obj.limit, obj.offset);
        });
    }

    // region all trending wallpapers from pager
    public void setAllLandScapeWallpaperObj(WallpaperParamsHolder paramsHolder, String limit, String offset, String loginUserId) {

        TmpDataHolder tmpDataHolder = new TmpDataHolder();
        tmpDataHolder.wallpaperParamsHolder = paramsHolder;
        tmpDataHolder.limit = limit;
        tmpDataHolder.offset = offset;
        tmpDataHolder.loginUserId = loginUserId;

        allLandscapeWallpapersObj.setValue(tmpDataHolder);

    }

    public LiveData<Resource<List<Wallpaper>>> getAllLandscapeWallpaperData() {
        return allLandscapeWallpaperData;
    }
    //endregion


    // region all trending wallpapers add network data

    public void setAllLandScapeWallpaperNetworkObj(String loginUserId,
                                                   WallpaperParamsHolder wallpaperParamsHolder, String limit, String offset) {

        if (!isLoading) {
            LandscapeWallpaperViewModel.TmpDataHolder tmpDataHolder = new LandscapeWallpaperViewModel.TmpDataHolder();
            tmpDataHolder.loginUserId = loginUserId;
            tmpDataHolder.wallpaperParamsHolder = wallpaperParamsHolder;
            tmpDataHolder.limit = limit;
            tmpDataHolder.offset = offset;

            allLandscapeWallpaperNetworkObj.setValue(tmpDataHolder);

            setLoadingState(true);
        }
    }

    public LiveData<Resource<Boolean>> getAllLandscapeWallpaperNetworkData() {
        return allLandscapeWallpaperNetworkData;
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
