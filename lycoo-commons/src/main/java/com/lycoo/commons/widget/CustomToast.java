package com.lycoo.commons.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lycoo.commons.R;
import com.lycoo.commons.helper.StyleManager;

/**
 * Created by lancy on 2018/4/14
 */
public class CustomToast extends Toast {
    private static final String TAG = CustomToast.class.getSimpleName();

    public enum MessageType {
        DEFAULT,
        INFO,
        WARN,
        ERROR
    }

    public CustomToast(Context context) {
        super(context);
    }

    public static Toast makeText(Context context, int textId) {
        return makeText(context, context.getString(textId), Toast.LENGTH_SHORT, MessageType.DEFAULT);
    }

    public static Toast makeText(Context context, CharSequence text) {
        return makeText(context, text, Toast.LENGTH_SHORT, MessageType.DEFAULT);
    }

    public static Toast makeText(Context context, int textId, int duration) {
        return makeText(context, context.getString(textId), duration, MessageType.DEFAULT);
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        return makeText(context, text, duration, MessageType.DEFAULT);
    }

    public static Toast makeText(Context context, int textId, MessageType messageType) {
        return makeText(context, context.getString(textId), Toast.LENGTH_SHORT, messageType);
    }

    public static Toast makeText(Context context, CharSequence text, MessageType messageType) {
        return makeText(context, text, Toast.LENGTH_SHORT, messageType);
    }

    public static Toast makeText(Context context, int textId, int duration, MessageType messageType) {
        return makeText(context, context.getString(textId), duration, messageType);
    }

    @SuppressLint("InflateParams")
    public static Toast makeText(Context context, CharSequence text, int duration, MessageType messageType) {
        Toast toast = new Toast(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout container = (LinearLayout) inflater.inflate(R.layout.common_custom_toast, null);

        Resources resources = context.getResources();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.c_custom_toast_edge_width),
                LinearLayout.LayoutParams.MATCH_PARENT);
        // Edge
        View edge = new View(context);
        edge.setLayoutParams(params);
        container.addView(edge);

        // Icon
        params = new LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.c_custom_toast_icon_size),
                resources.getDimensionPixelSize(R.dimen.c_custom_toast_icon_size));
        params.leftMargin
                = params.rightMargin
                = params.topMargin
                = params.bottomMargin
                = resources.getDimensionPixelSize(R.dimen.c_custom_toast_icon_margin_left);
        ImageView icon = new ImageView(context);
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setFocusable(false);
        icon.setFocusableInTouchMode(false);
        icon.setClickable(false);
        icon.setLayoutParams(params);
        container.addView(icon);

        // Text
        params = new LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.c_custom_toast_text_width),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView messageText = new TextView(context);
        messageText.setPadding(
                resources.getDimensionPixelSize(R.dimen.c_custom_toast_text_padding_left),
                0,
                resources.getDimensionPixelSize(R.dimen.c_custom_toast_text_padding_right),
                0);
        messageText.setMaxLines(1);
        messageText.setTypeface(StyleManager.getInstance(context).getTypeface());
        messageText.setTextSize(resources.getDimensionPixelSize(R.dimen.c_custom_toast_text_size));
        messageText.setTextColor(resources.getColor(R.color.c_def_textview));
        messageText.setLayoutParams(params);
        messageText.setText(text);
        container.addView(messageText);

        // 提示
        if (messageType == MessageType.INFO) {
            icon.setImageResource(R.drawable.common_ic_emoji_info);
            edge.setBackgroundResource(R.color.c_edge_custom_toast_info);
            container.setBackgroundResource(R.color.c_bg_custom_toast_info);
        }
        // 错误
        else if (messageType == MessageType.ERROR) {
            icon.setImageResource(R.drawable.common_ic_emoji_error);
            edge.setBackgroundResource(R.color.c_edge_custom_toast_error);
            container.setBackgroundResource(R.color.c_bg_custom_toast_error);
        }
        // 警告
        else if (messageType == MessageType.WARN) {
            icon.setImageResource(R.drawable.common_ic_emoji_warn);
            edge.setBackgroundResource(R.color.c_edge_custom_toast_warn);
            container.setBackgroundResource(R.color.c_bg_custom_toast_warn);
        }
        // 默认
        else {
            icon.setImageResource(R.drawable.common_ic_emoji_default);
            edge.setBackgroundResource(R.color.c_edge_custom_toast_none);
            container.setBackgroundResource(R.color.c_bg_custom_toast_none);
        }

        toast.setView(container);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(duration);

        return toast;
    }

}
