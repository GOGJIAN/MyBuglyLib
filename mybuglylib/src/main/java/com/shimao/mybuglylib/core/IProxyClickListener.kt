package com.shimao.mybuglylib.core

import android.view.View

/**
 * @author : jian
 * @date   : 2020/7/21 19:22
 * @version: 1.0
 */
interface IProxyClickListener {
    fun onProxyClick(wrap: WrapClickListener?, v: View): Boolean


    class WrapClickListener(
        l: View.OnClickListener?,
        proxyListener: IProxyClickListener?
    ) :
        View.OnClickListener {
        private var mProxyListener: IProxyClickListener? = proxyListener
        private var mBaseListener: View.OnClickListener? = l
        override fun onClick(v: View) {
            val handled =
                if (mProxyListener == null) false else mProxyListener!!.onProxyClick(
                    this@WrapClickListener,
                    v
                )
            if (!handled && mBaseListener != null) {
                mBaseListener!!.onClick(v)
            }
        }

    }
}