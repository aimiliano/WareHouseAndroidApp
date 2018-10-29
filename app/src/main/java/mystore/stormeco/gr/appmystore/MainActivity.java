package mystore.stormeco.gr.appmystore;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String event_bus_search_event_id="y_event_bus_id";

    TextView sleected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sleected = findViewById(R.id.tv_sel);
        if (MysStoreApp.getInstance().getLocalPref("shop_id").equals("")) {
            sleected.setText(R.string.selected_none);
        }
        else{
            sleected.setText(MysStoreApp.getInstance().getLocalPref("shop_name"));
        }

        findViewById(R.id.one).setOnClickListener(this);
        findViewById(R.id.two).setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.setting:

                getAllStores();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getAllStores() {

        initWaitDialog();
        new getStores(event_bus_search_event_id);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMessage event) {
        if (waitingDialog!=null) waitingDialog.dismiss();

        if (event.getId().equals(event_bus_search_event_id)) {
            final HashMap<String, Object> response = event.getData();

            if ((boolean) event.getData().get("status")) {


                final ArrayList<String> str = new ArrayList<>();

                str.add((String) response.get("shop_name_1"));
                str.add((String) response.get("shop_name_2"));

                new MaterialDialog.Builder(this)
                        .title(R.string.title)
                        .items(str)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                //get the Obj
                                String name_shop= str.get(which);
                                MysStoreApp.getInstance().setLocalPref("shop_name",name_shop);
                                sleected.setText(name_shop);

                                MysStoreApp.getInstance().setLocalPref("shop_id",String.valueOf(response.get("shop_id_"+(which+1))));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (MysStoreApp.getInstance().getLocalPref("shop_id").equals("")) {

            new MaterialDialog.Builder(MainActivity.this)
                    .title(R.string.app_name)
                    .content(R.string.no_shop_sleected)
                    .positiveText(R.string.ok)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

            return;
        }



        switch (view.getId()){
            case R.id.one:

                Intent in = new Intent(this,ScanActtivity.class);
                in.putExtra("type_id","0");
                startActivity(in);
                break;

            case R.id.two:
                Intent i2n = new Intent(this,ScanActtivity.class);
                i2n.putExtra("type_id","1");
                startActivity(i2n);
                break;
        }
    }
}
