package com.seeaspark

import adapters.TipsAdapter
import android.app.Fragment
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_landing.*
import models.SignupModel
import utils.Constants

class LandingActivity : BaseActivity() {

    var userData: SignupModel? = null
    private var currentPosition = 0

    override fun initUI() {
        displayLightModeUI()
    }

    private fun displayLightModeUI() {
        llBottomNavigation.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
    }

    override fun onCreateStuff() {
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)

        if (userData!!.response.user_type == Constants.MENTEE) {
            imgEvents.setImageResource(R.mipmap.ic_boost)
            imgEventsTips.setImageResource(R.mipmap.ic_boost_s)
        }
        /// adding home fragment
        replaceFragment(HomeFragment())
        displayTipsData()
    }

    override fun initListener() {
        imgNextTips.setOnClickListener(this)
        llHome.setOnClickListener(this)
        llNotes.setOnClickListener(this)
        llChat.setOnClickListener(this)
        llEvents.setOnClickListener(this)
        llCommunity.setOnClickListener(this)
        rlMainTips.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_landing

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            llHome -> {

            }
            llNotes -> {
                showToast(mContext!!, getString(R.string.work_in_progress))
            }
            llChat -> {
                showToast(mContext!!, getString(R.string.work_in_progress))
            }
            llEvents -> {
                showToast(mContext!!, getString(R.string.work_in_progress))
            }
            llCommunity -> {
                showToast(mContext!!, getString(R.string.work_in_progress))
            }
            imgNextTips -> {
                if (currentPosition < 5) {
                    currentPosition++
                    vpTips.currentItem = currentPosition
                } else {
                    rlMainTips.visibility = View.GONE
                    imgHome.setImageResource(R.mipmap.ic_home_s)
                }

                when (currentPosition) {
                    1 -> {
                        imgProfileTip.visibility = View.INVISIBLE
                        imgHomeTips.visibility = View.VISIBLE
                    }
                    2 -> {
                        imgHomeTips.visibility = View.INVISIBLE
                        imgNotesTips.visibility = View.VISIBLE
                    }
                    3 -> {
                        imgNotesTips.visibility = View.INVISIBLE
                        imgChatTips.visibility = View.VISIBLE
                    }
                    4 -> {
                        imgChatTips.visibility = View.INVISIBLE
                        imgEventsTips.visibility = View.VISIBLE
                    }
                    5 -> {
                        imgEventsTips.visibility = View.INVISIBLE
                        imgCommunityTips.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    internal fun alertLogoutDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("LOG OUT")
        alertDialog.setMessage("Are you sure you want to Log out?")
        alertDialog.setPositiveButton("CONFIRM") { dialog, which ->
            moveToSplash()
        }
        alertDialog.setNegativeButton("CANCEL") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }


    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slidedown_in, R.anim.slidedown_out)
    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().replace(R.id.llFragment, fragment, null).commit()
    }

    private fun displayTipsData() {

        val iconArray = intArrayOf(R.mipmap.ic_ava_ob, R.mipmap.ic_home_big, R.mipmap.ic_notes_big, R.mipmap.ic_chat_big,
                R.mipmap.ic_events_big, R.mipmap.ic_community_big)

        val titleArray = arrayListOf<String>(getString(R.string.Profile), getString(R.string.home), getString(R.string.notes),
                getString(R.string.chat), getString(R.string.events), getString(R.string.community))

        val descArray = arrayListOf<String>(getString(R.string.lorem_ipsum),
                getString(R.string.lorem_ipsum),
                getString(R.string.lorem_ipsum),
                getString(R.string.lorem_ipsum),
                getString(R.string.lorem_ipsum),
                getString(R.string.lorem_ipsum))


        val iconArrayMentee = intArrayOf(R.mipmap.ic_ava_ob, R.mipmap.ic_home_big, R.mipmap.ic_notes_big, R.mipmap.ic_chat_big,
                R.mipmap.ic_boost_big, R.mipmap.ic_community_big)

        val titleArrayMentee = arrayListOf<String>(getString(R.string.Profile), getString(R.string.home), getString(R.string.notes),
                getString(R.string.chat), getString(R.string.boost), getString(R.string.community))

        val descArrayMentee = arrayListOf<String>(getString(R.string.lorem_ipsum),
                getString(R.string.lorem_ipsum),
                getString(R.string.lorem_ipsum),
                getString(R.string.lorem_ipsum),
                getString(R.string.lorem_ipsum),
                getString(R.string.lorem_ipsum))

        if (userData!!.response.user_type == Constants.MENTOR)
            vpTips.adapter = TipsAdapter(iconArray, titleArray, descArray, mContext!!)
        else
            vpTips.adapter = TipsAdapter(iconArrayMentee, titleArrayMentee, descArrayMentee, mContext!!)

        cpIndicatorTips.setViewPager(vpTips)
        cpIndicatorTips.radius = 10f
        cpIndicatorTips.fillColor = Color.WHITE
        cpIndicatorTips.pageColor = ContextCompat.getColor(this, R.color.pagerColorCode)
    }


}