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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestPerception extends BaseActivity {
    private static final String TAG = "TestPerception";

    @BindView(R.id.tv_perception_log)
    TextView tvPerceptionLog;
    @BindView(R.id.sv_perception_log)
    ScrollView svPerceptionLog;
    
    private StringBuilder sb = new StringBuilder();
    
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
    }

    private void initData() {
        PeanutRuntime.getInstance().registerListener(mRuntimeListener);
        PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "TestPerception Activity Started");
    }

    @OnClick({
            R.id.btn_test_perception,
            R.id.btn_clear_log
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_test_perception:
                testPerceptionFunction();
                break;
            case R.id.btn_clear_log:
                clearLog();
                break;
        }
    }

    private void testPerceptionFunction() {
        PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "Testing Perception Function...");
        // Thêm logic test perception ở đây
        PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "Perception test completed");
    }

    private void clearLog() {
        sb.setLength(0);
        tvPerceptionLog.setText("");
        PrintLnLog.d(this, tvPerceptionLog, svPerceptionLog, sb, "Log cleared");
    }
}
