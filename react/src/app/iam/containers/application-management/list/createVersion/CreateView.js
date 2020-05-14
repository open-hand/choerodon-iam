import React, { useContext, useEffect } from 'react';
import { Table, Form, TextField, TextArea, Select, Button } from 'choerodon-ui/pro';
import { axios } from '@choerodon/boot';
import _ from 'lodash';
import Store from './stores';
import '../index.less';

const { Column } = Table;
export default function CreateView() {
  const { intlPrefix, permissions, intl, context, prefixCls, appServiceVersionDataSet, modal, history, projectId, applicationId } = useContext(Store);
  const { versionCreateDataSet } = context;

  async function handleOk(noQuery) {
    versionCreateDataSet.current.set('appServiceDetailsVOS', appServiceVersionDataSet.selected.map(record => ({
      id: record.get('id'),
      appServiceVersions: [
        {
          ...record.get('appServiceVersions'),
        },
      ],
    })));
    try {
      if (await versionCreateDataSet.submit()) {
        if (!noQuery) {
          versionCreateDataSet.query();
        }
      } else {
        return false;
      }
    } catch (err) {
      return false;
    }
    return true;
  }

  modal.handleOk(handleOk);
  modal.handleCancel(() => {
    versionCreateDataSet.reset();
  });
  async function createAndGoToPublish() {
    if (await handleOk(true)) {
      history.push(`/iam/choerodon/market-publish${history.location.search}`);
    }
  }
  useEffect(() => {
    modal.update({
      footer: (okBtn, cancelBtn) => (
        <React.Fragment>
          {okBtn}
          <Button
            onClick={() => createAndGoToPublish()}
            funcType="raised"
            color="primary"
          >
            创建并发布
          </Button>
          {cancelBtn}
        </React.Fragment>
      ),
    });
  }, []);
  function getForm() {
    return (
      <Form dataSet={versionCreateDataSet}>
        <TextField name="version" />
        <TextArea resize="vertical" name="description" />
      </Form>
    );
  }

  // 搜索请求防抖
  const queryAppServiceVersions = _.debounce(async (e, record) => {
    const res = await axios.get(`/iam/choerodon/v1/projects/${projectId}/applications/${applicationId}/services/${record.get('id')}/with_all_version?version=${e.target.value}`);
    record.set('allAppServiceVersions', res);
  }, 500);
  function debounceQuery(e, record) {
    e.persist();
    queryAppServiceVersions(e, record);
  }
  function getServiceTable(dataSet) {
    return (
      <Table dataSet={dataSet}>
        <Column name="name" />
        <Column
          editor={(record) => (
            <Select
              style={{ width: '100%', border: 'none' }}
              clearButton={false}
              name="allAppServiceVersions"
              onInput={(e) => debounceQuery(e, record)}
              searchable
              searchMatcher={1}
              primitiveValue={false}
            />
          )}
          name="appServiceVersions"
        />
      </Table>
    );
  }

  function renderService() {
    return (
      <React.Fragment>
        <div className="table-content">
          <div className="service-title-small">选择应用服务</div>
          {getServiceTable(appServiceVersionDataSet)}
        </div>
      </React.Fragment>
    );
  }

  return (
    <React.Fragment>
      <div style={{ padding: '0 .2rem' }} className="form-content"> {getForm()} </div>
      {renderService()}
    </React.Fragment>
  );
}
