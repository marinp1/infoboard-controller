package fi.patrikmarin.infoboard.controller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import static android.R.drawable.arrow_down_float;
import static android.R.drawable.arrow_up_float;

public class InfoboardController extends AppCompatActivity {

    // ==================== BLUETOOTH CONNECTION ====================================
    BluetoothCommunicator bluetoothCommunicator;
    public static Boolean SEARCH_FOR_SERVER = true;

    // ========================= STATUSBAR VARIABLES =================================
    ExpandableLinearLayout statusbarContent;
    Button statusbar;
    ViewFlipper statusbarViewHandler;
    // 0 = no connection, 1 = connecting, 2 = connected
    int statusID = 0;

    int MY_PERMISSIONS_REQUEST_FINE_LOCATION = -1;

    // ====================== WEATHER VARIABLES =======================
    Button weatherSettingsButton;
    ExpandableLinearLayout weatherSettingsContent;

    @Override
    protected void onDestroy() {
        bluetoothCommunicator.stopReceiving();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infoboard_controller);

        // ========================== TOOLBAR INIT =====================================
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ========================= BLUETOOTH INIT ===================================


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                case PackageManager.PERMISSION_DENIED:

                    ActivityCompat.requestPermissions(bluetoothCommunicator.controller,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                    bluetoothCommunicator = new BluetoothCommunicator(this);

                    break;

                case PackageManager.PERMISSION_GRANTED:
                    bluetoothCommunicator = new BluetoothCommunicator(this);
                    break;
            }
        }

        // ========================== STATUSBAR INIT =====================================

        statusbar = (Button) findViewById(R.id.statusbar);
        statusbarViewHandler = (ViewFlipper)findViewById(R.id.statusbarViewHandler);
        statusbarViewHandler.setMeasureAllChildren(false);
        statusbarViewHandler.setDisplayedChild(0);

        statusbarContent = (ExpandableLinearLayout) findViewById(R.id.statusbarContent);

        // ========================== WEATHER INIT ======================================
        weatherSettingsButton = (Button) findViewById(R.id.weatherSettingsButton);

        weatherSettingsContent = (ExpandableLinearLayout) findViewById(R.id.weatherSettingsContent);
        weatherSettingsContent.collapse();

        // =========================== FINAL INIT ==============================

        // Add listener when the layout is loaded so children size may be calculated correctly
        final RelativeLayout mainView = (RelativeLayout) findViewById(R.id.content_infoboard_controller);
        mainView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // only want to do this once
                        mainView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        statusbarContent.initLayout();
                        weatherSettingsContent.initLayout();
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_infoboard_controller, menu);
        return true;
    }

    // FIXME: Remove debug settings
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.setNoConnection) {
            changeStatus(0);
            return true;
        } else if (id == R.id.setConnecting) {
            changeStatus(1);
            return true;
        } else if (id == R.id.setConnected) {
            changeStatus(2);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void connectToInfoboard(View view) {

        if (SEARCH_FOR_SERVER) {

            FragmentManager fm = getSupportFragmentManager();
            DeviceSearchDialog deviceSearchDialog = DeviceSearchDialog.newInstance(bluetoothCommunicator.foundDevices, bluetoothCommunicator.foundIds, this);
            deviceSearchDialog.show(getSupportFragmentManager(), "fm_name");

        } else {
            bluetoothCommunicator.connect();
        }

    }

    // ============================= STATUSBAR METHODS =======================================

    /**
     * Handles expand action on statusbar
     * @param view the main view of the application
     */
    public void statusbarClick(View view) {
        // Don't expand if the application is connecting
        if (statusID == 1) {
            return;
        }

        // Otherwise toggle view
        if (statusbarContent.isExpanded()) {
            collapseStatus();
        } else {
            expandStatus();
        }
    }

    private void collapseStatus() {
        statusbarContent.collapse();
        statusbar.setCompoundDrawablesWithIntrinsicBounds(0, 0, arrow_down_float, 0);
    }

    private void expandStatus() {
        statusbarContent.expand();
        statusbar.setCompoundDrawablesWithIntrinsicBounds(0, 0, arrow_up_float, 0);
    }

    /**
     * Changes statusbar based on given status
     * @param ID connection status, 0 = no connection, 1 = connecting, 2 = connected
     */
    public void changeStatus(int ID) {

        if (ID < 0 || ID > 2) {
            System.out.println("Invalid status ID");
        } else {

            statusID = ID;

            // Always collapse statusbar on status change
            collapseStatus();

            if (ID == 0) {
                changeStatusBackground(ContextCompat.getColor(this, R.color.colorStatusNoConnection));
                statusbar.setCompoundDrawablesWithIntrinsicBounds(0, 0, arrow_down_float, 0);
                statusbar.setText(R.string.statusNoConnection);
                statusbarViewHandler.setDisplayedChild(0);
            } else if (ID == 1) {
                changeStatusBackground(ContextCompat.getColor(this, R.color.colorStatusConnecting));
                statusbar.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                statusbar.setText(R.string.statusConnecting);
            } else if (ID == 2) {
                changeStatusBackground(ContextCompat.getColor(this, R.color.colorStatusConnected));
                statusbar.setCompoundDrawablesWithIntrinsicBounds(0, 0, arrow_down_float, 0);
                statusbar.setText(R.string.statusConnected);
                statusbarViewHandler.setDisplayedChild(1);
            }

            // Recalculate children size
            statusbarContent.initLayout();
        }
    }

    private void changeStatusBackground(@ColorInt int color) {
        statusbar.setBackgroundColor(color);
        statusbarContent.setBackgroundColor(color);
    }

    // =============== WEATHER PANEL ====================

    public void toggleWeatherPanel(View view) {
        weatherSettingsContent.toggle();
        weatherSettingsContent.initLayout();
    }
}
