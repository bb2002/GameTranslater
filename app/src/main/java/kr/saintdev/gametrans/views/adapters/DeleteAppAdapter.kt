package kr.saintdev.gametrans.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import com.mikhaellopez.circularimageview.CircularImageView
import kr.saintdev.gametrans.R
import kr.saintdev.gametrans.libs.util.InstalledPackageManager

class DeleteAppAdapter(val items: ArrayList<InstalledPackageManager.ApplicationObject>, val listener: OnDeleteClickListener) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = parent.context

        val view = if(convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_applist_delable, parent, false)
        } else {
            convertView
        }

        val iconView = view.findViewById<CircularImageView>(R.id.delable_appitem_icon)
        val appNameView = view.findViewById<TextView>(R.id.delable_appitem_name)
        val delButton = view.findViewById<ImageButton>(R.id.delable_appitem_remove)

        val (appName, pkgName, appIcon) = items[position]
        iconView.setImageDrawable(appIcon)
        appNameView.text = appName
        delButton.setOnClickListener {
            listener.onClick(it, position)
        }

        return view
    }

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = items.size

    fun remove(idx: Int) {
        items.removeAt(idx)
        notifyDataSetChanged()
    }

    interface OnDeleteClickListener {
        fun onClick(view: View, idx: Int)
    }
}