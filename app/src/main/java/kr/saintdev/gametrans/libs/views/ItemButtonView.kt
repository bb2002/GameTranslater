package kr.saintdev.gametrans.libs.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import kr.saintdev.gametrans.R
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.Toast


class ItemButtonView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {
    private lateinit var textView: TextView
    private lateinit var iconView: ImageView

    init {
        initView()
        getAttrs(attrs)
    }

    private fun initView() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_button, this, false)
        addView(view)

        this.textView = view.findViewById(R.id.item_text)
        this.iconView = view.findViewById(R.id.item_icon)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemButtonView)

        textView.setText(typedArray.getResourceId(R.styleable.ItemButtonView_text, R.string.none))
        iconView.setImageResource(typedArray.getResourceId(R.styleable.ItemButtonView_symbol, R.drawable.app_icon))
        typedArray.recycle()
    }

    fun setText(str: String) {
        textView.text = str
    }

    fun setIcon(bitmap: Bitmap) = iconView.setImageBitmap(bitmap)
}