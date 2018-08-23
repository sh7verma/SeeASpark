package adapters

import android.content.Context
import android.os.Handler
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_walkthrough.*
import kotlinx.android.synthetic.main.item_walkthrough.view.*
import utils.Utils


class WalkthroughAdapter(mWalkArray: IntArray, mWalkDataArray: ArrayList<String>, mContext: Context) : PagerAdapter() {
    var mWalkArray: IntArray? = null
    var mWalkDataArray = ArrayList<String>()
    var mContext: Context? = null
    var mUtils: Utils? = null

    init {
        this.mWalkArray = mWalkArray
        this.mWalkDataArray = mWalkDataArray
        this.mContext = mContext
        mUtils = Utils(mContext)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return view === `object` as View
    }

    override fun getCount() = mWalkArray!!.size - 2

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container?.context).inflate(R.layout.item_walkthrough, container, false)
        Picasso.with(mContext).load(mWalkArray!![mUtils!!.getInt("walk_position", 0)]).into(view.imgWalk)
//        view.txtWalk.setText(mContentArray[position])
        container.addView(view)
        return view
    }

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) {
        // Remove the view from view group specified position
        parent.removeView(`object` as View)
    }

}