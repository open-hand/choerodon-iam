package io.choerodon.iam.api.vo.agile;

import io.choerodon.iam.infra.dto.WorkGroupDTO;
import io.choerodon.iam.infra.dto.WorkGroupProjectRelDTO;
import io.choerodon.iam.infra.dto.WorkGroupTreeClosureDTO;
import io.choerodon.iam.infra.dto.WorkGroupUserRelDTO;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author superlee
 * @since 2022-07-21
 */
public class MigrateWorkGroupDataVO {

    private List<WorkGroupDTO> workGroups;

    private List<WorkGroupProjectRelDTO> workGroupProjectRel;

    private List<WorkGroupTreeClosureDTO> workGroupTreeClosures;

    private List<WorkGroupUserRelDTO> workGroupUserRel;

    public List<WorkGroupDTO> getWorkGroups() {
        return workGroups;
    }

    public void setWorkGroups(List<WorkGroupDTO> workGroups) {
        this.workGroups = workGroups;
    }

    public List<WorkGroupProjectRelDTO> getWorkGroupProjectRel() {
        return workGroupProjectRel;
    }

    public void setWorkGroupProjectRel(List<WorkGroupProjectRelDTO> workGroupProjectRel) {
        this.workGroupProjectRel = workGroupProjectRel;
    }

    public List<WorkGroupTreeClosureDTO> getWorkGroupTreeClosures() {
        return workGroupTreeClosures;
    }

    public void setWorkGroupTreeClosures(List<WorkGroupTreeClosureDTO> workGroupTreeClosures) {
        this.workGroupTreeClosures = workGroupTreeClosures;
    }

    public List<WorkGroupUserRelDTO> getWorkGroupUserRel() {
        return workGroupUserRel;
    }

    public void setWorkGroupUserRel(List<WorkGroupUserRelDTO> workGroupUserRel) {
        this.workGroupUserRel = workGroupUserRel;
    }

    public void clear() {
        if (!ObjectUtils.isEmpty(workGroups)) {
            workGroups.clear();
        }
        if (!ObjectUtils.isEmpty(workGroupProjectRel)) {
            workGroupProjectRel.clear();
        }
        if (!ObjectUtils.isEmpty(workGroupTreeClosures)) {
            workGroupTreeClosures.clear();
        }
        if (!ObjectUtils.isEmpty(workGroupUserRel)) {
            workGroupUserRel.clear();
        }
    }
}
