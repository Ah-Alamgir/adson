package org.nguyenhoanglam.imagepicker.ui.camera;


import org.nguyenhoanglam.imagepicker.model.Image;

import java.util.List;

public interface OnImageReadyListener {
    void onImageReady(List<Image> images);
}
