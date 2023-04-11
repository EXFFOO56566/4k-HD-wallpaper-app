package com.panaceasoft.pswallpaper.repository.contactus.task;



import com.panaceasoft.pswallpaper.Config;
import com.panaceasoft.pswallpaper.api.ApiResponse;
import com.panaceasoft.pswallpaper.api.PSApiService;
import com.panaceasoft.pswallpaper.db.PSCoreDb;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewobject.ApiStatus;
import com.panaceasoft.pswallpaper.viewobject.common.Resource;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.BindingAdapter;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import retrofit2.Response;

/**
 * Created by Panacea-Soft on 7/2/18.
 * Contact Email : teamps.is.cool@gmail.com
 * Website : http://www.panacea-soft.com
 */

public class PostContactUsTask implements Runnable {

    //region Variables
    private final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

    private final PSApiService service;
    private final PSCoreDb db;
    private final String contactName;
    private final String contactEmail;
    private final String contactDesc;
    private final String contactPhone;

    public PostContactUsTask(PSApiService service, PSCoreDb db, String contactName, String contactEmail, String contactDesc, String contactPhone) {
        this.service = service;
        this.db = db;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactDesc = contactDesc;
        this.contactPhone = contactPhone;
    }

    //region Override Methods

    @Override
    public void run() {
        try {

            // Call the API Service
            Response<ApiStatus> response = service.rawPostContact(Config.API_KEY, contactName, contactEmail, contactDesc, contactPhone).execute();

            // Wrap with APIResponse Class
            ApiResponse<ApiStatus> apiResponse = new ApiResponse<>(response);

            Utils.psLog("apiResponse " + apiResponse);
            // If response is successful
            if (apiResponse.isSuccessful()) {

                statusLiveData.postValue(Resource.success(true));
            } else {

                statusLiveData.postValue(Resource.error(apiResponse.errorMessage, true));
            }
        } catch (Exception e) {
            statusLiveData.postValue(Resource.error(e.getMessage(), true));
        }
    }

    //endregion

    //region public methods

    /**
     * This function will return Status of Process
     * @return statusLiveData
     */
    public LiveData<Resource<Boolean>> getStatusLiveData() {
        return statusLiveData;
    }

    //endregion

}
