package fragments

import adapters.CommunityAdapter
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
import kotlinx.android.synthetic.main.fragment_community.*
import kotlinx.android.synthetic.main.fragment_event.*
import models.BaseSuccessModel
import models.PostModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import utils.Utils

class CommunityFragment : Fragment(), View.OnClickListener {

    var itemView: View? = null
    var mUtils: Utils? = null
    var mContext: Context? = null
    var mLandingInstance: LandingActivity? = null

    private var mLayoutManager: LinearLayoutManager? = null
    private var mCommunityAdapter: CommunityAdapter? = null
    private var mCommunityArray = ArrayList<PostModel.ResponseBean>()
    private var mCommunityFragment: CommunityFragment? = null

    private var totalItemCount: Int = 0
    private var lastVisibleItem: Int = 0
    private var visibleThreshold: Int = 3
    var isLoading: Boolean = false
    private var mOffset: Int = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_community, container, false)
        return itemView
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mContext = activity
        mCommunityFragment = this
        mLandingInstance = activity as LandingActivity
        mUtils = Utils(activity)
        initUI()
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun initUI() {
        if (mUtils!!.getInt("nightMode", 0) == 1)
            displayNightMode()
        else
            displayDayMode()

        txtTitleCustom.text = getString(R.string.community)

        imgBackCustom.setImageResource(R.mipmap.ic_bookmark)

        imgOption1Custom.setImageResource(R.mipmap.ic_search)
        imgOption1Custom.visibility = View.VISIBLE

        imgOption2Custom.setImageResource(R.mipmap.ic_add_top_bar)
        imgOption2Custom.visibility = View.VISIBLE

        mLayoutManager = LinearLayoutManager(mContext)
        rvCommunityListing.layoutManager = mLayoutManager
        rvCommunityListing.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleThreshold = mLayoutManager!!.childCount
                totalItemCount = mLayoutManager!!.itemCount;
                lastVisibleItem = mLayoutManager!!.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mCommunityArray.size > 5) {
                        if (mLandingInstance!!.connectedToInternet()) {
                            mOffset++
                            val postModel = PostModel.ResponseBean()
                            postModel.post_type = Constants.PROGRESS
                            mCommunityArray.add(postModel)
                            rvCommunityListing.post(Runnable { mCommunityAdapter!!.notifyItemInserted(mCommunityArray.size - 1) })
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

        srlCommunity.setColorSchemeResources(R.color.colorPrimary)
        srlCommunity.setOnRefreshListener {
            isLoading = false
            mOffset = 1
            hitAPI(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayDayMode() {
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.white_color))
        imgBackCustom.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        txtTitleCustom.setTextColor(ContextCompat.getColor(activity, R.color.black_color))
        imgOption1Custom.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        imgOption2Custom.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        rvCommunityListing.setBackgroundColor(ContextCompat.getColor(activity, R.color.background))
        txtNoCommunityListing.setTextColor(mLandingInstance!!.blackColor)
        llMainCommunityFrag.setBackgroundColor(mLandingInstance!!.whiteColor)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayNightMode() {
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))
        imgBackCustom.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        txtTitleCustom.setTextColor(ContextCompat.getColor(activity, R.color.white_color))
        imgOption1Custom.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        imgOption2Custom.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        rvCommunityListing.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))
        txtNoCommunityListing.setTextColor(mLandingInstance!!.whiteColor)
        llMainCommunityFrag.setBackgroundColor(mLandingInstance!!.blackColor)
    }

    private fun onCreateStuff() {

        if (mLandingInstance!!.connectedToInternet()) {
            if (mLandingInstance!!.db!!.getPostsByType(Constants.COMMUNITY).size > 0) {
                hitAPI(false)
                populateData()
            } else {
                hitAPI(true)
            }
        } else
            mLandingInstance!!.showInternetAlert(rvCommunityListing)
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
                intent = Intent(mContext, CommunityBookmarkActivity::class.java)
                startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
            }
            imgOption1Custom -> {
                intent = Intent(mContext, SearchActivity::class.java)
                intent.putExtra("path", "community")
                startActivity(intent)
            }
            imgOption2Custom -> {
                intent = Intent(mContext, ShareIdeaActivity::class.java)
                intent.putExtra("path", "community")
                startActivity(intent)
            }
        }
    }

    private fun hitAPI(isLoaderVisible: Boolean) {
        if (isLoaderVisible)
            mLandingInstance!!.showLoader()
        val call = RetrofitClient.getInstance().getPosts(mLandingInstance!!.mUtils!!.getString("access_token", "")
                , Constants.COMMUNITY.toString(), mOffset)
        call.enqueue(object : Callback<PostModel> {

            override fun onResponse(call: Call<PostModel>?, response: Response<PostModel>) {
                if (mCommunityFragment != null) {
                    if (response.body().response != null) {
                        addToLocalDatabase(response.body().response)
                    } else {
                        if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                            Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                            mLandingInstance!!.moveToSplash()
                        } else
                            mLandingInstance!!.showAlert(rvCommunityListing, response.body().error!!.message!!)
                    }
                    if (isLoaderVisible)
                        mLandingInstance!!.dismissLoader()
                }
            }

            override fun onFailure(call: Call<PostModel>?, t: Throwable?) {
                if (mCommunityFragment != null) {
                    if (isLoaderVisible)
                        mLandingInstance!!.dismissLoader()
                    mLandingInstance!!.showAlert(rvCommunityListing, t!!.localizedMessage)
                }
            }
        })
    }

    private fun addToLocalDatabase(response: List<PostModel.ResponseBean>) {
        if (mOffset == 1)
            mLandingInstance!!.db!!.deleteCommunityData(Constants.COMMUNITY)
        else {
            isLoading = response.isEmpty()
        }
        for (responseData in response) {
            mLandingInstance!!.db!!.addPosts(responseData)
            for (imagesData in responseData.images) {
                mLandingInstance!!.db!!.addPostImages(imagesData, responseData.id.toString(), Constants.COMMUNITY)
            }
        }
        populateData()
    }

    private fun populateData() {
        if (llMainCommunityFrag != null) {

            if (srlCommunity.isRefreshing)
                srlCommunity.isRefreshing = false

            if (mOffset == 1)
                mCommunityArray.clear()
            else {
                mCommunityArray.removeAt(mCommunityArray.size - 1)
                mCommunityAdapter!!.notifyItemRemoved(mCommunityArray.size)
            }

            mCommunityArray.addAll(mLandingInstance!!.db!!.getPostsByType(Constants.COMMUNITY))

            if (mCommunityArray.size == 0) {
                rvCommunityListing.visibility = View.GONE
                txtNoCommunityListing.visibility = View.VISIBLE
            } else {
                rvCommunityListing.visibility = View.VISIBLE
                txtNoCommunityListing.visibility = View.GONE
                if (rvCommunityListing.adapter == null) {
                    mCommunityAdapter = CommunityAdapter(mCommunityArray, mContext!!, mCommunityFragment)
                    rvCommunityListing.adapter = mCommunityAdapter
                } else {
                    mCommunityAdapter!!.notifyDataSetChanged()
                }
            }
        }
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
                        mLandingInstance!!.showAlert(rvCommunityListing, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                /// change db status to previous
                setPreviousDBStatus(postId)
            }
        })
    }

    fun updateBookmarkStatus(bookmarked: Int, postId: Int) {
        mLandingInstance!!.db!!.updateBookmarkStatus(postId, bookmarked)
        hitBookmarkAPI(bookmarked, postId)
    }

    private fun setPreviousDBStatus(postId: Int) {

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
                        mLandingInstance!!.showAlert(rvCommunityListing, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                mLandingInstance!!.showAlert(rvCommunityListing, t!!.localizedMessage)
            }
        })

    }

    private var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (intent.getIntExtra("status", 0) == Constants.LIKED) {
                    Log.e("Liked = ", "Yes")
                    for ((index, postData) in mCommunityArray.withIndex()) {
                        if (postData.id == intent.getIntExtra("postId", 0)
                                && postData.liked == Constants.UNLIKED) {
                            postData.liked = Constants.LIKED
                            postData.like++
                            mCommunityArray[index] = postData
                            break
                        }
                    }
                    mCommunityAdapter!!.notifyDataSetChanged()
                } else if (intent.getIntExtra("status", 0) == Constants.UNLIKED) {
                    Log.e("Liked = ", "No")
                    for ((index, postData) in mCommunityArray.withIndex()) {
                        if (postData.id == intent.getIntExtra("postId", 0)
                                && postData.liked == Constants.LIKED) {
                            postData.liked = Constants.UNLIKED
                            postData.like--
                            mCommunityArray[index] = postData
                            break
                        }
                    }
                    mCommunityAdapter!!.notifyDataSetChanged()
                } else if (intent.getIntExtra("status", 0) == Constants.BOOKMARK) {
                    Log.e("Bookmark = ", "Yes")
                    for ((index, postData) in mCommunityArray.withIndex()) {
                        if (postData.id == intent.getIntExtra("postId", 0)) {
                            postData.bookmarked = intent.getIntExtra("bookmarkStatus", 0)
                            mCommunityArray[index] = postData
                            break
                        }
                    }
                    mCommunityAdapter!!.notifyDataSetChanged()
                } else if (intent.getIntExtra("status", 0) == Constants.COMMENT) {
                    Log.e("comment = ", "Yes")
                    for ((index, postData) in mCommunityArray.withIndex()) {
                        if (postData.id == intent.getIntExtra("postId", 0)) {
                            postData.comment = intent.getIntExtra("commentCount", 0)
                            mCommunityArray[index] = postData
                            break
                        }
                    }
                    mCommunityAdapter!!.notifyDataSetChanged()
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
        for ((index, postData) in mCommunityArray.withIndex()) {
            if (postData.id == postId) {
                mLandingInstance!!.db!!.deletePostById(postData.id)
                mCommunityArray.remove(postData)
                mCommunityAdapter!!.notifyDataSetChanged()
                break
            }
        }
    }

    fun moveToCommunityDetail(communityId: Int, imgCommunityListing: ImageView) {
        if (mLandingInstance!!.connectedToInternet()) {
            val intent = Intent(mContext, CommunityDetailActivity::class.java)
            intent.putExtra("communityId", communityId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val option = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        imgCommunityListing, getString(R.string.transition_image))
                activity.startActivity(intent, option.toBundle())
            } else {
                startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
        } else
            mLandingInstance!!.showInternetAlert(rvCommunityListing)
    }

    private var nightModeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
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