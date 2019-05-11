import java.io.InputStream

fun getInput(inputStream: InputStream): ByteArray?{
    if(inputStream.available() > 0) {
        return inputStream.readBytes()
    }
    return null
}

fun getByte(inputStream: BitStream): Byte?{
    if(inputStream.available() > 0) {
        return inputStream.readBytes()
    }
    return null
}


//fun getInputNextByte(inputStream: InputStream): Byte{
//    if(inputStream.available() > 0) {
//        return inputStream.read()
//    }
//    return null
//}