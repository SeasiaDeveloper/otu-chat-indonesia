package com.eklanku.otuChat.utils.barcode;

import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.FocusingProcessor;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

public class CentralBarcodeFocusingProcessor extends FocusingProcessor<Barcode> {

    public CentralBarcodeFocusingProcessor(Detector<Barcode> detector, Tracker<Barcode> tracker) {
        super(detector, tracker);
    }

    @Override
    public int selectFocus(Detector.Detections<Barcode> detections) {
        SparseArray<Barcode> barcodes = detections.getDetectedItems();
        Frame.Metadata meta = detections.getFrameMetadata();
        int id = -1;

        for (int i = 0; i < barcodes.size(); ++i) {
            int tempId = barcodes.keyAt(i);
            Barcode barcode = barcodes.get(tempId);
            int dx = barcode.getBoundingBox().centerX();
            int dy = barcode.getBoundingBox().centerY();
            if (isBarcodeInCenter(meta, dx, dy)) {
                id = tempId;
            }
        }
        return id;
    }

    private boolean isBarcodeInCenter(Frame.Metadata meta, int dx, int dy) {
        int xPart = meta.getWidth() / 3;
        int dxLeft = meta.getWidth() - xPart;
        int dxRight = xPart;
        int yPart = meta.getHeight() / 3;
        int dyTop = meta.getHeight() - yPart;
        int dyBottom = yPart;
        return isInXRange(dx, dxLeft, dxRight) && (isInYRange(dy, dyTop, dyBottom));
    }

    private boolean isInXRange(int dx, int dxLeft, int dxRight) {
        return dx < dxLeft && dx > dxRight;
    }

    private boolean isInYRange(int dy, int dyTop, int dyBottom) {
        return dy < dyTop && dy > dyBottom;
    }
}