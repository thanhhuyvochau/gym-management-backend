package spring.project.base.util.formater;

import io.minio.ObjectWriteResponse;

public class MiniIOUtil {

    public static String buildUrl(String minioUrl, ObjectWriteResponse objectWriteResponse) {
        return minioUrl.concat("/").concat(objectWriteResponse.bucket()).concat("/").concat(objectWriteResponse.object());
    }
}
