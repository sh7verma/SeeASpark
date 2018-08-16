package fragments

import adapters.EventsAdapter
import android.app.Fragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.seeaspark.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.android.synthetic.main.fragment_home.*
import models.BaseSuccessModel
import models.PostModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import utils.Utils

class EventsFragment : Fragment(), View.OnClickListener {

    var itemView: View? = null
    var mUtils: Utils? = null
    var mContext: Context? = null
    var mLandingInstance: LandingActivity? = null

    private var mLayoutManager: LinearLayoutManager? = null
    private var mEventsAdapter: EventsAdapter? = null
    private var mEventsArray = ArrayList<PostModel.ResponseBean>()
    private var mEventFragment: EventsFragment? = null

    private var totalItemCount: Int = 0
    private var lastVisibleItem: Int = 0
    private var visibleThreshold: Int = 3
    var isLoading: Boolean = false
    private var mOffset: Int = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_event, container, false)
        return itemView
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mContext = activity
        mEventFragment = this
        mLandingInstance = activity as LandingActivity
        initUI()
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun initUI() {

        txtTitleCustom.text = getString(R.string.Events)

        imgBackCustom.setImageResource(R.mipmap.ic_bookmark)

        imgOption1Custom.setImageResource(R.mipmap.ic_search)
        imgOption1Custom.visibility = View.VISIBLE

        imgOption2Custom.setImageResource(R.mipmap.ic_add_top_bar)
        imgOption2Custom.visibility = View.VISIBLE

        mLayoutManager = LinearLayoutManager(mContext)

        rvEventsListing.layoutManager = mLayoutManager

        rvEventsListing.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleThreshold = mLayoutManager!!.childCount
                totalItemCount = mLayoutManager!!.itemCount;
                lastVisibleItem = mLayoutManager!!.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mEventsArray.size > 5) {
                        if (mLandingInstance!!.connectedToInternet()) {
                            mOffset++
                            val postModel = PostModel.ResponseBean()
                            postModel.post_type = Constants.PROGRESS
                            mEventsArray.add(postModel)
                            rvEventsListing.post(Runnable { mEventsAdapter!!.notifyItemInserted(mEventsArray.size - 1) })
                            hitAPI(false)
                            isLoading = true
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        srlEvents.setColorSchemeResources(R.color.colorPrimary)
        srlEvents.setOnRefreshListener {
            isLoading = false
            mOffset = 1
            hitAPI(false)
        }

        mUtils = Utils(activity)
        if (mUtils!!.getInt("nightMode", 0) == 1)
            displayNightMode()
        else
            displayDayMode()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayDayMode() {
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.white_color))
        imgBackCustom.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        txtTitleCustom.setTextColor(ContextCompat.getColor(activity, R.color.black_color))
        imgOption1Custom.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        imgOption2Custom.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        rvEventsListing.setBackgroundColor(ContextCompat.getColor(activity, R.color.background))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayNightMode() {
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))
        imgBackCustom.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        txtTitleCustom.setTextColor(ContextCompat.getColor(activity, R.color.white_color))
        imgOption1Custom.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        imgOption2Custom.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        rvEventsListing.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))
    }

    private fun onCreateStuff() {
        if (mLandingInstance!!.connectedToInternet()) {
            if (mLandingInstance!!.db!!.getPostsByType(Constants.EVENT).size > 0) {
                hitAPI(false)
                populateData()
            } else {
                hitAPI(true)
            }
        } else
            mLandingInstance!!.showInternetAlert(rvEventsListing)
    }

    private fun hitAPI(isLoaderVisible: Boolean) {
        if (isLoaderVisible)
            mLandingInstance!!.showLoader()
        val call = RetrofitClient.getInstance().getPosts(mLandingInstance!!.mUtils!!.getString("access_token", "")
                , Constants.EVENT.toString(), mOffset)
        call.enqueue(object : Callback<PostModel> {

            override fun onResponse(call: Call<PostModel>?, response: Response<PostModel>) {
                if (response.body().response != null) {
                    addToLocalDatabase(response.body().response)
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        mLandingInstance!!.moveToSplash()
                    } else
                        mLandingInstance!!.showAlert(rvEventsListing, response.body().error!!.message!!)
                }
                if (isLoaderVisible)
                    mLandingInstance!!.dismissLoader()
            }

            override fun onFailure(call: Call<PostModel>?, t: Throwable?) {
                if (isLoaderVisible)
                    mLandingInstance!!.dismissLoader()
                mLandingInstance!!.showAlert(rvEventsListing, t!!.localizedMessage)
            }
        })
    }

    private fun addToLocalDatabase(response: List<PostModel.ResponseBean>) {
        if (mOffset == 1)
            mLandingInstance!!.db!!.deleteEventData(Constants.EVENT)
        else
            isLoading = response.isEmpty()

        for (responseData in response) {
            mLandingInstance!!.db!!.addPosts(responseData)
            for (imagesData in responseData.images) {
                mLandingInstance!!.db!!.addPostImages(imagesData, responseData.id.toString(), Constants.EVENT)
            }
            for (goingUserData in responseData.going_list) {
                mLandingInstance!!.db!!.addPostGoingUsers(goingUserData, responseData.id.toString())
            }
        }
        populateData()
    }

    private fun populateData() {
        if (llMainEventFrag != null) {

            if (srlEvents.isRefreshing)
                srlEvents.isRefreshing = false

            if (mOffset == 1)
                mEventsArray.clear()
            else {
                mEventsArray.removeAt(mEventsArray.size - 1)
                mEventsAdapter!!.notifyItemRemoved(mEventsArray.size)
            }

            mEventsArray.addAll(mLandingInstance!!.db!!.getPostsByType(Constants.EVENT))
            if (rvEventsListing.adapter == null) {
                if (mEventsArray.size == 0) {
                    txtNoEventsListing.visibility = View.VISIBLE
                } else {
                    mEventsAdapter = EventsAdapter(mContext, mEventsArray, mEventFragment)
                    rvEventsListing.adapter = mEventsAdapter
                }
            } else {
                mEventsAdapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun initListener() {
        imgBackCustom.setOnClickListener(this)
        imgOption1Custom.setOnClickListener(this)
        imgOption2Custom.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        var intent: Intent? = null
        when (view) {
            imgBackCustom -> {
                intent = Intent(mContext, EventsBookmarkActivity::class.java)
                startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
            }
            imgOption1Custom -> {
                intent = Intent(mContext, SearchActivity::class.java)
                intent.putExtra("path", "events")
                startActivity(intent)
            }
            imgOption2Custom -> {
                intent = Intent(mContext, ShareIdeaActivity::class.java)
                intent.putExtra("path", "event")
                startActivity(intent)
            }
        }
    }

    fun moveToEventDetail(id: Int, imgEventsListing: ImageView) {
        if (mLandingInstance!!.connectedToInternet()) {
            val intent = Intent(mContext, EventsDetailActivity::class.java)
            intent.putExtra("eventId", id)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val option = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        imgEventsListing, getString(R.string.transition_image))
                activity.startActivity(intent, option.toBundle())
            } else {
                startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
        } else
            mLandingInstance!!.showInternetAlert(rvEventsListing)
    }

    override fun onStart() {
        LocalBroadcastManager.getInstance(activity).registerReceiver(receiver,
                IntentFilter(Constants.POST_BROADCAST))
        LocalBroadcastManager.getInstance(activity).registerReceiver(nightModeReceiver,
                IntentFilter(Constants.NIGHT_MODE))
        super.onStart()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiver)
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(nightModeReceiver)
        super.onDestroy()
    }

    fun updateLikeStatus(likedStatus: Int, postId: Int, likeCount: Int) {
        mLandingInstance!!.db!!.updateLikeStatus(postId, likedStatus, likeCount)
        hitLikeAPI(postId)
    }

    private fun hitLikeAPI(postId: Int) {
        val call = RetrofitClient.getInstance().postActivity(mUtils!!.getString("access_token", ""),
                postId, Constants.LIKE)
        call.enqueue(object : Callback<BaseSuccessModel> {
            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>?) {
                if (response!!.body().response == null) {
                    /// change db status to previous
                    setPreviousDBStatus(postId)
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        mLandingInstance!!.moveToSplash()
                    } else if (response.body().error!!.code == Constants.POST_DELETED) {
                        mLandingInstance!!.showToast(mContext!!, response.body().error!!.message!!)
                        removeElement(postId)
                    } else
                        mLandingInstance!!.showAlert(rvEventsListing, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                /// change db status to previous
                setPreviousDBStatus(postId)
            }
        })
    }

    private fun setPreviousDBStatus(postId: Int) {

    }

    fun updateBookmarkStatus(bookmarked: Int, postId: Int) {
        mLandingInstance!!.db!!.updateBookmarkStatus(postId, bookmarked)
        hitBookmarkAPI(bookmarked, postId)
    }

    private fun hitBookmarkAPI(bookmarked: Int, postId: Int) {
        val call = RetrofitClient.getInstance().markBookmark(mUtils!!.getString("access_token", ""),
                postId)
        call.enqueue(object : Callback<BaseSuccessModel> {

            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>) {
                if (response.body().response != null) {

                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        mLandingInstance!!.moveToSplash()
                    } else if (response.body().error!!.code == Constants.POST_DELETED) {
                        mLandingInstance!!.showToast(mContext!!, response.body().error!!.message!!)
                        removeElement(postId)
                    } else
                        mLandingInstance!!.showAlert(rvEventsListing, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                mLandingInstance!!.showAlert(rvEventsListing, t!!.localizedMessage)
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun resetData() {
        if (mUtils!!.getInt("nightMode", 0) == 1)
            displayNightMode()
        else
            displayDayMode()
        populateData()
    }

    private var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (intent.getIntExtra("status", 0) == Constants.LIKED) {
                    Log.e("Liked = ", "Yes")
                    for ((index, eventData) in mEventsArray.withIndex()) {
                        if (eventData.id == intent.getIntExtra("postId", 0)
                                && eventData.liked == Constants.UNLIKED) {
                            eventData.liked = Constants.LIKED
                            eventData.like++
                            mEventsArray[index] = eventData
                            break
                        }
                    }
                    mEventsAdapter!!.notifyDataSetChanged()
                } else if (intent.getIntExtra("status", 0) == Constants.UNLIKED) {
                    Log.e("Liked = ", "No")
                    for ((index, eventData) in mEventsArray.withIndex()) {
                        if (eventData.id == intent.getIntExtra("postId", 0)
                                && eventData.liked == Constants.LIKED) {
                            eventData.liked = Constants.UNLIKED
                            eventData.like--
                            mEventsArray[index] = eventData
                            break
                        }
                    }
                    mEventsAdapter!!.notifyDataSetChanged()
                } else if (intent.getIntExtra("status", 0) == Constants.BOOKMARK) {
                    Log.e("Bookmark = ", "Yes")
                    for ((index, eventData) in mEventsArray.withIndex()) {
                        if (eventData.id == intent.getIntExtra("postId", 0)) {
                            eventData.bookmarked = intent.getIntExtra("bookmarkStatus", 0)
                            mEventsArray[index] = eventData
                            break
                        }
                    }
                    mEventsAdapter!!.notifyDataSetChanged()
                } else if (intent.getIntExtra("status", 0) == Constants.COMMENT) {
                    Log.e("comment = ", "Yes")
                    for ((index, eventData) in mEventsArray.withIndex()) {
                        if (eventData.id == intent.getIntExtra("postId", 0)) {
                            eventData.comment = intent.getIntExtra("commentCount", 0)
                            mEventsArray[index] = eventData
                            break
                        }
                    }
                    mEventsAdapter!!.notifyDataSetChanged()
                } else if (intent.getIntExtra("status", 0) == Constants.DELETE) {
                    Log.e("Delete = ", "Yes")
                    removeElement(intent.getIntExtra("postId", 0))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun removeElement(postId: Int) {
        for ((index, postData) in mEventsArray.withIndex()) {
            if (postData.id == postId) {
                mLandingInstance!!.db!!.deletePostById(postId)
                mEventsArray.remove(postData)
                mEventsAdapter!!.notifyDataSetChanged()
                break
            }
        }
    }

    var nightModeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onReceive(context: Context, intent: Intent) {
            isLoading = false
            mOffset = 1
            if (intent.getIntExtra("status", 0) == Constants.DAY) {
                populateData()
                displayDayMode()
            } else {
                populateData()
                displayNightMode()
            }
        }
    }
}