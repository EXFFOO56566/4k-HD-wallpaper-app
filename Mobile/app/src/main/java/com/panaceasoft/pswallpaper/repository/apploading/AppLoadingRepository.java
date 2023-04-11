package com.panaceasoft.pswallpaper.repository.apploading;

import com.panaceasoft.pswallpaper.AppExecutors;
import com.panaceasoft.pswallpaper.Config;
import com.panaceasoft.pswallpaper.api.ApiResponse;
import com.panaceasoft.pswallpaper.api.PSApiService;
import com.panaceasoft.pswallpaper.db.PSCoreDb;
import com.panaceasoft.pswallpaper.repository.common.PSRepository;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewobject.DeletedObject;
import com.panaceasoft.pswallpaper.viewobject.PSAppInfo;
import com.panaceasoft.pswallpaper.viewobject.common.Resource;

import java.io.IOException;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Response;

public class AppLoadingRepository extends PSRepository {

    @Inject
    AppLoadingRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db) {
        super(psApiService, appExecutors, db);
    }

    public LiveData<Resource<PSAppInfo>> deleteTheSpecificObjects(String startDate, String endDate) {

        final MutableLiveData<Resource<PSAppInfo>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            Response<PSAppInfo> response;

            try {
                response = psApiService.getDeletedHistory(Config.API_KEY, startDate, endDate).execute();

                ApiResponse<PSAppInfo> apiResponse = new ApiResponse<>(response);

                if (apiResponse.isSuccessful()) {

//                    try {
//                        db.beginTransaction();
//
//                        if (apiResponse.body != null) {
//
//                            if (apiResponse.body.deletedObjects.size() > 0) {
//                                for (DeletedObject deletedObject : apiResponse.body.deletedObjects) {
//                                    db.wallpaperDao().deleteById(deletedObject.id);
//                                }
//                            }
//                        }
//
//                        db.setTransactionSuccessful();
//                    } catch (NullPointerException ne) {
//                        Utils.psErrorLog("Null Pointer Exception : ", ne);
//                    } catch (Exception e) {
//                        Utils.psErrorLog("Exception : ", e);
//                    } finally {
//                        db.endTransaction();
//                    }

                    try {
                        db.runInTransaction(() -> {
                            if (apiResponse.body != null) {

                                if (apiResponse.body.deletedObjects.size() > 0) {
                                    for (DeletedObject deletedObject : apiResponse.body.deletedObjects) {
                                        db.wallpaperDao().deleteById(deletedObject.id);
                                    }
                                }
                            }
                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                    statusLiveData.postValue(Resource.success(apiResponse.body));

                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, null));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), null));
            }

        });

        return statusLiveData;
    }
}
