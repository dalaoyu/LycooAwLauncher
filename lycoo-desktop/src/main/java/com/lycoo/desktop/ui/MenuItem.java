package com.lycoo.desktop.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.desktop.R;

/**
 * 菜单项
 *
 * Created by lancy on 2018/4/18
 */
public class MenuItem {

    public static final int TEXT = 0;
    public static final int SWITCH = 1;

    public enum Type {
        TEXT,
        SWITCH,
        SEEK_BAR
    }

    private View.OnClickListener mClickListener;
    private OnValueChangeListener mValueChangeListener;
    private OnStateChangeListener mStateChangeListener;

    private View mView;
    private ImageView mIconView;
    private TextView mTitleText;
    private TextView mDataText;
    private ImageView mArrowIcon;

    private Type mType;
    private int mIconId;
    private String mTitle;
    private String mData;

    public static MenuItem createTextItem(String title) {
        return new MenuItem(Type.TEXT, title);
    }

    public static MenuItem createTextItem(String title, int icon) {
        return new MenuItem(Type.TEXT, title, icon);
    }

    public static MenuItem createTextItem(String title, int icon, String data) {
        return new MenuItem(Type.TEXT, title, icon, data);
    }

    public static SwitchMenuItem createSwitchMenuItem(String title) {
        return new SwitchMenuItem(title);
    }

    public static SwitchMenuItem createSwitchMenuItem(String title, int icon) {
        return new SwitchMenuItem(title, icon);
    }

    public static SeekBarMenuItem createSeekBarMenuItem(String title) {
        return new SeekBarMenuItem(title);
    }

    public static SeekBarMenuItem createSeekBarMenuItem(String title, int icon) {
        return new SeekBarMenuItem(title, icon);
    }

    protected MenuItem(Type type, String title) {
        this.mType = type;
        this.mTitle = title;
    }

    protected MenuItem(Type type, String title, int icon) {
        this.mType = type;
        this.mTitle = title;
        this.mIconId = icon;
    }

    public MenuItem(Type type, String title, int icon, String data) {
        this.mType = type;
        this.mTitle = title;
        this.mIconId = icon;
        this.mData = data;
    }

    public View getView(View convertView, ViewGroup parent) {
        // Unbind previous view
        onUnbindView();

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.menu_item, parent, false);
        } else if (convertView.getTag() instanceof MenuItem) {
            ((MenuItem) convertView.getTag()).onUnbindView();
        }
        convertView.setTag(this);
        mView = convertView;

        // setup icon
        mIconView = convertView.findViewById(R.id.iv_icon);
        if (mIconId > 0) {
            mIconView.setImageResource(mIconId);
        } else {
            ViewUtils.setViewShown(false, mIconView);
        }

        // Setup title
        mTitleText = convertView.findViewById(R.id.tv_title);
        mTitleText.setTypeface(StyleManager.getInstance(parent.getContext()).getTypeface());
        mTitleText.setText(mTitle);

        // Bind view control
        ViewGroup container = convertView.findViewById(R.id.control_container);
        Type tag = (Type) container.getTag();
        boolean inflate = tag == null || tag != mType;
        if (inflate) {
            container.removeAllViews();
        }
        container.setTag(mType);

//        View view;
//        if (mType == Type.TEXT) {
//            view = inflater.inflate(R.layout.menu_item_text, parent, false);
//            mDataText = view.findViewById(R.id.tv_data);
//            if (!TextUtils.isEmpty(mData)) {
//                mDataText.setText(mData);
//            } else {
//                mDataText.setText("");
//            }
//            container.addView(view);
//        }
        onBindView(inflater, container, inflate);
        return convertView;
    }

    /**
     * 绑定控制视图
     *
     * @param inflater  布局解析器
     * @param container 父容器
     * @param inflate   inflate
     */
    protected void onBindView(LayoutInflater inflater, ViewGroup container, boolean inflate) {
        if (inflate) {
            inflater.inflate(R.layout.menu_item_text, container);
        }
        mDataText = container.findViewById(R.id.tv_data);
        if (!TextUtils.isEmpty(mData)) {
            mDataText.setText(mData);
        } else {
            mDataText.setText("");
        }
    }

    /**
     * 解绑控制视图
     *
     * Created by lancy on 2019/6/18 17:16
     */
    protected void onUnbindView() {
        mDataText = null;
    }

    /**
     * 增加
     * 针对调节类Item, 例如SeekBarMenuItem
     */
    public void increase() {
    }

    /**
     * 减少
     * 针对调节类Item, 例如SeekBarMenuItem
     */
    public void decrease() {
    }

    public void performClick() {
        if (mClickListener != null) {
            mClickListener.onClick(mView);
        }
    }

    public MenuItem setOnClickListener(View.OnClickListener listener) {
        mClickListener = listener;
        return this;
    }


    public MenuItem setOnValueChangeListener(OnValueChangeListener listener) {
        mValueChangeListener = listener;
        return this;
    }

    /**
     * 通知内容发生改变
     *
     * @param value 内容
     *
     *              Created by lancy on 2019/6/18 17:55
     */
    protected void notifyValueChange(int value) {
        if (mValueChangeListener != null) {
            mValueChangeListener.onValueChanged(this, value);
        }
    }

    public MenuItem setOnStateChangeListener(OnStateChangeListener stateChangeListener) {
        this.mStateChangeListener = stateChangeListener;
        return this;
    }

    /**
     * 通知状态发生改变
     *
     * @param state 状态
     *
     *              Created by lancy on 2019/6/18 17:55
     */
    protected void notifyStateChange(boolean state) {
        if (mStateChangeListener != null) {
            mStateChangeListener.onStateChanged(this, state);
        }
    }

    /**
     * 内容改变监听器
     * 用于菜单项代表一种值，例如：Spinner, SeekBar
     *
     * Created by lancy on 2019/6/18 17:16
     */
    public interface OnValueChangeListener {
        void onValueChanged(MenuItem item, int value);
    }

    /**
     * 状态改变监听器
     * 用于菜单项代表一种状态，例如Switch
     *
     * Created by lancy on 2019/6/18 18:02
     */
    public interface OnStateChangeListener {
        void onStateChanged(MenuItem item, boolean state);
    }
}
