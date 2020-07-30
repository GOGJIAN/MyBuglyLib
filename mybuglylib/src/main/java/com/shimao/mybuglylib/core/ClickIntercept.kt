package com.shimao.mybuglylib.core

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.recyclerview.widget.RecyclerView
import com.shimao.mybuglylib.R
import com.shimao.mybuglylib.data.model.ClickEvent
import com.shimao.mybuglylib.util.BIUtil
import java.lang.reflect.Field
import java.lang.reflect.Method


/**
 * @author : jian
 * @date   : 2020/7/21 19:04
 * @version: 1.0
 */
object ClickIntercept {

    var sHookMethod: Method? = null
    var sHookField: Field? = null
    var mPrivateTagKey:Int = 325

    fun init() {
        if (sHookMethod == null) {
            try {
                val viewClass = Class.forName("android.view.View")
                if (viewClass != null) {
                    sHookMethod = viewClass.getDeclaredMethod("getListenerInfo")
                    if (sHookMethod != null) {
                        sHookMethod!!.isAccessible = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                BIUtil()
                    .setType(BIUtil.TYPE_CRASH_CAUGHT)
                    .setCtx(BIUtil.CtxBuilder()
                        .kv("message",e.message+"click_intercept_init")
                        .kv("exception",e::class.java.name)
                        .kv("stack",BIUtil.exception(e))
                        .build())
                    .execute(null)
            }
        }
        if (sHookField == null) {
            try {
                val listenerInfoClass =
                    Class.forName("android.view.View\$ListenerInfo")
                if (listenerInfoClass != null) {
                    sHookField = listenerInfoClass.getDeclaredField("mOnClickListener")
                    if (sHookField != null) {
                        sHookField!!.isAccessible = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                BIUtil()
                    .setType(BIUtil.TYPE_CRASH_CAUGHT)
                    .setCtx(BIUtil.CtxBuilder()
                        .kv("message",e.message+"click_intercept_init")
                        .kv("exception",e::class.java.name)
                        .kv("stack",BIUtil.exception(e))
                        .build())
                    .execute(null)
            }
        }
    }

    fun hookViews(view: View, recycledContainerDeep: Int) {
        var recycledContainerDeep = recycledContainerDeep
        if (view.getVisibility() === View.VISIBLE) {
            val forceHook = recycledContainerDeep == 1
            if (view is ViewGroup) {
                val existAncestorRecycle = recycledContainerDeep > 0
                val p = view as ViewGroup
                if (!(p is AbsListView || p is RecyclerView) || existAncestorRecycle) {
                    hookClickListener(view, recycledContainerDeep, forceHook)
                    if (existAncestorRecycle) {
                        recycledContainerDeep++
                    }
                } else {
                    recycledContainerDeep = 1
                }
                val childCount = p.childCount
                for (i in 0 until childCount) {
                    val child: View = p.getChildAt(i)
                    hookViews(child, recycledContainerDeep)
                }
            } else {
                hookClickListener(view, recycledContainerDeep, forceHook)
            }
        }
    }

    private fun hookClickListener(
        view: View,
        recycledContainerDeep: Int,
        forceHook: Boolean
    ) {
        var needHook = forceHook
        if (!needHook) {
            needHook = view.isClickable
            if (needHook && recycledContainerDeep == 0) {
                needHook = view.getTag(R.id.tag_hook) == null
            }
        }
        if (needHook) {
            try {
                val getListenerInfo = sHookMethod!!.invoke(view)
                val baseClickListener: View.OnClickListener? =
                    if (getListenerInfo == null) null else if(sHookField!![getListenerInfo]==null)null else sHookField!![getListenerInfo] as View.OnClickListener //获取已设置过的监听器
                if (baseClickListener != null && baseClickListener !is IProxyClickListener.WrapClickListener) {
                    sHookField!![getListenerInfo] =
                        IProxyClickListener.WrapClickListener(baseClickListener, object :IProxyClickListener{
                            override fun onProxyClick(
                                wrap: IProxyClickListener.WrapClickListener?,
                                v: View
                            ): Boolean {

                                val id:String = try {
                                    JJBugReport.getInstance().sContext!!.resources.getResourceEntryName(v.id)
                                }catch (e:Resources.NotFoundException){
                                    "null"
                                }
                                JJBugReport.getInstance().addClickEvent(ClickEvent(System.currentTimeMillis(),v.context::class.java.name,id))
                                return false
                            }
                        })
                    view.setTag(R.id.tag_hook, recycledContainerDeep)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }
}