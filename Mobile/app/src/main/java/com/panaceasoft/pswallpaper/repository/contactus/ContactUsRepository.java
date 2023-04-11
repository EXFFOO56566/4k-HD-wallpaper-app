package com.panaceasoft.pswallpaper.repository.contactus;


import com.panaceasoft.pswallpaper.AppExecutors;
import com.panaceasoft.pswallpaper.api.PSApiService;
import com.panaceasoft.pswallpaper.db.PSCoreDb;
import com.panaceasoft.pswallpaper.repository.common.PSRepository;
import com.panaceasoft.pswallpaper.repository.contactus.task.PostContactUsTask;
import com.panaceasoft.pswallpaper.viewobject.common.Resource;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;

/**
 * Created by Panacea-Soft on 7/2/18.
 * Contact Email : teamps.is.cool@gmail.com
 * Website : http://www.panacea-soft.com
 */

public class ContactUsRepository extends PSRepository {

    @Inject
    ContactUsRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db) {
        super(psApiService, appExecutors, db);

    }

    /**
     * Post Contact Us
     * @param apiKey APIKey to access Web Service
     * @param contactName Name
     * @param contactEmail Email
     * @param contactDesc Desc
     * @return Status of Post
     */
    public LiveData<Resource<Boolean>> postContactUs(String apiKey, String contactName, String contactEmail, String contactDesc, String contactPhone) {

        PostContactUsTask postContactUsTask = new PostContactUsTask(
                psApiService,  db, contactName, contactEmail, contactDesc, contactPhone);

        appExecutors.networkIO().execute(postContactUsTask);

        return postContactUsTask.getStatusLiveData();

    }

}
