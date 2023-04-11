package com.panaceasoft.pswallpaper.viewmodel.livewallpaper.latest;

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

public class LatestLiveWallpaperViewModel extends PSViewModel implements Serializable {

    private LiveData<Resource<List<Wallpaper>>> allLatestLiveWallpaperData;
    private MutableLiveData<LatestLiveWallpaperViewModel.TmpDataHolder> allLatestLiveWallpapersObj = new MutableLiveData<>();

    private LiveData<Resource<Boolean>> allLatestLiveWallpaperNetworkData;
    private MutableLiveData<LatestLiveWallpaperViewModel.TmpDataHolder> allLatestLiveWallpaperNetworkObj = new MutableLiveData<>();

    public WallpaperParamsHolder latestLiveWallpaperParamsHolder = new WallpaperParamsHolder().getLatestLiveWallpaperHolder();

    @Inject
    LatestLiveWallpaperViewModel(WallpaperRepository repository) {
        Utils.psLog("DashBoard ViewModel...");


        allLatestLiveWallpaperData = Transformations.switchMap(allLatestLiveWallpapersObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.getWallpaperListByKey(obj.videoParamsHolder, obj.limit, obj.offset, obj.loginUserId);
        });

        allLatestLiveWallpaperNetworkData = Transformations.switchMap(allLatestLiveWallpaperNetworkObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getNextWallpaperListByKey(Config.API_KEY, obj.loginUserId,obj.videoParamsHolder, obj.limit, obj.offset);
        });

    }

    // region all latest video from pager
    public void setAllLatestLiveWallpaperObj(WallpaperParamsHolder paramsHolder, String limit, String offset, String loginUserId) {

        LatestLiveWallpaperViewModel.TmpDataHolder tmpDataHolder = new LatestLiveWallpaperViewModel.TmpDataHolder();
        tmpDataHolder.videoParamsHolder = paramsHolder;
        tmpDataHolder.limit = limit;
        tmpDataHolder.offset = offset;
        tmpDataHolder.loginUserId = loginUserId;

        allLatestLiveWallpapersObj.setValue(tmpDataHolder);

    }


    public LiveData<Resource<List<Wallpaper>>> getAllLatestLiveWallpaperData() {
        return allLatestLiveWallpaperData;
    }
    //endregion


    // region all trending video add network data

    public void setAllLatestLiveWallpaperNetworkObj(String loginUserId, WallpaperParamsHolder videoParamsHolder, String limit, String offset) {

        if(!isLoading) {
            LatestLiveWallpaperViewModel.TmpDataHolder tmpDataHolder = new LatestLiveWallpaperViewModel.TmpDataHolder();
            tmpDataHolder.loginUserId = loginUserId;
            tmpDataHolder.videoParamsHolder = videoParamsHolder;
            tmpDataHolder.limit = limit;
            tmpDataHolder.offset = offset;

            allLatestLiveWallpaperNetworkObj.setValue(tmpDataHolder);

            setLoadingState(true);
        }
    }

    public LiveData<Resource<Boolean>> getAllLatestLiveWallpaperNetworkData() {
        return allLatestLiveWallpaperNetworkData;
    }
    //endregion

    class TmpDataHolder {
        WallpaperParamsHolder videoParamsHolder = new WallpaperParamsHolder();
        public String loginUserId = "";
        public String limit = "";
        public String offset = "";
        public String wallpaperName="";
        public String catId="";

    }


}