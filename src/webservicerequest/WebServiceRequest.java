/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservicerequest;

import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import sun.net.www.http.HttpClient;

/**
 *
 * @author Israel Dell
 */
public class WebServiceRequest {

    public interface ESTADOS {
        String Aguascalientes = "AGU";
        String Baja_California_Norte = "BCN";
        String Baja_California_Sur = "BCS";
        String Campeche = "CAM";
        String Chiapas = "CHP";
        String Chihuahua = "CHH";
        String Coahuila_de_Zaragoza = "COA";
        String Colima = "COL";
        String Distrito_Federal = "DIF";
        String Durango = "DUR";
        String Guanajuato = "GUA";
        String Guerrero = "GRO";
        String Hidalgo = "HID";
        String Jalisco = "JAL";
        String Mexico = "MEX";
        String Michoacan_de_Ocampo = "MIC";
        String Morelos = "MOR";
        String Nayarit = "NAY";
        String Nuevo_Leon = "NLE";
        String Oaxaca = "OAX";
        String Puebla = "PUE";
        String Queretaro_de_Arteaga = "QUE";
        String Quintana_Roo = "ROO";
        String San_Luis_Potosi = "SLP";
        String Sinaloa = "SIN";
        String Sonora = "SON";
        String Tabasco = "TAB";
        String Tamaulipas = "TAM";
        String Tlaxcala = "TLA";
        String Veracruz = "VER";
        String Yucatan = "YUC";
        String Zacatecas = "ZAC";

    }
    
    public interface GENERO {

        String HOMBRE = "m";
        String MUJER = "f";
        String OTRO = "o";
    }

    public interface NIVEL_ESTUDIOS {

        String DOCTORADO = "p";
        String MASTER = "m";
        String LICENCIATURA = "b";
        String TECNICO_SUPERIOR = "a";
        String BACHILLERATO = "hs";
        String SECUNDARIA = "jhs";
        String PRIMARIA = "el";
        String NINGUNA = "none";
        String OTRO = "other";
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        WebServiceRequest wsr = new WebServiceRequest();
        String key_auth = "cUNzOTk4ZkN0VzFJdTlXampxck9PU1lOaVFxT1ZxWHI=";
        String key_crypt = "sjmTvKPJmA6f5o1HalCvoEUB9p4BSjRu";

        Usuario u = new Usuario();
        u.setAnioNacimiento("1999");
        u.setCiudad(ESTADOS.Distrito_Federal);
        u.setCurp("JUPA8106116Y0HDFRLL02");
        u.setEmail("juanito2.perez@gmail.com");
        u.setEstado(ESTADOS.Distrito_Federal);
        u.setGenero(GENERO.HOMBRE);
        u.setNivelEstudios(NIVEL_ESTUDIOS.LICENCIATURA);
        u.setNombre("Perenganito");
        u.setApellidos("Trejo Perez");
        u.setPais("MX"); // En caso de no ser mexicano, dejar en blanco                
        u.setPassword(wsr.encryptPassword("password"));
        u.setSobrenombre("JUPA8106116Y0HDFRLL02"); // Esta cadena debe ser única, lo mejor es que sea el curp o el correo electrónico
        u.setCodigoPostal("06080");

        Gson gson = new Gson();
        String userJson = gson.toJson(u);

        String encryptedUser = wsr.encrypt(userJson, key_crypt);
        String base64 = Base64.getEncoder().encodeToString(encryptedUser.getBytes(StandardCharsets.UTF_8));
        String jwt = wsr.buildJWT(key_auth);

        wsr.sendPostRequest(jwt, base64);
    }


    public String sendPostRequest(String token, String data) throws MalformedURLException, ProtocolException, IOException {
        String _data = "data="+data;
        byte[] postData = _data.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://mxverde.televisioneducativa.gob.mx:81/public/v1/suscribe?token=" + token;
        URL url = new URL(request);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setDoOutput(true);
        con.setInstanceFollowRedirects(false);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("charset", "utf-8");
        con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        con.setUseCaches(false);

        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(postData);
            wr.flush();
            wr.close();
        }
        int status = con.getResponseCode();
        System.out.println (status);
//         Get the response
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(con.getInputStream()));

        String line;
        while ((line = rd.readLine()) != null) {
            System.out.println(line);
        }
        return null;
    }

    public String encryptPassword(String password) {
        Hasher hasher = new Hasher();
        String salt = new RandomString(12).nextString();
        String hash = hasher.encode(password, salt);
        return hash;
    }

    public String buildJWT(String key_auth) {
        
        String compactJws = Jwts.builder()
                .setIssuer("formacioncontinua.sep.gob.mx")
                .setSubject("suscribe")
                .signWith(SignatureAlgorithm.HS256, key_auth)
                .compact();
        return compactJws;
    }

    /**
     *
     * @param message mensaje que será encryptado
     * @param key_crypt Llave de encriptación, debe ser de 32 caracteres de
     * largo
     * @return el mensaje encriptado
     * @throws Exception
     */
    public String encrypt(String message, String key_crypt) throws Exception {
        Encrypter e = new Encrypter();
        String encrypted = e.EncryptAES(message, key_crypt);
        return encrypted;
    }
}
