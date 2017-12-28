package com.example.print.printexample;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Arrays;

public class PrintService {

    private static PrintListener printListener;

    public enum PaperSize {
        A4,
        A5
    }

    public static void printPDFFile(final String printerIP, final int printerPort,
                                    final File file, final String filename, final PaperSize paperSize, final int copies) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                DataOutputStream out = null;
                FileInputStream inputStream = null;
                DataInputStream input = null;
                String response = "dfs";
                BufferedReader reader= null;
                try {
                    socket = new Socket(printerIP, printerPort);
                    out = new DataOutputStream(socket.getOutputStream());
                    input = new DataInputStream(socket.getInputStream());
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    // byte[] message = new byte[input.readShort()];
                    /*input.readFully(message);
                    response = new String(message);*/

                    inputStream = new FileInputStream(file);
                    byte[] buffer = new byte[3000];
                    byte[] buffer2 = {0X00, 0X0D, 0X0C, 0X1B, 0X40, 0X1B, 0X28, 0X52, 0X08, 0X00, 0X00, 0X52, 0X45, 0X4D, 0X4F, 0X54, 0X45, 0X31, 0X4C, 0X44};

                    final char ESC = 0x1B;
                    final String UEL = ESC + "%-12345X";
                    final String ESC_SEQ = ESC + "%-12345X@PJL <CR><LF>\r\n";

                    out.writeBytes(UEL);
                    out.writeBytes("@PJL COMMENT = 'Simple Job' <CR><LF>\r\n");
                    out.writeBytes("@PJL JOB NAME='" + filename + "' <CR><LF>\r\n");
                    out.writeBytes("@PJL SET PAPER=A4 <CR><LF>\r\n");
                    out.writeBytes("@PJL SET COPIES=1 <CR><LF>\r\n");
                    out.writeBytes("@PJL ENTER LANGUAGE=PCL <CR><LF>\r\n");
                    out.writeBytes(ESC + "E.... PCL-Job... .E");
                    /*out.write(buffer2);
                    while (inputStream.read(buffer) != -1) {
                        out.write(buffer);
                    }*/
                    out.writeBytes(ESC_SEQ);
                    out.writeBytes("@PJL EOJ <CR><LF> \r\n");
                    out.writeBytes(UEL);

                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (printListener != null)
                        printListener.networkError(response);
                } finally {
                    try {
                        if (inputStream != null)
                            inputStream.close();
                        if (out != null)
                            out.close();
                        if (socket != null)
                            socket.close();
                        if (printListener != null)

                        printListener.printCompleted(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (printListener != null)
                            printListener.networkError(response);
                    }
                }
            }
        });
        t.start();
    }

    public static void setPrintListener(PrintListener list) {
        printListener = list;
    }

    public static void print2() {
        DataOutputStream outToServer;
        Socket clientSocket;
        try {
            FileInputStream fileInputStream = new FileInputStream(android.os.Environment.getExternalStorageDirectory()  + java.io.File.separator+ "test.pdf");
            InputStream is =fileInputStream;
            clientSocket = new Socket("10.10.228.23", 9100);
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            byte[] buffer = new byte[3000];
            while (is.read(buffer) !=-1){
                outToServer.write(buffer);
            }
            outToServer.flush();
        }catch (Exception connectException){
        }
    }

    public interface PrintListener {
        void printCompleted(String response);

        void networkError(String response);
    }
}