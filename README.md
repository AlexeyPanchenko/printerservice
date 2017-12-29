# Printerservice
* Simple implementation of standard android print API for custom documents.
* Search printers by Wi-Fi in current network, using NsdManager.DiscoveryListener (`SERVICE_TYPE = "_pdl-datastream._tcp"`). Timeout for search services 3.5 s.
* Dispatch data(`File`, `InputStream`, `ByteArray`) to printer for printing. Interaction with printer using PJL commands. Tested only .pdf files
* Search and dispatch use RxJava2 and return Single

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
compile 'com.github.alexeypanchenko:printerservice:0.0.5'
```
## usage
#####Show preview document, using standard android print tools
```kotlin
import ru.alexeyp.printerserver.PrintService
    
fun showPreview() {
    val service = PrintService(context)
    service.showDocumentPreview(file, name)  // default name = "Document" 
}
```
#####Find printer, return `Single<List<PrinterInfo>>`
```kotlin
import ru.alexeyp.printerserver.PrintService
    
fun findPrinters() {
    val service = PrintService(context)
    service.findPrinters()
           ...
           .subscribe { printers -> }
}
```
#####Printer model
```kotlin
data class PrinterInfo(val name: String, val ip: String, val port: Int)
```
#####Dispatch data to printer return `Single<String>`
```kotlin
import ru.alexeyp.printerserver.PrintService
import ru.alexeyp.printerserver.connectors.PaperSize
    
fun print() {
    val service = PrintService(context)
    val file = File(".../path")
    val filename = "Document"       // default = "Document"
    val paperSize = PaperSize.A4    // default = PaperSize.A4
    val copies = 1                  // default = 1
    printService.print(printInfo.ip, printInfo.port, file, filename, paperSize, copies)
            ...
            .subscribe { successMessage -> }
}
```