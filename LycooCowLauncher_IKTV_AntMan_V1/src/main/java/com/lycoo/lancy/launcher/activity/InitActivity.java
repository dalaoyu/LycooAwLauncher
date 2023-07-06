package com.lycoo.lancy.launcher.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.widget.TextView;

import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.CollectionUtils;
import com.lycoo.commons.util.SystemPropertiesUtils;
import com.lycoo.desktop.bean.DesktopContainerItemInfo;
import com.lycoo.desktop.bean.DesktopItemInfo;
import com.lycoo.desktop.config.DesktopConstants;
import com.lycoo.desktop.helper.DesktopItemManager;
import com.lycoo.lancy.launcher.R;
import com.lycoo.lancy.launcher.config.Constants;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class InitActivity extends Activity {
    private static final String TAG = InitActivity.class.getSimpleName();

    @BindView(R.id.tv_hint)
    TextView tv_hint;
    private Context mContext = this;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        ButterKnife.bind(this);

        initData();
        initView();
        doInit();
    }


    private void initData() {

        mCompositeDisposable = new CompositeDisposable();
    }

    private void initView() {
        tv_hint.setTypeface(StyleManager.getInstance(mContext).getTypeface());
    }

    /**
     * 初始化
     * <p>
     * Created by lancy on 2017/12/14 18:36
     */
    private void doInit() {
        mCompositeDisposable.add(
                Observable
                        .zip(initDesktopItemsObservable(),
                                initDesktopContainerItemsObservable(),
                                (initDesktopItemsResult, initDesktopContainerItemsResult) ->
                                        initDesktopItemsResult && initDesktopContainerItemsResult)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result) {
                                mContext.getSharedPreferences(Constants.SP_DESKTOP, Context.MODE_PRIVATE)
                                        .edit()
                                        .putBoolean(Constants.DESKTOP_INITIALIZED, true)
                                        .apply();

                                launchMainUI();
                            }
                        }));
    }

    private Observable<Boolean> initDesktopItemsObservable() {
        return Observable
                .create(emitter -> {
                    SparseIntArray itemTypes = new SparseIntArray();
                    SparseArray<String> itemSources = new SparseArray<>();
                    SparseArray<String> itemLables = new SparseArray<>();
                    SparseArray<String> itemIcons = new SparseArray<>();
                    SparseArray<String> itemImages = new SparseArray<>();

                    Resources resources = getResources();
                    String[] arrays;
                    String[] configs;

                    // 类型
                    if (Build.MODEL.contains(Constants.MODEL_LXHD) || Build.MODEL.contains(Constants.MODEL_XIANGSHENGTONG)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_lxhd);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANGQI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_shangqi);
                    } else if (Build.MODEL.contains(Constants.MODEL_GAV) ||
                            Build.MODEL.contains(Constants.MODEL_QBA) ||
                            Build.MODEL.contains(Constants.MODEL_HCKE)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_gav);
                    } else if (Build.MODEL.contains(Constants.MODEL_LICHAO)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_lichao);
                    } else if (Build.MODEL.contains(Constants.MODEL_YIWEI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_yiwei);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANGJIN)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_shangjin);
                    } else if (Build.MODEL.contains(Constants.MODEL_HUAWEI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_huawei);
                    } else if (Build.MODEL.contains(Constants.MODEL_FENWEI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_fenwei);
                    } else if (Build.MODEL.contains(Constants.MODEL_OUBO)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_oubo);
                    } else if (Build.MODEL.contains(Constants.MODEL_MINGDU)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_mingdu);
                    } else if (Build.MODEL.contains(Constants.MODEL_XIANDAI) || Build.MODEL.contains(Constants.MODEL_LEHAO)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_xiandai);
                    } else if (Build.MODEL.contains(Constants.MODEL_YEXIANG)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_yexiang);
                    } else if (Build.MODEL.contains(Constants.MODEL_MINGGE)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_mingge);
                    } else if (Build.MODEL.contains(Constants.MODEL_JBA)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_jba);
                    } else if (Build.MODEL.contains(Constants.MODEL_BOK)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_bok);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHILE)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_shile);
                    } else if (Build.MODEL.contains(Constants.MODEL_AIXIANG)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_aixiang);
                    } else if (Build.MODEL.contains(Constants.MODEL_SS508)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_tongchuang);
                    } else if (Build.MODEL.contains(Constants.MODEL_AILIPU)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_ailipu);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANLING)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_shanling);
                    } else if (Build.MODEL.contains(Constants.MODEL_DISUONA)) {
                        arrays = resources.getStringArray(R.array.desktop_item_types_dsn);
                    } else {
                        arrays = resources.getStringArray(R.array.desktop_item_types);
                    }
                    for (String config : arrays) {
                        configs = config.split("__");
                        itemTypes.put(Integer.valueOf(configs[0]), Integer.valueOf(configs[1]));
                    }

                    // source
                    if (Build.MODEL.contains(Constants.MODEL_LXHD) || Build.MODEL.contains(Constants.MODEL_XIANGSHENGTONG)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_lxhd);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANGQI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_shangqi);
                    } else if (Build.MODEL.contains(Constants.MODEL_GAV) ||
                            Build.MODEL.contains(Constants.MODEL_QBA) ||
                            Build.MODEL.contains(Constants.MODEL_HCKE)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_gav);
                    } else if (Build.MODEL.contains(Constants.MODEL_LICHAO)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_lichao);
                    } else if (Build.MODEL.contains(Constants.MODEL_YIWEI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_yiwei);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANGJIN)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_shangjin);
                    } else if (Build.MODEL.contains(Constants.MODEL_HUAWEI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_huawei);
                    } else if (Build.MODEL.contains(Constants.MODEL_FENWEI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_fenwei);
                    } else if (Build.MODEL.contains(Constants.MODEL_OUBO)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_oubo);
                    } else if (Build.MODEL.contains(Constants.MODEL_MINGDU)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_mingdu);
                    } else if (Build.MODEL.contains(Constants.MODEL_XIANDAI) || Build.MODEL.contains(Constants.MODEL_LEHAO)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_xiandai);
                    } else if (Build.MODEL.contains(Constants.MODEL_YEXIANG)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_yexiang);
                    } else if (Build.MODEL.contains(Constants.MODEL_MINGGE)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_mingge);
                    } else if (Build.MODEL.contains(Constants.MODEL_JBA)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_jba);
                    } else if (Build.MODEL.contains(Constants.MODEL_BOK)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_bok);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHILE)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_shile);
                    } else if (Build.MODEL.contains(Constants.MODEL_AIXIANG)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_aixiang);
                    } else if (Build.MODEL.contains(Constants.MODEL_SS508)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_tongchuang);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANLING)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_shanling);
                    } else if (Build.MODEL.contains(Constants.MODEL_AILIPU)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_ailipu);
                    } else if (Build.MODEL.contains(Constants.MODEL_DISUONA)) {
                        arrays = resources.getStringArray(R.array.desktop_item_sources_dsn);
                    } else {
                        arrays = resources.getStringArray(R.array.desktop_item_sources);
                    }
                    for (String config : arrays) {
                        configs = config.split("__");
                        itemSources.put(Integer.valueOf(configs[0]), configs[1]);
                    }

                    // label
                    if (Build.MODEL.contains(Constants.MODEL_LXHD) || Build.MODEL.contains(Constants.MODEL_XIANGSHENGTONG)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_lxhd);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANGQI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_shangqi);
                    } else if (Build.MODEL.contains(Constants.MODEL_GAV) ||
                            Build.MODEL.contains(Constants.MODEL_QBA) ||
                            Build.MODEL.contains(Constants.MODEL_HCKE)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_gav);
                    } else if (Build.MODEL.contains(Constants.MODEL_LICHAO)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_lichao);
                    } else if (Build.MODEL.contains(Constants.MODEL_YIWEI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_yiwei);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANGJIN)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_shangjin);
                    } else if (Build.MODEL.contains(Constants.MODEL_HUAWEI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_huawei);
                    } else if (Build.MODEL.contains(Constants.MODEL_FENWEI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_fenwei);
                    } else if (Build.MODEL.contains(Constants.MODEL_OUBO)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_oubo);
                    } else if (Build.MODEL.contains(Constants.MODEL_MINGDU)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_mingdu);
                    } else if (Build.MODEL.contains(Constants.MODEL_XIANDAI) || Build.MODEL.contains(Constants.MODEL_LEHAO)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_xiandai);
                    } else if (Build.MODEL.contains(Constants.MODEL_YEXIANG)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_yexiang);
                    } else if (Build.MODEL.contains(Constants.MODEL_MINGGE)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_mingge);
                    } else if (Build.MODEL.contains(Constants.MODEL_JBA)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_jba);
                    } else if (Build.MODEL.contains(Constants.MODEL_BOK)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_bok);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHILE)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_shile);
                    } else if (Build.MODEL.contains(Constants.MODEL_AIXIANG)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_aixiang);
                    } else if (Build.MODEL.contains(Constants.MODEL_SS508)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_tongchuang);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANLING)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_shanling);
                    } else if (Build.MODEL.contains(Constants.MODEL_AILIPU)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_ailipu);
                    } else if (Build.MODEL.contains(Constants.MODEL_DISUONA)) {
                        arrays = resources.getStringArray(R.array.desktop_item_labels_line_in_dsn);
                    } else {
                        if (SystemPropertiesUtils.getBoolean(CommonConstants.PROPERTY_BOOT_MODEL_LINE_IN, false)) {
                            arrays = resources.getStringArray(R.array.desktop_item_labels_line_in);
                        } else {
                            arrays = resources.getStringArray(R.array.desktop_item_labels);
                        }
                    }
                    for (String config : arrays) {
                        configs = config.split("__");
                        itemLables.put(Integer.valueOf(configs[0]), configs[1]);
                    }

                    // icons
                    if (Build.MODEL.contains(Constants.MODEL_LXHD) || Build.MODEL.contains(Constants.MODEL_XIANGSHENGTONG)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_lxhd);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANGQI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_shangqi);
                    } else if (Build.MODEL.contains(Constants.MODEL_GAV) ||
                            Build.MODEL.contains(Constants.MODEL_QBA) ||
                            Build.MODEL.contains(Constants.MODEL_HCKE)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_gav);
                    } else if (Build.MODEL.contains(Constants.MODEL_LICHAO)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_lichao);
                    } else if (Build.MODEL.contains(Constants.MODEL_YIWEI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_yiwei);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANGJIN)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_shangjin);
                    } else if (Build.MODEL.contains(Constants.MODEL_HUAWEI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_huawei);
                    } else if (Build.MODEL.contains(Constants.MODEL_FENWEI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_fenwei);
                    } else if (Build.MODEL.contains(Constants.MODEL_OUBO)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_oubo);
                    } else if (Build.MODEL.contains(Constants.MODEL_MINGDU)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_mingdu);
                    } else if (Build.MODEL.contains(Constants.MODEL_XIANDAI) || Build.MODEL.contains(Constants.MODEL_LEHAO)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_xiandai);
                    } else if (Build.MODEL.contains(Constants.MODEL_YEXIANG)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_yexiang);
                    } else if (Build.MODEL.contains(Constants.MODEL_MINGGE)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_mingge);
                    } else if (Build.MODEL.contains(Constants.MODEL_JBA)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_jba);
                    } else if (Build.MODEL.contains(Constants.MODEL_BOK)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_bok);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHILE)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_shile);
                    } else if (Build.MODEL.contains(Constants.MODEL_AIXIANG)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_aixiang);
                    } else if (Build.MODEL.contains(Constants.MODEL_SS508)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_tongchuang);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANLING)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_shanling);
                    } else if (Build.MODEL.contains(Constants.MODEL_AILIPU)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_ailipu);
                    } else if (Build.MODEL.contains(Constants.MODEL_DISUONA)) {
                        arrays = resources.getStringArray(R.array.desktop_item_icons_line_in_dsn);
                    } else {
                        if (SystemPropertiesUtils.getBoolean(CommonConstants.PROPERTY_BOOT_MODEL_LINE_IN, false)) {
                            arrays = resources.getStringArray(R.array.desktop_item_icons_line_in);
                        } else {
                            arrays = resources.getStringArray(R.array.desktop_item_icons);
                        }
                    }

                    for (String config : arrays) {
                        configs = config.split("__");
                        itemIcons.put(Integer.valueOf(configs[0]), configs[1]);
                    }

                    // visible icons
                    List<String> visibleItemIcons;
                    if (Build.MODEL.contains(Constants.MODEL_LXHD) || Build.MODEL.contains(Constants.MODEL_XIANGSHENGTONG)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_lxhd));
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANGQI)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_shangqi));
                    } else if (Build.MODEL.contains(Constants.MODEL_GAV) ||
                            Build.MODEL.contains(Constants.MODEL_QBA) ||
                            Build.MODEL.contains(Constants.MODEL_HCKE)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_gav));
                    } else if (Build.MODEL.contains(Constants.MODEL_LICHAO)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_lichao));
                    } else if (Build.MODEL.contains(Constants.MODEL_YIWEI)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_yiwei));
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANGJIN)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_shangjin));
                    } else if (Build.MODEL.contains(Constants.MODEL_HUAWEI)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_huawei));
                    } else if (Build.MODEL.contains(Constants.MODEL_FENWEI)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_fenwei));
                    } else if (Build.MODEL.contains(Constants.MODEL_OUBO)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_oubo));
                    } else if (Build.MODEL.contains(Constants.MODEL_MINGDU)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_mingdu));
                    } else if (Build.MODEL.contains(Constants.MODEL_XIANDAI) || Build.MODEL.contains(Constants.MODEL_LEHAO)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_xiandai));
                    } else if (Build.MODEL.contains(Constants.MODEL_YEXIANG)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_yexiang));
                    } else if (Build.MODEL.contains(Constants.MODEL_MINGGE)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_mingge));
                    } else if (Build.MODEL.contains(Constants.MODEL_JBA)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_jba));
                    } else if (Build.MODEL.contains(Constants.MODEL_BOK)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_bok));
                    } else if (Build.MODEL.contains(Constants.MODEL_SHILE)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_shile));
                    } else if (Build.MODEL.contains(Constants.MODEL_AIXIANG)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_aixiang));
                    } else if (Build.MODEL.contains(Constants.MODEL_SS508)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_tongchuang));
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANLING)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_shanling));
                    } else if (Build.MODEL.contains(Constants.MODEL_AILIPU)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_ailipu));
                    } else if (Build.MODEL.contains(Constants.MODEL_AILIPU)) {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible_dsn));
                    } else {
                        visibleItemIcons = Arrays.asList(resources.getStringArray(R.array.desktop_item_icon_visible));
                    }

                    // images
                    if (Build.MODEL.contains(Constants.MODEL_LXHD) || Build.MODEL.contains(Constants.MODEL_XIANGSHENGTONG)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_lxhd);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANGQI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_shangqi);
                    } else if (Build.MODEL.contains(Constants.MODEL_GAV) ||
                            Build.MODEL.contains(Constants.MODEL_QBA) ||
                            Build.MODEL.contains(Constants.MODEL_HCKE)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_gav);
                    } else if (Build.MODEL.contains(Constants.MODEL_LICHAO)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_lichao);
                    } else if (Build.MODEL.contains(Constants.MODEL_YIWEI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_yiwei);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANGJIN)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_shangjin);
                    } else if (Build.MODEL.contains(Constants.MODEL_HUAWEI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_huawei);
                    } else if (Build.MODEL.contains(Constants.MODEL_FENWEI)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_fenwei);
                    } else if (Build.MODEL.contains(Constants.MODEL_OUBO)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_oubo);
                    } else if (Build.MODEL.contains(Constants.MODEL_MINGDU)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_mingdu);
                    } else if (Build.MODEL.contains(Constants.MODEL_XIANDAI) || Build.MODEL.contains(Constants.MODEL_LEHAO)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_xiandai);
                    } else if (Build.MODEL.contains(Constants.MODEL_YEXIANG)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_yexiang);
                    } else if (Build.MODEL.contains(Constants.MODEL_MINGGE)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_mingge);
                    } else if (Build.MODEL.contains(Constants.MODEL_JBA)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_jba);
                    } else if (Build.MODEL.contains(Constants.MODEL_BOK)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_bok);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHILE)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_shile);
                    } else if (Build.MODEL.contains(Constants.MODEL_AIXIANG)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_aixiang);
                    } else if (Build.MODEL.contains(Constants.MODEL_SS508)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_tongchuang);
                    } else if (Build.MODEL.contains(Constants.MODEL_AILIPU)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_ailipu);
                    } else if (Build.MODEL.contains(Constants.MODEL_DISUONA)) {
                        arrays = resources.getStringArray(R.array.desktop_item_images_dsn);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANLING)) {
                        if (SystemPropertiesUtils.getBoolean(CommonConstants.PROPERTY_BOOT_VAIDEO_ENABLE, false)) {
                            arrays = resources.getStringArray(R.array.desktop_item_images_shanling);
                        } else {
                            arrays = resources.getStringArray(R.array.desktop_item_images_no_bg_ktv);
                        }
                    } else {
                        if (Build.MODEL.contains(Constants.MODEL_AIBOSHENG)) {
                            arrays = resources.getStringArray(R.array.desktop_item_images_aibosheng);
                        } else {
                            arrays = resources.getStringArray(R.array.desktop_item_images);
                        }
                    }
                    for (String config : arrays) {
                        configs = config.split("__");
                        itemImages.put(Integer.valueOf(configs[0]), configs[1]);
                    }

                    saveDesktopItemInfos(
                            itemTypes,
                            itemLables,
                            itemSources,
                            itemIcons,
                            itemImages,
                            visibleItemIcons
                    );

                    emitter.onNext(true);
                });
    }

    private Observable<Boolean> initDesktopContainerItemsObservable() {
        return Observable
                .create(emitter -> {
                    String[] array;
                    if (Build.MODEL.contains(Constants.MODEL_LXHD) || Build.MODEL.contains(Constants.MODEL_XIANGSHENGTONG)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_lxhd);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANGQI)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_shangqi);
                    } else if (Build.MODEL.contains(Constants.MODEL_GAV) ||
                            Build.MODEL.contains(Constants.MODEL_QBA) ||
                            Build.MODEL.contains(Constants.MODEL_HCKE)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_gav);
                    } else if (Build.MODEL.contains(Constants.MODEL_LICHAO)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_lichao);
                    } else if (Build.MODEL.contains(Constants.MODEL_YIWEI)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_yiwei);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANGJIN)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_shangjin);
                    } else if (Build.MODEL.contains(Constants.MODEL_HUAWEI)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_huawei);
                    } else if (Build.MODEL.contains(Constants.MODEL_FENWEI)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_fenwei);
                    } else if (Build.MODEL.contains(Constants.MODEL_OUBO)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_oubo);
                    } else if (Build.MODEL.contains(Constants.MODEL_MINGDU)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_mingdu);
                    } else if (Build.MODEL.contains(Constants.MODEL_XIANDAI) || Build.MODEL.contains(Constants.MODEL_LEHAO)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_xiandai);
                    } else if (Build.MODEL.contains(Constants.MODEL_YEXIANG)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_yexiang);
                    } else if (Build.MODEL.contains(Constants.MODEL_MINGGE)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_mingge);
                    } else if (Build.MODEL.contains(Constants.MODEL_JBA)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_jba);
                    } else if (Build.MODEL.contains(Constants.MODEL_BOK)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_bok);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHILE)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_shile);
                    } else if (Build.MODEL.contains(Constants.MODEL_AIXIANG)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_aixiang);
                    } else if (Build.MODEL.contains(Constants.MODEL_SS508)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_tongchuang);
                    } else if (Build.MODEL.contains(Constants.MODEL_SHANLING)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_shanling);
                    } else if (Build.MODEL.contains(Constants.MODEL_AILIPU)) {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources_ailipu);
                    } else {
                        array = mContext.getResources().getStringArray(R.array.desktop_container_item_sources);
                    }
                    if (array.length > 0) {
                        List<DesktopContainerItemInfo> desktopContainerItemInfos = new ArrayList<>();
                        for (String source : array) {
                            String[] split = source.split("__");
                            DesktopContainerItemInfo containerItemInfo = new DesktopContainerItemInfo();
                            containerItemInfo.setContainerType(Integer.parseInt(split[0]));
                            containerItemInfo.setPackageName(split[1]);
                            desktopContainerItemInfos.add(containerItemInfo);
                        }
                        DesktopItemManager.getInstance(mContext).saveContainerItemInfos(desktopContainerItemInfos);
                    }
                    emitter.onNext(true);
                });
    }

    /**
     * 持久化坑位信息
     *
     * @param itemTypes   坑位类型
     * @param itemLables  坑位名称
     * @param itemSources 坑位资源
     * @param itemIcons   坑位图标, 类似ic_film, 加载的时候利用发射机制
     *                    <p>
     *                    Created by lancy on 2017/12/14 18:13
     */
    private void saveDesktopItemInfos(SparseIntArray itemTypes,
                                      SparseArray<String> itemLables,
                                      SparseArray<String> itemSources,
                                      SparseArray<String> itemIcons,
                                      SparseArray<String> itemImages,
                                      List<String> visibleItemIcons) {
        // 获取系统中所有安装的应用
        List<DesktopItemInfo> itemInfos = new ArrayList<>();
        for (int i = 0; i < itemTypes.size(); i++) {
            int tag = itemTypes.keyAt(i);
            int type = itemTypes.valueAt(i);

            DesktopItemInfo itemInfo = new DesktopItemInfo();
            itemInfo.setTag(tag);
            itemInfo.setType(type);
            itemInfo.setUpdateTime(DesktopConstants.DEF_UPDATETIME);

            switch (type) {
                case DesktopConstants.SPECIALIZED_APP:      // 固定应用
                case DesktopConstants.REPLACEABLE_APP:      // 可替换应用
                    if (!StringUtils.isEmpty(itemSources.get(tag)) && ApplicationUtils.isAppInstalled(mContext, itemSources.get(tag))) {
                        // 保证系统中安装了此应用再设置Label和Icon， 否则导致有图标但是打不开情况
                        itemInfo.setPackageName(itemSources.get(tag));
                        itemInfo.setLabel(itemLables.get(tag));
                        itemInfo.setIconUrl(itemIcons.get(tag));
                    }
                    itemInfo.setIconVisible(visibleItemIcons.contains(String.valueOf(tag)));
                    itemInfo.setImageUrl(itemImages.get(tag));
                    break;

                case DesktopConstants.CONFIG_APP:
                    itemInfo.setPackageName(itemSources.get(tag));
                    itemInfo.setLabel(itemLables.get(tag));
                    itemInfo.setIconUrl(itemIcons.get(tag));
                    itemInfo.setImageUrl(itemImages.get(tag));
                    itemInfo.setIconVisible(visibleItemIcons.contains(String.valueOf(tag)));
/*                    if (!ApplicationUtils.isAppInstalled(mContext, Constants.PACKAGE_NAME_KTV)) {
                        if (itemSources.get(tag).contains(Constants.PACKAGE_NAME_KTV)){
                            itemInfo.setPackageName(Constants.PACKAGE_NAME_WIFI_PATH);
                            itemInfo.setLabel("WIFI设置");
                            itemInfo.setImageUrl("bg_wifi_common");
                        }
                    }*/
                    break;

                case DesktopConstants.SPECIALIZED_PAGE:
                    itemInfo.setLabel(itemLables.get(tag));
                    itemInfo.setIconUrl(itemIcons.get(tag));
                    itemInfo.setImageUrl(itemImages.get(tag));
                    itemInfo.setAction(itemSources.get(tag));
                    itemInfo.setIconVisible(visibleItemIcons.contains(String.valueOf(tag)));
                    break;

                case DesktopConstants.CUSTOM_ITEM:
                    itemInfo.setLabel(itemLables.get(tag));
                    itemInfo.setIconUrl(itemIcons.get(tag));
                    itemInfo.setImageUrl(itemImages.get(tag));
                    itemInfo.setIconVisible(visibleItemIcons.contains(String.valueOf(tag)));
                    break;

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
                    itemInfo.setAction(itemSources.get(tag));
                    itemInfo.setLabel(itemLables.get(tag));
                    itemInfo.setIconUrl(itemIcons.get(tag));
                    itemInfo.setImageUrl(itemImages.get(tag));
                    itemInfo.setIconVisible(visibleItemIcons.contains(String.valueOf(tag)));
                    break;

                case DesktopConstants.WEBSITE:
                    itemInfo.setLabel(itemLables.get(tag));
                    itemInfo.setWebsiteUrl(itemSources.get(tag));
                    itemInfo.setImageUrl(itemImages.get(tag));
                    itemInfo.setIconVisible(visibleItemIcons.contains(String.valueOf(tag)));
                    break;

                /* BEGIN ************************** 未修改 ************************************** */
                case DesktopConstants.QIYI_COMMON_RECOMMENDATION:
                    itemInfo.setQiyiData(itemSources.get(tag));                        // recommendation's id
                    break;

                case DesktopConstants.QIYI_CHANNEL:
                    itemInfo.setLabel(itemLables.get(tag));                            // label
                    itemInfo.setIconUrl(itemIcons.get(tag));                           // icon
                    itemInfo.setQiyiData(itemSources.get(tag));                        // channel's id
                    break;

                case DesktopConstants.QIYI_SPECIALIZED_PAGE:
                    itemInfo.setLabel(itemLables.get(tag));
                    itemInfo.setIconUrl(itemIcons.get(tag));
                    itemInfo.setQiyiData(itemSources.get(tag));
                    break;
                /* END ************************** 未修改 ************************************** */

                // 播视广场舞模块
                case DesktopConstants.BOOSJ_DANCE_EXCHANGE:
                case DesktopConstants.BOOSJ_DANCE_RECOMMEND:
                case DesktopConstants.BOOSJ_DANCE_EXCLUSIVE:
                case DesktopConstants.BOOSJ_DANCE_ACTIVITY:
                case DesktopConstants.BOOSJ_DANCE_MUSIC:
                case DesktopConstants.BOOSJ_DANCE_GOLD_TEACHER:
                case DesktopConstants.BOOSJ_DANCE_DAREN:
                case DesktopConstants.BOOSJ_DANCE_HEALTH:
                case DesktopConstants.BOOSJ_DANCE_SQUARE:
                    itemInfo.setLabel(itemLables.get(tag));   // label
                    itemInfo.setIconUrl(itemIcons.get(tag));  // icon
                    itemInfo.setParam1(itemSources.get(tag)); // url
                    itemInfo.setImageUrl(itemImages.get(tag));
                    itemInfo.setIconVisible(visibleItemIcons.contains(String.valueOf(tag)));
                    break;

                case DesktopConstants.IKTV_ITEM_RADITIONAL_OPERA:
                    itemInfo.setLabel(itemLables.get(tag));   // label
                    itemInfo.setIconUrl(itemIcons.get(tag));  // icon
                    itemInfo.setParam1(itemSources.get(tag)); // url
                    itemInfo.setImageUrl(itemImages.get(tag));
                    itemInfo.setIconVisible(visibleItemIcons.contains(String.valueOf(tag)));
                    break;
                // 播视广场舞分类
                case DesktopConstants.BOOSJ_DANCE_CLASSIFICATION:
                    itemInfo.setLabel(itemLables.get(tag));   // label
                    itemInfo.setIconUrl(itemIcons.get(tag));  // icon
                    String source = itemSources.get(tag);
                    if (!StringUtils.isEmpty(source)) {
                        String[] split = source.split("##");
                        if (split.length == 2) {
                            itemInfo.setParam1(split[0]);      // root id
                            itemInfo.setParam2(split[1]);      // id
                        }
                    }
                    itemInfo.setImageUrl(itemImages.get(tag));
                    itemInfo.setIconVisible(visibleItemIcons.contains(String.valueOf(tag)));
                    break;

            }

            itemInfos.add(itemInfo);
        }

        if (!CollectionUtils.isEmpty(itemInfos)) {
            DesktopItemManager.getInstance(mContext).clearItemInfos();
            DesktopItemManager.getInstance(mContext).saveItemInfos(itemInfos);
        }
    }

    /**
     * 启动主界面
     * <p>
     * Created by lancy on 2017/12/14 18:12
     */
    private void launchMainUI() {
        mContext.startActivity(new Intent(mContext, MainActivity.class));
        this.finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 屏蔽所有按键
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mCompositeDisposable.clear();
    }
}
