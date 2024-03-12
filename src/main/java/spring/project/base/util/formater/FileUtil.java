package spring.project.base.util.formater;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.util.Base64;

public class FileUtil {
    public static Resource convertBase64ToResource(String base64) {
        // Convert Base64 to byte array
        byte[] fileContent = Base64.getDecoder().decode(base64);

        // Create a Resource from byte array
        return new ByteArrayResource(fileContent) {
            @Override
            public String getFilename() {
                return "face.jpg"; // Set the desired file name
            }
        };
    }
}
