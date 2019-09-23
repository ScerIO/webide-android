package io.scer.ide.ui.home.fragments

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.ComponentName
import android.content.Context
import android.os.IBinder
import android.content.ServiceConnection
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import io.scer.ide.R
import io.scer.ide.plugin.base.ITool
import io.scer.ui.ToolCard
import kotlinx.android.synthetic.main.fragment_tools.view.*

/**
 *
 */
class ToolsFragment : Fragment() {
    private val ACTION_PLUGIN = "io.scer.ide.intent.action.PLUGIN"
    private val CATEGORY_PLUGIN = "io.scer.ide.intent.category.PLUGIN"
    private var opServiceConnection: OpServiceConnection? = null
    private var opService: ITool? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tools, container, false)

        val pm = context!!.packageManager
        // Filter by ACTION_MAIN and CATEGORY_PLUGIN
        val queryIntent = Intent(ACTION_PLUGIN)
        queryIntent.addCategory(CATEGORY_PLUGIN)

        val pluginApps = ArrayList<ApplicationInfo>()
        pm.queryIntentServices(queryIntent, 0).forEach {
            if (it.serviceInfo != null) {
                opServiceConnection = OpServiceConnection()
//                pluginApps.add(it.serviceInfo.applicationInfo)
                Log.e("Detected plugin", it.serviceInfo.applicationInfo.packageName)
                val intent = Intent()
                intent.component = ComponentName(it.serviceInfo.packageName, it.serviceInfo.name)

//                val intent = activity!!.packageManager.getLaunchIntentForPackage(it.serviceInfo.packageName)
                activity!!.bindService(intent, opServiceConnection!!, Context.BIND_AUTO_CREATE)
            }
        }

        return view
    }

    companion object {
        fun newInstance() = ToolsFragment()
    }

    internal inner class OpServiceConnection : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, boundService: IBinder) {
            opService = ITool.Stub.asInterface(boundService)
            val container = this@ToolsFragment.view!!.findViewById<LinearLayout>(R.id.layout)
            val card = ToolCard(context!!)
            val cardContent = card.findViewById<FrameLayout>(R.id.content)
            val view = opService!!.view.apply(context, cardContent)
            cardContent.addView(view)
            container.addView(card)
            Log.e("InvokeOp", "onServiceConnected")
        }

        override fun onServiceDisconnected(className: ComponentName) {
            opService = null
            Log.e("InvokeOp", "onServiceDisconnected")
        }
    }
}
