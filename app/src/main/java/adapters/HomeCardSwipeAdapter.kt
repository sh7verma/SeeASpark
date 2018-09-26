package adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.seeaspark.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_home_community.view.*
import kotlinx.android.synthetic.main.item_home_events.view.*
import models.CardsDisplayModel
import utils.Constants
import utils.Utils


class HomeCardSwipeAdapter(context: Context, resource: Int,
                           mCardArray: ArrayList<CardsDisplayModel>) :
        ArrayAdapter<CardsDisplayModel>(context, resource, mCardArray) {

    var mContext: Context? = null
    var mCardArray = ArrayList<CardsDisplayModel>()
    var mUtils: Utils? = null
    private var mWidthCommunity = 0
    private var mScreenWidth: Int = 0

    init {
        mContext = context
        this.mCardArray = mCardArray
        mUtils = Utils(mContext)
        mScreenWidth = mUtils!!.getInt("width", 0)
        mWidthCommunity = mScreenWidth - mScreenWidth / 9
    }

    override fun getView(position: Int, contentView: View?, parent: ViewGroup): View? {
        var cardView = contentView
        val type = getItemViewType(position)

        var holderCard: CardViewHolder? = null
        var holderCommunity: CommunityViewHolder? = null
        var holderEvents: EventViewHolder? = null

        val inflater = LayoutInflater.from(context)
        when (type) {
            Constants.COMMUNITY -> {
                if (cardView == null) {
                    cardView = inflater.inflate(R.layout.item_home_community, parent, false)
                    holderCommunity = CommunityViewHolder(cardView)
                    cardView.tag = holderCommunity
                } else {
                    holderCommunity = cardView.tag as CommunityViewHolder
                }

                holderCommunity.txtCommunityTitle.text = mCardArray[position].title
                holderCommunity.txtCommunityDesc.text = mCardArray[position].description
                holderCommunity.txtCenterOption.text = mCardArray[position].profession_id

                if (mCardArray[position].date_time.isNotEmpty())
                    holderCommunity.txtDateCommunity.text = Constants.displayDateTime(mCardArray[position].date_time)

                Picasso.with(mContext).load(mCardArray[position].images[0].image_url)
                        .fit().into(holderCommunity.imgCommunityListing)
            }
            Constants.EVENT -> {
                if (cardView == null) {
                    cardView = inflater.inflate(R.layout.item_home_events, parent, false)
                    holderEvents = EventViewHolder(cardView)
                    cardView.tag = holderEvents
                } else {
                    holderEvents = cardView.tag as EventViewHolder
                }

                holderEvents.txtEventNameCard.text = mCardArray[position].title
                holderEvents.txtEventDescCard.text = mCardArray[position].description
                holderEvents.txtEventTimeCard.text = Constants.displayDateTime(mCardArray[position].date_time)
                Picasso.with(mContext).load(mCardArray[position].images[0].image_url).fit()
                        .into(holderEvents.imgEventCard)
            }
            else -> {
                if (cardView == null) {
                    cardView = inflater.inflate(R.layout.item_swipe_card, parent, false)
                    holderCard = CardViewHolder(cardView)
                    cardView.tag = holderCard
                } else {
                    holderCard = cardView.tag as CardViewHolder
                }

                Picasso.with(mContext).load(mCardArray[position].avatar.avtar_url)
                        .placeholder(R.mipmap.ic_avatar_1)
                        .into(holderCard.imgAvatarCard)
                holderCard.txtNameCard?.text = mCardArray[position].full_name
                holderCard.txtBioCard?.text = mCardArray[position].bio
                holderCard.txtProfessionCard?.text = mCardArray[position].profession_id
                holderCard.txtSkillCard?.text = mCardArray[position].skills[0]
                if (mCardArray[position].skills.size == 1) {
                    holderCard.txtSkillCountCard?.visibility = View.GONE
                } else {
                    holderCard.txtSkillCountCard?.visibility = View.VISIBLE
                    holderCard.txtSkillCountCard?.text = "+${mCardArray[position].skills.size - 1} more"
                }
            }
        }
        return cardView
    }

    override fun getItemViewType(position: Int): Int {
        when (mCardArray[position].post_type) {
            1 -> return Constants.COMMUNITY
            2 -> return Constants.EVENT
            else -> Constants.CARD
        }
        return 0
    }

    override fun getCount(): Int {
        return mCardArray.size
    }

    inner class CardViewHolder(itemView: View) {
        var cvCardUser: CardView? = null
        var imgAvatarCard: ImageView? = null
        var txtNameCard: TextView? = null
        var txtProfessionCard: TextView? = null
        var txtBioCard: TextView? = null
        var txtSkillCard: TextView? = null
        var txtSkillCountCard: TextView? = null

        init {
            this.imgAvatarCard = itemView.findViewById(R.id.imgAvatarCard) as ImageView
            this.txtNameCard = itemView.findViewById(R.id.txtNameCard) as TextView
            this.txtProfessionCard = itemView.findViewById(R.id.txtProfessionCard) as TextView
            this.txtBioCard = itemView.findViewById(R.id.txtBioCard) as TextView
            this.txtSkillCard = itemView.findViewById(R.id.txtSkillCard) as TextView
            this.txtSkillCountCard = itemView.findViewById(R.id.txtSkillCountCard) as TextView
            this.cvCardUser = itemView.findViewById(R.id.cvCardUser) as CardView
            if (mUtils!!.getInt("nightMode", 0) == 1)
                displayNightMode()
            else
                displayDayMode()
        }

        private fun displayDayMode() {
            cvCardUser!!.setCardBackgroundColor(ContextCompat.getColor(mContext!!, R.color.white_color))
            txtSkillCard!!.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
            txtSkillCountCard!!.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
            txtBioCard!!.setTextColor(ContextCompat.getColor(mContext!!, R.color.darkGreyText))
        }

        private fun displayNightMode() {
            cvCardUser!!.setCardBackgroundColor(ContextCompat.getColor(mContext!!, R.color.cardview_dark_background))
            txtSkillCard!!.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
            txtSkillCountCard!!.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
            txtBioCard!!.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
        }
    }

    inner class CommunityViewHolder(itemView: View) {
        val cvClick = itemView.cvCommunityListing!!
        val imgCommunityListing = itemView.imgCommunityListing!!
        val txtCommunityTitle = itemView.txtCommunityTitle!!
        val txtDateCommunity = itemView.txtDateCommunity!!
        val txtCommunityDesc = itemView.txtCommunityDesc!!
        val txtCenterOption = itemView.txtCenterOption!!

        /* init {
             if (mUtils!!.getInt("nightMode", 0) == 1)
                 displayNightMode()
             else
                 displayDayMode()
         }

         private fun displayDayMode() {
             cvClick.setCardBackgroundColor(ContextCompat.getColor(mContext!!, R.color.white_color))
             txtCommunityTitle.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
             txtCommunityDesc.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
         }

         private fun displayNightMode() {
             cvClick.setCardBackgroundColor(ContextCompat.getColor(mContext!!, R.color.cardview_dark_background))
             txtCommunityTitle.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
             txtCommunityDesc.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
         }*/
    }

    inner class EventViewHolder(itemView: View) {
        val cvEventsHome = itemView.cvEventsHome!!
        val imgEventCard = itemView.imgEventCard!!
        val txtEventNameCard = itemView.txtEventNameCard!!
        val txtEventDescCard = itemView.txtEventDescCard!!
        val txtEventTimeCard = itemView.txtEventTimeCard!!
        val txtEventExploreCard = itemView.txtEventExploreCard!!

        init {
            if (mUtils!!.getInt("nightMode", 0) == 1)
                displayNightMode()
            else
                displayDayMode()
        }

        private fun displayNightMode() {
            cvEventsHome.setCardBackgroundColor(ContextCompat.getColor(mContext!!, R.color.cardview_dark_background))
        }

        private fun displayDayMode() {
            cvEventsHome.setCardBackgroundColor(ContextCompat.getColor(mContext!!, R.color.white_color))
        }
    }
}