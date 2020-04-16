package io.choerodon.iam.infra.feign.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.infra.feign.FileFeignClient;

/**
 * @author scp
 * @date 2020/4/15
 * @description
 */
public class FileFeignClientFallback implements FileFeignClient {

    private static final String MSG_ERROR_UPLOAD = "error.file.upload";

    @Override
    public ResponseEntity<String> uploadFile(String bucketName, String directory, String fileName, Integer docType, String storageCode, MultipartFile multipartFile) {
        throw new CommonException(MSG_ERROR_UPLOAD);
    }
}
