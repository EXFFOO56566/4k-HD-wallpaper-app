package com.panaceasoft.pswallpaper.viewmodel.wallpaper.latest;

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

public class LatestWallpaperViewModel extends PSViewModel implements Serializable {

    private LiveData<Resource<List<Wallpaper>>> allLatestWallpaperData;
    private MutableLiveData<TmpDataHolder> allLatestWallpapersObj = new MutableLiveData<>();

    private LiveData<Resource<Boolean>> allLatestWallpaperNetworkData;
    private MutableLiveData<LatestWallpaperViewModel.TmpDataHolder> allLatestWallpaperNetworkObj = new MutableLiveData<>();

    public WallpaperParamsHolder wallpaperParamsHolder = new WallpaperParamsHolder().getLatestHolder();

    @Inject
    LatestWallpaperViewModel(WallpaperRepository repository) {
        Utils.psLog("DashBoard ViewModel...");


        allLatestWallpaperData = Transformations.switchMap(allLatestWallpapersObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.getWallpaperListByKey(obj.wallpaperParamsHolder, obj.limit, obj.offset, obj.loginUserId);
        });

        allLatestWallpaperNetworkData = Transformations.switchMap(allLatestWallpaperNetworkObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getNextWallpaperListByKey(Config.API_KEY, obj.loginUserId,obj.wallpaperParamsHolder, obj.limit, obj.offset);
        });

    }

    // region all trending wallpapers from pager
    public void setAllLatestWallpaperObj(WallpaperParamsHolder paramsHolder, String limit, String offset, String loginUserId) {

        TmpDataHolder tmpDataHolder = new TmpDataHolder();
        tmpDataHolder.wallpaperParamsHolder = paramsHolder;
        tmpDataHolder.limit = limit;
        tmpDataHolder.offset = offset;
        tmpDataHolder.loginUserId = loginUserId;

        allLatestWallpapersObj.setValue(tmpDataHolder);

    }


    public LiveData<Resource<List<Wallpaper>>> getAllLatestWallpaperData() {
        return allLatestWallpaperData;
    }
    //endregion


    // region all trending wallpapers add network data

    public void setAllLatestWallpaperNetworkObj( String loginUserId,  WallpaperParamsHolder wallpaperParamsHolder, String limit, String offset) {

        if(!isLoading) {
            TmpDataHolder tmpDataHolder = new TmpDataHolder();
            tmpDataHolder.loginUserId = loginUserId;
            tmpDataHolder.wallpaperParamsHolder = wallpaperParamsHolder;
            tmpDataHolder.limit = limit;
            tmpDataHolder.offset = offset;

            allLatestWallpaperNetworkObj.setValue(tmpDataHolder);

            setLoadingState(true);
        }
    }

    public LiveData<Resource<Boolean>> getAllLatestWallpaperNetworkData() {
        return allLatestWallpaperNetworkData;
    }
    //endregion

    class TmpDataHolder {
        WallpaperParamsHolder wallpaperParamsHolder = new WallpaperParamsHolder();
        public String loginUserId = "";
        public String limit = "";
        public String offset = "";
        public String wallpaperName="";
        public String catId="";

    }


}