package com.shimao.mybugly

import android.app.Application
import com.shimao.mybuglylib.core.JJBugReport

/**
 * @author : jian
 * @date   : 2020/7/17 11:50
 * @version: 1.0
 */
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        JJBugReport
            .getInstance()
            .baseUrl("https://m.lehe.com/api/jarvis/record/logger")
            .applicationName("demo")
            .delay(2500)
            .init(this)
    }
}