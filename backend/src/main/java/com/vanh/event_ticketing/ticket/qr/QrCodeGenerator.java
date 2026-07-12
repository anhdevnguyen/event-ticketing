package com.vanh.event_ticketing.ticket.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class QrCodeGenerator {
    public String newCode() {
        return UUID.randomUUID().toString();
    }

    public byte[] toPng(String code) {
        try {
            BitMatrix matrix = new QRCodeWriter().encode(code, BarcodeFormat.QR_CODE, 320, 320);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return out.toByteArray();
        } catch (WriterException | IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
