package com.ILoveDeshi.Android_Source_Code.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import org.nguyenhoanglam.imagepicker.model.Config;
import org.nguyenhoanglam.imagepicker.model.Image;
import org.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.util.ArrayList;

public class ProImage extends BottomSheetDialogFragment {

    private Function function;
    private String imageProfile;
    private ArrayList<Image> galleryImages;
    private int REQUESTGALLERYPICKER = 100;
    private ConstraintLayout conRemove, conImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pro_image, container, false);

        function = new Function(getActivity());
        if (function.isRtl()) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        conRemove = view.findViewById(R.id.con_remove_proImage);
        conImage = view.findViewById(R.id.con_image_proImage);

        conRemove.setOnClickListener(v -> {
            dismiss();
        });

        conImage.setOnClickListener(v -> chooseGalleryImage());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK && requestCode == REQUESTGALLERYPICKER) {
            galleryImages = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            imageProfile = galleryImages.get(0).getPath();
            dismiss();
        }
    }

    private void chooseGalleryImage() {
        try {
            ImagePicker.with(this)
                    .setFolderMode(true)
                    .setFolderTitle("Album")
                    .setImageTitle(getResources().getString(R.string.app_name))
                    .setStatusBarColor(function.imageGalleryToolBar())
                    .setToolbarColor(function.imageGalleryToolBar())
                    .setProgressBarColor(function.imageGalleryProgressBar())
                    .setMultipleMode(true)
                    .setMaxSize(1)
                    .setShowCamera(false)
                    .start();
        } catch (Exception e) {
            Log.e("error", e.toString());
        }
    }

}
