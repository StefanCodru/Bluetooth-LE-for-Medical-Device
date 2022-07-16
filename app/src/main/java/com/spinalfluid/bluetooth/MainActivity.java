package com.spinalfluid.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.metrics.LogSessionId;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainMain";

    int count = 0;

    ConstraintLayout getBluetoothLayout;
    ConstraintLayout viewStatsLayout;

    Button showDevicesButton;
    ListView devicesListView;
    ProgressBar progressBar;

    TextView daDateTV;
    TextView timeTV;
    ImageView checkImage;
    ImageView xImage;


    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> devicesNames;
    ArrayList<String> devicesMacAddress;


    BroadcastReceiver myReceiver;

    BluetoothLeScanner bluetoothLeScanner;
    BluetoothAdapter bluetoothAdapter;
    BluetoothManager bluetoothManager;

    BluetoothGatt bluetoothGatt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initializeViews();
        setupBluetoothManager();


    }


    public void initializeViews() {
        Log.i(TAG, "INITIALIZED VIEWS: ");

        getBluetoothLayout = findViewById(R.id.getBluetoothLayout);
        viewStatsLayout = findViewById(R.id.viewStatsLayout);
        viewStatsLayout.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        daDateTV = findViewById(R.id.dadate);
        timeTV = findViewById(R.id.time);
        checkImage = findViewById(R.id.checkicon);
        xImage = findViewById(R.id.xicon);

        showDevicesButton = findViewById(R.id.showDevicesButton);
        devicesListView = findViewById(R.id.showDevicesListView);
        devicesNames = new ArrayList<>();
        devicesMacAddress = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, devicesNames);
        devicesListView.setAdapter(arrayAdapter);


        showDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "BUTTON CLICKED: ");
                onStartScannerButtonClick();
            }
        });

        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                connectToDevice(i);
            }
        });

    }


    public void onStartScannerButtonClick() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            bluetoothLeScanner.startScan(scanCallback);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        bluetoothLeScanner.stopScan(scanCallback);
                    }
                }
            }, 5000);

            Log.i(TAG, "SCANNER STARTED: ");
        }
    }


    public void setupBluetoothManager() {
        Log.i(TAG, "SETTINGS UP BLUETOOTH MANAGER: ");

        bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivityForResult(intent, 1);
        }

        checkLocationPermissions();
    }


    public void checkLocationPermissions() {
        Log.i(TAG, "CHECKING LOCATION PERMISSIONS: ");

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 4);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 5);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 6);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 7);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 8);
        }
    }


    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {


                String name = "";

                if (result.getDevice().getName() == null) {
                    name = "Unknown";
                } else {
                    name = result.getDevice().getName();
                }

                if (!name.equals("Unknown") && !devicesNames.contains(name)) {
                    if (result.getDevice().getAddress() != null) {
                        devicesNames.add(name);
                        devicesMacAddress.add(result.getDevice().getAddress());

                        Log.i(TAG, "onScanResult: " + name + " AND UUIDs = " + Arrays.toString(result.getDevice().getUuids()));
                        arrayAdapter.notifyDataSetChanged();
                    }

                } else if (name.equals("Unknown")) {
                    if (result.getDevice().getAddress() != null) {
                        devicesMacAddress.add(result.getDevice().getAddress());
                        devicesNames.add(name);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }


            }


            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.i(TAG, "onBatchScanResults: ");
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.i(TAG, "onScanFailed: " + errorCode);
            super.onScanFailed(errorCode);
        }
    };


    public void connectToDevice(int position) {
        Log.i(TAG, "connectToDevice: " + devicesMacAddress.get(position));

        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(devicesMacAddress.get(position));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            bluetoothGatt = bluetoothDevice.connectGatt(this, false, gattCallback);

            progressBar.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    viewStatsLayout.setVisibility(View.VISIBLE);
                    viewStatsLayout.setAlpha(0f);

                    getBluetoothLayout.animate().alpha(0f).setDuration(1000);
                    viewStatsLayout.animate().alpha(1f).setDuration(1000);

                    getBluetoothLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                    xImage.setVisibility(View.GONE);
                }
            }, 2000);

        }
    }


    // public static String HM10_SERIAL_DATA =
    //    "0000ffe1-0000-1000-8000-00805f9b34fb";
    // public static String CLIENT_CHARACTERISTIC_CONFIGURATION_DESCRIPTOR =
    //    "00002902-0000-1000-8000-00805f9b34fb";
    // public final static String BROADCAST_NAME_SERVICES_DISCOVERED =
    //    "com.example.bluetooth.le.services_discovered";
    // public final static String BROADCAST_NAME_TX_CHARATERISTIC_CHANGED =
    //     UNIQUE_PACKAGE_NAME + "com.example.bluetooth.le.tx_characteristic_changed";
    // public static final String BROADCAST_NAME_CONNECTION_UPDATE =
    //     UNIQUE_PACKAGE_NAME + ".connection_update";
    // public static final String EXTRAS_CONNECTION_STATE = "CONNECTION_STATE";
    // public static final String EXTRAS_SERVICES_DISCOVERED = "SERVICES_DISCOVERED";
    // public static final String EXTRAS_TX_DATA = "TX_DATA";

    // This file variables:
    List<BluetoothGattService> m_gattServices;
    BluetoothGattCharacteristic m_characteristicTX;
    // private Context m_context = calling activity context ('this' keyword);


    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        gatt.discoverServices();
                    }
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {


            if (status == BluetoothGatt.GATT_SUCCESS) {

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    m_gattServices = gatt.getServices();
                    final UUID desiredUuid = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
                    String foundSuccess = "Communication Characteristic Not Found";
                    for (BluetoothGattService gattService : m_gattServices) {
                        BluetoothGattCharacteristic desiredCharacteristic = gattService.getCharacteristic(desiredUuid);
                        if (desiredCharacteristic != null) {

                            m_characteristicTX = desiredCharacteristic;
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                bluetoothGatt.setCharacteristicNotification(m_characteristicTX, true);
                                BluetoothGattDescriptor descriptor = m_characteristicTX.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                bluetoothGatt.writeDescriptor(descriptor);
                            }

                            break;
                        }
                    }
                    if (m_characteristicTX != null) {
                        foundSuccess = "Communication Characteristic Found";
                    }

                    Log.i(TAG, "FOUND SUCCESS: " + foundSuccess);
                }







            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            byte[] rawData = characteristic.getValue();
            String txData = new String(rawData).trim(); // toString does not work, but new String()

            Log.i(TAG, "onCharacteristicRead: " + txData);
        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            byte[] rawData = characteristic.getValue();
            String txData = new String(rawData).trim(); // toString does not work, but new String()

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(txData.equals("R")){
                        xImage.setVisibility(View.VISIBLE);
                        checkImage.setVisibility(View.GONE);

                        daDateTV.setText("Just now");
                        timeTV.setText("Just now");
                    } else {
                        xImage.setVisibility(View.GONE);
                        checkImage.setVisibility(View.VISIBLE);
                    }


                }
            });

            Log.i(TAG, "onCharacteristicCHANGED: " + txData);
        }





    };






}