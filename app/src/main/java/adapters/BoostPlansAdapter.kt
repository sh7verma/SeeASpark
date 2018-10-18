package adapters

import android.os.CountDownTimer
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.seeaspark.R
import fragments.BoostFragment
import fragments.HomeCardSwipeFragment
import kotlinx.android.synthetic.main.item_boost_plans.view.*
import models.PlansModel
import utils.Constants
import utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class BoostPlansAdapter(private var mPlansArray: ArrayList<PlansModel.Response>,
                        boostFragment: BoostFragment?)
    : RecyclerView.Adapter<BoostPlansAdapter.ViewHolder>() {

    var mTimer: CountDownTimer? = null
    var mTimerTime: Long = 0
    var localFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
    var boughtPosition = -1

    private var boostFragment: BoostFragment? = null
    private var homeFragment: HomeCardSwipeFragment? = null

    init {
        this.boostFragment = boostFragment
    }

    constructor(mPlansArray: ArrayList<PlansModel.Response>,
                homeFragment: HomeCardSwipeFragment,
                boostFragment: BoostFragment?) : this(mPlansArray, boostFragment) {
        this.homeFragment = homeFragment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_boost_plans, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtBoostPlanCard.text = mPlansArray[position].duration
        holder.txtPlanType.text = mPlansArray[position].unit
        holder.txtBoostPlanCost.text = StringBuilder().append(Constants.POUND)
                .append(mPlansArray[position].amount)
        if (mPlansArray[position].is_expired == 0) {
            boughtPosition = position
            holder.llTimer.visibility = View.VISIBLE
            holder.txtTimerBoost.visibility = View.VISIBLE
            timer(mPlansArray[position].remaining_time, holder.txtTimerBoost)
        } else {
            holder.llTimer.visibility = View.GONE
            holder.txtTimerBoost.visibility = View.GONE
        }
        holder.rlBoostPlan.setOnClickListener {
            if (homeFragment != null)
                homeFragment!!.buyPlan(position)
            else
                boostFragment!!.buyPlan(position)
        }
    }

    override fun getItemCount(): Int {
        return mPlansArray.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtBoostPlanCard = itemView.txtBoostPlanCard!!
        val txtPlanType = itemView.txtPlanType!!
        val txtBoostPlanCost = itemView.txtBoostPlanCost!!
        val rlBoostPlan = itemView.rlBoostPlan!!
        val txtTimerBoost = itemView.txtTimerBoost!!
        val llTimer = itemView.llTimer!!
    }

    fun timer(time: String, txtTimerHome: TextView) {
        try {
            localFormat.timeZone = TimeZone.getTimeZone("UTC")
            val utcDate = localFormat.parse(time)
            val cal = Calendar.getInstance()
            cal.time = utcDate
            mTimerTime = cal.timeInMillis
            mTimer = object : CountDownTimer(mTimerTime, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // mTimerTime = millisUntilFinished;
                    val calLocal = Calendar.getInstance()
                    calLocal.timeInMillis = millisUntilFinished
                    txtTimerHome.text = localFormat.format(calLocal.time)
                }

                override fun onFinish() {
                    if (homeFragment != null)
                        homeFragment!!.isBuyEnable = true
                    else
                        boostFragment!!.isBuyEnable = true
                    mPlansArray[boughtPosition].is_expired = 1
                    notifyDataSetChanged()
                }
            }
            mTimer!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}