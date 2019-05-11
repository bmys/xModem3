import com.google.common.io.ByteStreams
import java.io.InputStream

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


//fun getInputNextByte(inputStream: InputStream): Byte{
//    if(inputStream.available() > 0) {
//        return inputStream.read()
//    }
//    return null
//}