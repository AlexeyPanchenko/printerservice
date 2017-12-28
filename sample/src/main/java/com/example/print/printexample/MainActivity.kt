package com.example.print.printexample

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.DhcpInfo
import android.net.Uri
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.print.PrinterId
import android.print.PrinterInfo
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.io.DataOutputStream
import java.io.File
import java.net.*
import android.net.wifi.WifiManager
import android.widget.Toast
import com.example.print.printerserver.PrintService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    private val SERVICE_TYPE = "_http._tcp"

    private lateinit var nsdManager: NsdManager
    private lateinit var discoveryListener: NsdManager.DiscoveryListener
    private lateinit var resolveListener: NsdManager.ResolveListener
    private lateinit var nsdServiceInfo: NsdServiceInfo
    private lateinit var wifiManager: WifiP2pManager
    private val printService = PrintService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initWifi()
        Thread { initNsdManager() }.start()
        fab.setOnClickListener {
            //connectWithSocket()
            /*val file = File("${Environment.getExternalStorageDirectory()}/teste.pdf")
            PrintService.setPrintListener(object : PrintService.PrintListener {
                override fun printCompleted(response: String) {
                    log("printCompleted")
                    log("Response = $response")
                }

                override fun networkError(response: String) {
                    log("networkError")
                    log("Response = $response")
                }
            })
            PrintService.printPDFFile("10.10.228.23", 9100, file, "teste.pdf", PrintService.PaperSize.A4, 1)*/

            printService.findPrinters()
                    .subscribe(
                            { log("Printers = ${Gson().toJson(it)}")
                                print(it.first { it.name.contains("HP") })
                            },
                            { log("Exception = $it")})
        }
    }

    private fun print(printInfo: com.example.print.printerserver.model.PrinterInfo) {
        log("Printer = ${Gson().toJson(printInfo)}")
        val file = File("${Environment.getExternalStorageDirectory()}/teste.pdf")
        printService.print(printInfo.ip, printInfo.port, file.readBytes())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Toast.makeText(this, it, Toast.LENGTH_LONG).show() }, {
                    it.printStackTrace()
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                } )
    }

    private fun connectWithSocket() {
        Thread {
            val dSocket = DatagramSocket()
            dSocket.broadcast = true
            val sock = Socket("10.10.228.23", 631)
            val bb = byteArrayOf(0X00, 0X0D, 0X0C, 0X1B, 0X40, 0X1B, 0X28, 0X52, 0X08, 0X00, 0X00, 0X52, 0X45, 0X4D, 0X4F, 0X54, 0X45, 0X31, 0X4C, 0X44, 0X00, 0X00, 0X1B, 0X00, 0X00, 0X00, 0X1B, 0X40, 0X1B, 0X28, 0X52, 0X08, 0X00, 0X00, 0X52, 0X45, 0X4D, 0X4F, 0X54, 0X45, 0X31, 0X4C, 0X44, 0X00, 0X00, 0X4A, 0X45, 0X01, 0X00, 0X00, 0X1B, 0X00, 0X00, 0X00)
            val file = File("${Environment.getExternalStorageDirectory()}/teste.pdf")
            val ba = file.readBytes()
            val packet = DatagramPacket(bb, bb.size, InetAddress.getByName("10.10.228.23"),  631)

            dSocket.send(packet)
            /*log("Addr = ${Gson().toJson(sock)}")
            //val oStream = DataOutputStream(sock.getOutputStream())
            val oStream = sock.getOutputStream()
            *//*file.inputStream().use { input ->
                oStream.use { it.write(input.readBytes()) }
            }*//*
            val bb = byteArrayOf(0X00, 0X0D, 0X0C, 0X1B, 0X40, 0X1B, 0X28, 0X52, 0X08, 0X00, 0X00, 0X52, 0X45, 0X4D, 0X4F, 0X54, 0X45, 0X31, 0X4C, 0X44, 0X00, 0X00, 0X1B, 0X00, 0X00, 0X00, 0X1B, 0X40, 0X1B, 0X28, 0X52, 0X08, 0X00, 0X00, 0X52, 0X45, 0X4D, 0X4F, 0X54, 0X45, 0X31, 0X4C, 0X44, 0X00, 0X00, 0X4A, 0X45, 0X01, 0X00, 0X00, 0X1B, 0X00, 0X00, 0X00)
            oStream.write(bb, 0, bb.size)
            oStream.close()
            sock.close()*/
            log("Success printing")
        }.start()
    }

    fun getBroadcastAddress(): InetAddress {
        val wifi = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val dhcp = wifi.dhcpInfo ?: return InetAddress.getByName("255.255.255.255")
        val broadcast = (dhcp.ipAddress and dhcp.netmask) or dhcp.netmask.inv()
        val quads = ByteArray(4)
        for (k in 0..3)
            quads[k] = (broadcast shr k * 8 and 0xFF).toByte()
        return InetAddress.getByAddress(quads)
    }

    private fun resolveService() {
        nsdManager.resolveService(nsdServiceInfo, resolveListener)
    }

    private fun discoverServices() {
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    private fun initWifi() {
        wifiManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager

        val chanel = wifiManager.initialize(this, Looper.getMainLooper(), null)
    }

    private fun initServiceInfo() {
        nsdServiceInfo = NsdServiceInfo()
        try {
            //nsdServiceInfo.host = InetAddress.getByName("NPI5F15AB")
            val d = InetAddress.getByName("NPIEBD2C5")
            val l = InetSocketAddress(d, 9100)
            log("Inet = ${Gson().toJson(l)}")
        } catch (e: Exception) {
            log("Network EXCEPTION = $e")
        }
        //nsdServiceInfo.serviceName = "HP LaserJet Professional M1217nfw MFP"
        //nsdServiceInfo.serviceName = "Kiosk_DNS_HP_LaserJet_400_M401dw_(EBD2C5)"
        //nsdServiceInfo.serviceName = "Brother MFC-J2320"
        //nsdServiceInfo.serviceName = "HP LaserJet Pro MFP M127fw[4E4063]"
        nsdServiceInfo.serviceName = "10.10.228.23"
        //nsdServiceInfo.port = 631
        nsdServiceInfo.serviceType = SERVICE_TYPE
    }

    private fun initNsdManager() {
        nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager
        initDiscoveryListener()
        initializeResolveListener()
        initServiceInfo()
    }

    private fun initDiscoveryListener() {
        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                log("onServiceFound. serviceInfo = ${Gson().toJson(serviceInfo)}")
                nsdManager.resolveService(serviceInfo, resolveListener)
            }

            override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
                log("onStopDiscoveryFailed. serviceType = $serviceType, errorCode = $errorCode")
                nsdManager.stopServiceDiscovery(this)
            }

            override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
                log("onStartDiscoveryFailed. serviceType = $serviceType, errorCode = $errorCode")
                nsdManager.stopServiceDiscovery(this)
            }

            override fun onDiscoveryStarted(serviceType: String?) {
                log("onDiscoveryStarted. serviceType = $serviceType")
            }

            override fun onDiscoveryStopped(serviceType: String?) {
                log("onDiscoveryStopped. serviceType = $serviceType")
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
                log("onServiceLost. serviceInfo = ${serviceInfo?.serviceName}")
                nsdManager.stopServiceDiscovery(this)
            }
        }
    }

    private fun initializeResolveListener() {
        resolveListener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                log("onResolveFailed. serviceInfo = ${Gson().toJson(serviceInfo)}, errorCode = $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                log("onServiceResolved. serviceInfo = ${Gson().toJson(serviceInfo)}")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
            R.id.action_print -> {
                discoverServices()
                true
            }
            else -> super.onOptionsItemSelected(item)
    }

}