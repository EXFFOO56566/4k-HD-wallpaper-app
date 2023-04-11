package com.panaceasoft.pswallpaper.viewmodel.wallpaper.portrait;

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

public class PortraitWallpaperViewModel extends PSViewModel implements Serializable {
    private LiveData<Resource<List<Wallpaper>>> allPortraitWallpaperData;
    private MutableLiveData<TmpDataHolder> allPortraitWallpapersObj = new MutableLiveData<>();

    private LiveData<Resource<Boolean>> allPortraitWallpaperNetworkData;
    private MutableLiveData<TmpDataHolder> allPortraitWallpaperNetworkObj = new MutableLiveData<>();

    public WallpaperParamsHolder wallpaperParamsHolder = new WallpaperParamsHolder().getPortraitHolder();

    @Inject
    PortraitWallpaperViewModel(WallpaperRepository repository) {
        Utils.psLog("DashBoard ViewModel...");

        allPortraitWallpaperData = Transformations.switchMap(allPortraitWallpapersObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.getWallpaperListByKey(obj.wallpaperParamsHolder, obj.limit, obj.offset, obj.loginUserId);
        });

        allPortraitWallpaperNetworkData = Transformations.switchMap(allPortraitWallpaperNetworkObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.getNextWallpaperListByKey(Config.API_KEY, obj.loginUserId, obj.wallpaperParamsHolder, obj.limit, obj.offset);
        });
    }


    // region all trending wallpapers from pager

    public void setAllPortraitWallpaperObj(WallpaperParamsHolder paramsHolder, String limit, String offset) {

        TmpDataHolder tmpDataHolder = new TmpDataHolder();
        tmpDataHolder.wallpaperParamsHolder = paramsHolder;
        tmpDataHolder.limit = limit;
        tmpDataHolder.offset = offset;
        allPortraitWallpapersObj.setValue(tmpDataHolder);

    }

    public LiveData<Resource<List<Wallpaper>>> getAllPortraitWallpaperData() {
        return allPortraitWallpaperData;
    }
    //endregion


    // region all trending wallpapers add network data


    public void setAllPortraitWallpaperNetworkObj(String loginUserId,
                                                  WallpaperParamsHolder wallpaperParamsHolder, String limit, String offset) {

        if (!isLoading) {
            TmpDataHolder tmpDataHolder = new TmpDataHolder();
            tmpDataHolder.loginUserId = loginUserId;
            tmpDataHolder.wallpaperParamsHolder = wallpaperParamsHolder;
            tmpDataHolder.limit = limit;
            tmpDataHolder.offset = offset;


            allPortraitWallpaperNetworkObj.setValue(tmpDataHolder);

            setLoadingState(true);
        }
    }

    public LiveData<Resource<Boolean>> getAllPortraitWallpaperNetworkData() {
        return allPortraitWallpaperNetworkData;
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
