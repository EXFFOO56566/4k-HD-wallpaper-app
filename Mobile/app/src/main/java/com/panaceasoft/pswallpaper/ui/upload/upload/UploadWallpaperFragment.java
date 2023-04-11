package com.panaceasoft.pswallpaper.ui.upload.upload;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdRequest;
import com.panaceasoft.pswallpaper.Config;
import com.panaceasoft.pswallpaper.R;
import com.panaceasoft.pswallpaper.binding.FragmentDataBindingComponent;
import com.panaceasoft.pswallpaper.databinding.FragmentUploadWallpaperBinding;
import com.panaceasoft.pswallpaper.ui.common.PSFragment;
import com.panaceasoft.pswallpaper.utils.AutoClearedValue;
import com.panaceasoft.pswallpaper.utils.Constants;
import com.panaceasoft.pswallpaper.utils.PSDialogMsg;
import com.panaceasoft.pswallpaper.utils.Utils;
import com.panaceasoft.pswallpaper.viewmodel.wallpaper.WallpaperViewModel;
import com.panaceasoft.pswallpaper.viewobject.Wallpaper;


public class UploadWallpaperFragment extends PSFragment {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private WallpaperViewModel wallpaperViewModel;
    private boolean selected = false;
    private String imagePath = "";
    private PSDialogMsg psDialogMsg;
    private String catId = "";
    private String colorId = "";
    private String clickedCheckBox = "";
    private ProgressDialog progressDialog;
    private String wallpaperId = "";
    private boolean isAlreadyShowedSuccessDialog = true;
//    private String[] items;
    private String type = "1";



    @VisibleForTesting
    private AutoClearedValue<FragmentUploadWallpaperBinding> binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentUploadWallpaperBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload_wallpaper, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        if (getActivity() != null) {
            wallpaperId = getActivity().getIntent().getStringExtra(Constants.WALLPAPER);
        }

        if (Config.SHOW_ADMOB && connectivity.isConnected()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            binding.get().adView3.loadAd(adRequest);
        } else {
            binding.get().adView3.setVisibility(View.GONE);
        }


        return binding.get().getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE__SEARCH_FRAGMENT && resultCode == Constants.RESULT_CODE__SEARCH_WITH_CATEGORY) {

            catId = data.getStringExtra(Constants.INTENT__CAT_ID);
            binding.get().categoryValueTextView.setText(data.getStringExtra(Constants.INTENT__CAT_NAME));

        } else if (requestCode == Constants.REQUEST_CODE__SEARCH_FRAGMENT && resultCode == Constants.RESULT_CODE__SEARCH_WITH_COLOR) {

            colorId = data.getStringExtra(Constants.INTENT__COLOR_ID);
            binding.get().colorValueTextView.setText(data.getStringExtra(Constants.INTENT__COLOR_NAME));

        } else if(requestCode == Constants.REQUEST_CODE__SEARCH_FRAGMENT && resultCode == Constants.RESULT_CODE__WALLPAPER_FREE){

            binding.get().wallpaperTypesTextView.setText(getString(R.string.free));
            type = "1";
        }
        else if(requestCode == Constants.REQUEST_CODE__SEARCH_FRAGMENT && resultCode == Constants.RESULT_CODE__WALLPAPER_PREMIUM){

            binding.get().wallpaperTypesTextView.setText(getString(R.string.premium));
            type = "2";
        }



        else if (requestCode == Utils.RESULT_LOAD_IMAGE_CATEGORY && resultCode == Utils.RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            if (getContext() != null) {

                dataBindingComponent.getFragmentBindingAdapters().bindFullImageUri(binding.get().imageView5, selectedImage);

                selected = true;

                if (getActivity() != null && selectedImage != null) {
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);

                    if (cursor != null) {
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imagePath = cursor.getString(columnIndex);
                        cursor.close();
                    }
                }
            }
        }
    }

    @Override
    protected void initUIAndActions() {

        if (wallpaperId != null) {
            if (!wallpaperId.isEmpty()) {
                editMode();
            }
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);

        psDialogMsg = new PSDialogMsg(getActivity(), false);

        binding.get().categoryValueTextView.setOnClickListener(v -> navigationController.navigateToCategorySelectionActivity(getActivity(), ""));

        binding.get().wallpaperTypesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.get().colorValueTextView.setOnClickListener(v -> navigationController.navigateToColorSelectionActivity(getActivity(), ""));

        binding.get().wallpaperTypesTextView.setOnClickListener(v -> navigationController.navigateToWallpapersTypeActivity(getActivity(),""));

        binding.get().choosePhotoButton.setOnClickListener(v -> navigationController.getImageFromGallery(getActivity()));

        binding.get().portraitCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (!clickedCheckBox.equals(Constants.PORTRAIT)) {
                unCheckTheClickedCheckBox();
            }

            clickedCheckBox = Constants.PORTRAIT;
        });

        binding.get().landscapeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (!clickedCheckBox.equals(Constants.LANDSCAPE)) {
                unCheckTheClickedCheckBox();
            }

            clickedCheckBox = Constants.LANDSCAPE;
        });

        binding.get().squareCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (!clickedCheckBox.equals(Constants.SQUARE)) {
                unCheckTheClickedCheckBox();
            }

            clickedCheckBox = Constants.SQUARE;
        });

        binding.get().uploadPhotoButton.setOnClickListener(v -> {

//            String type = String.valueOf(binding.get().spinner2.getSelectedItemPosition() + 1);

            if (imgUploadCondition()) {
                if (type.equals("1")) {
                    if (checkUserInput()) {

                        isAlreadyShowedSuccessDialog = false;
                        wallpaperViewModel.setUploadWallpaperobj(catId, colorId, binding.get().nameEditText.getText().toString(),
                                type,
                                changeBooleanToNumber(binding.get().portraitCheckBox.isChecked()),
                                changeBooleanToNumber(binding.get().landscapeCheckBox.isChecked()),
                                changeBooleanToNumber(binding.get().squareCheckBox.isChecked()),
                                binding.get().pointEditText.getText().toString(),
                                binding.get().searchTagEditText.getText().toString(),
                                loginUserId,
                                wallpaperId,
                                wallpaperViewModel.deviceToken,
                                binding.get().creditValueTextView.getText().toString(),
                                changeBooleanToNumberForGif(binding.get().gifRadioButton.isChecked()),
                                changeBooleanToNumberForGif(binding.get().imageWallpaperRadioButton.isChecked()));

                        progressDialog.show();
                    }

                } else if (type.equals("2")) {
                    if (checkUserInput()) {

                        isAlreadyShowedSuccessDialog = false;
                        wallpaperViewModel.setUploadWallpaperobj(catId, colorId, binding.get().nameEditText.getText().toString(),
                                type,
                                changeBooleanToNumber(binding.get().portraitCheckBox.isChecked()),
                                changeBooleanToNumber(binding.get().landscapeCheckBox.isChecked()),
                                changeBooleanToNumber(binding.get().squareCheckBox.isChecked()),
                                binding.get().pointEditText.getText().toString(),
                                binding.get().searchTagEditText.getText().toString(),
                                loginUserId,
                                wallpaperId, wallpaperViewModel.deviceToken,
                                binding.get().creditValueTextView.getText().toString(),
                                changeBooleanToNumberForGif(binding.get().gifRadioButton.isChecked()),
                                changeBooleanToNumberForGif(binding.get().imageWallpaperRadioButton.isChecked()));

                        progressDialog.show();
                    }
                }
            }
        });


        RadioGroup rg = binding.get().imageTypeRadioGroup;

        rg.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.imageWallpaperRadioButton:
                    fieldsForImageWallpaper();
                    binding.get().wallpaperTypesTextView.setEnabled(true);
                    break;
                case R.id.gifRadioButton:
                    clearTheFieldsForGif();
//                    items = new String[]{getString(R.string.free)};
////                    if (binding.get().spinner2.getCount() > 0) {
////                        binding.get().spinner2.setSelection(0);
////                    }
                    type = "1";
                    binding.get().wallpaperTypesTextView.setText(R.string.free);

                    binding.get().wallpaperTypesTextView.setEnabled(false);
                    break;

            }
        });
    }

    @Override
    protected void initViewModels() {

        wallpaperViewModel = new ViewModelProvider(this, viewModelFactory).get(WallpaperViewModel.class);

    }

    @Override
    protected void initAdapters() {
//        items = new String[]{getString(R.string.free), getString(R.string.premium)};
//
//        if (this.getActivity() != null) {
//            ArrayAdapter<String> nvAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_main_layout, items);
//            //ArrayAdapter<String> nvAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
//
//            AutoClearedValue<ArrayAdapter<String>> adapter = new AutoClearedValue<>(this, nvAdapter);
//
//            adapter.get().setDropDownViewResource(R.layout.item_spinner_layout);
//
//            binding.get().spinner2.setAdapter(adapter.get());
//        }
    }

    @Override
    protected void initData() {

        getDeviceToken();

        wallpaperViewModel.getUploadWallpaperData().observe(this, result -> {

            if (result != null) {

                switch (result.status) {
                    case SUCCESS:
                        if (result.data != null) {

                            wallpaperId = result.data.wallpaper_id;

                            if (selected) {
                                wallpaperViewModel.setUploadWallpaperImageObj(imagePath, result.data.wallpaper_id, result.data.default_photo.img_id);
                                selected = false;
                            } else {

                                showSuccessDialog();

                            }

                        }

                        break;

                    case ERROR:
                        progressDialog.cancel();
                        psDialogMsg.showErrorDialog(getString(R.string.error_message__image_cannot_upload), getString(R.string.app__ok));
                        psDialogMsg.show();
                        break;
                }
            }

        });

        wallpaperViewModel.getUploadWallpaperImageData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {
                    case SUCCESS:
                        showSuccessDialog();
                        clearTheFields();
                        break;

                    case ERROR:
                        progressDialog.cancel();
                        psDialogMsg.showErrorDialog(getString(R.string.error_message__image_cannot_upload), getString(R.string.app__ok));
                        psDialogMsg.show();
                        break;
                }

            }
        });

        wallpaperViewModel.getWallpaperById().observe(this, result -> {

            if (result != null) {

                switch (result.status) {

                    case SUCCESS:
                        if (result.data != null) {
                            bindData(result.data);
                        }

                        wallpaperViewModel.setLoadingState(false);
                        break;

                    case LOADING:

                        if (result.data != null) {
                            bindData(result.data);
                        }
                        break;

                }

            }
        });

    }

    private void showSuccessDialog() {
        if (!isAlreadyShowedSuccessDialog) {
            Toast.makeText(getActivity(), getString(R.string.success_message__upload_successful), Toast.LENGTH_SHORT).show();
            progressDialog.cancel();
            isAlreadyShowedSuccessDialog = true;
        }
    }

    private void getDeviceToken() {
        wallpaperViewModel.deviceToken = pref.getString(Utils.NOTI_TOKEN, "");
    }

    private void bindData(Wallpaper wallpaper) {
        binding.get().nameEditText.setText(wallpaper.wallpaper_name);
        binding.get().searchTagEditText.setText(wallpaper.wallpaper_search_tags);
//        if (binding.get().spinner2 != null && binding.get().spinner2.getCount() > 1) {
//            binding.get().spinner2.setSelection(Integer.parseInt(wallpaper.types) - 1);
//        }
        if(wallpaper.types.equals("1")){
            binding.get().wallpaperTypesTextView.setText(getString(R.string.free));
        }else {
            binding.get().wallpaperTypesTextView.setText(getString(R.string.premium));
        }

        if(wallpaper.is_gif.equals("0")){
            binding.get().imageWallpaperRadioButton.setChecked(true);
        }else {
            binding.get().gifRadioButton.setChecked(true);
        }
        if (wallpaper.types.equals("1")) {
            binding.get().pointEditText.setText("");
        } else {
            binding.get().pointEditText.setText(String.valueOf(wallpaper.point));
        }
        binding.get().categoryValueTextView.setText(wallpaper.category.cat_name);
        binding.get().colorValueTextView.setText(wallpaper.color.name);
        binding.get().portraitCheckBox.setChecked(wallpaper.is_portrait.equals("1"));
        binding.get().landscapeCheckBox.setChecked(wallpaper.is_landscape.equals("1"));
        binding.get().squareCheckBox.setChecked(wallpaper.is_square.equals("1"));
        this.catId = wallpaper.category.cat_id;
        this.colorId = wallpaper.color_id;

        if (wallpaper.default_photo != null && !wallpaper.default_photo.img_path.equals("")) {

            dataBindingComponent.getFragmentBindingAdapters().bindFullImage(binding.get().imageView5, wallpaper.default_photo.img_path);
        }
    }

    private String changeBooleanToNumber(Boolean value) {
        if (value) {
            return "1";
        } else {
            return "0";
        }
    }

    private String changeBooleanToNumberForGif(Boolean value) {
        if (value) {
            return "1";
        } else {
            return "0";
        }
    }

    private boolean imgUploadCondition() {

        if (wallpaperId != null) {
            if (wallpaperId.equals("")) {
                if (!selected) {
                    PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
                    psDialogMsg.showErrorDialog(getString(R.string.error_message__img_not_selected), getString(R.string.message__ok_close));
                    psDialogMsg.show();

                    return false;

                }
            }

        }

        if (!connectivity.isConnected()) {

            PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
            psDialogMsg.showErrorDialog(getString(R.string.error_message__no_internet), getString(R.string.message__ok_close));
            psDialogMsg.show();

            return false;
        }

        return true;
    }

    private void unCheckTheClickedCheckBox() {
        if (!clickedCheckBox.isEmpty()) {
            switch (clickedCheckBox) {
                case Constants.PORTRAIT:
                    binding.get().portraitCheckBox.setChecked(false);

                    break;
                case Constants.LANDSCAPE:

                    binding.get().landscapeCheckBox.setChecked(false);

                    break;
                case Constants.SQUARE:

                    binding.get().squareCheckBox.setChecked(false);

                    break;
            }
        }
    }

    private boolean checkUserInput() {
        if (binding.get().nameEditText.getText().toString().isEmpty()) {
            psDialogMsg.showErrorDialog(getString(R.string.wallpaper_error__required_name), getString(R.string.app__ok));
            psDialogMsg.show();
            return false;
        }

        if (binding.get().wallpaperTypesTextView.getText().toString().isEmpty()){
            psDialogMsg.showErrorDialog(getString(R.string.wallpaper_error__required_wallpaper_type), getString(R.string.app__ok));
            psDialogMsg.show();
            return false;
        }
//        String type = String.valueOf(binding.get().spinner2.getSelectedItemPosition() + 1);
        if (type.equals("2")) {
            if (binding.get().pointEditText.getText().toString().isEmpty()) {
                psDialogMsg.showErrorDialog(getString(R.string.wallpaper_error__required_point), getString(R.string.app__ok));
                psDialogMsg.show();
                return false;
            }
        }

        if (type.equals("1")) {
            if (!binding.get().pointEditText.getText().toString().equals("")) {
                psDialogMsg.showErrorDialog(getString(R.string.wallpaper_error__not_required_point), getString(R.string.app__ok));
                psDialogMsg.show();
                return false;
            }
        }

        if (catId != null && catId.equals("")) {
            Utils.psLog("No CatId");
            psDialogMsg.showErrorDialog(getString(R.string.wallpaper_error__required_category), getString(R.string.app__ok));
            psDialogMsg.show();
            return false;

        }

        if (colorId.equals("")) {
            Utils.psLog("No Color");
            psDialogMsg.showErrorDialog(getString(R.string.wallpaper_error__required_color), getString(R.string.app__ok));
            psDialogMsg.show();
            return false;
        }

        if (!binding.get().gifRadioButton.isChecked()) {
            if ((!binding.get().squareCheckBox.isChecked())
                    && (!binding.get().landscapeCheckBox.isChecked())
                    && (!binding.get().portraitCheckBox.isChecked())) {
                psDialogMsg.showErrorDialog(getString(R.string.wallpaper_error__required_image_type), getString(R.string.app__ok));
                psDialogMsg.show();
                Utils.psLog("Select Checkbox");
                return false;
            }
        }
        if (!binding.get().pointEditText.getText().toString().isEmpty()) {
            if (Integer.parseInt(binding.get().pointEditText.getText().toString()) <= 0) {
                psDialogMsg.showErrorDialog(getString(R.string.upload_photo__point_greater_0), getString(R.string.app__ok));
                psDialogMsg.show();
                Utils.psLog("Select Checkbox");
                return false;
            }
        }

        if ((!binding.get().gifRadioButton.isChecked()) && imagePath.toLowerCase().trim().contains(Constants.IS_GIF)) {
            psDialogMsg.showErrorDialog(getString(R.string.wallpaper_error__gif_radio_on), getString(R.string.app__ok));
            psDialogMsg.show();
            return false;

        }

        if (binding.get().gifRadioButton.isChecked() && !imagePath.toLowerCase().trim().contains(Constants.IS_GIF)) {
            psDialogMsg.showErrorDialog(getString(R.string.wallpaper_error__image_wallpaper_on), getString(R.string.app__ok));
            psDialogMsg.show();
            return false;
        }

        return true;
    }

    private void clearTheFields() {
        binding.get().nameEditText.setText("");
        binding.get().searchTagEditText.setText("");
        binding.get().pointEditText.setText("");
        binding.get().categoryValueTextView.setText(getString(R.string.wallpaper_category_name));
        binding.get().colorValueTextView.setText(getString(R.string.wallpaper_select_color));
        binding.get().creditValueTextView.setText("");
        binding.get().portraitCheckBox.setChecked(false);
        binding.get().landscapeCheckBox.setChecked(false);
        binding.get().squareCheckBox.setChecked(false);

        binding.get().imageView5.setImageDrawable(ContextCompat.getDrawable(binding.get().getRoot().getContext(), R.drawable.placeholder_image));
    }

    private void editMode() {
        wallpaperViewModel.setWallpaperByIdObj(wallpaperId, loginUserId);
    }

    private void clearTheFieldsForGif() {
        binding.get().portraitCheckBox.setChecked(false);
        binding.get().landscapeCheckBox.setChecked(false);
        binding.get().squareCheckBox.setChecked(false);
        binding.get().pointEditText.setText("");
        binding.get().portraitCheckBox.setEnabled(false);
        binding.get().landscapeCheckBox.setEnabled(false);
        binding.get().squareCheckBox.setEnabled(false);
        binding.get().pointEditText.setEnabled(false);

    }

    private void fieldsForImageWallpaper() {
        binding.get().portraitCheckBox.setEnabled(true);
        binding.get().landscapeCheckBox.setEnabled(true);
        binding.get().squareCheckBox.setEnabled(true);
        binding.get().pointEditText.setEnabled(true);

    }
}
