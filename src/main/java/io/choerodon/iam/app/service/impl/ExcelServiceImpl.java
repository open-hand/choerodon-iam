package io.choerodon.iam.app.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.app.service.ExcelService;
import io.choerodon.iam.infra.dto.UploadHistoryDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.mapper.UploadHistoryMapper;
import io.choerodon.iam.infra.utils.excel.ExcelImportUserTask;
import io.choerodon.iam.infra.utils.excel.ExcelReadConfig;
import io.choerodon.iam.infra.utils.excel.ExcelReadHelper;

/**
 * @author superlee
 */
@Service
public class ExcelServiceImpl implements ExcelService {

    private final Logger logger = LoggerFactory.getLogger(ExcelServiceImpl.class);

    private ExcelImportUserTask excelImportUserTask;
    private ExcelImportUserTask.FinishFallback finishFallback;
    private UploadHistoryMapper uploadHistoryMapper;

    public ExcelServiceImpl(ExcelImportUserTask excelImportUserTask,
                            ExcelImportUserTask.FinishFallback finishFallback,
                            UploadHistoryMapper uploadHistoryMapper) {
        this.excelImportUserTask = excelImportUserTask;
        this.finishFallback = finishFallback;
        this.uploadHistoryMapper = uploadHistoryMapper;
    }

    @Override
    public void importUsers(Long organizationId, MultipartFile multipartFile) {
        ExcelReadConfig excelReadConfig = initExcelReadConfig();
        long begin = System.currentTimeMillis();
        try {
            List<UserDTO> users = ExcelReadHelper.read(multipartFile, UserDTO.class, excelReadConfig);
            if (users.isEmpty()) {
                throw new CommonException("error.excel.user.empty");
            }
            UploadHistoryDTO uploadHistory = initUploadHistory(organizationId);
            long end = System.currentTimeMillis();
            logger.info("read excel for {} millisecond", (end - begin));
            Long userId = DetailsHelper.getUserDetails().getUserId();
            excelImportUserTask.importUsers(userId, users, organizationId, uploadHistory, finishFallback);
        } catch (IOException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new CommonException("error.excel.read", e.getCause());
        } catch (IllegalArgumentException e) {
            throw new CommonException("error.excel.illegal.column", e);
        }
    }

    private UploadHistoryDTO initUploadHistory(Long organizationId) {
        UploadHistoryDTO uploadHistory = new UploadHistoryDTO();
        uploadHistory.setBeginTime(new Date(System.currentTimeMillis()));
        uploadHistory.setType("user");
        uploadHistory.setUserId(DetailsHelper.getUserDetails().getUserId());
        uploadHistory.setSourceId(organizationId);
        uploadHistory.setSourceType(ResourceLevel.ORGANIZATION.value());
        if (uploadHistoryMapper.insertSelective(uploadHistory) != 1) {
            throw new CommonException("error.uploadHistory.insert");
        }
        return uploadHistoryMapper.selectByPrimaryKey(uploadHistory);
    }

    private ExcelReadConfig initExcelReadConfig() {
        ExcelReadConfig excelReadConfig = new ExcelReadConfig();
        String[] skipSheetNames = {"readme"};
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("用户名*", "realName");
        propertyMap.put("登录名", "loginName");
        propertyMap.put("邮箱*", "email");
        propertyMap.put("角色编码*", "roleCodes");
        propertyMap.put("密码", "password");
        propertyMap.put("手机号", "phone");
        excelReadConfig.setSkipSheetNames(skipSheetNames);
        excelReadConfig.setPropertyMap(propertyMap);
        return excelReadConfig;
    }

    @Override
    public Resource getUserTemplates() {
        InputStream inputStream = this.getClass().getResourceAsStream("/templates/userTemplates.xlsx");
        return new InputStreamResource(inputStream);
    }

    @Override
    public HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        //设置下载文件名
        String filename = null;
        try {
            filename = URLEncoder.encode("用户导入模版.xlsx", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.info("url encodes exception: {}", e.getMessage());
            throw new CommonException("error.encode.url");
        }
        headers.add("Content-Disposition", "attachment;filename=\"" + filename + "\"");
        return headers;
    }
}
