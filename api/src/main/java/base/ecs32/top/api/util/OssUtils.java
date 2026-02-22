package base.ecs32.top.api.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class OssUtils {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    @Getter
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    /**
     * 上传文件
     */
    public void uploadFile(String objectName, InputStream inputStream, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream, metadata);
        ossClient.putObject(putObjectRequest);
    }

    /**
     * 生成带签名的 URL
     */
    public String generateSignedUrl(String bucketName, String objectName, long expirationInSeconds) {
        Date expiration = new Date(System.currentTimeMillis() + expirationInSeconds * 1000);
        URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
        return url.toString();
    }

    /**
     * 删除文件
     */
    public void deleteFile(String bucketName, String objectName) {
        ossClient.deleteObject(bucketName, objectName);
    }

    /**
     * 根据文件名推测 Content-Type
     */
    public static String guessContentTypeFromName(String filename) {
        if (filename == null) {
            return null;
        }
        String contentType = URLConnection.guessContentTypeFromName(filename.toLowerCase());
        return contentType;
    }

    /**
     * 从完整 URL 生成预签名 URL
     */
    public String generatePresignedUrlFromFullUrl(String fullUrl, long expirationInSeconds) {
        try {
            // 解析 URL 提取 objectName
            URL url = new URL(fullUrl);
            String objectName = url.getPath();
            // 移除开头的斜杠
            if (objectName.startsWith("/")) {
                objectName = objectName.substring(1);
            }
            return generateSignedUrl(bucketName, objectName, expirationInSeconds);
        } catch (Exception e) {
            throw new RuntimeException("Invalid file URL", e);
        }
    }

    /**
     * 生成上传文件的预签名 URL（PUT 方法）
     */
    public String generateUploadPresignedUrl(String objectName, String contentType, long expirationInSeconds) {
        Date expiration = new Date(System.currentTimeMillis() + expirationInSeconds * 1000);
        GeneratePresignedUrlRequest request =
                new GeneratePresignedUrlRequest(bucketName, objectName);
        request.setExpiration(expiration);
        request.setMethod(com.aliyun.oss.common.auth.HttpMethod.PUT);
        if (contentType != null && !contentType.isEmpty()) {
            request.setContentType(contentType);
        }
        URL url = ossClient.generatePresignedUrl(request);
        return url.toString();
    }

    /**
     * 获取上传凭证信息（包含完整 URL）
     */
    public Map<String, Object> getUploadCredentials(String objectName, String contentType, long expirationInSeconds) {
        String presignedUrl = generateUploadPresignedUrl(objectName, contentType, expirationInSeconds);
        String fullUrl = "https://" + bucketName + "." + endpoint.replace("https://", "").replace("/", "") + "/" + objectName;

        Map<String, Object> result = new HashMap<>();
        result.put("presignedUrl", presignedUrl);
        result.put("objectName", objectName);
        result.put("bucketName", bucketName);
        result.put("fullUrl", fullUrl);
        result.put("expiration", System.currentTimeMillis() + expirationInSeconds * 1000);

        return result;
    }
}
