import com.google.common.base.Splitter
import sun.misc.CRC16
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.zip.CRC32
import kotlin.experimental.inv

fun startTransmission(file: File, inputStream: InputStream, outputStream: OutputStream) {
    val byteArr = file.readBytes()
    val packets = byteArr.asIterable().chunked(128)
    var crc = false

    while(true){
        if(isSomethingCame(inputStream)){
            crc = getByte(inputStream) == ControlChars.NAK.char
            break
        }
    }
    var counter = 1

    for(packet in packets){
        if (counter == 256){
            counter = 1
        }

        if(send(inputStream, outputStream, counter, packet, crc) == 1){
            println("Transmission canceled")
            return
        }
        counter++
    }
    // end of file
    // check toInt
    outputStream.write(byteArrayOf(ControlChars.EOT.char))
}

fun send(inputStream: InputStream, outputStream: OutputStream, counter: Int, packet: List<Byte>, crc: Boolean): Int {
    sendHeader(outputStream, counter)
    while(true){
        if(crc)
            transmissionWithCRC(outputStream, packet)
        else
            transmissionWithSum(outputStream, packet)

        val rec = getByte(inputStream)

        if(rec == ControlChars.NAK.char)
            continue

        if(rec == ControlChars.ACK.char)
            break

        if(rec == ControlChars.CAN.char)
            return 1
    }
    return 0
}


fun sendHeader(outputStream: OutputStream, packetNumber: Int){
    val header: ByteArray = byteArrayOf(ControlChars.SOH.char, packetNumber.toByte(), packetNumber.toByte().inv())
    outputStream.write(header)
}

fun transmissionWithSum(outputStream: OutputStream, packet: List<Byte>){
    var checksum: Byte = 0

    for(byte in packet){
        checksum = checksum.plus(byte).toByte()
    }

    outputStream.write(packet.toByteArray())
    outputStream.write(byteArrayOf(checksum))
}

fun transmissionWithCRC( outputStream: OutputStream, packet: List<Byte>){
    val checksum = CRC_16.get(packet.toByteArray())
    outputStream.write(packet.toByteArray())
    outputStream.write(checksum)
}