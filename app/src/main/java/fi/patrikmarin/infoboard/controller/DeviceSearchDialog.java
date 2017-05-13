package fi.patrikmarin.infoboard.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.ArrayList;

/**
 * Created by marinp1 on 5.1.2017.
 */

public class DeviceSearchDialog extends DialogFragment {

    public static InfoboardController infoboardController;

    public DeviceSearchDialog() {

    }

    public static DeviceSearchDialog newInstance(ArrayList<String> discoveredDevices, ArrayList<String> deviceIds, InfoboardController controller) {

        infoboardController = controller;

        DeviceSearchDialog fragment = new DeviceSearchDialog();
        Bundle args = new Bundle();
        args.putString("title", "Device list");
        args.putStringArrayList("devices", discoveredDevices);
        args.putStringArrayList("deviceIds", deviceIds);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String title = getArguments().getString("title");

        builder.setTitle(title);

        final ArrayList<String> devices = getArguments().getStringArrayList("devices");
        final ArrayList<String> deviceIds = getArguments().getStringArrayList("deviceIds");

        builder.setItems(devices.toArray(new String[devices.size()]),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    DeviceSearchDialog.infoboardController.bluetoothCommunicator.SERVER_ADDRESS = deviceIds.get(which);

                    System.out.println("DEVICE " + devices.get(which) + "(" + deviceIds.get(which) + ") SELECTED");

                    DeviceSearchDialog.infoboardController.SEARCH_FOR_SERVER = false;

                    dialog.dismiss();
                }
            });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    /*
    private void updateLoop() {

        final Thread listener;

        listener = new Thread() {
            public void run() {
                while (bluetoothCommunicator.isDiscovering()) {

                    try {

                        Thread.sleep(100);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                interrupt();

            }
        };

        listener.start();
    }
    */
}