package net.risesoft.y9public.ftp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteUtil {

    public static byte[] inputStreamToByteArray(InputStream in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 4];
            int n = 0;
            while ((n = in.read(buffer)) > 0) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

}
