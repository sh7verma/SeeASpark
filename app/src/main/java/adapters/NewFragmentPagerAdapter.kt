package adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import fragments.WalkthroughAFragment
import fragments.WalkthroughBFragment

/**
 * Created by applify on 6/5/2018.
 */
class NewFragmentPagerAdapter(fragmentManager: FragmentManager, mContext: Context) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return WalkthroughAFragment.newInstance(position)
            1 -> return WalkthroughBFragment.newInstance(position)
            2 -> return WalkthroughBFragment.newInstance(position)
            3 -> return WalkthroughBFragment.newInstance(position)
            else -> return WalkthroughBFragment.newInstance(position)
        }
    }

    override fun getCount() = 5
}