/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.cmu.mastersofflyingobjects.passingsync;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends Fragment {

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private PassingSyncApplication app = null;

    // Layout Views
    private ListView mSiteswapList;
    private EditText mSiteswapEditor;
    private Button mStartButton;
    private Button mStartSyncButton;
    private Button mStartRandomButton;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;
    /**
     * The Handler that gets information back from the BluetoothService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_STARTSITESWAP:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
////                    mSiteswapListProvider.add("Me:  " + writeMessage);
//                    break;
//                case Constants.MESSAGE_READ:
//                    byte[] readBuf = (byte[]) msg.obj;
//                    // construct a string from the valid bytes in the buffer
//                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    mSiteswapListProvider.add(mConnectedDeviceName + ":  " + readMessage);
//
//                    MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.p7);
//                    mediaPlayer.start();
                    String siteswap = (String) msg.obj;
                    Intent intent = new Intent(getActivity(), RunSiteswapClientActivity.class);
                    intent.putExtra(MainActivity.EXTRA_SITESWAP, siteswap);
                    startActivity(intent);

                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mSiteswapListProvider;
    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                MainActivity.startSiteswap(getBluetoothService(), getActivity(), 'W', message, message);
            }
            return true;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // If the adapter is null, then Bluetooth is not supported
        if (getBluetoothAdapter() == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        setupSiteswapList();

        // If BT is not on, request that it be enabled.
        if (getBluetoothAdapter() != null && !getBluetoothAdapter().isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (app.getBluetoothAdapter() == null) {
            //TODO disable start button
        }
        // Initialize the BluetoothService to perform bluetooth connections

        getBluetoothService().addHandler(mHandler);
    }

    private BluetoothService getBluetoothService() {
        return app.getBluetoothService();
    }

    private BluetoothAdapter getBluetoothAdapter() {
        assert app != null;
        if (app == null) return null;
        return app.getBluetoothAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getBluetoothService() != null) {
            getBluetoothService().stop();
            getBluetoothService().removeHandler(mHandler);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (getBluetoothService() != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (getBluetoothService().getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                getBluetoothService().start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mSiteswapList = (ListView) view.findViewById(R.id.siteswaplist);
        mSiteswapEditor = (EditText) view.findViewById(R.id.siteswapeditor);
        mStartButton = (Button) view.findViewById(R.id.button_send);
        mStartSyncButton = (Button) view.findViewById(R.id.button_sync);
        mStartRandomButton = (Button) view.findViewById(R.id.button_random);
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupSiteswapList() {

        // Initialize the array adapter for the conversation thread
        mSiteswapListProvider = new ArrayAdapter<String>(getActivity(), R.layout.message);
        mSiteswapListProvider.add("56789");
        mSiteswapListProvider.add("6789a");
        mSiteswapListProvider.add("456789a");
        mSiteswapListProvider.add("86777");
        mSiteswapListProvider.add("867");
        mSiteswapListProvider.add("972");
        mSiteswapListProvider.add("567");
        mSiteswapListProvider.add("75666");
        mSiteswapListProvider.add("7747746677466");
        mSiteswapListProvider.add("777928892296626");

        mSiteswapList.setAdapter(mSiteswapListProvider);
        mSiteswapList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Initialize the compose field with a listener for the return key
        mSiteswapEditor.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mStartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                if (null != view) {
                    TextView textView = (TextView) view.findViewById(R.id.siteswapeditor);
                    String siteswap = textView.getText().toString();
                    MainActivity.startSiteswap(getBluetoothService(), getActivity(), 'W', siteswap, siteswap);
                }

            }
        });

        // Initialize the send button with a listener that for click events
        mStartSyncButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                if (null != view) {
                    TextView textView = (TextView) view.findViewById(R.id.siteswapeditor);
                    String sequence = textView.getText().toString();
                    MainActivity.startSiteswap(getBluetoothService(), getActivity(), 'S', sequence, sequence);
                }

            }
        });

        // Initialize the send button with a listener that for click events
        mStartRandomButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
//                startSiteswap('R', "random");
                Intent intent = new Intent(getActivity(), RandomSettingActivity.class);
                startActivity(intent);

            }
        });

//        // Initialize the send button with a listener that for click events
//        mStartRandomSwButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Send a message using content of the edit text widget
//                startSiteswap('T', "random siteswap");
//
//            }
//        });


        mSiteswapList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mSiteswapEditor.setText(mSiteswapListProvider.getItem(position));
            }
        });


    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (getBluetoothAdapter().getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }


    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        if (actionBar == null) return;
        actionBar.setSubtitle(resId);
    }

    private ActionBar getActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity == null)
            return null;
        return activity.getSupportActionBar();
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
//                    setupSiteswapList();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = getBluetoothAdapter().getRemoteDevice(address);
        // Attempt to connect to the device
        getBluetoothService().connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }

    public void setApp(PassingSyncApplication applicationContext) {
        this.app = applicationContext;
    }
}
