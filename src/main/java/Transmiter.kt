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
        if (counter == 255){
            counter = 0
        }
        counter++

        send(inputStream, outputStream, counter, packet, crc)
    }
    // end of file
    // check toInt
    outputStream.write(byteArrayOf(ControlChars.EOT.char))
}

fun send(inputStream: InputStream, outputStream: OutputStream, counter: Int, packet: List<Byte>, crc: Boolean){
    sendHeader(outputStream, counter)
    if(crc)
        transmissionWithCRC(inputStream, outputStream, packet)
    else
        transmissionWithSum(inputStream, outputStream, packet)
}


fun sendHeader(outputStream: OutputStream, packetNumber: Int){
    val header: ByteArray = byteArrayOf(ControlChars.SOH.char, packetNumber.toByte(), packetNumber.toByte().inv())
    outputStream.write(header)
}

fun transmissionWithSum(inputStream: InputStream, outputStream: OutputStream, packet: List<Byte>){

}

fun transmissionWithCRC(inputStream: InputStream, outputStream: OutputStream, packet: List<Byte>){
    val crc = CRC16()
    val byteArr = packet.toByteArray()
    for (byte in packet){
        crc.update(byte)
    }
    outputStream.write(byteArr)
    outputStream.write(crc.value)
}