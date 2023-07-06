package com.lycoo.honceh.Setting.settingWifi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lycoo.commons.util.LogUtils;
import com.lycoo.honceh.R;
import com.lycoo.honceh.Setting.settingWifi.preference.WifiInfoPreference;
import com.lycoo.honceh.Setting.settingWifi.adapter.WifiListAdapter;


import java.util.ArrayList;
import java.util.List;


public class WifiActivity extends AppCompatActivity implements PreferenceManager.OnPreferenceTreeClickListener {
    private static final String TAG = WifiActivity.class.getSimpleName();
    private Context mContext = this;
    private WifiManager wifiManager;
    private SwitchCompat wifiEnable;
    private TextView emptyWifi;
    private RecyclerView wifiView;
    private List<ScanResult> mWifiList;
    private List<ScanResult> results;
    private WifiListAdapter wifiListAdapter;
    private View dialogView;
    private WifiInfoPreference wifiInfoPreference;
    private PreferenceScreen preferenceScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        init();
//        scanWifi();
        connectWifi();

    }

    private void connectWifi() {
        wifiListAdapter = new WifiListAdapter(mWifiList, wifiManager);
        wifiListAdapter.setOnItemClickListener(new WifiListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(boolean isConnect, View view, int position, WifiInfo isConnectInfo, ScanResult scanResult) {
                if (isConnect) {
                    Toast.makeText(getApplicationContext(), "Wifi is Connected.......", Toast.LENGTH_SHORT).show();
                    showWifiInfoDialog(isConnectInfo);
                } else {
                    showWifiConnectDialog(scanResult);
                }
            }
        });

        wifiView.setAdapter(wifiListAdapter);
//        wifiView.getChildAt(0).requestFocus();
    }

    /**
     * 显示已连接WiFi的信息
     *
     * @param isConnectInfo Create by HonceH by 23/5/6
     */
    private void showWifiInfoDialog(WifiInfo isConnectInfo) {
        if (dialogView.getParent() != null) {
            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
        }
        while (preferenceScreen.getPreferenceCount() > 0) {
            Preference preference = preferenceScreen.getPreference(0);
            preferenceScreen.removePreference(preference);
        }
        preferenceScreen.removeAll();
        Preference IpPreference = new Preference(this);
        IpPreference.setTitle("TP地址");
        IpPreference.setSummary(intToIp(isConnectInfo.getIpAddress()));
        preferenceScreen.addPreference(IpPreference);
        Preference RssiPreference = new Preference(this);
        RssiPreference.setTitle("信号强度");
        int level = isConnectInfo.getRssi();
        if (level >= -50) {
            RssiPreference.setSummary("强");
        } else if (level >= -70) {
            RssiPreference.setSummary("中");
        } else {
            RssiPreference.setSummary("弱");
        }
        preferenceScreen.addPreference(RssiPreference);
        Preference StatusPreference = new Preference(this);
        StatusPreference.setTitle("状态信息");
        StatusPreference.setSummary("已连接");
        preferenceScreen.addPreference(StatusPreference);
        Preference LinkSpeedPreference = new Preference(this);
        LinkSpeedPreference.setTitle("连接速度");
        LinkSpeedPreference.setSummary(isConnectInfo.getLinkSpeed() + "Mbps");
        preferenceScreen.addPreference(LinkSpeedPreference);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isConnectInfo.getSSID());
        builder.setView(dialogView);
        builder.setPositiveButton("取消保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isConnecttoWifi(isConnectInfo);
                LogUtils.info(TAG, "wifi取消连接");
                wifiListAdapter.notifyDataSetChanged();
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    /**
     * wifi取消保存
     *
     * @param isConnectInfo
     */
    private void isConnecttoWifi(WifiInfo isConnectInfo) {
        int networkId = isConnectInfo.getNetworkId();
        wifiManager.disableNetwork(networkId);
        wifiManager.disconnect();
    }


    /**
     * 将ip地址格式化
     *
     * @param ipAddress
     * @return Create by HonceH by 23/5/6
     */
    private static String intToIp(int ipAddress) {
        return (ipAddress & 0xFF) + "." +
                ((ipAddress >> 8) & 0xFF) + "." +
                ((ipAddress >> 16) & 0xFF) + "." +
                ((ipAddress >> 24) & 0xFF);
    }

    /**
     * 连接WiFi
     *
     * @param scanResult
     * @param password   Create by HonceH by 23/5/6
     */
    private void connectToWifi(ScanResult scanResult, String password) {

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + scanResult.SSID + "\"";
        wifiConfig.preSharedKey = "\"" + password + "\"";

        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    private void init() {
        wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        dialogView = LayoutInflater.from(this).inflate(R.layout.wifiinfo, null);
        results=new ArrayList<>();
        wifiInfoPreference = (WifiInfoPreference) getSupportFragmentManager().findFragmentById(R.id.wifi_info_fragment);
        preferenceScreen = wifiInfoPreference.findPreference("wifi-info");
        wifiEnable = (SwitchCompat) findViewById(R.id.switch_wifi);
        emptyWifi = (TextView) findViewById(R.id.tv_empty_wifi);
        wifiView = (RecyclerView) findViewById(R.id.wifi_view);
        setwifiEnable();
        wifiView.setLayoutManager(new LinearLayoutManager(this));
        if (wifiManager.isWifiEnabled() == false) {
            Toast.makeText(getApplicationContext(), "Wifi is disabled... We need to enable it", Toast.LENGTH_SHORT).show();
        }
        mWifiList = new ArrayList<>();
        registerWifiReceiver();
    }

    /**
     * 注册WiFi监听器
     */
    private void registerWifiReceiver() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }

    /**
     * 设置wifi按钮，打开或关闭WiFi
     * Create by HonceH by 23/5/6
     */
    private void setwifiEnable() {
        boolean isWifiEnabled = wifiManager.isWifiEnabled();
        if (isWifiEnabled) {
            wifiEnable.setChecked(true);
            wifiView.setVisibility(View.VISIBLE);
            emptyWifi.setVisibility(View.GONE);
        } else {
            wifiEnable.setChecked(false);
            wifiView.setVisibility(View.GONE);
            emptyWifi.setVisibility(View.VISIBLE);
        }
        wifiEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 打开WiFi并显示列表
                    wifiManager.setWifiEnabled(true);
//                    scanWifi();
                    wifiView.setVisibility(View.VISIBLE);
                    emptyWifi.setVisibility(View.GONE);
//                    if (mWifiList.size() == 0) {
//                        LogUtils.info(TAG, "wifi扫描个数为空");
//                    } else {
//                        LogUtils.info(TAG, "wifi扫描个数 ：" + mWifiList.size());
//                    }
                    wifiListAdapter.notifyDataSetChanged();
                } else {
                    // 关闭WiFi并隐藏列表
                    wifiManager.setWifiEnabled(false);
                    wifiView.setVisibility(View.GONE);
                    emptyWifi.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 连接WiFi对话框
     *
     * @param scanResult Create by HonceH by 23/5/6
     */
    private void showWifiConnectDialog(ScanResult scanResult) {
        AlertDialog.Builder builder = new AlertDialog.Builder(WifiActivity.this);
        builder.setTitle(scanResult.SSID);
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 50, 20, 50);
        TextView textView = new TextView(WifiActivity.this);
        textView.setText("密码 :");
        linearLayout.addView(textView);
        EditText input = new EditText(WifiActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        linearLayout.addView(input);
        builder.setView(linearLayout);
        builder.setPositiveButton("连接", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String password = input.getText().toString();
                if (!TextUtils.isEmpty(password)) {
                    connectToWifi(scanResult, password);
                } else {
                    Toast.makeText(WifiActivity.this, "Password can not be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    /**
     * 扫描WiFi
     * Create by HonceH by 23/5/6
     */
    private void scanWifi() {
        boolean b = wifiManager.startScan();
        LogUtils.info(TAG, "开始扫描WiFi..." + b);
    }

    /**
     * WiFi信号的监听器
     * Create by HonceH by 23/5/6
     */
    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//            unregisterReceiver(this);
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (networkInfo != null) {
                        NetworkInfo.State state = networkInfo.getState();
                        if (state == NetworkInfo.State.CONNECTED) {
                            // 网络已连接
                            // TODO: 实现连接成功后的逻辑
                            Toast.makeText(mContext, "wifi连接成功", Toast.LENGTH_SHORT).show();
                        }
//                        else if (state == NetworkInfo.State.DISCONNECTED && wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
//                            // 网络已断开
////                            mWifiList.clear();
//                            Toast.makeText(mContext, "wifi连接断开", Toast.LENGTH_SHORT).show();
//                        } else if(state == NetworkInfo.State.CONNECTING && wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED ){
//                            Toast.makeText(mContext, "wifi正在连接", Toast.LENGTH_SHORT).show();
//                        }

                        wifiListAdapter.notifyDataSetChanged();

                    }
                    break;
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED || wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                        // 清空列表
                        Toast.makeText(mContext, "扫描更新WiFi", Toast.LENGTH_SHORT).show();
                        mWifiList.clear();
                        results.clear();
                        results = wifiManager.getScanResults();
                        for (ScanResult scanResult : results) {
                            if (scanResult.SSID.length() >= 1) {
                                mWifiList.add(scanResult);
                                LogUtils.info(TAG, "wifi 的名称 :" + scanResult.SSID);
                            }
                        }
                        // 更新列表
                        wifiListAdapter.notifyDataSetChanged();
                        wifiView.requestFocus();
                    }
                    break;
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                    switch (wifiState) {
                        case WifiManager.WIFI_STATE_ENABLED:
                            scanWifi();
                            LogUtils.info(TAG,"WIFI开启");
                            // WiFi已开启，执行相应操作
                            break;
                        case WifiManager.WIFI_STATE_DISABLED:
                            mWifiList.clear();
                            LogUtils.info(TAG,"WIFI关闭");
                            // WiFi已关闭，执行相应操作
                            break;
                    }
                    break;
            }

        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 重新扫描可用 WiFi
        scanWifi();
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        return false;
    }
}
