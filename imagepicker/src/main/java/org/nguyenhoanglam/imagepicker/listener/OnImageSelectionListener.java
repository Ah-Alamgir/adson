package org.nguyenhoanglam.imagepicker.listener;

import org.nguyenhoanglam.imagepicker.model.Image;

import java.util.List;

/**
 * Created by hoanglam on 8/18/17.
 */

public interface OnImageSelectionListener {
    void onSelectionUpdate(List<Image> images);
}
