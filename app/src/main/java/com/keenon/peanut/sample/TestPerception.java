package com.keenon.peanut.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.keenon.peanut.sample.R;
import com.keenon.peanut.sample.util.BaseActivity;
import com.keenon.peanut.sample.util.PrintLnLog;
import com.keenon.sdk.component.runtime.PeanutRuntime;
import com.keenon.sdk.external.PeanutSDK;
import com.keenon.sdk.external.IDataCallback;
import com.keenon.sdk.hedera.model.ApiError;
//TOPIC
import com.keenon.sdk.constant.TopicName;
import com.keenon.sdk.constant.ApiConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestPerception extends BaseActivity {
    private static final String TAG = "TestPerception";

    @BindView(R.id.tv_perception_log)
    TextView tvPerceptionLog;
    @BindView(R.id.sv_perception_log)
    ScrollView svPerceptionLog;
    @BindView(R.id.btn_test_perception)
    android.widget.Button btnTestPerception;
    @BindView(R.id.btn_battery_status)
    android.widget.Button btnBatteryStatus;
    
    private StringBuilder sb = new StringBuilder();
    private boolean isSubscribed = false;
    private boolean isBatterySubscribed = false;
    
    private IDataCallback sensorCallback = new IDataCallback() {
        @Override
        public void success(String result) {
            PrintLnLog.d(TestPerception.this, tvPerceptionLog, svPerceptionLog, sb, "SENSOR_STATUS Data: " + result);
        }

        @Override
        public void error(ApiError error) {
            PrintLnLog.d(TestPerception.this, tvPerceptionLog, svPerceptionLog, sb, "SENSOR_STATUS Error: " + error.toString());
        }
    };
    
    private IDataCallback batteryCallback = new IDataCallback() {
        @Override
        public void success(String result) {
            PrintLnLog.d(TestPerception.this, tvPerceptionLog, svPerceptionLog, sb, "BATTERY_STATUS Data: " + result);
        }

        @Override
        public void error(ApiError error) {
            PrintLnLog.d(TestPerception.this, tvPerceptionLog, svPerceptionLog, sb, "BATTERY_STATUS Error: " + error.toString());
        }
    };
    
    private PeanutRuntime.Listener mRuntimeListener = new PeanutRuntime.Listener() {
        @Override
        public void onEvent(int event, Object obj) {
            Log.d(TAG, "onEvent:" + event + ", content: " + obj);
            tvPerceptionLog.setTextColor(Color.RED);
            PrintLnLog.d(TestPerception.this, tvPerceptionLog, svPerceptionLog, sb, "Perception Event = " + event);
        }

        @Override
        public void onHealth(Object content) {
            Log.d(TAG, "onHealth:" + content);
            tvPerceptionLog.setTextColor(Color.GREEN);
            PrintLnLog.d(TestPerception.this, tvPerceptionLog, svPerceptionLog, sb, "Perception Health = " + content);
        }

        @Override
        public void onHeartbeat(Object content) {
            Log.d(TAG, "onHeartbeat:" + content);
            tvPerceptionLog.setTextColor(Color.BLACK);
            PrintLnLog.d(TestPerception.this, tvPerceptionLog, svPerceptionLog, sb, "Perception Heartbeat = " + content);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_perception);
        ButterKnife.bind(this);
        setButtonBack();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PeanutRuntime.getInstance().removeListener(mRuntimeListener);
        // Unsubscribe khi destroy
        if (isSubscribed) {
            unsubscribeSensorStatus();
        }
        if (isBatterySubscribed) {
            unsubscribeBatteryStatus();
        }
    }

    private void initData() {
        PeanutRuntime.getInstance().registerListener(mRuntimeListener);
        PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "TestPerception Activity Started");
        updateButtonTexts();
    }
    
    private void updateButtonTexts() {
        // Cập nhật text cho nút Test Perception
        if (isSubscribed) {
            btnTestPerception.setText("Unsubscribe Sensor");
        } else {
            btnTestPerception.setText("Subscribe Sensor");
        }
        
        // Cập nhật text cho nút Battery Status
        if (isBatterySubscribed) {
            btnBatteryStatus.setText("Unsubscribe Battery");
        } else {
            btnBatteryStatus.setText("Subscribe Battery");
        }
    }

    @OnClick({
            R.id.btn_test_perception,
            R.id.btn_clear_log,
            R.id.btn_battery_status
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_test_perception:
                testPerceptionFunction();
                break;
            case R.id.btn_clear_log:
                clearLog();
                break;
            case R.id.btn_battery_status:
                testBatteryStatus();
                break;
        }
    }

    private void testPerceptionFunction() {
        PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "=== TEST SENSOR_STATUS TOPIC ===");
        
        if (!isSubscribed) {
            subscribeSensorStatus();
        } else {
            unsubscribeSensorStatus();
        }
    }
    
    private void subscribeSensorStatus() {
        try {
            PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "Đang subscribe SENSOR_STATUS...");
//            PeanutSDK.getInstance().subscribe(TopicName.SENSOR_STATUS, sensorCallback);
            isSubscribed = true;
            updateButtonTexts();

            PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "✓ Đã subscribe SENSOR_STATUS thành công!");
            PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "Đang lắng nghe dữ liệu từ SENSOR_STATUS...");
        } catch (Exception e) {
            PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "❌ Lỗi khi subscribe SENSOR_STATUS: " + e.getMessage());
        }
    }
    
    private void unsubscribeSensorStatus() {
        try {
            PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "Đang unsubscribe SENSOR_STATUS...");
            PeanutSDK.getInstance().unSubscribe(TopicName.SENSOR_STATUS, sensorCallback);
            isSubscribed = false;
            updateButtonTexts();
            PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "✓ Đã unsubscribe SENSOR_STATUS thành công!");
        } catch (Exception e) {
            PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "❌ Lỗi khi unsubscribe SENSOR_STATUS: " + e.getMessage());
        }
    }
    
    private void testBatteryStatus() {
        PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "=== TEST BATTERY_STATUS TOPIC ===");
        
        if (!isBatterySubscribed) {
            subscribeBatteryStatus();
        } else {
            unsubscribeBatteryStatus();
        }
    }
    
    private void subscribeBatteryStatus() {
        try {
            PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "Đang subscribe BATTERY_STATUS...");
//            PeanutSDK.getInstance().subscribe(TopicName.BATTERY_STATUS, batteryCallback);
            isBatterySubscribed = true;
            updateButtonTexts();
            PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "✓ Đã subscribe BATTERY_STATUS thành công!");
            PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "Đang lắng nghe dữ liệu pin từ BATTERY_STATUS...");
        } catch (Exception e) {
            PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "❌ Lỗi khi subscribe BATTERY_STATUS: " + e.getMessage());
        }
    }
    
    private void unsubscribeBatteryStatus() {
        try {
            PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "Đang unsubscribe BATTERY_STATUS...");
            PeanutSDK.getInstance().unSubscribe(TopicName.BATTERY_STATUS, batteryCallback);
            isBatterySubscribed = false;
            updateButtonTexts();
            PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "✓ Đã unsubscribe BATTERY_STATUS thành công!");
        } catch (Exception e) {
            PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "❌ Lỗi khi unsubscribe BATTERY_STATUS: " + e.getMessage());
        }
    }

    private void clearLog() {
        sb.setLength(0);
        tvPerceptionLog.setText("");
        PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "Log cleared");
    }

    
}
