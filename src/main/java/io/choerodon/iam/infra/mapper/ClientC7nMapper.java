package io.choerodon.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.Client;

import io.choerodon.iam.api.vo.ClientRoleQueryVO;

/**
 * @author scp
 * @since 2020/5/23
 */
public interface ClientC7nMapper {

    List<Client> selectClientsByRoleIdAndOptions(
            @Param("roleId") Long roleId,
            @Param("sourceId") Long sourceId,
            @Param("sourceType") String sourceType,
            @Param("clientRoleSearchDTO") ClientRoleQueryVO clientRoleSearchDTO,
            @Param("param") String param);

    List<Long> listClientsInProject(@Param("organizationId") Long organizationId);

    List<Client> listClientByProjectId(@Param("organizationId") Long organizationId,
                                       @Param("projectId") Long projectId,
                                       @Param("name") String name,
                                       @Param("enabledFlag") Integer enabledFlag);

    Integer selectClientCountFromMemberRoleByOptions(
            @Param("roleId") Long roleId,
            @Param("sourceType") String sourceType,
            @Param("sourceId") Long sourceId,
            @Param("clientRoleSearchDTO") ClientRoleQueryVO clientRoleSearchDTO,
            @Param("param") String param);
}
