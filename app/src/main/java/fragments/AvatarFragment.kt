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
import models.AvatarModel
import models.SignupModel


class AvatarFragment : Fragment(), View.OnClickListener {

    var mCreateProfileInstance: CreateProfileActivity? = null
    var itemView: View? = null
    var mAvatarAdapter: AvatarAdapter? = null
    var mAvatarArray = ArrayList<AvatarModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_avatar, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mCreateProfileInstance = activity as CreateProfileActivity
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    private fun initListener() {
        imgBackAvatar.setOnClickListener(this)

    }

    private fun onCreateStuff() {

    }

    override fun onClick(view: View?) {
        when (view) {
            imgBackAvatar -> {
                mCreateProfileInstance!!.moveToPrevious()
            }
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            if (mCreateProfileInstance!!.mGender != 0) {
                mAvatarArray.clear()
                if (mCreateProfileInstance!!.mGender != 3) {
                    for (avatar: AvatarModel in mCreateProfileInstance!!.profileData!!.avatars) {
                        if (avatar.gender == mCreateProfileInstance!!.mGender)
                            mAvatarArray.add(avatar)
                    }
                } else
                    mAvatarArray.addAll(mCreateProfileInstance!!.profileData!!.avatars)

                rvAvatar.layoutManager = GridLayoutManager(activity!!, 3) as RecyclerView.LayoutManager?
                mAvatarAdapter = AvatarAdapter(mAvatarArray,
                        activity!!, mCreateProfileInstance!!)
                rvAvatar.adapter = mAvatarAdapter
            }
        }
    }
}