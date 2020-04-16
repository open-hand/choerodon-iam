package io.choerodon.iam.infra.feign;

import org.hzero.common.HZeroService;
import org.hzero.iam.infra.feign.fallback.UserDetailsClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.iam.infra.config.MultipartSupportConfig;
import io.choerodon.iam.infra.feign.fallback.FileFeignClientFallback;

/**
 * @author superlee
 */
@FeignClient(value = HZeroService.File.NAME,
        configuration = MultipartSupportConfig.class,
        fallback = FileFeignClientFallback.class, path = "/v1/files")

public interface FileFeignClient {

    /***
     * 上传文件
     * @param bucketName
     * @param directory
     * @param fileName
     * @param docType
     * @param storageCode
     * @param multipartFile
     * @return
     */
    @PostMapping(
            value = "/multipart",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadFile(@RequestParam("bucketName") String bucketName,
                                      @RequestParam(value = "directory", required = false) String directory,
                                      @RequestParam(value = "fileName", required = false) String fileName,
                                      @RequestParam(value = "docType", defaultValue = "0") Integer docType,
                                      @RequestParam(value = "storageCode", required = false) String storageCode,
                                      @RequestParam("file") MultipartFile multipartFile);
}
