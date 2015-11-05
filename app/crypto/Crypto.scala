package crypto

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

import org.apache.commons.codec.binary.Base64
import play.api.Logger

/**
 * Created by ahmetkucuk on 18/10/15.
 */
trait Encryption {
  def encrypt(text: String, secret: String): String
  def decrypt(encrypted: String, secret: String): String
}

class JavaCryptoEncryption(algorithmName: String) extends Encryption {




  def encryptBytes(bytes: Array[Byte], secret: String): Array[Byte] = {
    val secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), algorithmName)
    val encipher = Cipher.getInstance(algorithmName + "/ECB/PKCS5Padding")
    encipher.init(Cipher.ENCRYPT_MODE, secretKey)
    encipher.doFinal(bytes)
  }

  def encrypt(theText: String, secret: String): String = {
    def encodeBase64(bytes: Array[Byte]) = Base64.encodeBase64String(bytes)
    Logger.debug("key:" + Base64.encodeBase64String(secret.getBytes("UTF-8")))
    encodeBase64(encryptBytes(theText.getBytes("UTF-8"), secret))
//    new String(encryptBytes(text.getBytes, secret))
  }


  def decrypt(encrypted: String, secret: String): String = {
    def decodeBase64(text: String) = Base64.decodeBase64(text)
//    decryptBytes(decodeBase64(encrypted), secret).toString
    new String(decryptBytes(decodeBase64(encrypted), secret))
  }

  def decryptBytes(bytes: Array[Byte], secret: String): Array[Byte] = {
    val secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), algorithmName)
    val encipher = Cipher.getInstance(algorithmName + "/ECB/PKCS5Padding")
    encipher.init(Cipher.DECRYPT_MODE, secretKey)
    encipher.doFinal(bytes)
  }

  //    def encodeBase64(bytes: Array[Byte]) = Base64.encodeBase64String(bytes)
  //    def decodeBase64(text: String) = Base64.decodeBase64(text)
  //
  //    val encrypted = encodeBase64(crypto.AES.encrypt("deneyelim", "0123456789012345"))
  //    Logger.debug("Encrypted: " + encrypted)
  //    val decrypted = crypto.AES.decrypt(decodeBase64(encrypted), "0123456789012345")
  //    Logger.debug("Decrypted: " + decrypted)


}

object DES extends JavaCryptoEncryption("DES")
object AES extends JavaCryptoEncryption("AES")
