package money.freedompay.testsdk.extencions

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import money.freedompay.testsdk.R
import money.freedompay.testsdk.ui.fragments.RootFragment

fun FragmentActivity.replaceFragment(
    target: Fragment,
    @IdRes containerId: Int = R.id.container,
    addToBackStack: Boolean = true
) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(containerId, target, target.javaClass.name)
    if (addToBackStack) transaction.addToBackStack(target.javaClass.name)
    transaction.commit()
}

fun FragmentActivity.popBackStack() {
    if (supportFragmentManager.backStackEntryCount > 1) {
        supportFragmentManager.popBackStack()
    } else {
        finish()
    }
}

fun Fragment.popBackStack() {
    val fragmentManager = parentFragment?.childFragmentManager ?: requireActivity().supportFragmentManager
    if (fragmentManager.fragments.size > 1) {
        fragmentManager.popBackStack()
    } else {
        requireActivity().popBackStack()
    }
}

fun Fragment.backToRoot() {
    val fragmentManager = parentFragment?.childFragmentManager ?: requireActivity().supportFragmentManager
    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    this.replaceFragment(RootFragment())
}

fun Fragment.replaceFragment(
    target: Fragment,
    @IdRes containerId: Int = R.id.container,
    addToBackStack: Boolean = true,
    fragmentManager: FragmentManager = requireActivity().supportFragmentManager
) {
    val transaction = fragmentManager.beginTransaction()
    transaction.setCustomAnimations(
        R.anim.nav_enter,
        R.anim.nav_exit,
        R.anim.nav_pop_enter,
        R.anim.nav_pop_exit
    )
    transaction.replace(containerId, target, target.javaClass.name)
    if (addToBackStack) transaction.addToBackStack(target.javaClass.name)
    transaction.commit()
}

fun Fragment.addFragment(
    target: Fragment,
    @IdRes containerId: Int = R.id.container,
    fragmentManager: FragmentManager = requireActivity().supportFragmentManager
) {
    val transaction = fragmentManager.beginTransaction()
    transaction.setCustomAnimations(
        R.anim.nav_enter,
        R.anim.nav_exit,
        R.anim.nav_pop_enter,
        R.anim.nav_pop_exit
    )
    transaction.add(containerId, target, target.javaClass.name)
    transaction.addToBackStack(target.javaClass.name)
    transaction.commit()
}
