package com.lycoo.desktop.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.ResourceUtils;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.commons.view.AutoRippleView;
import com.lycoo.commons.view.CornerMark;
import com.lycoo.desktop.R;
import com.lycoo.desktop.bean.DockItemInfo;
import com.lycoo.desktop.config.DesktopConstants;
import com.lycoo.desktop.dialog.DockItemMenuDialog;
import com.lycoo.desktop.helper.DockItemManager;
import com.lycoo.desktop.helper.GlideApp;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * 桌面坑位
 * SPECIALIZED_PAGE， 容器坑位推荐使用ACTION打开
 * <p>
 * Created by lancy on 2017/12/9
 */
public class DockItem extends FrameLayout implements View.OnClickListener, View.OnLongClickListener, View.OnKeyListener, View.OnTouchListener {
    private static final String TAG = DockItem.class.getSimpleName();
    private static final boolean DEBUG_UI = false;

    private View.OnClickListener mCustomClickListener;

    private ImageView mBackground;
    private ImageView mIcon;
    private ImageView mOverlay;
    private TextView mLabel;
    private CornerMark mCornerMark;
    private DockItemProgressBar mProgressBar;
    private AutoRippleView mRippleView;

    public int width;
    public int height;
    public int bgWidth;
    public int bgHeight;
    public int iconWidth;
    public int iconHeight;
    public int iconMarginTop;
    public int overlayWidth;
    public int overlayHeight;
    public int rippleViewWidth;
    public int rippleViewHeight;
    public int nextFocusUpId;
    public int nextFocusDownId;
    public int nextFocusRightId;
    public int nextFocusLeftId;

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

    private boolean mIconEnable;
    private boolean mOverlayEnable;
    private boolean mLabelEnable;
    private boolean mCornerMarkEnable;
    private boolean mRippleEnable;

    private CompositeDisposable mCompositeDisposable;
    private boolean mClicked;

    public DockItem(Context context) {
        this(context, true, true, true, true, true);
    }

    public DockItem(Context context, boolean iconEnable, boolean overlayEnable, boolean labelEnable, boolean cornerMarkEnable, boolean rippleEnable) {
        super(context);
        mContext = context;
        mStatus = Status.IDLE;

        mIconEnable = iconEnable;
        mOverlayEnable = overlayEnable;
        mLabelEnable = labelEnable;
        mCornerMarkEnable = cornerMarkEnable;
        mRippleEnable = rippleEnable;

        mCompositeDisposable = new CompositeDisposable();
    }

    /**
     * 初始化控件
     *
     * Created by lancy on 2017/12/9 16:12
     */
    public void initView(ViewGroup.LayoutParams params) {
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

//        setNextFocusLeftId(nextFocusLeftId);
//        setNextFocusRightId(nextFocusRightId);
//        setNextFocusUpId(nextFocusUpId);
//        setNextFocusDownId(nextFocusDownId);

        if (DEBUG_UI) {
            setBackgroundColor(Color.BLUE);
        }
    }


    /**
     * 初始化背景
     *
     * Created by lancy on 2017/12/9 17:41
     */
    private void initBackground() {
        LayoutParams params = new LayoutParams(bgWidth, bgHeight);
        mBackground = new ImageView(mContext);
        mBackground.setScaleType(ImageView.ScaleType.FIT_XY);
//        mBackground.setBackgroundResource(R.drawable.def_image_dock_item);
        mBackground.setLayoutParams(params);
        mBackground.setFocusable(true);
        mBackground.setFocusableInTouchMode(false);
        mBackground.setOnClickListener(this);
        mBackground.setOnLongClickListener(this);
        mBackground.setOnKeyListener(this);
        mBackground.setOnTouchListener(this);
        addView(mBackground);
    }

    /**
     * 初始化图标
     *
     * Created by lancy on 2017/12/9 17:41
     */
    private void initIcon() {
        LayoutParams params = new LayoutParams(iconWidth, iconHeight);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = iconMarginTop;
        mIcon = new ImageView(mContext);
        mIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        mIcon.setImageResource(R.drawable.def_ic_desktop_item);
        mIcon.setFocusable(false);
        mIcon.setFocusableInTouchMode(false);
        mIcon.setLayoutParams(params);
        addView(mIcon);
    }

    /**
     * 初始化覆盖物
     * 用于一些效果的展示， 例如毛玻璃， 水晶球等
     *
     * Created by lancy on 2018/5/11 11:31
     */
    private void initOverlay() {
        LayoutParams params = new LayoutParams(overlayWidth, overlayHeight);
        mOverlay = new ImageView(mContext);
        mOverlay.setScaleType(ImageView.ScaleType.FIT_XY);
        mOverlay.setImageResource(R.drawable.def_ic_overlay);
        mOverlay.setFocusable(false);
        mOverlay.setFocusableInTouchMode(false);
        mOverlay.setLayoutParams(params);
        addView(mOverlay);
    }

    /**
     * 初始化名称
     *
     * Created by lancy on 2017/12/9 17:42
     */
    private void initLabel() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = params.rightMargin = mContext.getResources().getDimensionPixelSize(R.dimen.def_desktop_item_gap);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_VERTICAL;
        mLabel = new TextView(mContext);
        mLabel.setPadding(
                mContext.getResources().getDimensionPixelSize(R.dimen.def_textview_padding_left),
                mContext.getResources().getDimensionPixelSize(R.dimen.def_textview_padding_top),
                mContext.getResources().getDimensionPixelSize(R.dimen.def_textview_padding_right),
                mContext.getResources().getDimensionPixelSize(R.dimen.def_textview_padding_bottom));
        mLabel.setGravity(Gravity.CENTER);
        mLabel.setSingleLine();
        mLabel.setEllipsize(TextUtils.TruncateAt.END);
        mLabel.setText(mContext.getString(R.string.def_desktop_item_label));
        mLabel.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mLabel.setTextSize(mContext.getResources().getDimension(R.dimen.def_dock_item_label_text_size));
        mLabel.setTextColor(mContext.getResources().getColor(R.color.desktop_item_label));
        mLabel.setLayoutParams(params);
        addView(mLabel);
        if (DEBUG_UI) {
            mLabel.setBackgroundColor(Color.CYAN);
        }
    }

    /**
     * 初始化角标
     *
     * Created by lancy on 2017/12/15 9:13
     */
    private void initCornerMark() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.topMargin
                = params.leftMargin
                = getResources().getDimensionPixelSize(R.dimen.def_desktop_item_gap);
        mCornerMark = new CornerMark(mContext);
        mCornerMark.setLayoutParams(params);
        mCornerMark.setTopPadding(getResources().getDimensionPixelSize(R.dimen.def_dock_item_corner_mark_top_padding));
        mCornerMark.setTextContentSize(getResources().getDimensionPixelSize(R.dimen.def_dock_item_corner_mark_content_text_size));
        mCornerMark.setBgColor(getResources().getColor(R.color.desktop_item_corner_mark));
        mCornerMark.setFocusable(false);
        mCornerMark.setFocusableInTouchMode(false);
        mCornerMark.setVisibility(View.GONE);
        addView(mCornerMark);
    }

    /**
     * Initialize RippleView
     *
     * Created by lancy on 2018/4/11 1:06
     */
    private void initRippleView() {
        LayoutParams params = new LayoutParams(rippleViewWidth, rippleViewHeight);
        params.topMargin
                = params.bottomMargin
                = params.leftMargin
                = params.rightMargin
                = getResources().getDimensionPixelSize(R.dimen.def_desktop_item_gap);
        mRippleView = new AutoRippleView(mContext);
        mRippleView.setRippleColor(getResources().getColor(R.color.dock_item_ripple_color));
        mRippleView.setLayoutParams(params);
        mRippleView.setFocusable(false);
        mRippleView.setFocusableInTouchMode(false);
        mRippleView.setOvalShape(true);
        addView(mRippleView);
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
            mProgressBar = new DockItemProgressBar(mContext);
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
    public void update(DockItemInfo itemInfo) {
        setType(itemInfo.getType());
        switch (itemInfo.getType()) {
            case DesktopConstants.SPECIALIZED_APP:              // 固定应用
            case DesktopConstants.REPLACEABLE_APP:              // 可替换应用
            case DesktopConstants.CONFIG_APP:                   // 可远程配置应用
                updateApp(itemInfo);
                break;
            case DesktopConstants.CUSTOM_ITEM:             // 自定义坑位
            case DesktopConstants.SPECIALIZED_PAGE:             // 固定页面
            case DesktopConstants.BOOSJ_DANCE_CLASSIFICATION:   // 广场舞分类页面
                updateItem(itemInfo);
                break;
        }
    }

    private void updateApp(DockItemInfo itemInfo) {
        // 角标
        if (isCornerMarkEnabled()) {
            ViewUtils.setViewShown(false, getCornerMark());
        }

        // 图标
        if (isIconEnabled()) {
            if (!StringUtils.isEmpty(itemInfo.getIconUrl())) {
                loadImage(itemInfo.getIconUrl(), getIcon());
            } else {
                if (StringUtils.isEmpty(itemInfo.getPackageName())) {
                    loadImage("def_ic_desktop_item", getIcon());
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

    private void updateItem(DockItemInfo itemInfo) {
        // 角标
        if (isCornerMarkEnabled()) {
            ViewUtils.setViewShown(false, getCornerMark());
        }

        // 图标
        if (isIconEnabled()) {
            loadImage(itemInfo.getIconUrl(), getIcon());
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
     * 更新 “固定页面” 坑位
     *
     * @param itemInfo 坑位信息
     *
     *                 Created by lancy on 2018/5/11 14:07
     */
    private void updateSpecializedPage(DockItemInfo itemInfo) {
        // 角标
        if (isCornerMarkEnabled()) {
            ViewUtils.setViewShown(false, getCornerMark());
        }

        // 图标
        if (isIconEnabled()) {
            loadImage(itemInfo.getIconUrl(), getIcon());
        }

        // 名称
        if (isLabelEnabled()) {
            getLabel().setText(itemInfo.getLabel());
        }

        // 背景
        loadImage(itemInfo.getImageUrl(), getBg());
    }


    /**
     * 更新 “可配置应用” 坑位
     *
     * @param itemInfo 坑位信息
     *
     *                 Created by lancy on 2018/5/11 14:08
     */
    private void updateConfigApp(DockItemInfo itemInfo) {
        // 角标
        if (isCornerMarkEnabled()) {
            ViewUtils.setViewShown(false, getCornerMark());
        }

        // 图标
        if (isIconEnabled()) {
            if (StringUtils.isEmpty(itemInfo.getPackageName())) {
                getIcon().setImageResource(R.drawable.def_ic_desktop_item);
            } else {
                // 如果配置了图标，显示配置图标, 否则显示应用本身图标
                if (!StringUtils.isEmpty(itemInfo.getIconUrl())) {
                    loadImage(itemInfo.getIconUrl(), getIcon());
                } else {
                    Drawable icon = ApplicationUtils.getAppIcon(mContext, itemInfo.getPackageName());
                    if (icon != null) {
                        getIcon().setImageDrawable(icon);
                    }
                }
            }
        }

        // 名称
        if (isLabelEnabled()) {
            if (StringUtils.isEmpty(itemInfo.getPackageName())) {
                getLabel().setText(mContext.getString(R.string.def_desktop_item_label));
            } else {
                // 如果配置了名称，显示配置名称, 否则显示应用本身名称
                if (!StringUtils.isEmpty(itemInfo.getLabel())) {
                    getLabel().setText(itemInfo.getLabel());
                } else {
                    getLabel().setText(ApplicationUtils.getAppLabel(mContext, itemInfo.getPackageName()));
                }
            }
        }

        // 背景
        loadImage(itemInfo.getImageUrl(), getBg());
    }

    /**
     * 更新 “固定应用” 坑位
     *
     * @param itemInfo 坑位信息
     *
     *                 Created by lancy on 2018/5/11 14:09
     */
    private void updateSpecializedApp(DockItemInfo itemInfo) {
        // 角标
        if (isCornerMarkEnabled()) {
            ViewUtils.setViewShown(false, getCornerMark());
        }

        // 图标
        if (isIconEnabled()) {
            if (StringUtils.isEmpty(itemInfo.getPackageName())) {
                getIcon().setImageResource(R.drawable.def_ic_desktop_item);
            } else {
                Drawable icon = ApplicationUtils.getAppIcon(mContext, itemInfo.getPackageName());
                if (icon != null) {
                    getIcon().setImageDrawable(icon);
                }
            }
        }

        // 名称
        if (isLabelEnabled()) {
            if (StringUtils.isEmpty(itemInfo.getPackageName())) {
                getLabel().setText(mContext.getString(R.string.def_desktop_item_label));
            } else {
                getLabel().setText(ApplicationUtils.getAppLabel(mContext, itemInfo.getPackageName()));
            }

        }

        // 背景
        loadImage(itemInfo.getImageUrl(), getBg());
    }

    /**
     * 更新 “可替换应用” 坑位
     *
     * @param itemInfo 坑位信息
     *
     *                 Created by lancy on 2018/5/11 14:09
     */
    private void updateReplaceableApp(DockItemInfo itemInfo) {
        // 角标
        if (isCornerMarkEnabled()) {
            getCornerMark().setTextContent(getResources().getString(R.string.replaceable));
            ViewUtils.setViewShown(true, getCornerMark());
        }

        // 图标
        if (isIconEnabled()) {
            if (StringUtils.isEmpty(itemInfo.getPackageName())) {
                getIcon().setImageResource(R.drawable.def_ic_desktop_item);
            } else {
                Drawable icon = ApplicationUtils.getAppIcon(mContext, itemInfo.getPackageName());
                if (icon != null) {
                    getIcon().setImageDrawable(icon);
                }
            }
        }

        // 名称
        if (isLabelEnabled()) {
            if (StringUtils.isEmpty(itemInfo.getPackageName())) {
                getLabel().setText(mContext.getString(R.string.def_desktop_item_label));
            } else {
                getLabel().setText(ApplicationUtils.getAppLabel(mContext, itemInfo.getPackageName()));
            }

        }

        // 背景
        loadImage(itemInfo.getImageUrl(), getBg());
    }


    /**
     * 加载图片
     *
     * @param url 图片地址
     * @param iv  目标控件
     *            <p>
     *            Created by lancy on 2017/12/15 9:24
     */
    public void loadImage(String url, ImageView iv) {
        if (!StringUtils.isEmpty(url)) {
            ViewUtils.setViewShown(true, iv);
            GlideApp.with(mContext)
                    .load(url.contains("/")
                            ? url
                            : ResourceUtils.getIdByName(mContext, "drawable", url))
                    .skipMemoryCache(true)
//                    .override(Target.SIZE_ORIGINAL) // 会导致drawbale下面xml类型的不能正常显示
                    .priority(Priority.HIGH)
                    .circleCrop()
                    .into(iv);
        }
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (isRippleEnabled()) {
            mRippleView.ripple(keyCode, event);
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isRippleEnabled()) {
            mRippleView.ripple(event);
        }
        return false;
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
                            DockItemManager.getInstance(mContext).openItem((DockItem) view.getParent());
                            mClicked = false;
                        }, throwable -> {
                            LogUtils.error(TAG, "failed to click, error message : " + throwable.getMessage());
                            throwable.printStackTrace();
                            mClicked = false;
                        }));
    }

    @Override
    public boolean onLongClick(View view) {
        if (getType() != DesktopConstants.REPLACEABLE_APP) {
            return true;
        }

        mCompositeDisposable.add(
                Observable
                        .just(((DockItem) view.getParent()).getId())
                        .delay(isRippleEnabled() ? AutoRippleView.DURATION + 500 + getResources().getInteger(R.integer.extra_ripple_duration) : 0, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::showMenuDialog, throwable -> {
                            LogUtils.error(TAG, "failed to long click, error message : " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
        return true;
    }

    /**
     * 显示菜单
     *
     * Created by lancy on 2018/1/6 17:07
     */
    private void showMenuDialog(int id) {
        if (mMenuDialog != null && mMenuDialog.isShowing()) {
            return;
        }
        mMenuDialog = new DockItemMenuDialog(mContext, R.style.MenuDialogStyle, id);
        mMenuDialog.show();
    }

    /**
     * 销毁时调用，在Dock中统一对调用DockItem的此方法
     *
     * Created by lancy on 2018/5/11 11:02
     */
    public void onDestroy() {
        mCompositeDisposable.clear();
    }

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

    public DockItemProgressBar getProgressBar() {
        return mProgressBar;
    }

    public AutoRippleView getRippleView() {
        return mRippleView;
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

    public void setSelector(Drawable selector) {
        getBg().setBackground(selector);
    }

    public void setSelector(int drawableId) {
        getBg().setBackgroundResource(drawableId);
    }


    public void setCustomClickListener(OnClickListener customClickListener) {
        mCustomClickListener = customClickListener;
    }

    public void doClick() {
        if (mCustomClickListener !=null){
            mCustomClickListener.onClick(this);
        }
    }
}
