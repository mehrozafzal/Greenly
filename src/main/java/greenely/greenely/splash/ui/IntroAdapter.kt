package greenely.greenely.splash.ui

import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import greenely.greenely.R


class IntroAdapter : androidx.viewpager.widget.PagerAdapter() {

    override fun getCount(): Int = 4

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val linearLayout: View

        when (position) {
            0 -> linearLayout = LayoutInflater.from(container.context).inflate(R.layout.splash_introduction_screen_one, null)
            1 -> linearLayout = LayoutInflater.from(container.context).inflate(R.layout.splash_introduction_screen_two, null)
            2 -> linearLayout = LayoutInflater.from(container.context).inflate(R.layout.splash_introduction_screen_three, null)
            3 -> linearLayout = LayoutInflater.from(container.context).inflate(R.layout.splash_intoduction_screen_four, null)

            else -> throw IndexOutOfBoundsException("No step for $position")
        }

        container.addView(linearLayout)
        return linearLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        if (obj is View) container.removeView(obj)
        else container.removeAllViews()
    }

}


