package io.choerodon.iam.infra.valitador;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.infra.dto.LovDTO;
import io.choerodon.iam.infra.dto.LovGridFieldDTO;
import io.choerodon.iam.infra.dto.LovQueryFieldDTO;
import io.choerodon.iam.infra.enums.LovQueryFieldType;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author bgzyy
 * @since 2019/11/1
 */
public class LovValidator {
    //    如果 lov_grid_field 是查询字段，则 queryField 不能为空
    public static Boolean checkQuery(String gridFieldName, List<LovQueryFieldDTO> queryFields) {
        Boolean result = false;

        if (!ObjectUtils.isEmpty(queryFields)) {
            for (LovQueryFieldDTO queryFieldDTO : queryFields) {
                if (gridFieldName.equals(queryFieldDTO.getQueryFieldName())) {
                    result = true;
                }
            }
        }
        return result;
    }

    public static void checkGridEmpty(LovGridFieldDTO lovGridFieldDTO, List<LovQueryFieldDTO> queryFields) {
        if (lovGridFieldDTO.getGridFieldQueryFlag() && !checkQuery(lovGridFieldDTO.getGridFieldName(), queryFields)) {
            throw new CommonException("error.lov.query.empty");
        }
    }

    public static void checkQueryEmpty(LovQueryFieldDTO lovQueryFieldDTO) {
//        如果组件类型是 lookup, 则 lookup_code 不能为空
        if (LovQueryFieldType.LOOKUP_INPUT.getValue().equals(lovQueryFieldDTO.getQueryFieldType()) && StringUtils.isEmpty(lovQueryFieldDTO.getQueryFieldLookupCode())) {
            throw new CommonException("error.lookup.code.empty");
        }
//        如果组件类型是 lov, 则 lov_code 不能为空
        if (LovQueryFieldType.LOV_INPUT.getValue().equals(lovQueryFieldDTO.getQueryFieldType()) && StringUtils.isEmpty(lovQueryFieldDTO.getQueryFieldLovCode())) {
            throw new CommonException("error.lov.code.empty");
        }
    }

    public static void checkTree(LovDTO lovDTO) {
//        如果是树形, idField & parentField 必输
        if (lovDTO.getTreeFlag() && (StringUtils.isEmpty(lovDTO.getIdField()) || StringUtils.isEmpty(lovDTO.getParentField()))) {
            throw new CommonException("error.lov.tree.empty");
        }
    }
}
