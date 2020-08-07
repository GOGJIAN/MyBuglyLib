package com.shimao.mybuglylib.core

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import com.shimao.mybuglylib.data.ICallBack
import com.shimao.mybuglylib.data.db.CrashDatabase
import com.shimao.mybuglylib.data.model.ActivityEvent
import com.shimao.mybuglylib.data.model.FragmentEvent
import com.shimao.mybuglylib.util.BIUtil

/**
 * @author : jian
 * @date   : 2020/7/17 16:11
 * @version: 1.0
 */
class JJBugActivityLifecycleCallBack : ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
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

        if (activity::class.java.name == JJBugReport.getInstance().mainActivity){
            BIUtil()
                .setType(BIUtil.TYPE_BEHAVIOR)
                .setCtx(
                    BIUtil.CtxBuilder()
                        .kv("activitys", JJBugReport.getInstance().getActivityString())
                        .kv("fragments",JJBugReport.getInstance().getFragmentString())
                        .kv("clicks",JJBugReport.getInstance().getClickString())
                        .build())
                .execute(null)
            JJBugReport.getInstance().clearRecord()
        }
    }
}