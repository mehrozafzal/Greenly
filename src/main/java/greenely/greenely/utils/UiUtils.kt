package greenely.greenely.utils

import android.app.Activity
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.nex3z.notificationbadge.NotificationBadge
import greenely.greenely.R
import io.intercom.android.sdk.Intercom

fun TextView.underlineText() {
    this.paintFlags = this.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG
}

fun ViewPager2.onPageSelected(selection: (Int) -> Unit) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) = selection(position)

        override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
        ) = Unit

        override fun onPageScrollStateChanged(state: Int) = Unit
    })

}

fun NotificationBadge.initialize(parentView: View) {
    this.setNumber(Intercom.client().unreadConversationCount, true)
    Intercom.client().addUnreadConversationCountListener {
        this.setNumber(Intercom.client().unreadConversationCount, true)
    }
    parentView.setOnClickListener {
        Intercom.client().displayMessenger()
    }
}


fun TextView.setCustomTextStyle(styleID: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.setTextAppearance(styleID)
    } else
        this.setTextAppearance(context, styleID)
}

fun FragmentManager.replaceCurrentFragment(fragment: Fragment) {
    this.beginTransaction().replace(R.id.content, fragment)
            .commit()
}


fun FragmentManager.replaceFragmentWithHorizontalSlideAnimation(fragment: Fragment) {
    this
            ?.beginTransaction()
            ?.addToBackStack(fragment.javaClass.simpleName)
            ?.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right)
            ?.replace(R.id.content, fragment)
            ?.commit()
}

fun FragmentManager.addFragmentWithHorizontalSlideAnimation(fragment: Fragment) {
    this
            ?.beginTransaction()
            ?.addToBackStack(fragment.javaClass.simpleName)
            ?.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right)
            ?.add(R.id.content, fragment)
            ?.commit()
}

fun FragmentManager.replaceFragmentWithBottomUpAnimation(fragment: Fragment) {
    this
            ?.beginTransaction()
            ?.addToBackStack(fragment.javaClass.simpleName)
            ?.setCustomAnimations(
                    R.anim.slide_up_animation,
                    0,
                    0,
                    R.anim.slide_out_down)
            ?.replace(R.id.content, fragment)
            ?.commit()
}

fun Toolbar.setRightBackGreenIconAndNavigation(activity: Activity?) {
    setNavigationIcon(R.drawable.ic_arrow_back_green)
    setBackDefaultNavigation(activity)
}

fun Toolbar.setCloseGreenIconAndNavigation(activity: Activity?) {
    setNavigationIcon(R.drawable.ic_close_green)
    setBackDefaultNavigation(activity)
}

fun Toolbar.setBackDefaultNavigation(activity: Activity?) {
    setNavigationOnClickListener {
        activity?.onBackPressed()
    }
}


fun TextView.strikeThroughText() {
    this.paintFlags = this.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG
}

internal fun Drawable.tint(@ColorInt color: Int): Drawable {
    val wrapped = DrawableCompat.wrap(this)
    DrawableCompat.setTint(wrapped, color)
    return wrapped
}





