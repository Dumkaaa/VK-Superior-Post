package ru.maxost.vk_superior_post

import android.app.Application
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import com.vk.sdk.VKSdk
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import ru.maxost.switchlog.SwitchLog

/**
 * Created by Maxim Ostrovidov on 06.09.17.
 * (c) White Soft
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        //log
        if(BuildConfig.DEBUG) SwitchLog.setLogMethod(SwitchLog.METHOD_ANDROID_LOG)

        //vk
        startTokenTracker()
        VKSdk.initialize(this)

        //rx
        RxJavaPlugins.setErrorHandler {
            if(it is UndeliverableException) it.printStackTrace()
            else throw it
        }
    }

    private fun startTokenTracker() {
        val tokenTracker = object: VKAccessTokenTracker() {
            override fun onVKAccessTokenChanged(oldToken: VKAccessToken?,
                                                newToken: VKAccessToken?) {
                //TODO
            }
        }
        tokenTracker.startTracking()
    }
}