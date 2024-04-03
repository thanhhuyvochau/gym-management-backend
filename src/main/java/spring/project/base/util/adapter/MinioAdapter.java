package spring.project.base.util.adapter;


import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import spring.project.base.common.ApiException;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MinioAdapter {

    private final MinioClient minioClient;

    public MinioAdapter(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Value("${minio.bucket.name}")
    String defaultBucketName;

    private final List<String> acceptContentTypes = new ArrayList<>(Arrays.asList("image/jpeg", "image/png"));

    public ObjectWriteResponse uploadFile(String name, String contentType, InputStream content, long size) {
        if (!acceptContentTypes.contains(contentType)) {
            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Uploaded file must be: .jpeg, .jpg, .png");
        }
        try {
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(defaultBucketName)
                    .stream(content, size, -1)
                    .contentType(contentType)
                    .object(name)
                    .build();
            return minioClient.putObject(objectArgs);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}