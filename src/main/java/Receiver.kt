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
        collectData(transmissionTypeChar, inputStream, outputStream)
    else
        println("Cannot start transmission!")
}

fun collectData(transmissionTypeChar: ControlChars, inputStream: InputStream, outputStream: OutputStream){
    var packetNumber = 1
    val msg = LinkedList<Byte>()

    do{
        val rec = getData(packetNumber, transmissionTypeChar, inputStream, outputStream)

        if(rec != null){
            msg.addAll(rec)
            packetNumber++
        }

    }
        while(rec != null)
}

fun getData(packetNumber: Int, transmissionTypeChar: ControlChars, inputStream: InputStream, outputStream: OutputStream): LinkedList<Byte>?{

    // if EOT arrived return null if SOH continue
    val firstHeaderByte = receiveBytes(inputStream, 1)

    if(firstHeaderByte[0] == ControlChars.EOT.char){
        return null
    }

    else if(firstHeaderByte[0] == ControlChars.SOH.char){

    // read header packet number, packet number completion
    val header = receiveBytes(inputStream, 2)

    if(transmissionTypeChar == ControlChars.C){
        val (controlSum, msg) = getDataWithAlgebraicSum(inputStream)
        val receivedControlSum = receiveBytes(inputStream, 1)

        // check control sum
        if(controlSum != receivedControlSum[0]){
            // incorrect checksum
            outputStream.write(byteArrayOf(ControlChars.NAK.char))
            return getData(packetNumber, transmissionTypeChar, inputStream, outputStream)

        }

        return if(!isHeaderProper(header, packetNumber)){
            // incorrect checksum
            outputStream.write(byteArrayOf(ControlChars.NAK.char))
            getData(packetNumber, transmissionTypeChar, inputStream, outputStream)
        }

        else{
            outputStream.write(byteArrayOf(ControlChars.ACK.char))
            return msg
        }
    }
    }

    return null
}

fun getDataWithAlgebraicSum(inputStream: InputStream): Pair<Byte, LinkedList<Byte>>{
    var algebraicSum: Byte = 0
    val msg = LinkedList<Byte>()

    while (msg.size <= 128) {
        val rec = getByte(inputStream)
        if (rec != null) {
            msg.add(rec)
            algebraicSum = algebraicSum.plus(rec).toByte()
        }
    }
    return Pair(algebraicSum, msg)
}

fun isSomethingCame(inputStream: InputStream): Boolean {
    return inputStream.available() > 0
}

fun isHeaderProper(msg: ByteArray, packetNumber: Int): Boolean {
    if (msg.size != 2)
        return false

    if(msg[0] != packetNumber.toByte())
        return false

    if (msg[0] != msg[1].inv())
        return false

    return true
}

fun receiveBytes(inputStream: InputStream, count: Int): ByteArray {
    val bitList = LinkedList<Byte>()

    while (bitList.size <= count) {
        val rec = getByte(inputStream)
        if (rec != null) {
            bitList.add(rec)
        }
    }
    return bitList.toByteArray()
}