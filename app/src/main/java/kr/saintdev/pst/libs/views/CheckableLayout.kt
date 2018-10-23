package kr.saintdev.pst.libs.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.CheckBox
import android.widget.Checkable
import android.widget.RelativeLayout
import kr.saintdev.pst.R

class CheckableLayout(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs), Checkable {

    override fun isChecked() : Boolean {
        val cbBox = findViewById<CheckBox>(R.id.appitem_checkbox)
        return cbBox.isChecked
    }

    override fun toggle() {
        val cbBox = findViewById<CheckBox>(R.id.appitem_checkbox)
        isChecked = !cbBox.isChecked
    }

    override fun setChecked(checked: Boolean) {
        val cbBox = findViewById<CheckBox>(R.id.appitem_checkbox)
        if(cbBox.isChecked != checked) {
            cbBox.isChecked = checked
        }
    }
}