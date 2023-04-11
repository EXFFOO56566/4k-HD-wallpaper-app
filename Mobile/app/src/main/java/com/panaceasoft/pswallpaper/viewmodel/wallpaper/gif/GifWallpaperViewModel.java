package com.panaceasoft.pswallpaper.viewmodel.wallpaper.gif;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

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

public class GifWallpaperViewModel extends PSViewModel implements Serializable {

    private LiveData<Resource<List<Wallpaper>>> allGifWallpaperData;
    private MutableLiveData<GifWallpaperViewModel.TmpDataHolder> allGifWallpapersObj = new MutableLiveData<>();

    private LiveData<Resource<Boolean>> allLatestWallpaperNetworkData;
    private MutableLiveData<GifWallpaperViewModel.TmpDataHolder> allLatestWallpaperNetworkObj = new MutableLiveData<>();

    public WallpaperParamsHolder wallpaperParamsHolder = new WallpaperParamsHolder().getLatestHolder();

    @Inject
    GifWallpaperViewModel(WallpaperRepository repository) {
        Utils.psLog("DashBoard ViewModel...");

        allGifWallpaperData = Transformations.switchMap(allGifWallpapersObj, obj -> {
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
    public void setAllGifWallpaperObj(WallpaperParamsHolder paramsHolder, String limit, String offset, String loginUserId) {

        GifWallpaperViewModel.TmpDataHolder tmpDataHolder = new GifWallpaperViewModel.TmpDataHolder();
        tmpDataHolder.wallpaperParamsHolder = paramsHolder;
        tmpDataHolder.limit = limit;
        tmpDataHolder.offset = offset;
        tmpDataHolder.loginUserId = loginUserId;

        allGifWallpapersObj.setValue(tmpDataHolder);

    }


    public LiveData<Resource<List<Wallpaper>>> getAllGifWallpaperData() {
        return allGifWallpaperData;
    }
    //endregion


    // region all trending wallpapers add network data

    public void setAllGifWallpaperNetworkObj(String loginUserId, WallpaperParamsHolder wallpaperParamsHolder, String limit, String offset) {

        if(!isLoading) {
            GifWallpaperViewModel.TmpDataHolder tmpDataHolder = new GifWallpaperViewModel.TmpDataHolder();
            tmpDataHolder.loginUserId = loginUserId;
            tmpDataHolder.wallpaperParamsHolder = wallpaperParamsHolder;
            tmpDataHolder.limit = limit;
            tmpDataHolder.offset = offset;

            allLatestWallpaperNetworkObj.setValue(tmpDataHolder);

            setLoadingState(true);
        }
    }

    public LiveData<Resource<Boolean>> getAllGifWallpaperNetworkData() {
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