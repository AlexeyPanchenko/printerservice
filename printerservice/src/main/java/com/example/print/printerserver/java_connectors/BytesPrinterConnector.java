package com.example.print.printerserver.java_connectors;


import java.io.DataOutputStream;
import java.io.IOException;

public class BytesPrinterConnector extends PrinterConnector {

    private byte[] bytes;

    public BytesPrinterConnector(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.write(bytes);
    }
}
