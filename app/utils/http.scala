package utils

object Http {
  import java.net.URLEncoder

  def encodeUrlParams(params: Map[String,String]): String = {
   params.map {
     case (k, v) =>
       URLEncoder.encode(k,"utf-8") + "=" + URLEncoder.encode(v, "utf-8")
   }.mkString("&")
  }


}

object Encoding {
  import org.apache.commons.codec.binary.Base64

  def encodeBase64(str: String): String = {
    new String( Base64.encodeBase64( str.toArray.map(_.toByte)) )
  }

  def decodeBase64(base64: String): String = {
    new String( Base64.decodeBase64( base64.toArray.map(_.toByte) ) )
  }

}