import com.google.common.io.ByteStreams
import java.io.File
import java.io.InputStream
import java.util.*

fun getInput(inputStream: InputStream): ByteArray?{
    if(inputStream.available() > 0) {
        return inputStream.readBytes()
    }
    return null
}

fun getByte(inputStream: InputStream): Byte?{
    val byteArray = ByteArray(1)
    if(inputStream.available() > 0) {
        ByteStreams.read(inputStream, byteArray, 0, 1)
        return byteArray[0]
    }
    return null
}

fun writeToFile(path: String, bytesList: List<Byte>){
    val file = File(path)
    file.writeBytes(bytesList.toByteArray())
}

fun isSomethingCame(inputStream: InputStream): Boolean {
    return inputStream.available() > 0
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

//fun getInputNextByte(inputStream: InputStream): Byte{
//    if(inputStream.available() > 0) {
//        return inputStream.read()
//    }
//    return null
//}