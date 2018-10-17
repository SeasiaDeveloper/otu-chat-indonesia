package com.eklanku.otuChat.ui.activities.barcode;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.views.barcode.CameraSourcePreview;
import com.eklanku.otuChat.utils.barcode.BarcodeGraphicTracker;
import com.eklanku.otuChat.utils.barcode.CentralBarcodeFocusingProcessor;
import com.eklanku.otuChat.utils.helpers.ServiceManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import rx.Observable;

import static com.eklanku.otuChat.utils.ValidationUtils.isValidWebQRCode;

public class WebQRCodeActivity extends BaseLoggableActivity implements BarcodeGraphicTracker.BarcodeUpdateListener {
    private static final String TAG = WebQRCodeActivity.class.getSimpleName();
    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    private CameraSource cameraSource;
    private CameraSourcePreview preview;

    private boolean autoFocus = true;

    public static void start(Context context) {
        Intent intent = new Intent(context, WebQRCodeActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        preview = findViewById(R.id.preview);
        createCameraSource(autoFocus);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_web_qrcode;
    }

    private void createCameraSource(boolean autoFocus) {
        Context context = getApplicationContext();
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        barcodeDetector.setProcessor(new CentralBarcodeFocusingProcessor(barcodeDetector, new BarcodeGraphicTracker(this)));

        if (!barcodeDetector.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f);

        // make sure that auto focus is an available option
        builder = builder.setAutoFocusEnabled(autoFocus);

        cameraSource = builder
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (preview != null) {
            preview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preview != null) {
            preview.release();
        }
    }

    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (cameraSource != null) {
            try {
                preview.start(cameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onBarcodeDetected(Barcode barcode) {
        String webToken = barcode.rawValue;
        Log.d(TAG, "onBarcodeDetected");
        if (isValidWebQRCode(webToken)) {
            ServiceManager.getInstance().upgradeWebSessionToken(webToken)
                    .onErrorResumeNext(e -> Observable.empty())
                    .subscribe();
            finish();
        }
    }
}
