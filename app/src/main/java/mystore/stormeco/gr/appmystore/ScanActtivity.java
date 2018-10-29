package mystore.stormeco.gr.appmystore;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture;
import com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic;
import com.google.android.gms.vision.barcode.Barcode;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

import xyz.belvi.mobilevisionbarcodescanner.BarcodeRetriever;

public class ScanActtivity extends AppCompatActivity implements BarcodeRetriever {

    String Type_id;
    private String event_id="my_send_to";
    BarcodeCapture barcodeCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_acttivity);
        Type_id = getIntent().getStringExtra("type_id");

         barcodeCapture = (BarcodeCapture) getSupportFragmentManager().findFragmentById(R.id.barcode);
         barcodeCapture.setRetrieval(this);
    }

    // for one time scan
    @Override
    public void onRetrieved(final Barcode barcode) {
        Log.d("ScanActtivity", "Barcode read: " + barcode.displayValue);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                AlertDialog.Builder builder = new AlertDialog.Builder(ScanActtivity.this)
//                        .setTitle("SCAN OK")
//                        .setMessage(barcode.displayValue);

                barcodeCapture.stopScanning();

                initWaitDialog();

                new sendBarcode(barcode.rawValue,MysStoreApp.getInstance().getLocalPref("shop_id"),Type_id,event_id);


//                builder.show().setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialogInterface){
//
//                    }
//                });
            }
        });


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMessage event) {
        if (waitingDialog!=null){
            waitingDialog.dismiss();
        }
        barcodeCapture.stopScanning();

        if (event.getId().equals(event_id)) {
            final HashMap<String, Object> response = event.getData();

            if ((boolean) event.getData().get("status")) {

                new MaterialDialog.Builder(ScanActtivity.this)
                        .title(R.string.app_name)
                        .content((String) event.getData().get("message"))
                        .positiveText(R.string.ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                            }
                        })
                        .show();
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    MaterialDialog waitingDialog;
    protected void initWaitDialog() {
        if (waitingDialog==null){
            waitingDialog = new MaterialDialog.Builder(this)
                    .title(R.string.app_name)
                    .content(R.string.waiting)
                    .progress(true, 0)
                    .show();

            waitingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {


                }
            });
        }
        else if (!waitingDialog.isShowing()) {
            waitingDialog.show();
        }

    }

    // for multiple callback
    @Override
    public void onRetrievedMultiple(final Barcode closetToClick, final List<BarcodeGraphic> barcodeGraphics) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                String message = "Code selected : " + closetToClick.displayValue + "\n\nother " +
//                        "codes in frame include : \n";
//                for (int index = 0; index < barcodeGraphics.size(); index++) {
//                    Barcode barcode = barcodeGraphics.get(index).getBarcode();
//                    message += (index + 1) + ". " + barcode.displayValue + "\n";
//                }
//                AlertDialog.Builder builder = new AlertDialog.Builder(ScanActtivity.this)
//                        .setTitle("code retrieved")
//                        .setMessage(message);
//                builder.show();
//            }
//        });

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {
        // when image is scanned and processed
    }

    @Override
    public void onRetrievedFailed(String reason) {
        // in case of failure

    }

    @Override
    public void onPermissionRequestDenied() {

    }
}
