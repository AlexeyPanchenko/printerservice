package ru.alexeyp.printexample

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import ru.alexeyp.printerserver.PrintService
import ru.alexeyp.printerserver.connectors.PaperSize
import ru.alexeyp.printerserver.model.PrinterInfo
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    private val printService = PrintService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            printService.findPrinters()
                    .subscribe(
                            { log("Printers = ${Gson().toJson(it)}")
                                print(it.first { !it.name.contains("HP") })
                            },
                            { log("Exception = $it") })
        }
    }

    private fun print(printInfo: PrinterInfo) {
        log("Printer = ${Gson().toJson(printInfo)}")
        val file = File("${Environment.getExternalStorageDirectory()}/teste.pdf")
        printService.print(printInfo.ip, printInfo.port, file, "dsa", PaperSize.A4)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Toast.makeText(this, it, Toast.LENGTH_LONG).show() }, {
                    it.printStackTrace()
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                } )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
            R.id.action_print -> true
            else -> super.onOptionsItemSelected(item)
    }

}