package fi.patrikmarin.infoboard.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by marinp1 on 31.12.2016.
 */

// FIXME: Fix threads
public class BluetoothCommunicator {

    InfoboardController controller;
    BluetoothCommunicator bluetoothCommunicator = this;

    private BluetoothAdapter bluetoothAdapter = null;
    public BluetoothSocket bluetoothSocket = null;

    public boolean isConnected = false;

    private Thread listener;

    // Stream to send commands to server
    private OutputStream outputStream = null;

    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // TODO: Prompt for device selection instead of hardcoding here
    private static String SERVER_ADDRESS = "30:3A:64:7C:BD:88";

    public BluetoothCommunicator(InfoboardController controller) {
        this.controller = controller;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initListener();
        selectRemoteDevice();
        // TODO: Prompt if bluetooth is not enabled
    }

    public void selectRemoteDevice() {
        // TODO: Prompt for remote device
        // TODO: Try to connect to remote, if successful save address
    }

    public boolean connect() {

        //FIXME: Not working
        controller.changeStatus(1);

        if (listener.isAlive()) listener.interrupt();

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
            listener.start();
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

    private void initListener() {

        listener = new Thread() {
            public void run() {
                while (true) {
                    boolean isConnected = bluetoothCommunicator.bluetoothSocket.isConnected();

                    if (isConnected != bluetoothCommunicator.isConnected) {
                        // TODO: Do something
                    }

                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

    }

}
