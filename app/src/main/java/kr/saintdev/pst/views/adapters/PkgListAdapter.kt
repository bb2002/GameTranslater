package kr.saintdev.pst.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.android.synthetic.main.item_applist.view.*
import kr.saintdev.pst.R
import kr.saintdev.pst.libs.util.InstalledPackageManager

class PkgListAdapter(val items: ArrayList<InstalledPackageManager.ApplicationObject>) : BaseAdapter() {
    private val itemViews = arrayListOf<View>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = parent.context

        val view = if(convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_applist, parent, false)
        } else {
            convertView
        }

        val iconView = view.findViewById<CircularImageView>(R.id.appitem_icon)
        val appNameView = view.findViewById<TextView>(R.id.appitem_name)

        val (appName, pkgName, appIcon) = items[position]
        iconView.setImageDrawable(appIcon)
        appNameView.text = appName

        this.itemViews.add(view)

        return view
    }

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = items.size

    fun getCheckedItems() : ArrayList<Int> {
        val checkedItem = arrayListOf<Int>()

        for (i in 0 until itemViews.size) {
            val v = itemViews[i]
            if(v.checkbox.isChecked) checkedItem.add(i)
        }

        return checkedItem
    }
}