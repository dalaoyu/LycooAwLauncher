package com.lycoo.desktop.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.helper.SystemPropertiesManager;
import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.ResourceUtils;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.commons.view.AutoRippleView;
import com.lycoo.commons.view.CornerMark;
import com.lycoo.commons.view.PassiveRippleView;
import com.lycoo.desktop.R;
import com.lycoo.desktop.bean.DesktopItemInfo;
import com.lycoo.desktop.config.DesktopConstants;
import com.lycoo.desktop.dialog.DesktopItemMenuDialog;
import com.lycoo.desktop.helper.DesktopItemManager;
import com.lycoo.desktop.helper.GlideApp;


import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 桌面坑位
 * SPECIALIZED_PAGE， 容器坑位推荐使用ACTION打开
 * <p>
 * Created by lancy on 2017/12/9
 */
public class DesktopItem extends FrameLayout implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = DesktopItem.class.getSimpleName();
    private static final boolean DEBUG_UI = false;
    public static final String SP_DESKTOP = "sp_desktop";
    public static final String ICON_URL = "icon_url";
    public static final String LABEL = "label";

    public static final String IMAGES = "images";

    /**
     * 水波纹持续时间
     * 使用水波效果时，需考虑到水波纹持续时间，否则会造成点击item时，水波纹还没有完成便进入其他页面，造成会面不完整
     */
    public static final int RIPPLE_DURATION = 300;

    private ImageView mBackground;
    private ImageView mOverlay;

    private CornerMark mCornerMark;
    private DesktopItemProgressBar mProgressBar;
    private PassiveRippleView mPassiveRippleView;

    private boolean mOverlayEnable;

    private boolean mCornerMarkEnable;
    private boolean mRippleEnable;

    //
    private int mCornerRadius;

    // Icon相关
    private ImageView mIcon;
    private boolean mIconEnable;
    public int iconWidth;
    public int iconHeight;
    private int mIconTopMargin;
    private int mIconLeftMargin;

    // label相关
    private TextView mLabel;
    private boolean mLabelEnable;
    private int mLabelGravity;
    private int mLabelSize;
    private int mLabelBottomMargin;
    private int mLabelRightMargin;

    private int mLabelColor;
    private Typeface mLabelTypeface;
    private boolean mLabelBold;

    public int width;
    public int height;
    public int topMargin;
    public int bottomMargin;
    public int leftMargin;
    public int rightMargin;

    public int overlayWidth;
    public int overlayHeight;
    private int cornerMarkTopPadding;
    private int cornerMarkContentTextSize;

    public int nextFocusUpId;
    public int nextFocusDownId;
    public int nextFocusRightId;
    public int nextFocusLeftId;
    public int belowId;
    public int rightOfId;

    private Animation mFocusAnimation;
    private Animation mUnFocusAnimation;
    private String icon_url;

    public enum Status {
        IDLE,           // 空闲
        PREPAREING,     // 准备下载（搜索应用）
        DOWNLOADING,    // 正在下载应用
        INSTALLING      // 正在安装应用
    }

    private int mType;
    private Status mStatus;
    private Context mContext;

    private Dialog mMenuDialog;
    private CompositeDisposable mCompositeDisposable;
    private boolean mClicked;
    private OnClickListener mCustomClickListener;

    private List<String> replaceableApps = Arrays.asList(getResources().getStringArray(R.array.replaceable_apps));

    public DesktopItem(Context context) {
        this(context, true, true, true, true, true);
    }

    public DesktopItem(Context context, boolean iconEnable, boolean overlayEnable, boolean labelEnable, boolean cornerMarkEnable, boolean rippleEnable) {
        super(context);
        mContext = context;
        mStatus = Status.IDLE;

        mIconEnable = iconEnable;
        mOverlayEnable = overlayEnable;
        mLabelEnable = labelEnable;
        mCornerMarkEnable = cornerMarkEnable;
        mRippleEnable = rippleEnable;

        mCornerRadius = 0;

        // Label相关
        mLabelGravity = Gravity.START;
        mLabelSize = mContext.getResources().getDimensionPixelSize(R.dimen.def_desktop_item_label_text_size);
        mLabelTypeface = StyleManager.getInstance(mContext).getTypeface();
        mLabelColor = mContext.getResources().getColor(R.color.desktop_item_label);
        mLabelBottomMargin = 0;

        mCompositeDisposable = new CompositeDisposable();
    }

    /**
     * 初始化控件
     * <p>
     * Created by lancy on 2017/12/9 16:12
     */
    public void initView() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.topMargin = topMargin;
        params.bottomMargin = bottomMargin;
        params.leftMargin = leftMargin;
        params.rightMargin = rightMargin;
        params.addRule(RelativeLayout.BELOW, belowId);
        params.addRule(RelativeLayout.RIGHT_OF, rightOfId);
        setLayoutParams(params);

        initBackground();

        if (isIconEnabled()) {
            initIcon();
        }

        if (isOverlayEnable()) {
            initOverlay();
        }

        if (isLabelEnabled()) {
            initLabel();
        }

        if (isCornerMarkEnabled()) {
            initCornerMark();
        }

        if (isRippleEnabled()) {
            initRippleView();
        }

//        setFocusable(true);
//        setFocusableInTouchMode(true);
        setNextFocusLeftId(nextFocusLeftId);
        setNextFocusRightId(nextFocusRightId);
        setNextFocusUpId(nextFocusUpId);
        setNextFocusDownId(nextFocusDownId);

        setOnClickListener(this);
        setOnLongClickListener(this);

        if (DEBUG_UI) {
            setBackgroundColor(Color.BLUE);
        }
    }

    /**
     * 初始化背景
     * <p>
     * Created by lancy on 2017/12/9 17:41
     */
    private void initBackground() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mBackground = new ImageView(mContext);
        mBackground.setScaleType(ImageView.ScaleType.FIT_XY);
        mBackground.setBackgroundResource(R.drawable.def_image_desktop_item);
        mBackground.setLayoutParams(params);
        mBackground.setFocusable(false);
        mBackground.setFocusableInTouchMode(false);
        addView(mBackground);
    }

    /**
     * 初始化图标
     * <p>
     * Created by lancy on 2017/12/9 17:41
     */
    private void initIcon() {
        LayoutParams params = new LayoutParams(iconWidth, iconHeight);
        if (mIconTopMargin == 0 && mIconLeftMargin == 0) {
            params.gravity = Gravity.CENTER;
        } else {
            params.topMargin = getIconTopMargin();
            params.leftMargin = getIconLeftMargin();
        }
        mIcon = new ImageView(mContext);
        mIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        mIcon.setImageResource(R.drawable.def_ic_desktop_item);
        mIcon.setFocusable(false);
        mIcon.setFocusableInTouchMode(false);
        mIcon.setLayoutParams(params);
        mIcon.setVisibility(View.GONE);
        addView(mIcon);
    }

    /**
     * 初始化覆盖物
     * 用于一些效果的展示， 例如毛玻璃， 水晶球等
     * <p>
     * Created by lancy on 2018/5/11 11:31
     */
    private void initOverlay() {
        LayoutParams params = new LayoutParams(overlayWidth, overlayHeight);
        params.gravity = Gravity.CENTER;
        mOverlay = new ImageView(mContext);
        mOverlay.setScaleType(ImageView.ScaleType.FIT_XY);
        mOverlay.setImageResource(R.drawable.def_ic_overlay);
        mOverlay.setFocusable(false);
        mOverlay.setFocusableInTouchMode(false);
        mOverlay.setLayoutParams(params);
        mOverlay.setVisibility(View.GONE);
        addView(mOverlay);
    }

    /**
     * 初始化名称
     * <p>
     * Created by lancy on 2017/12/9 17:42
     */
    private void initLabel() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin
                = params.rightMargin
                = mContext.getResources().getDimensionPixelSize(R.dimen.def_desktop_item_gap);
        params.bottomMargin = getLabelBottomMargin();
        params.rightMargin = getLabelRightMargin();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_VERTICAL;
        mLabel = new TextView(mContext);
        mLabel.setPadding(
                mContext.getResources().getDimensionPixelSize(R.dimen.def_textview_padding_left),
                mContext.getResources().getDimensionPixelSize(R.dimen.def_textview_padding_top),
                mContext.getResources().getDimensionPixelSize(R.dimen.def_textview_padding_right),
                mContext.getResources().getDimensionPixelSize(R.dimen.def_textview_padding_bottom));
        mLabel.setSingleLine();
        mLabel.setEllipsize(TextUtils.TruncateAt.END);
        mLabel.setText(mContext.getString(R.string.def_desktop_item_label));
        if (mLabelBold) {
            mLabel.setTypeface(getLabelTypeface(), Typeface.BOLD);
        } else {
            mLabel.setTypeface(getLabelTypeface());
        }
        mLabel.setTextSize(getLabelSize());
        mLabel.setTextColor(getLabelColor());
        mLabel.setGravity(getLabelGravity());
        mLabel.setLayoutParams(params);
        mLabel.setVisibility(View.GONE);
        addView(mLabel);
        if (DEBUG_UI) {
            mLabel.setBackgroundColor(Color.CYAN);
        }
    }

    /**
     * 初始化角标
     * <p>
     * Created by lancy on 2017/12/15 9:13
     */
    private void initCornerMark() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.topMargin
                = params.leftMargin
                = getResources().getDimensionPixelSize(R.dimen.def_desktop_item_gap);
        mCornerMark = new CornerMark(mContext);
        mCornerMark.setLayoutParams(params);
        mCornerMark.setTopPadding(getResources().getDimensionPixelSize(R.dimen.def_desktop_item_corner_mark_top_padding));
        mCornerMark.setTextContentSize(getResources().getDimensionPixelSize(R.dimen.def_desktop_item_corner_mark_content_text_size));
        mCornerMark.setBgColor(getResources().getColor(R.color.desktop_item_corner_mark));
        mCornerMark.setFocusable(false);
        mCornerMark.setFocusableInTouchMode(false);
        mCornerMark.setVisibility(View.GONE);
        addView(mCornerMark);
    }

    /**
     * Initialize RippleView
     * <p>
     * Created by lancy on 2018/4/11 1:06
     */
    private void initRippleView() {
        removeRippleView();

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.topMargin
                = params.bottomMargin
                = params.leftMargin
                = params.rightMargin
                = getResources().getDimensionPixelSize(R.dimen.def_desktop_item_gap);
        mPassiveRippleView = new PassiveRippleView(mContext);
        mPassiveRippleView.setRippleColor(getResources().getColor(R.color.desktop_item_ripple_color));
        mPassiveRippleView.setLayoutParams(params);
        mPassiveRippleView.setFocusable(false);
        mPassiveRippleView.setFocusableInTouchMode(false);
        addView(mPassiveRippleView);
    }

    /**
     * Remove RippleView
     * <p>
     * Created by lancy on 2018/4/11 1:14
     */
    private void removeRippleView() {
        if (mPassiveRippleView != null) {
            removeView(mPassiveRippleView);
        }
    }

    public void showProgressBar() {
        hideProgressBar();

        if (mProgressBar == null) {
            LayoutParams params = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            params.leftMargin
                    = params.rightMargin
                    = params.bottomMargin
                    = params.topMargin
                    = mContext.getResources().getDimensionPixelSize(R.dimen.def_desktop_item_gap);
            mProgressBar = new DesktopItemProgressBar(mContext);
            mProgressBar.setLayoutParams(params);
        }
        addView(mProgressBar);
    }

    public void hideProgressBar() {
        if (mProgressBar != null) {
            removeView(mProgressBar);
            mProgressBar = null;
        }
    }

    /**
     * 更新坑位
     *
     * @param itemInfo 坑位信息
     *                 <p>
     *                 Created by lancy on 2017/12/14 19:53
     */
    public void update(DesktopItemInfo itemInfo) {
        setType(itemInfo.getType());
        switch (itemInfo.getType()) {
            case DesktopConstants.SPECIALIZED_APP:  // 固定应用
            case DesktopConstants.CONFIG_APP:       // 可远程配置应用
                updateApp(itemInfo);
                break;

            case DesktopConstants.REPLACEABLE_APP:  // 可替换应用
                updateReplaceApp(itemInfo);
//                updateApp(itemInfo);
                break;

            // 网址类坑位
            case DesktopConstants.WEBSITE:
                updateItem(itemInfo);
                break;

            case DesktopConstants.QIYI_SPECIALIZED_PAGE:        // 爱奇艺固定类型坑位，例如搜索，历史等
            case DesktopConstants.SPECIALIZED_PAGE:             // 固定页面
            case DesktopConstants.CUSTOM_ITEM:                  // 定制坑位
                updateItem(itemInfo);
                break;


            // 容器类型
            case DesktopConstants.TV_CONTAINER:
            case DesktopConstants.AOD_CONTAINER:
            case DesktopConstants.MUSIC_CONTAINER:
            case DesktopConstants.GAME_CONTAINER:
            case DesktopConstants.EDUCATION_CONTAINER:
            case DesktopConstants.APP_CONTAINER:
            case DesktopConstants.SETUP_CONTAINER:
            case DesktopConstants.TOOLS_CONTAINER:
            case DesktopConstants.EXTRUDE_RECOMMENDATION_CONTAINER:
            case DesktopConstants.COMMON_RECOMMENDATION_CONTAINER:
                updateItem(itemInfo);
                break;

            // 爱奇艺轮播推荐坑位
            case DesktopConstants.QIYI_EXTRUDE_RECOMMENDATION:
                updateItem(itemInfo);
                break;

            // 爱奇艺普通推荐坑位
            case DesktopConstants.QIYI_COMMON_RECOMMENDATION:
                updateItem(itemInfo);
                break;

            // 爱奇艺频道坑位
            case DesktopConstants.QIYI_CHANNEL:
                updateItem(itemInfo);
                break;

            // 播视广场舞
            case DesktopConstants.BOOSJ_DANCE_EXCHANGE:
            case DesktopConstants.BOOSJ_DANCE_RECOMMEND:
            case DesktopConstants.BOOSJ_DANCE_HEALTH:
            case DesktopConstants.BOOSJ_DANCE_EXCLUSIVE:
            case DesktopConstants.BOOSJ_DANCE_ACTIVITY:
            case DesktopConstants.BOOSJ_DANCE_MUSIC:
            case DesktopConstants.BOOSJ_DANCE_GOLD_TEACHER:
            case DesktopConstants.BOOSJ_DANCE_DAREN:
            case DesktopConstants.BOOSJ_DANCE_SQUARE:
            case DesktopConstants.BOOSJ_DANCE_CLASSIFICATION:
                updateItem(itemInfo);
                break;

            // IKTV模块
            case DesktopConstants.IKTV_ITEM_SINGERS:             // 歌手点歌
            case DesktopConstants.IKTV_ITEM_HOT_NEW_SONGS:       // 新歌热歌
            case DesktopConstants.IKTV_ITEM_LOCAL_SONGS:         // 本地歌曲
            case DesktopConstants.IKTV_ITEM_SONGS:               // 歌名点歌
            case DesktopConstants.IKTV_ITEM_FAVORITE_SONGS:      // 收藏歌曲
            case DesktopConstants.IKTV_ITEM_LANGUAGE:            // 语种点歌
            case DesktopConstants.IKTV_ITEM_TOPIC:               // 主题点歌
            case DesktopConstants.IKTV_ITEM_VARIETY:             // 综艺点歌
            case DesktopConstants.IKTV_ITEM_TIKTOK_SONGS:        // 抖音神曲
            case DesktopConstants.IKTV_ITEM_RADITIONAL_OPERA:    // 戏曲
            case DesktopConstants.IKTV_ITEM_BL:                  // 百灵
            case DesktopConstants.HEALTH_SCYD:                   // 身材有道
            case DesktopConstants.JSYX_RADITIONAL_OPERA:         // 江苏有线的戏曲
                updateItem(itemInfo);

                break;
        }
    }

    private void updateApp(DesktopItemInfo itemInfo) {
        // 角标
        if (isCornerMarkEnabled()) {
            ViewUtils.setViewShown(false, getCornerMark());
        }

        // 图标
        if (isIconEnabled() && itemInfo.isIconVisible()) {
            if (!StringUtils.isEmpty(itemInfo.getIconUrl())) {
                loadIcon(itemInfo.getIconUrl(), getIcon());
            } else {
                if (StringUtils.isEmpty(itemInfo.getPackageName())) {
                    loadIcon("def_ic_desktop_item", getIcon());
                } else {
                    Drawable icon = ApplicationUtils.getAppIcon(mContext, itemInfo.getPackageName());
                    if (icon != null) {
                        getIcon().setImageDrawable(icon);
                    }
                    ViewUtils.setViewShown(true, getIcon());
                }
            }
        }

        // 名称
        if (isLabelEnabled()) {
            if (!StringUtils.isEmpty(itemInfo.getLabel())) {
                getLabel().setText(itemInfo.getLabel());
            } else {
                if (StringUtils.isEmpty(itemInfo.getPackageName())) {
                    getLabel().setText(mContext.getString(R.string.def_desktop_item_label));
                } else {
                    getLabel().setText(ApplicationUtils.getAppLabel(mContext, itemInfo.getPackageName()));
                }
            }
            ViewUtils.setViewShown(true, getLabel());
        }

        // 背景
        loadImage(itemInfo.getImageUrl(), getBg());
    }

    private void updateReplaceApp(DesktopItemInfo itemInfo) {

        String packageName = mContext.getSharedPreferences(SP_DESKTOP, Context.MODE_PRIVATE).getString(String.valueOf(itemInfo.getTag()), null);
        if (packageName != null) {
            mContext.getSharedPreferences(packageName, Context.MODE_PRIVATE)
                    .edit()
                    .putString(ICON_URL, itemInfo.getIconUrl())
                    .putString(LABEL, itemInfo.getLabel())
                    .putString(IMAGES, itemInfo.getImageUrl())
                    .apply();
        }

        // 角标
        if (isCornerMarkEnabled()) {
            ViewUtils.setViewShown(false, getCornerMark());
        }

        // 图标
        if (isIconEnabled() && itemInfo.isIconVisible()) {
            if (StringUtils.isEmpty(itemInfo.getPackageName())) {
                LogUtils.error(TAG, "getImageUrl::" + itemInfo.getImageUrl());
                loadIcon("def_ic_desktop_item", getIcon());
            } else {
                if (packageName != null && itemInfo.getPackageName().equals(packageName)) {
                    icon_url = mContext.getSharedPreferences(packageName, Context.MODE_PRIVATE).getString(ICON_URL, null);
                    if (icon_url == null) {
                        Drawable icon = ApplicationUtils.getAppIcon(mContext, itemInfo.getPackageName());
                        if (icon != null) {
                            getIcon().setImageDrawable(icon);
                        }
                        ViewUtils.setViewShown(true, getIcon());
                    } else {
                        LogUtils.error(TAG, "icon_url" + icon_url);
                        loadIcon(icon_url, getIcon());
                        ViewUtils.setViewShown(icon_url != null, getIcon());
                    }
                } else {
                    Drawable icon = ApplicationUtils.getAppIcon(mContext, itemInfo.getPackageName());
                    if (icon != null) {
                        getIcon().setImageDrawable(icon);
                    }
                    ViewUtils.setViewShown(true, getIcon());
                }
            }
        }

        // 名称
        if (isLabelEnabled()) {
            if (StringUtils.isEmpty(itemInfo.getPackageName())) {
                getLabel().setText(mContext.getString(R.string.def_desktop_item_label));
            } else {
                if (packageName != null && itemInfo.getPackageName().equals(packageName)) {
                    String label = mContext.getSharedPreferences(packageName, Context.MODE_PRIVATE).getString(LABEL, null);
                    getLabel().setText(label);
                } else {
                    getLabel().setText(ApplicationUtils.getAppLabel(mContext, itemInfo.getPackageName()));
                }
            }
            ViewUtils.setViewShown(true, getLabel());
        }

//        loadImage(itemInfo.getImageUrl(), getBg());
        // 背景
        if (StringUtils.isEmpty(itemInfo.getPackageName())) {
            loadImage(itemInfo.getImageUrl(), getBg());
        } else {
            if (packageName != null && itemInfo.getPackageName().equals(packageName)) {
                String imageUrl = mContext.getSharedPreferences(packageName, Context.MODE_PRIVATE).getString(IMAGES, null);
                loadImage(imageUrl, getBg());
            } else {
                if (SystemPropertiesManager.getInstance(mContext).getBoolean(CommonConstants.PROPERTY_LAUNCHER_IS_HANSHENG, false)) {
                    if (!itemInfo.getPackageName().equals(packageName)) {
                        for (String apps : replaceableApps) {
                            if (!itemInfo.getPackageName().equals(apps)) {
                                if (itemInfo.getTag() == 102) {
                                    loadImage("bg_106", getBg());
                                } else if (itemInfo.getTag() == 103) {
                                    loadImage("bg_109", getBg());
                                } else if (itemInfo.getTag() == 104) {
                                    loadImage("bg_112", getBg());
                                } else {
                                    loadImage(itemInfo.getImageUrl(), getBg());
                                }
                            }
                        }
                    } else {
                        loadImage(itemInfo.getImageUrl(), getBg());
                    }
                } else {
                    loadImage(itemInfo.getImageUrl(), getBg());
                }
            }
        }
    }


    private void updateItem(DesktopItemInfo itemInfo) {
        // 角标
        if (isCornerMarkEnabled()) {
            ViewUtils.setViewShown(false, getCornerMark());
        }

        // 图标
        if (isIconEnabled() && itemInfo.isIconVisible()) {
            loadIcon(itemInfo.getIconUrl(), getIcon());
        }

        // 名称
        if (isLabelEnabled()) {
            getLabel().setText(itemInfo.getLabel());
            ViewUtils.setViewShown(true, getLabel());
        }
        // 背景
        loadImage(itemInfo.getImageUrl(), getBg());
    }

    /**
     * 加载图标
     *
     * @param url 图片地址
     * @param iv  目标控件
     *            <p>
     *            Created by lancy on 2018/6/16 12:07
     */
    public void loadIcon(String url, ImageView iv) {
        if (!TextUtils.isEmpty(url)) {
            ViewUtils.setViewShown(true, iv);
            GlideApp.with(mContext)
                    .load(url.contains("/") ? url : ResourceUtils.getIdByName(mContext, "drawable", url))
                    .skipMemoryCache(true)
                    .priority(Priority.HIGH)
                    .into(iv);
        }
    }


    /**
     * 加载背景图片
     *
     * @param url 图片地址
     * @param iv  目标控件
     *            <p>
     *            Created by lancy on 2017/12/15 9:24
     */
    @SuppressLint("CheckResult")
    public void loadImage(String url, ImageView iv) {
        if (!TextUtils.isEmpty(url)) {
            ViewUtils.setViewShown(true, iv);
            RequestOptions options = new RequestOptions()
                    .skipMemoryCache(true)
                    .priority(Priority.HIGH);
            // 圆角的半径必须大于0
            if (getCornerRadius() > 0) {
                options.transform(new RoundedCorners(getCornerRadius()));
            }
            GlideApp.with(mContext)
                    .asBitmap()
                    .load(url.contains("/") ? url : ResourceUtils.getIdByName(mContext, "drawable", url))
                    .apply(options)
                    .into(iv);


        } else {
            iv.setImageResource(R.color.def_desktop_item);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRippleEnabled()) {
            mPassiveRippleView.ripple(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isRippleEnabled()) {
            mPassiveRippleView.ripple(keyCode, event);
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (isRippleEnabled()) {
            mPassiveRippleView.ripple(keyCode, event);
        }

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (getType() == DesktopConstants.REPLACEABLE_APP) {
                showMenuDialog(getId());
            }
            return true;
        }

//        if (keyCode == KeyEvent.KEYCODE_MENU && getType() == DesktopConstants.REPLACEABLE_APP) {
//            showMenuDialog(getId());
//
//        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(final View view) {
        if (mClicked) {
            return;
        }

        mClicked = true;
        mCompositeDisposable.add(
                Observable
                        .timer(isRippleEnabled() ? AutoRippleView.DURATION + getResources().getInteger(R.integer.extra_ripple_duration) : 0, TimeUnit.MILLISECONDS) // 如果使用水波纹，需要延迟
                        .subscribe(aLong -> {
                            DesktopItemManager.getInstance(mContext).openItem((DesktopItem) view);
                            mClicked = false;
                        }, throwable -> {
                            LogUtils.error(TAG, "failed to click, error message : " + throwable.getMessage());
                            throwable.printStackTrace();
                            mClicked = false;
                        }));
    }

    @Override
    public boolean onLongClick(View v) {
        if (getType() != DesktopConstants.REPLACEABLE_APP) {
            return true;
        }

        mCompositeDisposable.add(
                Observable
                        .just(v.getId())
                        .delay(isRippleEnabled() ? PassiveRippleView.DURATION + 500 : 0, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::showMenuDialog,
                                throwable -> {
                                    LogUtils.error(TAG, "failed to long click, error message : " + throwable.getMessage());
                                    throwable.printStackTrace();
                                }));
        return true;
    }

    /**
     * 自定义点击事件
     */
    public void doClick() {
        if (mCustomClickListener != null) {
            mCustomClickListener.onClick(this);
        }
    }

    /**
     * 显示菜单
     * <p>
     * Created by lancy on 2018/1/6 17:07
     */
    private void showMenuDialog(int id) {
        if (mMenuDialog != null && mMenuDialog.isShowing()) {
            return;
        }
        mMenuDialog = new DesktopItemMenuDialog(mContext, R.style.MenuDialogStyle, id);
        mMenuDialog.show();
    }

    // ===========================================================================================================
    /* ***************************************************************************************************************** */
    public ImageView getBg() {
        return mBackground;
    }

    public ImageView getIcon() {
        return mIcon;
    }

    public TextView getLabel() {
        return mLabel;
    }

    public CornerMark getCornerMark() {
        return mCornerMark;
    }

    public DesktopItemProgressBar getProgressBar() {
        return mProgressBar;
    }

    public PassiveRippleView getRippleView() {
        return mPassiveRippleView;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        this.mStatus = status;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public boolean isIconEnabled() {
        return mIconEnable;
    }

    public boolean isOverlayEnable() {
        return mOverlayEnable;
    }

    public boolean isLabelEnabled() {
        return mLabelEnable;
    }

    public boolean isCornerMarkEnabled() {
        return mCornerMarkEnable;
    }

    public boolean isRippleEnabled() {
        return mRippleEnable;
    }

    public void onDestroy() {
        mCompositeDisposable.clear();
    }
    // =======================================================================================

    public int getCornerRadius() {
        return mCornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        this.mCornerRadius = cornerRadius;
    }

    public Animation getFocusAnimation() {
        return mFocusAnimation;
    }

    public void setFocusAnimation(Animation focusAnimation) {
        this.mFocusAnimation = focusAnimation;
    }

    public Animation getUnFocusAnimation() {
        return mUnFocusAnimation;
    }

    public void setUnFocusAnimation(Animation unFocusAnimation) {
        this.mUnFocusAnimation = unFocusAnimation;
    }

    // Icon ===================================================================================

    public int getIconTopMargin() {
        return mIconTopMargin;
    }

    public void setIconTopMargin(int iconTopMargin) {
        this.mIconTopMargin = iconTopMargin;
    }

    public int getIconLeftMargin() {
        return mIconLeftMargin;
    }

    public void setIconLeftMargin(int iconLeftMargin) {
        this.mIconLeftMargin = iconLeftMargin;
    }

    // Label ===================================================================================

    public int getLabelGravity() {
        return mLabelGravity;
    }

    public void setLabelGravity(int labelGravity) {
        this.mLabelGravity = labelGravity;
    }

    public int getLabelSize() {
        return mLabelSize;
    }

    public void setLabelSize(int labelSize) {
        this.mLabelSize = labelSize;
    }

    public Typeface getLabelTypeface() {
        return mLabelTypeface;
    }

    public void setLabelTypeface(Typeface labelTypeface) {
        this.mLabelTypeface = labelTypeface;
    }

    public void setLabelBold(boolean labelBold) {
        this.mLabelBold = labelBold;
    }

    public int getLabelColor() {
        return mLabelColor;
    }

    public void setLabelColor(int labelColor) {
        this.mLabelColor = labelColor;
    }

    public int getLabelRightMargin() {
        return mLabelRightMargin;
    }

    public void setLabelRightMargin(int labelRightMargin) {
        this.mLabelRightMargin = labelRightMargin;
    }

    public int getLabelBottomMargin() {
        return mLabelBottomMargin;
    }

    public void setLabelBottomMargin(int labelBottomMargin) {
        this.mLabelBottomMargin = labelBottomMargin;
    }

    public void setLabelEnable(boolean labelEnable) {
        this.mLabelEnable = labelEnable;
    }

    public void setCustomClickListener(OnClickListener customClickListener) {
        this.mCustomClickListener = customClickListener;
    }
}
