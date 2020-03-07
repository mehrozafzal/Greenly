package greenely.greenely.gamification.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class GamificationPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private var titleList: MutableList<String> = ArrayList()
    private var fragmentList: MutableList<Fragment> = ArrayList()


    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(title: String, fragment: Fragment) {
        titleList.add(title)
        fragmentList.add(fragment)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }

}