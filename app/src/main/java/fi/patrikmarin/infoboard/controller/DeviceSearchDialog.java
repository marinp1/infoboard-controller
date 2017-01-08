package fi.patrikmarin.infoboard.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by marinp1 on 5.1.2017.
 */

public class DeviceSearchDialog extends DialogFragment {

    public DeviceSearchDialog() {

    }

    public static DeviceSearchDialog newInstance(ArrayList<String> discoveredDevices, ArrayList<String> deviceIds) {
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String title = getArguments().getString("title");

        builder.setTitle(title);

        final ArrayList<String> devices = getArguments().getStringArrayList("devices");
        final ArrayList<String> deviceIds = getArguments().getStringArrayList("deviceIds");

        builder.setItems((String[]) devices.toArray(),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println(deviceIds.get(which));
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
}