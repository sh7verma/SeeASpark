package adapters

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.seeaspark.R
import com.squareup.picasso.Picasso
import models.CardsDisplayModel
import utils.Utils


class HomeCardSwipeAdapter(context: Context, resource: Int, mCardArray: ArrayList<CardsDisplayModel>) :
        ArrayAdapter<CardsDisplayModel>(context, resource, mCardArray) {

    var mContext: Context? = null
    var mCardArray = ArrayList<CardsDisplayModel>()
    private var lastPosition = -1
    var mUtils: Utils? = null

    init {
        mContext = context
        this.mCardArray = mCardArray
        mUtils = Utils(mContext)
    }

    override fun getView(position: Int, contentView: View?, parent: ViewGroup): View? {
        val holder: ViewHolder
        var cardView = contentView

        if (cardView == null) {
            val inflater = LayoutInflater.from(context)
            cardView = inflater.inflate(R.layout.item_swipe_card, parent, false)
            holder = ViewHolder(cardView)

            cardView.tag = holder
        } else {
            holder = cardView.tag as ViewHolder
        }

        Picasso.with(mContext).load(mCardArray[position].avatar.avtar_url)
                .placeholder(R.mipmap.ic_avatar_1)
                .into(holder.imgAvatarCard)
        holder.txtNameCard?.text = mCardArray[position].full_name
        holder.txtBioCard?.text = mCardArray[position].bio
        holder.txtProfessionCard?.text = mCardArray[position].profession_id
        holder.txtSkillCard?.text = mCardArray[position].skills[0]
        if (mCardArray[position].skills.size == 1) {
            holder.txtSkillCountCard?.visibility = View.GONE
        } else {
            holder.txtSkillCountCard?.visibility = View.VISIBLE
            holder.txtSkillCountCard?.text = "+${mCardArray[position].skills.size - 1} more"
        }
        return cardView
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    inner class ViewHolder(itemView: View) {
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
}