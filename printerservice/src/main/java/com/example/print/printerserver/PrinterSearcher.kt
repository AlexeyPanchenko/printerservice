package com.example.print.printerserver

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Handler
import com.example.print.printerserver.model.PrinterInfo
import com.example.print.printerserver.utils.log
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

class PrinterException(message: String) : Exception(message)

internal class PrinterSearcher(private val nsdManager: NsdManager) {

    private val SERVICE_TYPE = "_pdl-datastream._tcp"
    private val TIMEOUT = 3500L

    fun find() = Observable.create<PrinterInfo> {
            nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, initDiscoveryListener(it))
    }

    private fun initDiscoveryListener(emitter: ObservableEmitter<PrinterInfo>): NsdManager.DiscoveryListener {
        return object : NsdManager.DiscoveryListener {
            override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                log("onServiceFound = ${Gson().toJson(serviceInfo)}")
                nsdManager.resolveService(serviceInfo, initResolveListener(emitter))
            }
            override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
                log("onStopDiscoveryFailed")
                stopDiscovery()
                emitter.onError(PrinterException("onStopDiscoveryFailed. ErrorCode = $errorCode"))
            }

            override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
                log("onStartDiscoveryFailed")
                stopDiscovery()
                emitter.onError(PrinterException("onStartDiscoveryFailed. ErrorCode = $errorCode"))
            }

            override fun onDiscoveryStarted(serviceType: String?) {
                Handler().postDelayed({
                    try {
                        stopDiscovery()
                    } catch (e: Exception) {
                        emitter.onError(e)
                    }
                }, TIMEOUT)
            }

            override fun onDiscoveryStopped(serviceType: String?) {
                log("onDiscoveryStopped")
                emitter.onComplete()
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
                log("onServiceLost = ${Gson().toJson(serviceInfo)}")
                stopDiscovery()
            }

            fun stopDiscovery() {
                nsdManager.stopServiceDiscovery(this)
            }
        }
    }

    private fun initResolveListener(emitter: ObservableEmitter<PrinterInfo>): NsdManager.ResolveListener {
        return object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                log("onResolveFailed = ${Gson().toJson(serviceInfo)}")
                emitter.onError(PrinterException("onResolveFailed. ErrorCode = $errorCode"))
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                log("onServiceResolved = ${Gson().toJson(serviceInfo)}")
                emitter.onNext(PrinterInfo(serviceInfo.serviceName, serviceInfo.host.hostName, serviceInfo.port))
            }
        }
    }
}