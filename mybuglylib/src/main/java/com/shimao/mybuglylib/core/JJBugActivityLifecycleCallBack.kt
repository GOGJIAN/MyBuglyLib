package com.shimao.mybuglylib.core

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import com.shimao.mybuglylib.data.model.ActivityEvent
import com.shimao.mybuglylib.data.model.FragmentEvent

/**
 * @author : jian
 * @date   : 2020/7/17 16:11
 * @version: 1.0
 */
class JJBugActivityLifecycleCallBack : ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activity.window.decorView.viewTreeObserver.addOnGlobalLayoutListener {
            ClickIntercept.hookViews(
                activity.window.decorView,
                0
            )
        }


        JJBugReport.getInstance().addActivityRecord(
            ActivityEvent(
                System.currentTimeMillis(),
                activity::class.java.name,
                "onActivityCreated"
            )
        )
    }

    override fun onActivityStarted(activity: Activity) {
        JJBugReport.getInstance().addActivityRecord(
            ActivityEvent(
                System.currentTimeMillis(),
                activity::class.java.name,
                "onActivityStarted"
            )
        )
    }

    override fun onActivityResumed(activity: Activity) {

        JJBugReport.getInstance().addActivityRecord(
            ActivityEvent(
                System.currentTimeMillis(),
                activity::class.java.name,
                "onActivityResumed"
            )
        )
    }

    override fun onActivityPaused(activity: Activity) {
        JJBugReport.getInstance().addActivityRecord(
            ActivityEvent(
                System.currentTimeMillis(),
                activity::class.java.name,
                "onActivityPaused"
            )
        )
    }

    override fun onActivityStopped(activity: Activity) {
        JJBugReport.getInstance().addActivityRecord(
            ActivityEvent(
                System.currentTimeMillis(),
                activity::class.java.name,
                "onActivityStopped"
            )
        )
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        JJBugReport.getInstance().addActivityRecord(
            ActivityEvent(
                System.currentTimeMillis(),
                activity::class.java.name,
                "onActivitySaveInstanceState"
            )
        )
    }

    override fun onActivityDestroyed(activity: Activity) {
        JJBugReport.getInstance().addActivityRecord(
            ActivityEvent(
                System.currentTimeMillis(),
                activity::class.java.name,
                "onActivityDestroyed"
            )
        )
    }
}