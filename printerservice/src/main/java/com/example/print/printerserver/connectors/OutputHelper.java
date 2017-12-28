package com.example.print.printerserver.connectors;

import java.io.DataOutputStream;
import java.io.IOException;

class OutputHelper {

    private static final char ESC = 0x1b;
    private static final String UEL = ESC + "%-12345X";
    private static final String ESC_SEQ = ESC + "%-12345\r\n";

    public static void writeHeader(DataOutputStream out, String filename, Paper paperSize, int copies) throws IOException {
        out.writeBytes(UEL);
        out.writeBytes("@PJL \r\n");
        out.writeBytes("@PJL JOB NAME = '" + filename + "' \r\n");
        out.writeBytes("@PJL SET PAPER=" + paperSize.name());
        out.writeBytes("@PJL SET COPIES=" + copies);
        out.writeBytes("@PJL ENTER LANGUAGE = PDF\r\n");
    }

    public static void writeFooter(DataOutputStream out) throws IOException {
        out.writeBytes(ESC_SEQ);
        out.writeBytes("@PJL \r\n");
        out.writeBytes("@PJL RESET \r\n");
        out.writeBytes("@PJL EOJ NAME = '$filename'");
        out.writeBytes(UEL);
    }
}
