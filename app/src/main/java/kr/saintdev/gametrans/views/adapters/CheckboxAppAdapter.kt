package kr.saintdev.gametrans.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.mikhaellopez.circularimageview.CircularImageView
import kr.saintdev.gametrans.R
import kr.saintdev.gametrans.libs.util.InstalledPackageManager

class CheckboxAppAdapter(val items: List<InstalledPackageManager.ApplicationObject>) : BaseAdapter() {
    private val checkedItemsIndex = arrayListOf<Int>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = parent.context

        val view = if(convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_applist_checkbox, parent, false)
        } else {
            convertView
        }

        val iconView = view.findViewById<CircularImageView>(R.id.appitem_icon)
        val appNameView = view.findViewById<TextView>(R.id.appitem_name)

        val (appName, pkgName, appIcon) = items[position]
        iconView.setImageDrawable(appIcon)
        appNameView.text = appName

        val ckBox = view.findViewById<CheckBox>(R.id.appitem_checkbox)
        ckBox.setOnCheckedChangeListener {
            _, bool ->
            if(bool) {
                checkedItemsIndex.add(position)
            } else {
                checkedItemsIndex.remove(position)
            }
        }

        return view
    }

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = items.size

    fun getCheckedItems() : ArrayList<Int> {
        return checkedItemsIndex
    }
}