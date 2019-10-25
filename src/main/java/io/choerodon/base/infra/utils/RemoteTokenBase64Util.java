package io.choerodon.base.infra.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import io.choerodon.base.api.vo.RemoteTokenAuthorizationVO;
import io.choerodon.base.infra.dto.OrganizationDTO;
import io.choerodon.core.exception.CommonException;

public class RemoteTokenBase64Util {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CHARACTER_ENCODING = "utf-8";

    private RemoteTokenBase64Util() {
    }

    /**
     * 将 组织的远程连接令牌的名称/联系邮箱/UUID 令牌
     * 编码成
     * Base64编码的Token信息
     *
     * @param name  令牌名
     * @param email 联系邮箱
     * @param token UUID令牌
     * @return Base64编码的令牌信息
     */
    public static String encode(String name, String email, String token, String gateway, OrganizationDTO organizationDTO) {

        Map<String, Object> encodeMap = new HashMap<>();
        encodeMap.put("name", name);
        encodeMap.put("email", email);
        encodeMap.put("token", token);
        encodeMap.put("gateway", gateway);
        encodeMap.put("organizationName", organizationDTO.getName());
        encodeMap.put("organizationCode", organizationDTO.getCode());
        try {
            return Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(encodeMap).getBytes(CHARACTER_ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new CommonException("error.remote.token.encode.getBytes", e);
        } catch (JsonProcessingException e) {
            throw new CommonException("error.remote.token.encode.json", e);
        }
    }

    /**
     * 将 Base64编码的Token信息
     * 解码成
     * 组织的远程连接令牌的名称/联系邮箱/UUID 令牌
     *
     * @param base64Token token编码信息
     * @return RemoteTokenAuthorizationVO
     */
    public static RemoteTokenAuthorizationVO decode(String base64Token) {
        Map decodeMap;
        try {
            decodeMap = objectMapper.readValue(Base64.getDecoder().decode(base64Token), Map.class);
        } catch (IllegalArgumentException e) {
            throw new CommonException("error.remote.token.illegal", e);
        } catch (IOException e) {
            throw new CommonException("error.remote.token.authorization.decode", e);
        }
        RemoteTokenAuthorizationVO tokenVO = new RemoteTokenAuthorizationVO();
        tokenVO.setName((String) decodeMap.get("name"));
        tokenVO.setEmail((String) decodeMap.get("email"));
        tokenVO.setRemoteToken((String) decodeMap.get("token"));
        tokenVO.setAuthorizationUrl((String) decodeMap.get("gateway"));
        tokenVO.setOrganizationCode((String) decodeMap.get("organizationCode"));
        tokenVO.setOrganizationName((String) decodeMap.get("organizationName"));
        return tokenVO;
    }
}
