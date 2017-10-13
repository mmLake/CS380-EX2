package com.maya;

import java.io.*;
import java.net.Socket;
import java.util.zip.CRC32;

public class Ex2Client {

    private static final String HOST_IP = "18.221.102.182";
    private static final int PORT = 38102;

    public static void main(String[] args) {
        Ex2Client client = new Ex2Client();
        client.main();
    }

    public void main(){
        try (Socket socket = new Socket(HOST_IP, PORT)) {
            //Read from server
            InputStream is = socket.getInputStream();

            //Write to server
            OutputStream os = socket.getOutputStream();

            byte[] values = new byte[100];

            //read in values and concatenate 2 bytes into 1
            for(int i = 0; i < 100; i++){
                int firstHalf = is.read();
                int secondHalf = is.read();

                firstHalf = firstHalf << 4;
                values[i] = (byte)(firstHalf | secondHalf);
            }

            //instantiate CRC32 using byte[] values
            CRC32 crc32 = new CRC32();
            crc32.update(values);
            long crcVal = crc32.getValue();

            //convert long from CRC32 to byteArray
            byte[] finalResult = new byte[4];
            for (int i = 3; i >=0; i--){
                finalResult[i] = (byte) (crcVal & 0xFF);
                crcVal>>= Byte.SIZE;
            }

            //print byteArray result
            StringBuilder builderB = new StringBuilder();
            for (byte b : finalResult){
                builderB.append(String.format("%02x", b));
            }
            System.out.println(builderB.toString());

            // Send guess to server
            os.write(finalResult);

            //print whether or not guess is correct
            int lastVal = is.read();
            String determineCorrect = (lastVal == 1) ? "correct": "incorrect";
            System.out.println("Input is " + determineCorrect);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
