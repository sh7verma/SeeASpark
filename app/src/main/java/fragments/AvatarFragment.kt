package fragments

import adapters.AvatarAdapter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.fragment_avatar.*


class AvatarFragment : Fragment() {
    var mCreateProfileInstance: CreateProfileActivity? = null
    var itemView: View? = null
    var mAvatarAdapter: AvatarAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_avatar, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mCreateProfileInstance = activity as CreateProfileActivity
        onCreateStuff()
        super.onActivityCreated(savedInstanceState)
    }

    private fun onCreateStuff() {
        rvAvatar.layoutManager = GridLayoutManager(activity!!, 3) as RecyclerView.LayoutManager?
        mAvatarAdapter = AvatarAdapter(ArrayList<String>(), activity!!,mCreateProfileInstance!!)
        rvAvatar.adapter = mAvatarAdapter
    }
}