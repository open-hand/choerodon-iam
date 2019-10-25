import React, { useContext, useState } from 'react';
import { runInAction, observable } from 'mobx';
import { observer } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';
import _ from 'lodash';
import { Table, Form, TextField, TextArea, Icon, Select, Button, Tooltip } from 'choerodon-ui/pro';

const { Option } = Select;
export default observer(({ record, dataSet, projectId, applicationId }) => {
  const [isButton, setIsButton] = useState(!record.appServiceVersions[0]);
  const filteredData = dataSet.current.get('appServiceDetailsVOS').filter(v => v.id !== record.id);
  if (!record) {
    return;
  }
  function handleSelectChange(value) {
    const currentData = [
      {
        id: record.id,
        appServiceVersions: [
          {
            id: value,
          },
        ],
      },
    ];
    runInAction(() => {
      dataSet.current.set('appServiceDetailsVOS', filteredData.concat(currentData));
      record.appServiceVersions[0] = { id: value };
    });
  }
  function handleDelete() {
    const currentData = [
      {
        id: record.id,
        appServiceVersions: [
        ],
      },
    ];
    runInAction(() => {
      dataSet.current.set('appServiceDetailsVOS', filteredData.concat(currentData));
    });
    setIsButton(true);
  }
  function showSelect() {
    if (record.allAppServiceVersions.length > 0) {
      setIsButton(false);
    }
  }
  function getAddButton() {
    const addBtn = (
      <Button 
        colSpan={1} 
        icon="add"
        style={{ marginTop: '.04rem', width: 'auto' }}
        color="primary"
        onClick={showSelect}
        // disabled={record.allAppServiceVersions.length === 0}
      >
      添加应用服务版本
      </Button>
    );
    return record.allAppServiceVersions && record.allAppServiceVersions.length === 0 ? (<Tooltip placement="top" title="当前无版本可用">{addBtn}</Tooltip>) : addBtn;
  }
  // 搜索请求防抖
  const queryAppServiceVersions = _.debounce(async (e) => {
    const res = await axios.get(`/base/v1/projects/${projectId}/applications/${applicationId}/services/${record.id}/with_all_version?version=${e.target.value}`);
    record.allAppServiceVersions = observable(res);
  }, 500);
  function debounceQuery(e) {
    e.persist();
    queryAppServiceVersions(e);
  }
  return (
    <div style={{ display: 'flex' }}>
      <Form columns={2}>
        <Select 
          value={record.id} 
          disabled
          colSpan={1}
          label="应用服务"
        >
          <Option value={record.id}>{record.name}</Option>
        </Select>
        {isButton 
          ? getAddButton()
          : (
            <Select 
              value={record.appServiceVersions[0] && record.appServiceVersions[0].id}
              onChange={handleSelectChange}
              onInput={debounceQuery}
              searchable
            >
              {record.allAppServiceVersions && record.allAppServiceVersions.map((v) => (
                <Option value={v.id}>
                  {v.version}
                </Option>
              ))}
            </Select>
          )}
      </Form>
      {!isButton ? (
        <Button 
          icon="delete"
          style={{ marginTop: '.1rem', width: 'auto', padding: '0 .05rem' }}
          onClick={handleDelete}
        />
      ) : <div style={{ width: '30px', height: '30px', padding: '0 .15rem' }} />}
    </div>
  );
});
