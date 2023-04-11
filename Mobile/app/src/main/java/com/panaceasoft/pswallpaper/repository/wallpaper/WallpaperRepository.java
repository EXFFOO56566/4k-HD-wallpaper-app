package com.panaceasoft.pswallpaper.repository.wallpaper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.panaceasoft.pswallpaper.AppExecutors;
import com.panaceasoft.pswallpaper.Config;
import com.panaceasoft.pswallpaper.api.ApiResponse;
import com.panaceasoft.pswallpaper.api.PSApiService;
import com.panaceasoft.pswallpaper.db.PSCoreDb;
import com.panaceasoft.pswallpaper.db.WallpaperDao;
import com.panaceasoft.pswallpaper.repository.common.NetworkBoundResource;
import com.panaceasoft.pswallpaper.repository.common.PSRepository;
import com.panaceasoft.pswallpaper.utils.Constants;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewobject.ApiStatus;
import com.panaceasoft.pswallpaper.viewobject.Image;
import com.panaceasoft.pswallpaper.viewobject.UploadedWallpaper;
import com.panaceasoft.pswallpaper.viewobject.User;
import com.panaceasoft.pswallpaper.viewobject.Wallpaper;
import com.panaceasoft.pswallpaper.viewobject.WallpaperMap;
import com.panaceasoft.pswallpaper.viewobject.common.Resource;
import com.panaceasoft.pswallpaper.viewobject.holder.WallpaperParamsHolder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class WallpaperRepository extends PSRepository {

    private final WallpaperDao wallpaperDao;
    private String isSelected;
    private float oldRating;
    private String buyStatus;
    private Wallpaper wallpaper;
    private int count;
    private int favMaxSorting = 0;
    private int downloadMaxSorting = 0;

    @Inject
    protected WallpaperRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, WallpaperDao wallpaperDao) {
        super(psApiService, appExecutors, db);
        this.wallpaperDao = wallpaperDao;
    }

    //region General Wallpapers

    public LiveData<Resource<List<Wallpaper>>> getWallpaperListByKey(WallpaperParamsHolder paramsHolder, String limit, String offset, String loginUserId) {

        return new NetworkBoundResource<List<Wallpaper>, List<Wallpaper>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<Wallpaper> itemList) {
                Utils.psLog("SaveCallResult of getProductListByKey.");

                try {
                    db.runInTransaction(() -> {
                        String mapKey = paramsHolder.getKeyForProductMap();

                        db.wallpaperMapDao().deleteByMapKey(mapKey);

                        wallpaperDao.insertAll(itemList);

                        String dateTime = Utils.getDateTime();

                        for (int i = 0; i < itemList.size(); i++) {

                            db.wallpaperMapDao().insert(new WallpaperMap(mapKey + itemList.get(i).wallpaper_id, mapKey, itemList.get(i).wallpaper_id, i + 1, dateTime));

                        }
                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Wallpaper> data) {

                // Recent news always load from server
                return connectivity.isConnected();

            }

            @NonNull
            @Override
            protected LiveData<List<Wallpaper>> loadFromDb() {
                Utils.psLog("Load getProductListByKey From Db");

                String mapKey = paramsHolder.getKeyForProductMap();

                return wallpaperDao.getWallpaperByKey(mapKey);

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Wallpaper>>> createCall() {
                Utils.psLog("Call API Service to getProductListByKey.");
                String type;
                if (Config.ENABLE_PREMIUM) {
                    type = "";
                }
                else{
                    type = "1";
                }

                if (paramsHolder.type.equals(Constants.ONE) || paramsHolder.type.equals(Constants.TWO)) {
                    type = paramsHolder.type;
                }

                return psApiService.getWallpaperList(Config.API_KEY, Utils.checkUserId(loginUserId), limit, offset, paramsHolder.wallpaperName, paramsHolder.catId, paramsHolder.addedUserId, type,
                        paramsHolder.isRecommended, paramsHolder.isPortrait, paramsHolder.isLandscape,
                        paramsHolder.isSquare, paramsHolder.colorId, paramsHolder.rating_max, paramsHolder.rating_min, paramsHolder.point_max, paramsHolder.point_min, paramsHolder.isWallpaper,paramsHolder.isGif,paramsHolder.isLiveWallpaper,
                        paramsHolder.orderBy, paramsHolder.orderType);

            }

            @Override
            protected void onFetchFailed(String message) {
                Utils.psLog("Fetch Failed (getProductListByKey) : " + message);
            }

        }.

                asLiveData();

    }

    public LiveData<Resource<Boolean>> getNextWallpaperListByKey(String apiKey,
                                                                 String loginUserId,
                                                                 WallpaperParamsHolder wallpaperParamsHolder,
                                                                 String limit,
                                                                 String offset) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();

        String type;
        if (Config.ENABLE_PREMIUM) {
            type = "";
        }
        else{
            type = "1";
        }

        if (wallpaperParamsHolder.type.equals(Constants.ONE) || wallpaperParamsHolder.type.equals(Constants.TWO)) {
            type = wallpaperParamsHolder.type;
        }

        LiveData<ApiResponse<List<Wallpaper>>> apiResponse = psApiService.getWallpaperList(apiKey, Utils.checkUserId(loginUserId), limit, offset, wallpaperParamsHolder.wallpaperName, wallpaperParamsHolder.catId, wallpaperParamsHolder.addedUserId, type,
                wallpaperParamsHolder.isRecommended, wallpaperParamsHolder.isPortrait, wallpaperParamsHolder.isLandscape,
                wallpaperParamsHolder.isSquare, wallpaperParamsHolder.colorId, wallpaperParamsHolder.rating_max, wallpaperParamsHolder.rating_min, wallpaperParamsHolder.point_max, wallpaperParamsHolder.point_min,
                wallpaperParamsHolder.isWallpaper,wallpaperParamsHolder.isGif, wallpaperParamsHolder.isLiveWallpaper, wallpaperParamsHolder.orderBy, wallpaperParamsHolder.orderType);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                if (response.body != null) {
                    appExecutors.diskIO().execute(() -> {

//                        try {
//
//                            db.beginTransaction();
//
//                            db.wallpaperDao().insertAll(response.body);
//
//                            int startIndex = db.wallpaperMapDao().getMaxSortingByValue(wallpaperParamsHolder.getKeyForProductMap());
//
//                            String mapKey = wallpaperParamsHolder.getKeyForProductMap();
//                            String dateTime = Utils.getDateTime();
//
//                            for (int i = 0; i < response.body.size(); i++) {
//                                db.wallpaperMapDao().insert(new WallpaperMap(mapKey + response.body.get(i).wallpaper_id, mapKey, response.body.get(i).wallpaper_id, startIndex + i + 1, dateTime));
//                            }
//
//                            db.setTransactionSuccessful();
//
//                        } catch (NullPointerException ne) {
//                            Utils.psErrorLog("Null Pointer Exception : ", ne);
//                        } catch (Exception e) {
//                            Utils.psErrorLog("Exception : ", e);
//                        } finally {
//                            db.endTransaction();
//                        }

                        try {
                            db.runInTransaction(() -> {

                                db.wallpaperDao().insertAll(response.body);

                                int startIndex = db.wallpaperMapDao().getMaxSortingByValue(wallpaperParamsHolder.getKeyForProductMap());

                                String mapKey = wallpaperParamsHolder.getKeyForProductMap();
                                String dateTime = Utils.getDateTime();

                                for (int i = 0; i < response.body.size(); i++) {
                                    db.wallpaperMapDao().insert(new WallpaperMap(mapKey + response.body.get(i).wallpaper_id, mapKey, response.body.get(i).wallpaper_id, startIndex + i + 1, dateTime));
                                }

                            });
                        } catch (Exception ex) {
                            Utils.psErrorLog("Error at ", ex);
                        }

                        statusLiveData.postValue(Resource.success(true));
                    });
                } else {
                    statusLiveData.postValue(Resource.error(response.errorMessage, null));
                }

            } else {
                statusLiveData.postValue(Resource.error(response.errorMessage, null));
            }
        });

        return statusLiveData;

    }

    //region Get Wallpaper Only from Database for Wallpaper Detail Fragment

    public LiveData<List<Wallpaper>> getWallpaperListByKeyOnlyFromDatabase(WallpaperParamsHolder paramsHolder) {

        return db.wallpaperDao().getWallpaperByKey(paramsHolder.getKeyForProductMap());

    }

    //region Get Wallpaper Only from Database

    public LiveData<Resource<Wallpaper>> getWallpaperById(String id, String userId) {

        String functionKey = "getAllDownloadedWallpapers";

        return new NetworkBoundResource<Wallpaper, Wallpaper>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull Wallpaper wallpaper) {
                Utils.psLog("SaveCallResult of get all wallpapers.");

                try {
                    db.runInTransaction(() -> wallpaperDao.insert(wallpaper));
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Wallpaper data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<Wallpaper> loadFromDb() {
                Utils.psLog("Load all wallpapers From DB.");

                return wallpaperDao.getWallpaperById(id);

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Wallpaper>> createCall() {
                Utils.psLog("Call all wallpapers webservice.");
                return psApiService.getWallpaperById(Config.API_KEY, id, Utils.checkUserId(userId));
            }

            @Override
            protected void onFetchFailed(String message) {
                Utils.psLog("Fetch Failed of all wallpapers");
                rateLimiter.reset(functionKey);
            }

        }.asLiveData();
    }

    //endregion

    public LiveData<Resource<List<Wallpaper>>> getAllFavoriteWallpapers(String apiKey, String loginUserId, String limit, String offset, String wallpaperType) {

        String functionKey = "getAllWallpapers";

        return new NetworkBoundResource<List<Wallpaper>, List<Wallpaper>>(appExecutors) {


            @Override
            protected void saveCallResult(@NonNull List<Wallpaper> wallpaperList) {
                Utils.psLog("SaveCallResult of get all wallpapers.");

                try {
                    db.runInTransaction(() -> {

                        wallpaperDao.insertAll(wallpaperList);

                        String dateTime = Utils.getDateTime();

                        String mapKey = new WallpaperParamsHolder().getFavQueryHolder().getKeyForProductMap();

                        db.wallpaperMapDao().deleteByMapKey(mapKey);

                        for (int i = 0; i < wallpaperList.size(); i++) {

                            db.wallpaperMapDao().insert(new WallpaperMap(mapKey + wallpaperList.get(i).wallpaper_id, mapKey, wallpaperList.get(i).wallpaper_id, i + 1, dateTime));
                        }

                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Wallpaper> data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<Wallpaper>> loadFromDb() {
                Utils.psLog("Load all wallpapers From DB.");
                String mapKey = new WallpaperParamsHolder().getFavQueryHolder().getKeyForProductMap();
                return wallpaperDao.getWallpaperByKey(mapKey);

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Wallpaper>>> createCall() {
                Utils.psLog("Call all wallpapers webservice.");
                return psApiService.getFavoriteWallpaper(apiKey, Utils.checkUserId(loginUserId),wallpaperType, limit, offset);
            }

            @Override
            protected void onFetchFailed(String message) {
                Utils.psLog("Fetch Failed of all wallpapers");
                rateLimiter.reset(functionKey);
            }

        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> getNextPageFavouriteList(String apiKey, String loginUserId, String limit, String offset, String wallpaperType) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();

        LiveData<ApiResponse<List<Wallpaper>>> apiResponse = psApiService.getFavoriteWallpaper(apiKey, Utils.checkUserId(loginUserId),wallpaperType, limit, offset);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {

                    String mapKey = new WallpaperParamsHolder().getFavQueryHolder().getKeyForProductMap();

                    try {
                        db.runInTransaction(() -> {

                            if (response.body != null) {

                                int lastSortingPosition = db.wallpaperMapDao().getMaxSortingByValue(mapKey);

                                wallpaperDao.insertAll(response.body);

                                String dateTime = Utils.getDateTime();

                                for (int i = 0; i < response.body.size(); i++) {
                                    db.wallpaperMapDao().insert(new WallpaperMap(mapKey + response.body.get(i).wallpaper_id, mapKey, response.body.get(i).wallpaper_id, lastSortingPosition + i + 1, dateTime));
                                }

                            }
                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                    statusLiveData.postValue(Resource.success(true));
                });

            } else {
                statusLiveData.postValue(Resource.error(response.errorMessage, null));
            }

        });

        return statusLiveData;

    }

    //endregion


    //region Uploaded Wallpapers

    public LiveData<Resource<List<Wallpaper>>> getAllUploadedWallpapers(String apiKey, String loginUserId, String limit, String offset) {

        String functionKey = "getAllUploadedWallpapers";

        String dataTime = Utils.getDateTime();

        return new NetworkBoundResource<List<Wallpaper>, List<Wallpaper>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<Wallpaper> wallpaperList) {
                Utils.psLog("SaveCallResult of getAllUploadedWallpapers.");

                try {
                    db.runInTransaction(() -> {

                        db.uploadWallpaperDao().deleteUploadedWallpapers();

                        for (int i = 0; i < wallpaperList.size(); i++) {
                            db.uploadWallpaperDao().insert(new UploadedWallpaper(wallpaperList.get(i).wallpaper_id, i + 1, dataTime));
                        }

                        wallpaperDao.insertAll(wallpaperList);

                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Wallpaper> data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<Wallpaper>> loadFromDb() {
                Utils.psLog("Load all wallpapers From DB.");

                return db.uploadWallpaperDao().getAllUploadedWallpapers();

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Wallpaper>>> createCall() {
                Utils.psLog("Call all wallpapers webservice.");
                return psApiService.getUploadedWallpaper(apiKey, Utils.checkUserId(loginUserId), limit, offset);
            }

            @Override
            protected void onFetchFailed(String message) {
                Utils.psLog("Fetch Failed of all wallpapers");
                rateLimiter.reset(functionKey);
            }

        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> getNextPageUploadedWallpaperList(String apiKey, String loginUserId, String limit, String offset, String wallpaperType) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();

        LiveData<ApiResponse<List<Wallpaper>>> apiResponse = psApiService.getFavoriteWallpaper(apiKey, Utils.checkUserId(loginUserId),wallpaperType, limit, offset);

        String dataTime = Utils.getDateTime();

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);


            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {

                    try {
                        db.runInTransaction(() -> {
                            if (response.body != null) {

                                int lastSortingPosition = db.uploadWallpaperDao().getLastUploadSorting() + 1;

                                for (int i = 0; i < response.body.size(); i++) {
                                    lastSortingPosition += i;
                                    db.uploadWallpaperDao().insert(new UploadedWallpaper(response.body.get(i).wallpaper_id, lastSortingPosition, dataTime));
                                }

                                wallpaperDao.insertAll(response.body);
                            }
                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                    statusLiveData.postValue(Resource.success(true));
                });

            } else {
                statusLiveData.postValue(Resource.error(response.errorMessage, null));
            }

        });

        return statusLiveData;

    }

    //endregion


    //region Downloaded Wallpapers

    public LiveData<Resource<List<Wallpaper>>> getAllDownloadedWallpapers(String apiKey, String loginUserId, String limit, String offset, String wallpaperType) {

        String functionKey = "getAllDownloadedWallpapers";

        String mapKey = new WallpaperParamsHolder().getDownloadQueryHolder().getKeyForProductMap();

        return new NetworkBoundResource<List<Wallpaper>, List<Wallpaper>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<Wallpaper> wallpaperList) {
                Utils.psLog("SaveCallResult of get all wallpapers.");

                try {
                    db.runInTransaction(() -> {

                        String dateTime = Utils.getDateTime();

                        db.wallpaperMapDao().deleteByMapKey(mapKey);

                        for (int i = 0; i < wallpaperList.size(); i++) {

                            db.wallpaperMapDao().insert(new WallpaperMap(mapKey + wallpaperList.get(i).wallpaper_id, mapKey, wallpaperList.get(i).wallpaper_id, i + 1, dateTime));
                        }

                        wallpaperDao.insertAll(wallpaperList);

                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Wallpaper> data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<Wallpaper>> loadFromDb() {
                Utils.psLog("Load all wallpapers From DB.");

                return wallpaperDao.getWallpaperByKey(mapKey);

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Wallpaper>>> createCall() {
                Utils.psLog("Call all wallpapers webservice.");
                return psApiService.getDownloadedWallpaper(apiKey, Utils.checkUserId(loginUserId),wallpaperType, limit, offset);
            }

            @Override
            protected void onFetchFailed(String message) {
                Utils.psLog("Fetch Failed of all wallpapers");
                rateLimiter.reset(functionKey);
            }

        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> getNextPageDownloadList(String apiKey, String loginUserId, String limit, String offset, String wallpaperType) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();

        LiveData<ApiResponse<List<Wallpaper>>> apiResponse = psApiService.getDownloadedWallpaper(apiKey, Utils.checkUserId(loginUserId),wallpaperType, limit, offset);

        String dateTime = Utils.getDateTime();

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);


            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {

                    String mapKey = new WallpaperParamsHolder().getDownloadQueryHolder().getKeyForProductMap();

                    try {
                        db.runInTransaction(() -> {
                            if (response.body != null) {

                                int lastSortingPosition = db.wallpaperMapDao().getMaxSortingByValue(mapKey);

                                for (int i = 0; i < response.body.size(); i++) {
                                    lastSortingPosition += i;

                                    db.wallpaperMapDao().insert(new WallpaperMap(mapKey + response.body.get(i).wallpaper_id, mapKey, response.body.get(i).wallpaper_id, lastSortingPosition + i + 1, dateTime));
                                }
                                wallpaperDao.insertAll(response.body);
                            }

                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                    statusLiveData.postValue(Resource.success(true));
                });

            } else {
                statusLiveData.postValue(Resource.error(response.errorMessage, null));
            }

        });

        return statusLiveData;

    }

    //endregion


    //region Post touch count

    public LiveData<Resource<Wallpaper>> uploadTouchCountPostToServer(String wallpaperId, String user_login_id) {

        final MutableLiveData<Resource<Wallpaper>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            Response<Wallpaper> response;

            try {
                response = psApiService.setRawPostTouchCount(
                        Config.API_KEY, Utils.checkUserId(user_login_id), wallpaperId, Utils.checkUserId(user_login_id)).execute();

                ApiResponse<Wallpaper> apiResponse = new ApiResponse<>(response);

                if (apiResponse.isSuccessful()) {
//                    db.beginTransaction();
//                    try {
//
//                        if (apiResponse.body != null) {
//
//                            wallpaperDao.insert(apiResponse.body);
//                        }
//
//                        db.setTransactionSuccessful();
//
//                    } catch (Exception e) {
//                        Utils.psErrorLog("Error in uploadTouchCountPostToServer.", e);
//                    } finally {
//                        db.endTransaction();
//                    }

                    try {
                        db.runInTransaction(() -> {
                            if (apiResponse.body != null) {

                                wallpaperDao.insert(apiResponse.body);
                            }
                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                    statusLiveData.postValue(Resource.success(response.body()));
                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, response.body()));
                }
            } catch (IOException e) {
                Utils.psErrorLog("", e.getMessage());
                statusLiveData.postValue(Resource.error(e.getMessage(), null));
            }

        });

        return statusLiveData;
    }

    //endregion


    //region Post download count

    public LiveData<Resource<Wallpaper>> uploadDownloadCountPostToServer(String wallpaperId, String user_login_id) {

        final MutableLiveData<Resource<Wallpaper>> statusLiveData = new MutableLiveData<>();

        String mapKey = new WallpaperParamsHolder().getDownloadQueryHolder().getKeyForProductMap();

        String dateTime = Utils.getDateTime();

        appExecutors.networkIO().execute(() -> {

            Response<Wallpaper> response;

            try {
                response = psApiService.setRawPostDownloadCount(
                        Config.API_KEY, Utils.checkUserId(user_login_id), wallpaperId, Utils.checkUserId(user_login_id)).execute();

                ApiResponse<Wallpaper> apiResponse = new ApiResponse<>(response);

                downloadMaxSorting = db.wallpaperMapDao().getMaxSortingByValue(mapKey);

                if (downloadMaxSorting == 0) {
                    downloadMaxSorting = 1;
                }

                if (apiResponse.isSuccessful()) {

//                    db.beginTransaction();
//
//                    try {
//
//                        if (apiResponse.body != null) {
//
//                            wallpaperDao.insert(apiResponse.body);
//
//                            db.wallpaperMapDao().deleteSpecificItemByMapKeyAndId(mapKey, wallpaperId);
//
//                            db.wallpaperMapDao().insert(new WallpaperMap(mapKey + wallpaperId, mapKey, wallpaperId, downloadMaxSorting + 1, dateTime));
//                        }
//
//                        db.setTransactionSuccessful();
//
//                    } catch (Exception e) {
//                        Utils.psErrorLog("Error in uploadDownloadCountPostToServer.", e);
//                    } finally {
//                        db.endTransaction();
//                    }

                    try {
                        db.runInTransaction(() -> {
                            if (apiResponse.body != null) {

                                wallpaperDao.insert(apiResponse.body);

                                db.wallpaperMapDao().deleteSpecificItemByMapKeyAndId(mapKey, wallpaperId);

                                db.wallpaperMapDao().insert(new WallpaperMap(mapKey + wallpaperId, mapKey, wallpaperId, downloadMaxSorting + 1, dateTime));
                            }
                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                    statusLiveData.postValue(Resource.success(response.body()));
                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, response.body()));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), null));
            }

        });

        return statusLiveData;
    }

    //endregion


    //region Post Favourite Wallpaper

    public LiveData<Resource<Boolean>> uploadFavouritePostToServer(String wallpaperId, String login_user_id) {

        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        String mapKey = new WallpaperParamsHolder().getFavQueryHolder().getKeyForProductMap();

        Utils.psLog("Favourite Query is " + mapKey);

        String dateTime = Utils.getDateTime();

        appExecutors.networkIO().execute(() -> {

            try {

                try {
                    db.runInTransaction(() -> {
                        favMaxSorting = db.wallpaperMapDao().getMaxSortingByValue(mapKey);

                        isSelected = wallpaperDao.selectFavouriteById(wallpaperId);
                        if (isSelected.equals(Constants.ONE)) {
                            wallpaperDao.updateProductForFavById(wallpaperId, Constants.ZERO);

                            db.wallpaperMapDao().deleteSpecificItemByMapKeyAndId(mapKey, wallpaperId);

                        } else {
                            wallpaperDao.updateProductForFavById(wallpaperId, Constants.ONE);

                            db.wallpaperMapDao().insert(new WallpaperMap(mapKey + wallpaperId, mapKey, wallpaperId, favMaxSorting + 1, dateTime));
                        }

                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }

                // Call the API Service
                Response<Wallpaper> response;

                response = psApiService.setRawPostFavourite(Config.API_KEY, Utils.checkUserId(login_user_id), wallpaperId, Utils.checkUserId(login_user_id)).execute();

                // Wrap with APIResponse Class
                ApiResponse<Wallpaper> apiResponse = new ApiResponse<>(response);

                // If response is successful
                if (apiResponse.isSuccessful()) {

                    try {
                        db.runInTransaction(() -> {
                            if (apiResponse.body != null) {
                                wallpaperDao.insert(apiResponse.body);

                                if (apiResponse.body.is_favourited.equals(Constants.ONE)) {
                                    db.wallpaperMapDao().insert(new WallpaperMap(mapKey + wallpaperId, mapKey, apiResponse.body.wallpaper_id, favMaxSorting + 1, dateTime));

                                } else if (apiResponse.body.is_favourited.equals(Constants.ZERO)) {
                                    db.wallpaperMapDao().deleteSpecificItemByMapKeyAndId(mapKey, wallpaperId);
                                }
                            }
                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                    statusLiveData.postValue(Resource.success(apiResponse.getNextPage() != null));

                } else {

                    try {
                        db.runInTransaction(() -> {
                            isSelected = wallpaperDao.selectFavouriteById(wallpaperId);

                            if (isSelected.equals(Constants.ONE)) {
                                wallpaperDao.updateProductForFavById(wallpaperId, Constants.ZERO);

                                db.wallpaperMapDao().deleteSpecificItemByMapKeyAndId(mapKey, wallpaperId);

                            } else {
                                wallpaperDao.updateProductForFavById(wallpaperId, Constants.ONE);

                                db.wallpaperMapDao().insert(new WallpaperMap(mapKey + wallpaperId, mapKey, wallpaperId, favMaxSorting + 1, dateTime));
                            }
                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, false));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), false));
            }
        });

        return statusLiveData;
    }


    //region Post Rating

    public LiveData<Resource<Wallpaper>> uploadRatingToServer(String wallpaperId, String login_user_id, float rating) {

        final MutableLiveData<Resource<Wallpaper>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            try {

                try {
                    db.runInTransaction(() -> {
                        oldRating = wallpaperDao.selectRatingById(wallpaperId);
//
                        wallpaperDao.updateRatingById(rating, wallpaperId);

                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }

                // Call the API Service
                Response<Wallpaper> response;

                response = psApiService.setRawPostRating(Config.API_KEY, Utils.checkUserId(login_user_id), wallpaperId, Utils.checkUserId(login_user_id), String.valueOf(rating)).execute();

                // Wrap with APIResponse Class
                ApiResponse<Wallpaper> apiResponse = new ApiResponse<>(response);

                // If response is successful
                if (apiResponse.isSuccessful()) {

                    if(response.body() != null) {
                        wallpaperDao.updateRatingById(response.body().rating_count, wallpaperId);
                        statusLiveData.postValue(Resource.success(apiResponse.body));
                    }

                } else {

                    try {
                        db.runInTransaction(() -> wallpaperDao.updateRatingById(oldRating, wallpaperId));
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, null));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), null));
            }
        });

        return statusLiveData;
    }

    //endregion


    //region buy wallpaper

    public LiveData<Resource<Boolean>> uploadBuyingStatusToServer(String userId, int point, String symbol, String wallpaperId) {

        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            try {

                try {
                    db.runInTransaction(() -> {
                        buyStatus = wallpaperDao.getBuyingStatusById(wallpaperId);

                        if (buyStatus.equals(Constants.ZERO)) {
                            wallpaperDao.updateBuyingStatusById(wallpaperId, Constants.ONE);
                        }

                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }

                // Call the API Service
                Response<Wallpaper> response;

                response = psApiService.buyWallpaperRepo(Config.API_KEY, Utils.checkUserId(userId), userId, point, symbol, wallpaperId).execute();

                // Wrap with APIResponse Class
                ApiResponse<Wallpaper> apiResponse = new ApiResponse<>(response);

                // If response is successful
                if (apiResponse.isSuccessful()) {

                    statusLiveData.postValue(Resource.success(apiResponse.getNextPage() != null));

                    User user = db.userDao().getUserRawData(userId);
                    int totalPoint = Integer.parseInt(user.total_point);
                    int balance = totalPoint - point;

                    User updatedUser = new User(user.user_id,
                            user.user_is_sys_admin,
                            user.facebook_id,
                            user.google_id,
                            user.user_name,
                            user.user_email,
                            user.user_phone,
                            user.user_password,
                            user.user_about_me,
                            user.user_cover_photo,
                            user.user_profile_photo,
                            user.added_date,
                            user.like_count,
                            user.comment_count,
                            user.favourite_count,
                            String.valueOf(balance));

                    db.userDao().update(updatedUser);

                } else {

                    try {
                        db.runInTransaction(() -> {

                            if (wallpaperDao.getBuyingStatusById(wallpaperId).equals(Constants.ONE)) {
                                wallpaperDao.updateBuyingStatusById(wallpaperId, Constants.ZERO);
                            }

                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, false));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), false));
            }
        });

        return statusLiveData;
    }

    //endregion


    //region SYNC with BE

    public LiveData<Resource<Boolean>> deleteExtraWallpapers(int totalWallpaperCount) {

        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            try {
                db.runInTransaction(() -> {

                    int rowCount = db.wallpaperDao().getTotalRowCount();

                    if (rowCount > totalWallpaperCount) {
                        count = rowCount - totalWallpaperCount;

                        Utils.psErrorLog("Extra Rows are : ", String.valueOf(count));

                        db.wallpaperDao().deleteExtraRowCount(String.valueOf(rowCount - totalWallpaperCount));
                    }

                });
            } catch (Exception ex) {
                Utils.psErrorLog("Error at ", ex);
            }

            statusLiveData.postValue(Resource.success(true));


        });

        return statusLiveData;
    }

    //endregion


    //region upload and delete own wallpapers

    public LiveData<Resource<Wallpaper>> uploadWallpaper(String catId, String colorId, String wallpaperName, String types, String is_portrait, String is_landscape,
                                                         String is_square, String point, String searchTags, String userId, String wallpaperId, String token, String credit,String isGif, String isWallpaper) {

        final MutableLiveData<Resource<Wallpaper>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            String dataTime = Utils.getDateTime();

            try {
                // Call the API Service
                Response<Wallpaper> response;

                response = psApiService.uploadWallpaper(Config.API_KEY, catId, colorId, wallpaperName, types, is_portrait, is_landscape,
                        is_square, point, searchTags, userId, wallpaperId, token, credit, isGif, isWallpaper).execute();

                // Wrap with APIResponse Class
                ApiResponse<Wallpaper> apiResponse = new ApiResponse<>(response);

                // If response is successful
                if (apiResponse.isSuccessful()) {

                    try {
                        db.runInTransaction(() -> {
                            if (apiResponse.body != null) {

                                db.uploadWallpaperDao().insert(new UploadedWallpaper(apiResponse.body.wallpaper_id, 0, dataTime));
                                db.wallpaperDao().insert(apiResponse.body);
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

    public LiveData<Resource<Wallpaper>> uploadWallpaperImage(String filePath, String wallpaperId, String imgId) {

        //Init File
        MultipartBody.Part body = null;
        if (!filePath.equals("")) {
            File file = new File(filePath);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);

            // MultipartBody.Part is used to send also the actual file news_title
            body =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        }
        // add another part within the multipart request
        RequestBody idRB =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), wallpaperId);

        RequestBody imgIdRB =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), imgId);

        MultipartBody.Part finalBody = body;
        return new NetworkBoundResource<Wallpaper, Image>(appExecutors) {

            // Temp ResultType To Return

            @Override
            protected void saveCallResult(@NonNull Image item) {
                Utils.psLog("SaveCallResult");

                try {
                    db.runInTransaction(() -> {

                        Wallpaper wallpaper = wallpaperDao.getWallpaperObjectById(wallpaperId);
                        // update user data
                        wallpaper.default_photo = item;

                        wallpaperDao.insert(wallpaper);

                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Wallpaper data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<Wallpaper> loadFromDb() {

                return wallpaperDao.getWallpaperById(wallpaperId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Image>> createCall() {
                Utils.psLog("Call API Service to upload image.");

                return psApiService.uploadWallpaperImage(Config.API_KEY, idRB, imgIdRB, finalBody);
            }

            @Override
            protected void onFetchFailed(String message) {
                Utils.psLog("Fetch Failed of uploading image.");
            }
        }.asLiveData();
    }


    public LiveData<Resource<Boolean>> deleteUserWallpaperById(String wallpaperId, String userId) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();

        appExecutors.networkIO().execute(() -> {

            try {
                db.runInTransaction(() -> {

                    this.wallpaper = db.wallpaperDao().getWallpaperObjectById(wallpaperId);

                    db.wallpaperDao().deleteById(wallpaperId);

                });
            } catch (Exception ex) {
                Utils.psErrorLog("Error at ", ex);
            }

            try {
                Response<ApiStatus> response1 = psApiService.deleteWallpaperById(Config.API_KEY, wallpaperId, userId).execute();

                ApiResponse<ApiStatus> apiResponse = new ApiResponse<>(response1);


                if (apiResponse.isSuccessful()) {

                    statusLiveData.postValue(Resource.success(true));

                } else {

                    appExecutors.diskIO().execute(() -> {

                        try {
                            db.runInTransaction(() -> db.wallpaperDao().insert(this.wallpaper));
                        } catch (Exception ex) {
                            Utils.psErrorLog("Error at ", ex);
                        }

                        statusLiveData.postValue(Resource.error(apiResponse.errorMessage, null));

                    });
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return statusLiveData;
    }

    //endregion


}
