package com.panaceasoft.pswallpaper.viewmodel.wallpaper.square;

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

public class SquareWallpaperViewModel extends PSViewModel implements Serializable {
    private LiveData<Resource<List<Wallpaper>>> allSquareWallpaperData;
    private MutableLiveData<TmpDataHolder> allSquareWallpapersObj = new MutableLiveData<>();

    private LiveData<Resource<Boolean>> allSquareWallpaperNetworkData;
    private MutableLiveData<TmpDataHolder> allSquareWallpaperNetworkObj = new MutableLiveData<>();

    public WallpaperParamsHolder wallpaperParamsHolder = new WallpaperParamsHolder().getSquareHolder();

    @Inject
    SquareWallpaperViewModel(WallpaperRepository repository) {
        Utils.psLog("DashBoard ViewModel...");


        allSquareWallpaperData = Transformations.switchMap(allSquareWallpapersObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.getWallpaperListByKey(obj.wallpaperParamsHolder, obj.limit, obj.offset, obj.loginUserId);
        });

        allSquareWallpaperNetworkData = Transformations.switchMap(allSquareWallpaperNetworkObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.getNextWallpaperListByKey(Config.API_KEY, obj.loginUserId, obj.wallpaperParamsHolder, obj.limit, obj.offset);
        });
    }


    // region all trending wallpapers from pager
    public void setAllSquareWallpaperObj(WallpaperParamsHolder paramsHolder, String limit, String offset) {

        TmpDataHolder tmpDataHolder = new TmpDataHolder();
        tmpDataHolder.wallpaperParamsHolder = paramsHolder;
        tmpDataHolder.limit = limit;
        tmpDataHolder.offset = offset;

        allSquareWallpapersObj.setValue(tmpDataHolder);

    }

    public LiveData<Resource<List<Wallpaper>>> getAllSquareWallpaperData() {
        return allSquareWallpaperData;
    }
    //endregion


    // region all trending wallpapers add network data

    public void setAllSquareWallpaperNetworkObj(String loginUserId, WallpaperParamsHolder wallpaperParamsHolder, String limit, String offset) {

        if (!isLoading) {
            TmpDataHolder tmpDataHolder = new TmpDataHolder();
            tmpDataHolder.loginUserId = loginUserId;
            tmpDataHolder.wallpaperParamsHolder = wallpaperParamsHolder;
            tmpDataHolder.limit = limit;
            tmpDataHolder.offset = offset;

            allSquareWallpaperNetworkObj.setValue(tmpDataHolder);

            setLoadingState(true);
        }
    }

    public LiveData<Resource<Boolean>> getAllSquareWallpaperNetworkData() {
        return allSquareWallpaperNetworkData;
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
