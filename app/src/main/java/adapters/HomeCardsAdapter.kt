package adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daimajia.swipe.SwipeLayout
import com.seeaspark.R
import fragments.HomeFragment
import kotlinx.android.synthetic.main.item_cards.view.*
import models.CardModel


class HomeCardsAdapter(mCardsArray: ArrayList<CardModel>, mContext: Context, mWidth: Int, mHomeFragment: HomeFragment?)
    : RecyclerView.Adapter<HomeCardsAdapter.ViewHolder>() {

    private var mCardsArray = ArrayList<CardModel>()
    var mContext: Context? = null
    private var mScreenWidth: Int = 0
    private var mScreenCalculated: Int = 0
    private var elementPosition = -1
    private var isDraged = false
    private var mHomeFragment: HomeFragment? = null

    init {
        this.mCardsArray = mCardsArray
        this.mContext = mContext
        mScreenWidth = mWidth / 2
        mScreenCalculated = mScreenWidth / 100
        this.mHomeFragment = mHomeFragment;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_cards, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: HomeCardsAdapter.ViewHolder?, position: Int) {

        holder!!.llData.setOnClickListener {
            mHomeFragment!!.openShortProfile()
        }

        holder!!.swlCard.addSwipeListener(object : SwipeLayout.SwipeListener {
            override fun onOpen(layout: SwipeLayout?) {
                Log.e("onOpen = ", "Yes")
                if (isDraged) {
                    removeCard()
                }
            }

            override fun onUpdate(layout: SwipeLayout, leftOffset: Int, topOffset: Int) {
                Log.e("onUpdate = ", "Yes")
                isDraged = true
                elementPosition = holder.adapterPosition
                if (layout.dragEdge.name == mContext!!.getString(R.string.right)) {
                    /// in negative
                    if (leftOffset < mScreenCalculated - (mScreenCalculated + mScreenCalculated)) {
                        val points = leftOffset / mScreenCalculated / 100f
                        if (points >= -1.0)
                            holder.llHandshake.alpha = points - (points + points)
                    }

                } else if (layout.dragEdge.name == mContext!!.getString(R.string.left)) {
                    /// in positive
                    if (leftOffset > mScreenCalculated) {
                        val points = leftOffset / mScreenCalculated / 100f
                        if (points <= 1.0)
                            holder.llPass.alpha = points
                    }
                }
            }

            override fun onStartOpen(layout: SwipeLayout?) {
                Log.e("onStartOpen = ", "Yes")
            }

            override fun onStartClose(layout: SwipeLayout?) {
                Log.e("onStartClose = ", "Yes")
            }

            override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
                Log.e("onHandRelease = ", "Yes")
            }

            override fun onClose(layout: SwipeLayout?) {
                Log.e("onClose = ", "Yes")
            }

        })
    }

    private fun removeCard() {
        mCardsArray.removeAt(elementPosition)
        notifyItemRemoved(elementPosition)
        isDraged = false
        if (mCardsArray.size == 0)
            mHomeFragment!!.showOutOfCards()
    }

    override fun getItemCount(): Int {
        Log.e("Size = ", mCardsArray.size.toString())
        return mCardsArray.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var swlCard = itemView.swlCard!!
        var llHandshake = itemView.swlCard.llHandshake!!
        var llPass = itemView.swlCard.llPass!!
        var llData = itemView.llData

        init {
            swlCard.showMode = SwipeLayout.ShowMode.LayDown
            swlCard.addDrag(SwipeLayout.DragEdge.Right, swlCard.findViewWithTag(mContext!!.getString(R.string.handshake)))
            swlCard.addDrag(SwipeLayout.DragEdge.Left, swlCard.findViewWithTag(mContext!!.getString(R.string.pass)))
        }
    }

    /* override fun getSwipeLayoutResourceId(position: Int): Int {
         return R.id.swlCard;
     }*/
}