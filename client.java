import java.rmi.*;
import java.io.*;
import java.net.MalformedURLException;

import javax.crypto.*;
import javax.crypto.spec.*;

public class client implements Serializable
{
    final static int STUDENT_ID = 33371415;
    // final static int PASSWORD = 1070317006;
    final static String SERV_ADDR = "scc311-server.lancs.ac.uk";
    final static String SERV_NAME = "CW_server";
    final static String FILE_PATH = "Specs.doc";
    final static String KEY_PATH = "33371415.key";


    public static void main(String[] args)
    {
        CW_server_interface impl;
        Client_request request = new Client_request(STUDENT_ID, 0, PASSWORD);
        Server_response response;
        Object key;
    
        try{
            impl = (CW_server_interface) Naming.lookup("rmi://" + SERV_ADDR + "/" + SERV_NAME);
            key = readKey(KEY_PATH);
            response = getObject(impl, request, key);
            writeTo(FILE_PATH, response);

        }catch(Exception e){
            System.out.println("Failed to lookup rmi: " + e);
        }
    }

    public static Object readKey(String keyPath){
        try{
            FileInputStream fis = new FileInputStream(keyPath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = (Object) ois.readObject();
            ois.close();
            return obj;

        }catch(Exception e){
            System.out.println("Failed to read file: " + e);
        }
        return null;
    }

    public static Server_response getObject(CW_server_interface impl, Client_request request, Object key){
        try{
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            SecretKey secretKey = (SecretKey) key; //Convert key object to secret key
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            SealedObject sObj = new SealedObject(request, cipher);
            sObj = impl.getSpec(STUDENT_ID, sObj);

            return (Server_response) sObj.getObject(secretKey);

        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }

    public static void writeTo(String filePath, Server_response response){
        File file = new File(filePath);
        try{
            OutputStream stream = new FileOutputStream(file);
            response.write_to(stream);
            stream.close();
        }catch(Exception ex){
            System.out.println("Failed to write file.");
        }
    }
}