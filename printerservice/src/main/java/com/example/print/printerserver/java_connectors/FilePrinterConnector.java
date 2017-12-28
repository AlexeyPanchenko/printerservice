package com.example.print.printerserver.java_connectors;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FilePrinterConnector extends PrinterConnector{

    private File file;

    public FilePrinterConnector(File file) {
        this.file = file;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[3000];
        while (inputStream.read(buffer) != -1)
            out.write(buffer);
    }
}
