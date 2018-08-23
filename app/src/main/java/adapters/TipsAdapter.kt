package adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_tips.view.*
import utils.Utils

class TipsAdapter(mImageArray: IntArray, mContentArray: ArrayList<String>, mDescArray: ArrayList<String>, mContext: Context, avatar: String) : PagerAdapter() {
    var mImageArray: IntArray? = null
    var mContentArray = ArrayList<String>()
    var mDescArray = ArrayList<String>()
    var mContext: Context? = null
    var mUtils: Utils? = null
    private var width = 0
    private var height = 0
    private var avatar: String? = null

    init {
        this.mImageArray = mImageArray
        this.mDescArray = mDescArray
        this.mContentArray = mContentArray
        this.mContext = mContext
        mUtils = Utils(mContext)
        this.avatar = avatar
        var drawable = ContextCompat.getDrawable(mContext!!, R.mipmap.ic_ava_ob)

        width = drawable!!.intrinsicWidth
        height = drawable.intrinsicHeight
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as View
    }

    override fun getCount() = mImageArray!!.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container?.context).inflate(R.layout.item_tips, container, false)

        if (position == 0) {
            Picasso.with(mContext).load(avatar)
                    .resize(width, height).into(view.imgTipIcon)
        } else {
            view.imgTipIcon.setImageResource(mImageArray!![position])
        }
            view.txtTipTitle.text = mContentArray[position]
            view.txtTipDesc.text = mDescArray[position]
            container.addView(view)

        return view
    }

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) {
        parent.removeView(`object` as View)
    }
}