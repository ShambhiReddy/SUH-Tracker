package com.suh.itboy.tracker.Service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class AddressIntentService extends IntentService {
    private static final String TAG = AddressIntentService.class.getSimpleName();

    protected ResultReceiver mReceiver;
    private Messenger messenger;

    public AddressIntentService() {
        super("AddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent ");
        if (intent != null) {
            final String action = intent.getAction();
            mReceiver = intent.getParcelableExtra(Constants.RESULT_RECEIVER);
//            if (Constants.ACTION_GET_ADDRESS.equals(action)) {
                final double longitude = intent.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0);
                final double latitude = intent.getDoubleExtra(Constants.EXTRA_LATITUDE, 0);
            messenger = intent.getParcelableExtra("messenger");
                handleActionGetAddress(longitude, latitude);
//            }

        }
    }

    /**
     * Handle action in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetAddress(double longitude, double latitude) {
        Log.d(TAG, "handleActionGetAddress");

        String errorMessage = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            int loop = 0;
            do {
                addresses = geocoder.getFromLocation(longitude, latitude, 1);
                loop++;
            } while (addresses.size() == 0 && loop < 10);

        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = "Service Not Available";
            Log.e(TAG, "Exception: " + e.toString());
        } catch (IllegalArgumentException e) {
            //inalid longitude or latitude
            errorMessage = "Inavlid longitude or latitude: long: " + longitude + " Latitude: " + latitude;
        }
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty())
                errorMessage = "No address was found.";

            deliverResultToReceiver(Constants.RESULT_FAILURE, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }

            deliverResultToReceiver(Constants.RESULT_SUCCESS, TextUtils.join(System.getProperty("line.Separator"), addressFragments));
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {

        Message msg = Message.obtain();


        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        bundle.putInt(Constants.RESULT_KEY, resultCode);
        msg.setData(bundle);
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mReceiver.send(resultCode, bundle);
    }

    public final class Constants {
        public static final int RESULT_FAILURE = 0;
        public static final int RESULT_SUCCESS = 1;

        public static final String RESULT_DATA_KEY = "com.suh.itboy.tracker.Service.RESULT_DATA_KEY";
        public static final String ACTION_GET_ADDRESS = "com.suh.itboy.tracker.Service.action.ADDRESS";

        public static final String RESULT_KEY = "com.suh.itboy.tracker.Service.RESULT_KEY";

        public static final String EXTRA_LONGITUDE = "com.suh.itboy.tracker.Service.extra.LONGITUDE";
        public static final String EXTRA_LATITUDE = "com.suh.itboy.tracker.Service.extra.LATITUDE";

        public static final String RESULT_RECEIVER = "com.suh.itboy.tracker.Receiver";
    }

}
