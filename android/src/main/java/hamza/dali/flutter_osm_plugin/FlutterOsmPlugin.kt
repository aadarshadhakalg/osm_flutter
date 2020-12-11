package hamza.dali.flutter_osm_plugin

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.PluginRegistry
import java.util.concurrent.atomic.AtomicInteger
import io.flutter.embedding.engine.plugins.lifecycle.FlutterLifecycleAdapter;

class FlutterOsmPlugin(activity: Activity) : Application.ActivityLifecycleCallbacks,
        FlutterPlugin, ActivityAware, DefaultLifecycleObserver {

    init {
        registrarActivityHashCode = activity.hashCode()
    }


    companion object {
        var state = AtomicInteger(0)
        var registrarActivityHashCode = 0
        var pluginBinding: FlutterPluginBinding? = null
        var lifecycle: Lifecycle? = null
        var register: PluginRegistry.Registrar? = null
        protected const val VIEW_TYPE = "plugins.dali.hamza/osmview"
        val CREATED = 1
        val STARTED = 2
        val RESUMED = 3
        val PAUSED = 4
        val STOPPED = 5
        val DESTROYED = 6

        fun registerWith(register: PluginRegistry.Registrar) {
            if (register.activity() == null) {
                return
            }
            this.register = register
            val flutterOsmView = FlutterOsmPlugin(register.activity())
            register.activity().application.registerActivityLifecycleCallbacks(flutterOsmView)
            register.platformViewRegistry().registerViewFactory(VIEW_TYPE, OsmFactory(state, register.messenger(),
                    null,
                    null,
                    null,
                    -1, register))
        }
    }

    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        pluginBinding = binding
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        pluginBinding = null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity.hashCode() != registrarActivityHashCode) {
            return
        }
        state.set(CREATED)
    }

    override fun onActivityStarted(activity: Activity) {
        if (activity.hashCode() != registrarActivityHashCode) {
            return
        }
        state.set(STARTED)
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity.hashCode() != registrarActivityHashCode) {
            return
        }
        state.set(RESUMED)
    }

    override fun onActivityPaused(activity: Activity) {
        if (activity.hashCode() != registrarActivityHashCode) {
            return
        }
        state.set(PAUSED)
    }

    override fun onActivityStopped(activity: Activity) {
        if (activity.hashCode() != registrarActivityHashCode) {
            return
        }
        state.set(STOPPED)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        TODO("Not yet implemented")
    }

    override fun onActivityDestroyed(activity: Activity) {

        if (activity.hashCode() != registrarActivityHashCode) {
            return
        }
        if (activity.isFinishing) {
            register = null
        }
        state.set(DESTROYED)

    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        lifecycle = FlutterLifecycleAdapter.getActivityLifecycle(binding)
        lifecycle?.addObserver(this)

        pluginBinding?.platformViewRegistry?.registerViewFactory(VIEW_TYPE, OsmFactory(state, pluginBinding!!.binaryMessenger,
                binding.activity.application,
                lifecycle,
                binding.activity,
                registrarActivityHashCode, register))
    }

    override fun onDetachedFromActivityForConfigChanges() {
        this.onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        lifecycle = FlutterLifecycleAdapter.getActivityLifecycle(binding)
        lifecycle?.addObserver(this)

        pluginBinding?.platformViewRegistry?.registerViewFactory(VIEW_TYPE, OsmFactory(state, pluginBinding!!.binaryMessenger,
                binding.activity.application,
                lifecycle,
                binding.activity,
                registrarActivityHashCode, register))
    }

    override fun onDetachedFromActivity() {
        lifecycle?.removeObserver(this)
    }


}