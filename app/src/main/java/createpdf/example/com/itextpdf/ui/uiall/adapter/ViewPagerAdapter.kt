package createpdf.example.com.itextpdf.ui.uiall.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import java.util.ArrayList


class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val mFragmentList = ArrayList<Fragment>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return ""
    }

    fun addFragment(position: Int, fragment: Fragment) {
        mFragmentList.add(position, fragment)
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    fun setListFragment(fragmentList: List<Fragment>) {
        mFragmentList.addAll(fragmentList)
        notifyDataSetChanged()
    }
}