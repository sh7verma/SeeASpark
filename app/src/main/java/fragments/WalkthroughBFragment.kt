package fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_walkthrough.view.*


class WalkthroughBFragment : Fragment() {

    var itemView: View? = null
    private val walkArray = intArrayOf(R.mipmap.walk1, R.mipmap.walk2, R.mipmap.walk3, R.mipmap.walk4, R.mipmap.walk5)
    var positionLocal: Int = 0
    private val textArray = arrayListOf<String>("Explore hundreds of events related to your field.",
            "Explore hundreds of events related to your field.",
            "Go through the articles written by fellow mentors.",
            "Create notes and share with mentees who will give feedback and ask queries.",
            "Chat with mentors or mentees. You can even share photos, videos, files, and notes.")

    companion object {
        fun newInstance(position: Int): Fragment {
            val fragment = WalkthroughBFragment()
            val bundle = Bundle()
            bundle.putInt("position", position)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_walkthgrough, container, false)
        if (getArguments() != null) {
            positionLocal = getArguments()!!.getInt("position");
        }
        Picasso.with(activity).load(walkArray!![positionLocal]).into(itemView!!.imgWalk)
        itemView!!.txtWalk.text = textArray[positionLocal]
        return itemView
    }

}