package org.nguyenhoanglam.imagepicker.ui.imagepicker;

import org.nguyenhoanglam.imagepicker.model.Folder;
import org.nguyenhoanglam.imagepicker.model.Image;
import org.nguyenhoanglam.imagepicker.ui.common.MvpView;

import java.util.List;

/**
 * Created by hoanglam on 8/17/17.
 */

public interface ImagePickerView extends MvpView {

    void showLoading(boolean isLoading);

    void showFetchCompleted(List<Image> images, List<Folder> folders);

    void showError(Throwable throwable);

    void showEmpty();

    void showCapturedImage(List<Image> images);

    void finishPickImages(List<Image> images);

}