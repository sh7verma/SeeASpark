package fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_walkthrough.*
import kotlinx.android.synthetic.main.item_walkthrough.view.*


class WalkthroughAFragment : Fragment() {

    var count: Int = 0
    var itemView: View? = null
    private val walkArray = intArrayOf(R.mipmap.walk1a, R.mipmap.walk1, R.mipmap.walk1c, R.mipmap.walk1b)
    private val textArray = arrayListOf<String>("Go through thousands of mentor or mentee profiles",
            "You can also view it in Night mode.",
            "Swipe right to handshake with the mentor or mentee you want to connect.",
            "Swipe left to pass or move on to other profile")

    val mHandler = Handler()
    var runnable: Runnable? = null
    var temp = 0

    companion object {
        fun newInstance(position: Int): Fragment {
            val fragment = WalkthroughAFragment()
            val bundle = Bundle()
            bundle.putInt("position", position)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_walkthgrough, container, false)
        count = 0
        Picasso.with(activity!!).load(walkArray!![count]).into(itemView!!.imgWalk)
        itemView!!.txtWalk.text = textArray[0]
        temp = 1
        return itemView
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            val runnable: Runnable = object : Runnable {
                override fun run() {
                    count++
                    if (count < 4) {
                        itemView!!.txtWalk.text = textArray[count]
                        if (count == 1)
                            Picasso.with(activity).load(walkArray[count]).placeholder(walkArray[0]).into(itemView!!.imgWalk)
                        else if (count == 2)
                            Picasso.with(activity).load(walkArray[count]).placeholder(walkArray[1]).into(itemView!!.imgWalk)
                        else if (count == 3)
                            Picasso.with(activity).load(walkArray[count]).placeholder(walkArray[2]).into(itemView!!.imgWalk)
                        mHandler.postDelayed(this, 4000)
                    }
                }
            }
            mHandler.postDelayed(runnable, 4000)
        } else {
            if (runnable != null)
                mHandler.removeCallbacks(runnable)
            if (temp == 1) {
                count = 0
                Picasso.with(activity!!).load(walkArray[count]).into(itemView!!.imgWalk)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}