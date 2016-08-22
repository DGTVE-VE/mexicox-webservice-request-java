/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservicerequest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Encoder;

/**
 *
 * @author Israel Dell
 */
public class Encrypter {
    public String EncryptAES(String text,String key) throws Exception
    {
      SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");

      // Instantiate the cipher
      Cipher cipher = Cipher.getInstance("AES");

      cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
      byte[] encrypted = cipher.doFinal(text.getBytes());

      String encrypttext = new BASE64Encoder().encode(encrypted);

      return encrypttext;
    }

    public static void main (String [] args) throws Exception{
        RandomString rs = new RandomString(32);
        String key = rs.nextString();
        Encrypter e = new Encrypter();
        String enc = e.EncryptAES("Mi cadena a encriptar", key);
        System.out.println("llave:"+key);
        System.out.println(enc);
    }
}
