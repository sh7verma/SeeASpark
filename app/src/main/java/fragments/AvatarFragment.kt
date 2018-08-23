package fragments

import adapters.AvatarAdapter
import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_avatar.*
import models.AvatarModel


class AvatarFragment : Fragment(), View.OnClickListener {

    var mCreateProfileInstance: CreateProfileActivity? = null
    var itemView: View? = null
    var mAvatarAdapter: AvatarAdapter? = null
    var mAvatarArray = ArrayList<AvatarModel>()
    private var mAvatarFragment: AvatarFragment? = null

    private var mSkinsArray = ArrayList<AvatarModel.SkinsBean>()
    private var finalPosition = 0
    private var mPosition: Int = -1
    private var isPopupOpened = false
    private var mContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_avatar, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mCreateProfileInstance = activity as CreateProfileActivity
        mContext = activity
        mAvatarFragment = this
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    private fun initListener() {
        imgBackAvatar.setOnClickListener(this)
        rlAnimate.setOnClickListener(this)
        imgSkin1.setOnClickListener(this)
        imgSkin2.setOnClickListener(this)
        imgSkin3.setOnClickListener(this)
        imgSkin4.setOnClickListener(this)
        imgSkin5.setOnClickListener(this)
    }

    private fun onCreateStuff() {

    }

    override fun onClick(view: View?) {
        when (view) {
            rlAnimate -> {
                clickedOutSide()
                isPopupOpened = false
            }
            imgBackAvatar -> {
                mCreateProfileInstance!!.moveToPrevious()
            }
            imgSkin1 -> {
                Log.e("CLicked = ", "Skin1")
                if (isPopupOpened) {
                    selectSkinToneImage(0)
                    closeAnimation()
                    isPopupOpened = false
                }
            }
            imgSkin2 -> {
                Log.e("CLicked = ", "Skin2")
                if (isPopupOpened) {
                    selectSkinToneImage(1)
                    closeAnimation()
                    isPopupOpened = false
                }
            }
            imgSkin3 -> {
                Log.e("CLicked = ", "Skin3")
                if (isPopupOpened) {
                    selectSkinToneImage(2)
                    closeAnimation()
                    isPopupOpened = false
                }
            }
            imgSkin4 -> {
                Log.e("CLicked = ", "Skin4")
                if (isPopupOpened) {
                    selectSkinToneImage(3)
                    closeAnimation()
                    isPopupOpened = false
                }

            }
            imgSkin5 -> {
                Log.e("CLicked = ", "Skin5")
                if (isPopupOpened) {
                    selectSkinToneImage(4)
                    closeAnimation()
                    isPopupOpened = false
                }
            }
        }
    }

    private fun selectSkinToneImage(position: Int) {
        Picasso.with(mContext).load(mSkinsArray[position].avtar_url)
                .placeholder(R.drawable.placeholder_image)
                .resize(resources.getDimension(R.dimen._35sdp).toInt(), resources.getDimension(R.dimen._35sdp).toInt())
                .into(imgPopup)
        mCreateProfileInstance!!.mAvatarId = mSkinsArray[position].name
        mAvatarArray[mPosition].avtar_url = mSkinsArray[position].avtar_url
        mAvatarArray[mPosition].id = mSkinsArray[position].id
        mAvatarArray[mPosition].name = mSkinsArray[position].name
    }


    fun showAnimation(xImages: Int, yImages: Int, skins: List<AvatarModel.SkinsBean>, position: Int, imgAvatar: ImageView) {
        isPopupOpened = true
        mSkinsArray.clear()
        mSkinsArray.addAll(skins)
        finalPosition = position % 3
        Picasso.with(mContext).load(mAvatarArray[position].avtar_url)
                .placeholder(R.drawable.placeholder_image)
                .resize(resources.getDimension(R.dimen._35sdp).toInt(), resources.getDimension(R.dimen._35sdp).toInt())
                .into(imgPopup)
        clickedAnimation(imgAvatar, xImages, yImages, finalPosition)
        mPosition = position
    }

    private fun beginPopupAnimation(xImages: Int, yImages: Int) {
        imgPopup.x = xImages + (mCreateProfileInstance!!.mWidth / 32).toFloat()
        imgPopup.y = yImages - (mCreateProfileInstance!!.mHeight / 64).toFloat()

        val fromY = yImages - (mCreateProfileInstance!!.mHeight / 64).toFloat()
        val toY = yImages - (mCreateProfileInstance!!.mHeight / 6).toFloat()


        imgPopup.alpha = 1f
        val anim = ObjectAnimator.ofFloat(imgPopup, "translationY", fromY, toY)
        anim.duration = 200
        anim.start()
        rlAnimate.visibility = View.VISIBLE

        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                imgPopup.alpha = 0f
                setImages()
                imagesAnimation(xImages, yImages)
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })
    }

    private fun setImages() {
        Picasso.with(mContext).load(mSkinsArray[0].avtar_url)
                .placeholder(R.drawable.placeholder_image)
                .resize(resources.getDimension(R.dimen._35sdp).toInt(), resources.getDimension(R.dimen._35sdp).toInt())
                .into(imgSkin1)
        Picasso.with(mContext).load(mSkinsArray[1].avtar_url)
                .placeholder(R.drawable.placeholder_image)
                .resize(resources.getDimension(R.dimen._35sdp).toInt(), resources.getDimension(R.dimen._35sdp).toInt())
                .into(imgSkin2)
        Picasso.with(mContext).load(mSkinsArray[2].avtar_url)
                .placeholder(R.drawable.placeholder_image)
                .resize(resources.getDimension(R.dimen._35sdp).toInt(), resources.getDimension(R.dimen._35sdp).toInt())
                .into(imgSkin3)
        Picasso.with(mContext).load(mSkinsArray[3].avtar_url)
                .placeholder(R.drawable.placeholder_image)
                .resize(resources.getDimension(R.dimen._35sdp).toInt(), resources.getDimension(R.dimen._35sdp).toInt())
                .into(imgSkin4)
        Picasso.with(mContext).load(mSkinsArray[4].avtar_url)
                .placeholder(R.drawable.placeholder_image)
                .resize(resources.getDimension(R.dimen._35sdp).toInt(), resources.getDimension(R.dimen._35sdp).toInt())
                .into(imgSkin5)
    }

    private fun closePopupAnimation() {
        val fromY = imgPopup.y
        val toY = imgPopup.y + (mCreateProfileInstance!!.mHeight/6) .toFloat()

        imgPopup.alpha = 1f
        val anim = ObjectAnimator.ofFloat(imgPopup, "translationY", fromY, toY)
        anim.startDelay = 400
        anim.duration = 200
        anim.start()

        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                imgPopup.alpha = 0f
                rlAnimate.visibility = View.GONE
                mAvatarAdapter!!.notifyDataSetChanged()
                Handler().postDelayed({
                    mCreateProfileInstance!!.moveToNext()
                }, 500)
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })
    }

    private fun imagesAnimation(xImages: Int, yImages: Int) {
        var xFinal = xImages
        val yFinal = yImages - (mCreateProfileInstance!!.mHeight / 5)

        when (finalPosition) {
            0 -> {
                animateLeftToRightImages()
            }
            1 -> {
                xFinal -= (xFinal * .73).toInt()
                ObjectAnimator.ofFloat(imgSkin3, "translationX", 0f, ((mCreateProfileInstance!!.mWidth/7) * 2).toFloat()).setDuration(0).start()
                animateCenterImages()
            }
            2 -> {
                xFinal -= (xFinal * .84).toInt()
                animateRightToLeftImages()
            }
        }
        Log.e("Location = ", "$xFinal ,$yFinal")
        rlSkinImages.alpha = 1f
        rlSkinImages.x = xFinal.toFloat()
        rlSkinImages.y = yFinal.toFloat()
        rlBackgroundImages.x = xFinal.toFloat()
        rlBackgroundImages.y = yFinal.toFloat()

        beginAlphaAnimation()
    }

    private fun animateCenterImages() {
        translateCenterToLeftAnimate(imgSkin2, 1, 1)
        translateCenterToLeftAnimate(imgSkin1, 2, 2)

        translateCenterToRightAnimate(imgSkin4, 3, 1)
        translateCenterToRightAnimate(imgSkin5, 4, 2)
    }

    private fun animateLeftToRightImages() {
        translateRightAnimate(imgSkin1, 0)
        translateRightAnimate(imgSkin2, 1)
        translateRightAnimate(imgSkin3, 2)
        translateRightAnimate(imgSkin4, 3)
        translateRightAnimate(imgSkin5, 4)
    }

    private fun animateRightToLeftImages() {
        translateLeftAnimate(imgSkin5, 0)
        translateLeftAnimate(imgSkin4, 1)
        translateLeftAnimate(imgSkin3, 2)
        translateLeftAnimate(imgSkin2, 3)
        translateLeftAnimate(imgSkin1, 4)
    }

    private fun closeAnimation() {
        when (finalPosition) {
            0 -> {
                closeAlphaAnimation()
                closeRightToLeft()
                closePopupAnimation()
            }
            1 -> {
                closeAlphaAnimation()
                closeCenter()
                closePopupAnimation()
            }
            2 -> {
                closeAlphaAnimation()
                closeLeftToRight()
                closePopupAnimation()
            }
        }
    }

    private fun beginAlphaAnimation() {
        rlBackgroundImages.animate().alpha(1f).setStartDelay(200).duration = 300
    }

    private fun closeAlphaAnimation() {
        rlBackgroundImages.animate().alpha(0f).duration = 200
        rlSkinImages.animate().alpha(0f).setStartDelay(200).duration = 0
    }

    private fun clickedOutSide() {
        rlBackgroundImages.animate().alpha(0f).duration = 200
        rlSkinImages.animate().alpha(0f).setStartDelay(200).duration = 0
        mAvatarAdapter!!.notifyDataSetChanged()
        rlAnimate.visibility = View.GONE
    }

    private fun closeCenter() {
        translateRightCloseAnimate(imgSkin1, 0, 2, ((mCreateProfileInstance!!.mWidth/7) * 2).toFloat())
        translateRightCloseAnimate(imgSkin2, 1, 1, ((mCreateProfileInstance!!.mWidth/7) * 2).toFloat())
        translateLeftCloseAnimate(imgSkin4, 2, 1, ((mCreateProfileInstance!!.mWidth/7) * 2).toFloat())
        translateLeftCloseAnimate(imgSkin5, 3, 2, ((mCreateProfileInstance!!.mWidth/7) * 2).toFloat())
    }

    private fun closeRightToLeft() {
        translateLeftCloseAnimate(imgSkin5, 4, 4, 0f)
        translateLeftCloseAnimate(imgSkin4, 3, 3, 0f)
        translateLeftCloseAnimate(imgSkin3, 2, 2, 0f)
        translateLeftCloseAnimate(imgSkin2, 1, 1, 0f)
    }

    private fun closeLeftToRight() {
        translateRightCloseAnimate(imgSkin1, 0, 4, ((mCreateProfileInstance!!.mWidth / 7) * 4).toFloat())
        translateRightCloseAnimate(imgSkin2, 1, 3, ((mCreateProfileInstance!!.mWidth / 7) * 4).toFloat())
        translateRightCloseAnimate(imgSkin3, 2, 2, ((mCreateProfileInstance!!.mWidth / 7) * 4).toFloat())
        translateRightCloseAnimate(imgSkin4, 3, 1, ((mCreateProfileInstance!!.mWidth / 7) * 4).toFloat())
    }

    private fun translateRightCloseAnimate(imgSkin: ImageView, position: Int, durationAnim: Int, finalX: Float) {
        val fromX = ((mCreateProfileInstance!!.mWidth / 7) * position).toFloat()
        val duration = 80 * durationAnim
        val anim = ObjectAnimator.ofFloat(imgSkin, "translationX", fromX, finalX)
        anim.duration = duration.toLong()
        anim.start()
    }

    private fun translateLeftCloseAnimate(imgSkin: ImageView, position: Int, durationTime: Int, finalX: Float) {
        val fromX = ((mCreateProfileInstance!!.mWidth / 7) * position).toFloat()
        val duration = 80 * durationTime
        val anim = ObjectAnimator.ofFloat(imgSkin, "translationX", fromX, finalX)
        anim.duration = duration.toLong()
        anim.start()
    }

    private fun translateRightAnimate(imgSkin: ImageView, position: Int) {
        val finalX = ((mCreateProfileInstance!!.mWidth / 7) * position).toFloat()
        val duration = 80 * position
        val anim = ObjectAnimator.ofFloat(imgSkin, "translationX", 0f, finalX)
        anim.duration = duration.toLong()
        anim.start()
    }

    private fun translateCenterToRightAnimate(imgSkin: ImageView, position: Int, durationTime: Int) {
        /// 3 and 4
        val fromX = ((mCreateProfileInstance!!.mWidth / 7) * 2).toFloat()
        val toX = ((mCreateProfileInstance!!.mWidth / 7) * position).toFloat()
        val duration = 80 * durationTime
        val anim = ObjectAnimator.ofFloat(imgSkin, "translationX", fromX, toX)
        anim.duration = duration.toLong()
        anim.start()
    }

    private fun translateCenterToLeftAnimate(imgSkin: ImageView, position: Int, durationTime: Int) {
        /// 0 and 1
        val fromX = ((mCreateProfileInstance!!.mWidth / 7) * 2).toFloat()
        val toX = fromX - ((mCreateProfileInstance!!.mWidth / 7) * position).toFloat()
        val duration = 80 * durationTime
        val anim = ObjectAnimator.ofFloat(imgSkin, "translationX", fromX, toX)
        anim.duration = duration.toLong()
        anim.start()
    }

    private fun translateLeftAnimate(imgSkin: ImageView, position: Int) {
        val fromX = ((mCreateProfileInstance!!.mWidth / 7) * 4).toFloat()
        val toX = fromX - ((mCreateProfileInstance!!.mWidth / 7) * position).toFloat()
        val duration = 80 * position
        val anim = ObjectAnimator.ofFloat(imgSkin, "translationX", fromX, toX)
        anim.duration = duration.toLong()
        anim.start()
    }

    private fun clickedAnimation(imgAvatar: ImageView, xImages: Int, yImages: Int, finalPosition: Int) {
        val animationZoomOut = AnimationUtils.loadAnimation(mContext, R.anim.zoom_out)
        imgAvatar.startAnimation(animationZoomOut)
        val animationZoomIn = AnimationUtils.loadAnimation(mContext, R.anim.zoom_in)

        animationZoomOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                imgAvatar.startAnimation(animationZoomIn)
            }

            override fun onAnimationStart(p0: Animation?) {
            }
        })

        animationZoomIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                beginPopupAnimation(xImages, yImages)
            }

            override fun onAnimationStart(p0: Animation?) {
            }
        })
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            if (mCreateProfileInstance!!.mGender != 0) {
                mAvatarArray.clear()
                if (mCreateProfileInstance!!.mGender != 3) {
                    for (avatar: AvatarModel in mCreateProfileInstance!!.userData!!.avatars) {
                        if (avatar.gender == mCreateProfileInstance!!.mGender)
                            mAvatarArray.add(avatar)
                    }
                } else
                    mAvatarArray.addAll(mCreateProfileInstance!!.userData!!.avatars)

                rvAvatar.layoutManager = GridLayoutManager(activity!!, 3)
                mAvatarAdapter = AvatarAdapter(mAvatarArray, activity!!, mCreateProfileInstance!!, mAvatarFragment)
                rvAvatar.adapter = mAvatarAdapter
            }
        }
    }
}