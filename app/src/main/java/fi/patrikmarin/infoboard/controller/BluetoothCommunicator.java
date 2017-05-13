package fi.patrikmarin.infoboard.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by marinp1 on 31.12.2016.
 */

// FIXME: Fix threads
public class BluetoothCommunicator {

    InfoboardController controller;
    BluetoothCommunicator bluetoothCommunicator;

    private BluetoothAdapter bluetoothAdapter = null;
    public BluetoothSocket bluetoothSocket = null;
    public BluetoothServerSocket bluetoothServerSocket = null;

    public boolean isConnected = false;

    ArrayList<String> foundDevices = new ArrayList<String>();
    ArrayList<String> foundIds = new ArrayList<String>();

    // Stream to send commands to server
    private OutputStream outputStream = null;

    boolean isDiscovering() {
        return bluetoothAdapter.isDiscovering();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (!device.getName().equals("null")) {
                    foundDevices.add(device.getName());
                    foundIds.add(device.getAddress());
                }

                System.out.println("Added device " + device.getName());

            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                bluetoothAdapter.cancelDiscovery();
                System.out.println("Discovery finished");
            }

        }
    };

    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // TODO: Prompt for device selection instead of hardcoding here
    public String SERVER_ADDRESS = "30:3A:64:7C:BD:88";

    public BluetoothCommunicator(InfoboardController controller) {
        this.controller = controller;
        bluetoothCommunicator = this;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (controller.MY_PERMISSIONS_REQUEST_FINE_LOCATION == -1) {
            System.out.println("Permissions are required.");
            //TODO: Inform user
        }

        bluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        controller.registerReceiver(mReceiver, filter);
    }


    public void stopReceiving() {
        controller.unregisterReceiver(mReceiver);
    }


    public void selectRemoteDevice() {
        // TODO: Prompt for remote device
        // TODO: Try to connect to remote, if successful save address
    }

    public boolean connect() {

        //FIXME: Not working
        controller.changeStatus(1);

        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(SERVER_ADDRESS);

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(SPP_UUID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bluetoothAdapter.cancelDiscovery();

        try {
            bluetoothSocket.connect();
            isConnected = true;
            controller.changeStatus(2);
            return true;
        } catch (Exception e) {
            e.printStackTrace();

            // Try to close the socket
            try {
                bluetoothSocket.close();
                controller.changeStatus(0);
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            return false;
        }
    }

}
