package com.example.print.printerserver.java_connectors;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamPrinterConnector extends PrinterConnector {

    private InputStream inputStream;

    public StreamPrinterConnector(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        byte[] buffer = new byte[3000];
        while (inputStream.read(buffer) != -1)
            out.write(buffer);
    }
}
