package com.flux.common.oss.template;

import com.flux.common.core.result.ResultCode;
import com.flux.common.oss.config.OssProperties;
import com.flux.common.web.exception.BizException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName OssTemplate
 * @Author njq
 * @Date 2026/10/28
 * @Description 对象存储操作封装。bucket 统一从配置取，业务方只需要关心对象 key。
 * 上传返回的是对象 key，要访问地址再调 {@link #getUrl(String)}
 */
public class OssTemplate {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private static final String DEFAULT_BIZ_TYPE = "file";
    private static final DateTimeFormatter DATE_PATH = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final OssProperties properties;

    public OssTemplate(S3Client s3Client, S3Presigner presigner, OssProperties properties) {
        this.s3Client = s3Client;
        this.presigner = presigner;
        this.properties = properties;
    }

    // ==================== 上传 ====================

    /**
     * 上传并自动生成对象 key，bizType 用来分目录，比如 avatar、excel
     *
     * @return 对象 key
     */
    public String upload(MultipartFile file, String bizType) {
        if (file == null || file.isEmpty()) {
            throw new BizException(ResultCode.USER_UPLOAD_FILE_ERROR, "上传文件不能为空");
        }
        String objectKey = generateKey(bizType, file.getOriginalFilename());
        String contentType = StringUtils.hasText(file.getContentType())
                ? file.getContentType() : DEFAULT_CONTENT_TYPE;
        try (InputStream in = file.getInputStream()) {
            put(in, objectKey, contentType, file.getSize());
        } catch (IOException e) {
            throw new BizException(ResultCode.USER_UPLOAD_FILE_ERROR, "读取上传文件失败", e);
        }
        return objectKey;
    }

    /**
     * 上传，对象 key 自己指定。流由调用方负责关闭
     */
    public void upload(InputStream in, String objectKey, String contentType, long contentLength) {
        put(in, objectKey, contentType, contentLength);
    }

    public void upload(byte[] bytes, String objectKey, String contentType) {
        PutObjectRequest request = putObjectRequest(objectKey, contentType, bytes.length);
        try {
            s3Client.putObject(request, RequestBody.fromBytes(bytes));
        } catch (SdkException e) {
            throw uploadError(e);
        }
    }

    // ==================== 下载 ====================

    public byte[] download(String objectKey) {
        try {
            ResponseBytes<GetObjectResponse> bytes = s3Client.getObjectAsBytes(getObjectRequest(objectKey));
            return bytes.asByteArray();
        } catch (SdkException e) {
            throw new BizException(ResultCode.SYSTEM_RESOURCE_ACCESS_ERROR, "下载文件失败：" + e.getMessage(), e);
        }
    }

    public void download(String objectKey, OutputStream out) {
        try (ResponseInputStream<GetObjectResponse> in = s3Client.getObject(getObjectRequest(objectKey))) {
            in.transferTo(out);
        } catch (IOException e) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR, "写出文件失败", e);
        } catch (SdkException e) {
            throw new BizException(ResultCode.SYSTEM_RESOURCE_ACCESS_ERROR, "下载文件失败：" + e.getMessage(), e);
        }
    }

    // ==================== 其它 ====================

    public void delete(String objectKey) {
        try {
            s3Client.deleteObject(builder -> builder.bucket(properties.getBucket()).key(objectKey));
        } catch (SdkException e) {
            throw new BizException(ResultCode.SYSTEM_RESOURCE_ACCESS_ERROR, "删除文件失败：" + e.getMessage(), e);
        }
    }

    public boolean exists(String objectKey) {
        HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(objectKey)
                .build();
        try {
            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            // 部分 S3 兼容实现只返回 404，不抛 NoSuchKeyException
            if (e.statusCode() == 404) {
                return false;
            }
            throw new BizException(ResultCode.SYSTEM_RESOURCE_ACCESS_ERROR, "查询文件失败：" + e.getMessage(), e);
        } catch (SdkException e) {
            throw new BizException(ResultCode.SYSTEM_RESOURCE_ACCESS_ERROR, "查询文件失败：" + e.getMessage(), e);
        }
    }

    /**
     * 列出指定前缀下的对象 key，最多 maxKeys 个
     */
    public List<String> list(String prefix, int maxKeys) {
        ListObjectsV2Request.Builder builder = ListObjectsV2Request.builder()
                .bucket(properties.getBucket())
                .maxKeys(maxKeys);
        if (StringUtils.hasText(prefix)) {
            builder.prefix(prefix);
        }
        try {
            return s3Client.listObjectsV2(builder.build()).contents().stream()
                    .map(S3Object::key)
                    .toList();
        } catch (SdkException e) {
            throw new BizException(ResultCode.SYSTEM_RESOURCE_ACCESS_ERROR, "查询文件列表失败：" + e.getMessage(), e);
        }
    }

    // ==================== 地址 ====================

    /**
     * 文件的访问地址。配了 publicUrl 用它，否则按 path-style 拼 endpoint/bucket/key
     */
    public String getUrl(String objectKey) {
        if (StringUtils.hasText(properties.getPublicUrl())) {
            return trimEnd(properties.getPublicUrl()) + "/" + objectKey;
        }
        return trimEnd(properties.getEndpoint()) + "/" + properties.getBucket() + "/" + objectKey;
    }

    /**
     * 带签名的临时访问地址，bucket 没开公开读时用这个
     */
    public String getPresignedUrl(String objectKey, Duration expiry) {
        GetObjectRequest getObjectRequest = getObjectRequest(objectKey);
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiry)
                .getObjectRequest(getObjectRequest)
                .build();
        try {
            return presigner.presignGetObject(presignRequest).url().toString();
        } catch (SdkException e) {
            throw new BizException(ResultCode.SYSTEM_RESOURCE_ACCESS_ERROR, "生成临时地址失败：" + e.getMessage(), e);
        }
    }

    /**
     * 生成对象 key：bizType/yyyy/MM/dd/uuid.ext
     */
    public String generateKey(String bizType, String originalFilename) {
        String biz = StringUtils.hasText(bizType) ? bizType.replaceAll("^/+|/+$", "") : DEFAULT_BIZ_TYPE;
        String ext = StringUtils.getFilenameExtension(originalFilename);
        String name = UUID.randomUUID().toString().replace("-", "");
        return biz + "/" + LocalDate.now().format(DATE_PATH) + "/" + name
                + (StringUtils.hasText(ext) ? "." + ext : "");
    }

    // ==================== 内部方法 ====================

    private void put(InputStream in, String objectKey, String contentType, long contentLength) {
        PutObjectRequest request = putObjectRequest(objectKey, contentType, contentLength);
        try {
            s3Client.putObject(request, RequestBody.fromInputStream(in, contentLength));
        } catch (SdkException e) {
            throw uploadError(e);
        }
    }

    private PutObjectRequest putObjectRequest(String objectKey, String contentType, long contentLength) {
        return PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(objectKey)
                .contentType(StringUtils.hasText(contentType) ? contentType : DEFAULT_CONTENT_TYPE)
                .contentLength(contentLength)
                .build();
    }

    private GetObjectRequest getObjectRequest(String objectKey) {
        return GetObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(objectKey)
                .build();
    }

    private BizException uploadError(SdkException e) {
        return new BizException(ResultCode.USER_UPLOAD_FILE_ERROR, "上传文件到 OSS 失败：" + e.getMessage(), e);
    }

    private String trimEnd(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
