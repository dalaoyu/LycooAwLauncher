package com.lycoo.desktop.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.lycoo.desktop.R;

/**
 * 开关选项
 *
 * Created by lancy on 2019/6/18
 */
public class SwitchMenuItem extends MenuItem implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = SwitchMenuItem.class.getSimpleName();

    private Switch mSwitch;
    private boolean mChecked;

    protected SwitchMenuItem(String title) {
        super(Type.SWITCH, title);
    }

    protected SwitchMenuItem(String title, int icon) {
        super(Type.SWITCH, title, icon);
        setOnClickListener(v -> {
            if (mSwitch != null) {
                mSwitch.toggle();
            }
        });
    }

    @Override
    protected void onBindView(LayoutInflater inflater, ViewGroup container, boolean inflate) {
        if (inflate) {
            inflater.inflate(R.layout.menu_item_switch, container);
        }
        mSwitch = container.findViewById(R.id.switch_view);
        updateContent();
    }

    @Override
    protected void onUnbindView() {
        super.onUnbindView();
        mSwitch = null;
    }

    private void updateContent() {
        if (mSwitch == null) {
            return;
        }
        mSwitch.setOnCheckedChangeListener(null);
        mSwitch.setChecked(mChecked);
        mSwitch.setOnCheckedChangeListener(this);
    }

    public SwitchMenuItem setChecked(boolean checked) {
        this.mChecked = checked;
        return this;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mChecked = isChecked;
        notifyStateChange(isChecked);
    }

}
