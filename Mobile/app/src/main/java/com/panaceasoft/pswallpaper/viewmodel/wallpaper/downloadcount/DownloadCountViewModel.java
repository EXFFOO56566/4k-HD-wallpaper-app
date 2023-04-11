package com.panaceasoft.pswallpaper.viewmodel.wallpaper.downloadcount;

import com.panaceasoft.pswallpaper.repository.wallpaper.WallpaperRepository;
import com.panaceasoft.pswallpaper.utils.AbsentLiveData;
import com.panaceasoft.pswallpaper.viewmodel.common.PSViewModel;
import com.panaceasoft.pswallpaper.viewobject.Wallpaper;
import com.panaceasoft.pswallpaper.viewobject.common.Resource;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

/**
 * Created by Panacea-Soft on 2/16/19.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class DownloadCountViewModel extends PSViewModel {

    private final LiveData<Resource<Wallpaper>> sendDownloadCountPostData;
    private MutableLiveData<TmpDataHolder> sendDownloadCountDataPostObj = new MutableLiveData<>();

    @Inject
    DownloadCountViewModel(WallpaperRepository dashboardRepository) {
        sendDownloadCountPostData = Transformations.switchMap(sendDownloadCountDataPostObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }
            return dashboardRepository.uploadDownloadCountPostToServer(obj.wallpaperId, obj.loginUserId);
        });
    }

    public void setDownloadCountPostDataObj(String wallpaperId,String loginUserId) {

        TmpDataHolder tmpDataHolder = new TmpDataHolder();
        tmpDataHolder.wallpaperId = wallpaperId;
        tmpDataHolder.loginUserId = loginUserId;

        sendDownloadCountDataPostObj.setValue(tmpDataHolder);

    }

    public LiveData<Resource<Wallpaper>> getDownloadCountPostData() {
        return sendDownloadCountPostData;
    }

    class TmpDataHolder {
        public String wallpaperId="";
        public String loginUserId = "";
    }
}
