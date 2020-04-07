import React, { useContext, useState } from 'react';
import { Button, Modal as OldModal } from 'choerodon-ui';
import { Radio } from 'choerodon-ui/pro';
import { axios } from '@choerodon/boot';

const DeleteRoleModal = ({ deleteRoleRecord, handleCancel, projectId }) => {
  const [deleteRoleAll, setDeleteRoleAll] = useState('');

  function handleChangeDeleteRole(value) {
    setDeleteRoleAll(value);
  }

  const handleSelfCancel = (isDelete) => {
    setDeleteRoleAll('');
    if (handleCancel) {
      handleCancel(isDelete);
    }
  };

  const handleOkDeleteRole = async () => {
    const roleIds = (deleteRoleRecord.get('roles') || []).map((item) => {
      if (typeof item === 'number') {
        return item;
      } else {
        return item.id;
      }
    });
    await axios.post(`/base/v1/projects/${projectId}/role_members/delete?sync_all=${deleteRoleAll}`, {
      memberType: 'user',
      view: 'userView',
      sourceId: Number(projectId),
      data: { [deleteRoleRecord.get('id')]: roleIds },
    });
    handleSelfCancel(true);
  };

  return (
    <OldModal
      title="移除角色"
      okText="移除"
      visible={deleteRoleRecord}
      disableOk={String(deleteRoleAll) === ''}
      onCancel={() => handleSelfCancel(false)}
      destroyOnClose
      onOk={handleOkDeleteRole}
    >
      <React.Fragment>
        <p>确认移除用户{deleteRoleRecord && deleteRoleRecord.get('realName')}在项目群内的所有角色吗？</p>
        <Radio onChange={(value) => handleChangeDeleteRole(value)} name="deleteRole" value={false}>仅移除该用户在项目群内的所有角色</Radio>
        <Radio onChange={(value) => handleChangeDeleteRole(value)} name="deleteRole" value>移除该用户在项目群内所有角色及其所有子项目下的【项目成员】角色</Radio>
      </React.Fragment>
    </OldModal>
  );
};

export default DeleteRoleModal;
