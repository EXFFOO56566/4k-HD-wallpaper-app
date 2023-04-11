package com.panaceasoft.pswallpaper.viewmodel.wallpaper.touchcount;

import com.panaceasoft.pswallpaper.repository.wallpaper.WallpaperRepository;
import com.panaceasoft.pswallpaper.utils.AbsentLiveData;
import com.panaceasoft.pswallpaper.viewmodel.common.PSViewModel;
import com.panaceasoft.pswallpaper.viewobject.Wallpaper;
import com.panaceasoft.pswallpaper.viewobject.common.Resource;

import javax.inject.Inject;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class TouchCountViewModel extends PSViewModel {

    private final LiveData<Resource<Wallpaper>> sendTouchCountPostData;
    private MutableLiveData<TmpDataHolder> sendTouchCountDataPostObj = new MutableLiveData<>();

    @Inject
    TouchCountViewModel(WallpaperRepository dashboardRepository) {
        sendTouchCountPostData = Transformations.switchMap(sendTouchCountDataPostObj, new Function<TmpDataHolder, LiveData<Resource<Wallpaper>>>() {
            @Override
            public LiveData<Resource<Wallpaper>> apply(TmpDataHolder obj) {

                if (obj == null) {
                    return AbsentLiveData.create();
                }
                return dashboardRepository.uploadTouchCountPostToServer(obj.wallpaperId, obj.loginUserId);
            }
        });
    }

    public void setTouchCountPostDataObj(String wallpaperId, String loginUserId) {

        TmpDataHolder tmpDataHolder = new TmpDataHolder();
        tmpDataHolder.wallpaperId = wallpaperId;
        tmpDataHolder.loginUserId = loginUserId;

        sendTouchCountDataPostObj.setValue(tmpDataHolder);

    }

    public LiveData<Resource<Wallpaper>> getTouchCountPostData() {
        return sendTouchCountPostData;
    }

    class TmpDataHolder {
        public String wallpaperId = "";
        public String loginUserId = "";

    }
}
