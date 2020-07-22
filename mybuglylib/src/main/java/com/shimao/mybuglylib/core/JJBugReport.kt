package com.shimao.mybuglylib.core

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.shimao.mybuglylib.data.ICallBack
import com.shimao.mybuglylib.data.db.CrashDatabase
import com.shimao.mybuglylib.data.model.ActivityEvent
import com.shimao.mybuglylib.data.model.ClickEvent
import com.shimao.mybuglylib.data.model.FragmentEvent
import com.shimao.mybuglylib.util.BIUtil
import com.shimao.mybuglylib.util.PublicParams
import com.shimao.mybuglylib.util.Util
import java.lang.IllegalArgumentException

/**
 * @author : jian
 * @date   : 2020/7/17 11:53
 * @version: 1.0
 */
class JJBugReport private constructor() {
    companion object{
        private var sInstance : JJBugReport ?= null

        fun getInstance():JJBugReport{
            if(sInstance == null){
                synchronized(JJBugReport::class.java){
                    if (sInstance == null){
                        sInstance = JJBugReport()
                    }
                }
            }
            return sInstance!!
        }

    }
    var sActivityList = mutableListOf<ActivityEvent>()
    var sFragmentList = mutableListOf<FragmentEvent>()
    var sClickList = mutableListOf<ClickEvent>()
    var sBaseUrl:String = ""
    var sUA:String? = null
    var sIsDebug:Boolean = false
    var sContext:Context? = null
    lateinit var sApplication:String

    private var sCallback: JJBugCallBack? = null
    var sDelay: Long = 250

    fun init(context: Context?){
        if (context == null) throw NullPointerException("Context can not be null!")
        if(sBaseUrl.isEmpty()) throw IllegalArgumentException("base url can not be empty!")
        sContext = if (context !is Application) context.applicationContext else context
        sApplication = sContext!!.packageManager.getApplicationLabel((sContext as Application).applicationInfo).toString()
        sIsDebug = sContext!!.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        PublicParams.retrievePublicInfo(sContext!!)
        if (!Util.isMainProcess(context)){
            throw IllegalStateException("Must init at MainProcess!")
        }
        JJBugHandler.newInstance(Thread.getDefaultUncaughtExceptionHandler()).setCallback(sCallback).register()
        registerRecoveryLifecycleCallback()
        CrashDatabase.init(context)
        ClickIntercept.init()
        Thread(Runnable {
            CrashDatabase.get().crashDao().deleteAlreadyPost()
            val list = CrashDatabase.get().crashDao().getAllUnPostData()
            for (crash in list){
                BIUtil()
                    .setType(BIUtil.TYPE_CRASH)
                    .setCtx(
                        BIUtil.CtxBuilder()
                        .kv("message",crash.message)
                        .kv("exception",crash.exception)
                        .kv("stack",crash.stack)
                        .kv("activitys", crash.activitys)
                        .kv("fragments",crash.fragments)
                        .kv("clicks",crash.clicks)
                        .build())
                    .execute(object : ICallBack.CallBackImpl<Any>(){
                        override fun onNext(data: Any?) {
                            Log.d("TAGTAG","next")
                            CrashDatabase.get().crashDao().updateStatusById(crash.id)
                        }

                        override fun onError(e: String) {
                            Log.d("TAGTAG","error")
                        }
                    })
            }
        }).start()

    }

    private fun registerRecoveryLifecycleCallback() {
        (sContext as Application).registerActivityLifecycleCallbacks(JJBugActivityLifecycleCallBack())
    }

    fun callback(callback: JJBugCallBack?): JJBugReport {
        sCallback = callback
        return this
    }

    fun baseUrl(baseUrl:String): JJBugReport{
        sBaseUrl = baseUrl
        return this
    }

    fun applicationName(name: String): JJBugReport {
        sApplication = name
        return this
    }

    fun delay(delay: Long): JJBugReport {
        sDelay = delay
        return this
    }
}