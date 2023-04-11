package com.panaceasoft.pswallpaper.ui.notification;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.binding.FragmentDataBindingComponent;
import com.panaceasoft.pswallpaper.databinding.FragmentNotificationSettingBinding;
import com.panaceasoft.pswallpaper.ui.common.PSFragment;
import com.panaceasoft.pswallpaper.utils.AutoClearedValue;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewmodel.common.NotificationViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
/**
 * NotificationSettingFragment
 */
public class NotificationSettingFragment extends PSFragment {


    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    @VisibleForTesting
    private AutoClearedValue<FragmentNotificationSettingBinding> binding;


    private NotificationViewModel notificationViewModel;

    //endregion


    //region Override Methods

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentNotificationSettingBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification_setting, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }

    @Override
    protected void initUIAndActions() {

        //fadeIn Animation
        fadeIn(binding.get().getRoot());

        binding.get().notiSwitch.setChecked(notificationViewModel.pushNotificationSetting);
        binding.get().notiSwitch.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b) -> {
               Utils.psLog("Checked : " + b);

               if(connectivity.isConnected()) {

                   if (!notificationViewModel.isLoading) {
                       updateNotificationSetting(b);
                   } else {
                       Toast.makeText(getContext(), "Please Wait! Still updating notification setting.", Toast.LENGTH_SHORT).show();
                       binding.get().notiSwitch.setChecked(!b);
                   }
               }else {
                   Toast.makeText(getContext(), R.string.error_message__no_internet, Toast.LENGTH_SHORT).show();
                   binding.get().notiSwitch.setChecked(!b);
               }
           });
    }

    @Override
    protected void initViewModels() {
        notificationViewModel = new ViewModelProvider(this, viewModelFactory).get(NotificationViewModel.class);
    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

        try {

            if(pref != null) {
                updateNotificationMessage();
            }

        }catch (NullPointerException ne){
            Utils.psErrorLog("Null Pointer Exception.", ne);
        }catch(Exception e){
            Utils.psErrorLog("Error in getting notification flag data.", e);
        }

        notificationViewModel.getLoadingStatus().observe(this, status -> {
            if(status == null) {
                Utils.psLog("Status is null");
                notificationViewModel.isLoading = false;
            }else {
                Utils.psLog("Status Update : " + status.isRunning());
                notificationViewModel.isLoading = status.isRunning();
                String error = status.getErrorMessageIfNotHandled();
                if(error != null) {
                    notificationViewModel.isLoading = false;
                    Utils.psLog("Error in Status : " + error);
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();

        if(pref != null) {
            updateNotificationMessage();
        }
    }

    private void updateNotificationMessage() {

        notificationViewModel.pushNotificationSetting = pref.getBoolean(Utils.NOTI_SETTING, false);
        binding.get().notiSwitch.setChecked(notificationViewModel.pushNotificationSetting);
        String message = pref.getString(Utils.NOTI_MSG, "");

        if (!message.equals("")) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                binding.get().messageTextView.setText(Html.fromHtml(message, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL));
            } else {
                binding.get().messageTextView.setText(Html.fromHtml(message));
            }


        }
    }

    @VisibleForTesting
    private void updateNotificationSetting(Boolean setting) {

        if (getActivity() != null) {

            if (notificationViewModel.pushNotificationSetting != setting) {

                if (setting) {
                    notificationViewModel.registerNotification(getContext(), Utils.PLATFORM, "");

                } else {
                    notificationViewModel.unregisterNotification(getContext(), Utils.PLATFORM, "");

                }
                notificationViewModel.pushNotificationSetting = setting;
            }
        }
    }

    //endregion


}
