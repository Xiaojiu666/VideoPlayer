package com.sn.videoplayer.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.sn.videoplayer.R


class SettingItemView(context: Context, var attributes: AttributeSet?) :
    LinearLayout(context, attributes) {
    var itemTitle: TextView? = null
    var itemSubTitle: TextView? = null

    init {
        val settingItemView = View.inflate(context, R.layout.layout_setting_item, this)
        itemTitle = settingItemView.findViewById(R.id.setting_title)
        itemSubTitle = settingItemView.findViewById(R.id.setting_sub_title)
        initAttrs()
    }

    private fun initAttrs() {
        val ta: TypedArray = context.obtainStyledAttributes(attributes, R.styleable.SettingItemView)

        val title = ta.getString(R.styleable.SettingItemView_item_title);
        val subTitle = ta.getString(R.styleable.SettingItemView_item_sub_title);
        itemTitle!!.text = title
        itemSubTitle!!.text = subTitle
        ta.recycle()
    }
}