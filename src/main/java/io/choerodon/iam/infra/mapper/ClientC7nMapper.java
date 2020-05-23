package io.choerodon.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.Client;

import io.choerodon.iam.api.vo.ClientRoleQueryVO;

/**
 * @author scp
 * @date 2020/5/23
 * @description
 */
public interface ClientC7nMapper {

    List<Client> selectClientsByRoleIdAndOptions(
            @Param("roleId") Long roleId,
            @Param("sourceId") Long sourceId,
            @Param("sourceType") String sourceType,
            @Param("clientRoleSearchDTO") ClientRoleQueryVO clientRoleSearchDTO,
            @Param("param") String param);

    Integer selectClientCountFromMemberRoleByOptions(
            @Param("roleId") Long roleId,
            @Param("sourceType") String sourceType,
            @Param("sourceId") Long sourceId,
            @Param("clientRoleSearchDTO") ClientRoleQueryVO clientRoleSearchDTO,
            @Param("param") String param);
}
