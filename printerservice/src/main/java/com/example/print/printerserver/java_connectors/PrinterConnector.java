package com.example.print.printerserver.java_connectors;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public abstract class PrinterConnector {

    public abstract void writeData(DataOutputStream out) throws IOException;

    public Single<String> print(final String printerIP, final int printerPort, final String filename, final PaperSize paperSize, final int copies) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                Socket socket = null;
                DataOutputStream out = null;
                try {
                    socket = new Socket(printerIP, printerPort);
                    out = new DataOutputStream(socket.getOutputStream());
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    writePJL(out, filename, paperSize, copies);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.onError(e);
                } finally {
                    try {
                        if (out != null)
                            out.close();
                        if (socket != null)
                            socket.close();
                        emitter.onSuccess("Succ");
                    } catch (IOException e) {
                        e.printStackTrace();
                        emitter.onError(e);
                    }
                }
            }
        });
    }

    private void writePJL(DataOutputStream out, String filename, PaperSize paperSize, int copies) throws IOException {
        final char ESC = 0x1b;
        final String UEL = ESC + "%-12345X";
        final String ESC_SEQ = ESC + "%-12345\r\n";

        out.writeBytes(UEL);
        out.writeBytes("@PJL \r\n");
        out.writeBytes("@PJL JOB NAME = '" + filename + "' \r\n");
        out.writeBytes("@PJL SET PAPER=" + paperSize.name());
        out.writeBytes("@PJL SET COPIES=" + copies);
        out.writeBytes("@PJL ENTER LANGUAGE = PDF\r\n");
        writeData(out);
        out.writeBytes(ESC_SEQ);
        out.writeBytes("@PJL \r\n");
        out.writeBytes("@PJL RESET \r\n");
        out.writeBytes("@PJL EOJ NAME = '" + filename + "'");
        out.writeBytes(UEL);
    }
}
