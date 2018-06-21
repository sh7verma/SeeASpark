package adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.daimajia.swipe.SwipeLayout
import com.seeaspark.R
import com.squareup.picasso.Picasso
import fragments.HomeFragment
import kotlinx.android.synthetic.main.item_cards.view.*
import kotlinx.android.synthetic.main.item_community.view.*
import kotlinx.android.synthetic.main.item_home_events.view.*
import kotlinx.android.synthetic.main.item_out_of_cards.view.*
import kotlinx.android.synthetic.main.item_progress.view.*
import models.CardsDisplayModel
import utils.Constants


class HomeCardsAdapter(mCardsArray: ArrayList<CardsDisplayModel>, mContext: Context, mWidth: Int, mHomeFragment: HomeFragment?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mCardsArray = ArrayList<CardsDisplayModel>()
    var mContext: Context? = null
    private var mScreenWidth: Int = 0
    private var mScreenCalculated: Int = 0
    private var elementPosition = -1
    private var isDraged = false
    private var mHomeFragment: HomeFragment? = null
    var width: Int = 0
    var height: Int = 0

    init {
        this.mCardsArray = mCardsArray
        this.mContext = mContext
        mScreenWidth = mWidth / 2
        mScreenCalculated = mScreenWidth / 100
        this.mHomeFragment = mHomeFragment

        var drawable = ContextCompat.getDrawable(mContext, R.mipmap.ic_avatar_1)
        width = drawable!!.intrinsicWidth
        height = drawable!!.intrinsicHeight
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        when (viewType) {
            Constants.COMMUNITY -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_community, parent, false)
                return CommunityViewHolder(view)
            }
            Constants.EVENT -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_events, parent, false)
                return EventViewHolder(view)
            }
            Constants.OUT_OF_CARD -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_out_of_cards, parent, false)
                return OutOfCardsViewHolder(view)
            }
            Constants.PROGRESS -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_progress, parent, false)
                return LoadMoreViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_cards, parent, false)
                return CardViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (mCardsArray[position].post_type) {
            Constants.COMMUNITY -> {
                (holder as CommunityViewHolder)
                holder.txtCommunityTitle.text = mCardsArray[position].title
                holder.txtCommunityDesc.text = mCardsArray[position].description
                holder.txtDateCommunity.text = Constants.displayDateTime(mCardsArray[position].date_time)

                Picasso.with(mContext).load(mCardsArray[position].images[0].image_url).fit().into(holder.imgCommunityHome)

                holder.cvClick.setOnClickListener {
                    Toast.makeText(mContext, mContext!!.getString(R.string.work_in_progress), Toast.LENGTH_LONG).show()
                }
            }
            Constants.EVENT -> {
                (holder as EventViewHolder)
                holder.txtEventNameCard.text = mCardsArray[position].title
                holder.txtEventDescCard.text = mCardsArray[position].description
                holder.txtEventTimeCard.text = Constants.displayDateTime(mCardsArray[position].date_time)

                Picasso.with(mContext).load(mCardsArray[position].images[0].image_url).fit().into(holder.imgEventCard)

                holder.txtEventExploreCard.setOnClickListener {
                    Toast.makeText(mContext, mContext!!.getString(R.string.work_in_progress), Toast.LENGTH_LONG).show()
                }
            }
            Constants.OUT_OF_CARD -> {
                (holder as OutOfCardsViewHolder)
            }
            Constants.PROGRESS -> {
                (holder as LoadMoreViewHolder)
            }
            else -> {
                (holder as CardViewHolder)
                holder.txtNameCard.text = mCardsArray[position].full_name

                holder.txtProfessionCard.text = mCardsArray[position].profession.name

                holder.txtSkillCard.text = mCardsArray[position].skills[0]

                Picasso.with(mContext).load(mCardsArray[position].avatar).resize(width, width).placeholder(R.mipmap.ic_avatar_1).into(holder.imgAvatarCard)

                if (mCardsArray[position].skills.size == 1) {
                    holder.txtSkillCountCard.visibility = View.GONE
                } else {
                    holder.txtSkillCountCard.visibility = View.VISIBLE
                    holder.txtSkillCountCard.text = "+${mCardsArray[position].skills.size - 1} more"
                }

                holder.llData.setOnClickListener {
                    mHomeFragment!!.openShortProfile(mCardsArray[position])
                }

                holder.swlCard.addSwipeListener(object : SwipeLayout.SwipeListener {
                    override fun onOpen(layout: SwipeLayout?) {
                        Log.e("onOpen = ", "Yes")
                        if (isDraged) {
                            var isSwiped = -1
                            if (layout!!.dragEdge.name == mContext!!.getString(R.string.right)) {
                                isSwiped = 1
                            } else {
                                isSwiped = 0
                            }
                            removeCard(isSwiped, mCardsArray[elementPosition].id)
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
        }


    }

    private fun removeCard(swiped: Int, id: Int) {
        mCardsArray.removeAt(elementPosition)
        notifyItemRemoved(elementPosition)
        isDraged = false
        mHomeFragment!!.swipeRightLeft(swiped, id)
    }

    override fun getItemCount(): Int {
        return mCardsArray.size
    }

    override fun getItemViewType(position: Int): Int {
        when (mCardsArray[position].post_type) {
            1 -> return Constants.COMMUNITY
            2 -> return Constants.EVENT
            3 -> return Constants.OUT_OF_CARD
            4 -> return Constants.PROGRESS
            else -> Constants.CARD
        }
        return 0
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var swlCard = itemView.swlCard!!
        var llHandshake = itemView.swlCard.llHandshake!!
        var llPass = itemView.swlCard.llPass!!
        var llData = itemView.llData!!
        var txtNameCard = itemView.txtNameCard!!
        var txtProfessionCard = itemView.txtProfessionCard!!
        var txtSkillCard = itemView.txtSkillCard!!
        var txtSkillCountCard = itemView.txtSkillCountCard!!
        var imgAvatarCard = itemView.imgAvatarCard!!

        init {
            swlCard.showMode = SwipeLayout.ShowMode.LayDown
            swlCard.addDrag(SwipeLayout.DragEdge.Right, swlCard.findViewWithTag(mContext!!.getString(R.string.handshake)))
            swlCard.addDrag(SwipeLayout.DragEdge.Left, swlCard.findViewWithTag(mContext!!.getString(R.string.pass)))
        }
    }

    inner class CommunityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvClick = itemView.cvClick!!
        val imgCommunityHome = itemView.imgCommunityHome!!
        val txtCommunityTitle = itemView.txtCommunityTitle!!
        val txtDateCommunity = itemView.txtDateCommunity!!
        val txtCommunityDesc = itemView.txtCommunityDesc!!
        val txtCenterOption = itemView.txtCenterOption!!
    }


    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgEventCard = itemView.imgEventCard!!
        val txtEventNameCard = itemView.txtEventNameCard!!
        val txtEventDescCard = itemView.txtEventDescCard!!
        val txtEventTimeCard = itemView.txtEventTimeCard!!
        val txtEventExploreCard = itemView.txtEventExploreCard!!
    }

    inner class OutOfCardsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPlanDetails = itemView.txtPlanDetails!!
    }

    inner class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar = itemView.progressBar!!
    }

}