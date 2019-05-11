import org.joda.time.DateTime
import org.joda.time.Minutes.minutes
import org.joda.time.Seconds.seconds
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.experimental.inv

fun initTransmission(outputStream: OutputStream, transmissionType: ControlChars) {
    outputStream.write(byteArrayOf(transmissionType.char))
}

fun start(transmissionTypeChar: ControlChars, inputStream: InputStream, outputStream: OutputStream) {
    val now = DateTime.now()
    val end = now + minutes(1)
    var nextTry = now + seconds(10)
    var tries = 1
    var isGood = false

    initTransmission(outputStream, transmissionTypeChar)

    // get header
    while (DateTime.now() > end && tries < 10) {
        if(isSomethingCame(inputStream)){
            isGood = true
            break
        }
        // send again if time elapsed
        if (DateTime.now() > nextTry) {
            nextTry += seconds(10)
            tries++
            initTransmission(outputStream, transmissionTypeChar)
        }
    }
    if(isGood)
        getData(inputStream)
    else
        println("Cannot start transmission!")
}

fun getData(inputStream: InputStream) {
    // read header
    val header = receiveBytes(inputStream, 3)

    val msg = LinkedList<Byte>()

    while (msg.size <= 128) {
        val rec = getInput(inputStream)
        if (rec != null) {
            msg.addAll(rec.asList())
        }
    }
}

fun isSomethingCame(inputStream: InputStream): Boolean {
    return inputStream.available() > 0
}

fun isHeaderProper(msg: ByteArray): Boolean {
    if (msg.size != 3)
        return false

    if (msg[0] != ControlChars.SOH.char)
        return false

    if (msg[1] != msg[2].inv())
        return false

    return true
}

fun receiveBytes(inputStream: InputStream, count: Int): ByteArray {
    val bitList = LinkedList<Byte>()

    while (bitList.size <= count) {
        val rec = getInput(inputStream)
        if (rec != null) {
            bitList.addAll(rec.asList())
        }
    }
    return bitList.toByteArray()
}