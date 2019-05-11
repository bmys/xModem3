import java.io.File
import java.io.InputStream

object MainKt {
    @JvmStatic
    fun main(args: Array<String>) {
        if(args[0] == "t"){
            val fileName = args[1]
            val t = startTransmission(File(fileName), System.`in`, System.out)
        }
        else{
            val sign:ControlChars;
            if(args[2] == "crc"){
                sign = ControlChars.NAK
            }
            else{
                sign = ControlChars.C
            }
            val t = start(sign, System.`in`, System.out)
        }
    }
}