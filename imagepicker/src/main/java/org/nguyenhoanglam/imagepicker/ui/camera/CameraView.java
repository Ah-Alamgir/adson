package org.nguyenhoanglam.imagepicker.ui.camera;

import org.nguyenhoanglam.imagepicker.model.Image;
import org.nguyenhoanglam.imagepicker.ui.common.MvpView;

import java.util.List;

/**
 * Created by hoanglam on 8/22/17.
 */

public interface CameraView extends MvpView {

    void finishPickImages(List<Image> images);
}
