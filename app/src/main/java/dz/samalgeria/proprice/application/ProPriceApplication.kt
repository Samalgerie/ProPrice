package dz.samalgeria.proprice.application

import android.app.Application
import dz.samalgeria.proprice.model.database.*


class ProPriceApplication : Application() {
    val database by lazy { WeMixRoomDatabase.getDatabase(this@ProPriceApplication) }
    val rawRepository by lazy { RawRepository(database.rawDao()) }
    val extraRepository by lazy { ExtraRepository(database.extraDao()) }
    val productRepository by lazy { ProductRepository(database.productDao()) }
    val receiptRepository by lazy { ReceiptRepository(database.receiptDao()) }

    /* override fun onCreate() {
         super.onCreate()
         Thread.setDefaultUncaughtExceptionHandler { thread, e ->
             handleUncaughtException(
                 thread,
                 e
             )
         }
     }

     fun handleUncaughtException(thread: Thread?, e: Throwable) {
         e.printStackTrace() // not all Android versions will print the stack trace automatically
         val intent = Intent()
         intent.action = "com.mydomain.SEND_LOG" // see step 5.
         intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // required when starting from Application
         startActivity(intent)
         System.exit(1) // kill off the crashed app
     }
 */
}