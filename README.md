# Printerservice
* Simple implementation of standard android print API for custom documents.
* Search printers by Wi-Fi in current network, using NsdManager.DiscoveryListener (`SERVICE_TYPE = "_pdl-datastream._tcp"`). Timeout for search services 3.5 s.
* Dispatch data(`File`, `InputStream`, `ByteArray`) to printer for printing. Interaction with printer using PJL commands. Tested only .pdf files
* Work with mobile thermal printers CITIZEN (find printers, print text/Bitmap, customize fonts)
* Search and dispatch use RxJava2 and return Single/Completable

## dependencies
In the root project build.gradle
```groovy
allprojects {
   ...
    repositories {
        ...
        maven {
            url  "https://dl.bintray.com/alexeypanchenko/maven"
        }
    }
}
```
In the app module build.gradle
```groovy
compile 'com.github.alexeypanchenko:printerservice:0.0.8'
```
## usage
### Stationary printers
##### Show preview document, using standard android print tools
```kotlin
import ru.alexeyp.printerservice.PrintService
    
fun showPreview() {
    val service = PrintService(context)
    service.showDocumentPreview(file, name)  // default name = "Document" 
}
```
##### Find printer, return `Single<List<PrinterInfo>>`
```kotlin
import ru.alexeyp.printerservice.PrintService
    
fun findPrinters() {
    val service = PrintService(context)
    service.findPrinters()
           ...
           .subscribe { printers -> }
}
```
##### Printer model
```kotlin
data class PrinterInfo(val name: String, val ip: String, val port: Int)
```
##### Dispatch data to printer return `Single<String>`
```kotlin
import ru.alexeyp.printerservice.PrintService
import ru.alexeyp.printerservice.connectors.PaperSize
    
fun print() {
    val service = PrintService(context)
    val file = File(".../path")
    val filename = "Document"       // default = "Document"
    val paperSize = PaperSize.A4    // default = PaperSize.A4
    val copies = 1                  // default = 1
    val timeout = 5000L                  // default = 4000L
    printService.print(printInfo.ip, printInfo.port, file, filename, paperSize, copies, timeout)
            ...
            .subscribe { successMessage -> }
}
```
### CITIZEN printers
##### Find printer (Async), return `Single<List<PrinterInfo>>`
```kotlin
import ru.alexeyp.printerservice.citizen.CitizenPrintService
import ru.alexeyp.printerservice.citizen.PrinterType
    
fun findPrinters() {
    val service = CitizenPrintService(PrinterType.WI_FI)
    val wifiTimeout = 10    // 1-30, default 5
    service.findPrinters(context, wifiTimeout)
           ...
           .observeOn(AndroidSchedulers.mainThread())
           .subscribe { printers -> }
}
```
##### Print text (Async), return `Completable`
```kotlin
import ru.alexeyp.printerservice.citizen.CitizenPrintService
import ru.alexeyp.printerservice.citizen.PrinterType
import ru.alexeyp.printerservice.citizen.CitizenConfig
    
fun printText() {
    val address = "10.10.10.10"  // ip address or bluetooth address
    val printer = CitizenPrintService(PrinterType.WI_FI).Printer(address)
    val alignment          // default CitizenConfig.Alignment.CENTER
    val attributes         // default CitizenConfig.Font.DEFAULT
    val textSize           // default CitizenConfig.Width.WIDTH1 or CitizenConfig.Height.HEIGHT1
    printer.printText("text gor printing", alignment, attributes, textSize)
           ...
           .observeOn(AndroidSchedulers.mainThread())
           .subscribe { }
}
```
##### Print bitmap (Async), return `Completable`
```kotlin
import ru.alexeyp.printerservice.citizen.CitizenPrintService
import ru.alexeyp.printerservice.citizen.PrinterType
import ru.alexeyp.printerservice.citizen.CitizenConfig
    
fun printText() {
    val address = "10.10.10.10"  // ip address or bluetooth address
    val printer = CitizenPrintService(PrinterType.WI_FI).Printer(address)
    val alignment          // default CitizenConfig.Alignment.CENTER
    printer.printBitmap(bitmap, alignment)
           ...
           .observeOn(AndroidSchedulers.mainThread())
           .subscribe { }
}
```