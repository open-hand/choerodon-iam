import React, { useContext, useEffect } from 'react';
import { Table, Form, TextField, TextArea, Icon, Select, Button } from 'choerodon-ui/pro';
import { axios } from '@choerodon/boot';
import _ from 'lodash';
import Store from './stores';
import '../index.less';
import FixVersionAdder from './FixVersionAdder';

const { Column } = Table;
const { Option } = Select;
export default function CreateView() {
  const { intl, context, appServiceVersionDataSet, modal, status, history, projectId, applicationId } = useContext(Store);
  const { versionCreateDataSet, versionDataSet } = context;

  async function handleOk() {
    if (status !== 'published') {
      versionCreateDataSet.current.set('appServiceDetailsVOS', appServiceVersionDataSet.selected.map(record => ({
        id: record.get('id'),
        appServiceVersions: [
          {
            ...record.get('appServiceVersions'),
          },
        ],
      })));
    }

    try {
      if (await versionCreateDataSet.submit()) {
        versionDataSet.query();
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
    if (await handleOk()) {
      history.push(`/iam/market-publish${history.location.search}`);
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
            {`${okBtn.props.children}并发布`}
          </Button>
          {cancelBtn}
        </React.Fragment>
      ),
    });
  }, []);
  function getForm() {
    return (
      <Form disabled={status === 'published'} dataSet={versionCreateDataSet}>
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

  // async function queryAppServiceVersions(e, record) {
  //   const res = await axios.get(`/iam/choerodon/v1/projects/${projectId}/applications/${applicationId}/services/${record.get('id')}/with_all_version?version=${e.target.value}`);
  //   record.set('allAppServiceVersions', res);
  // }
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

  function renderAddFixVersion() {
    return (
      <div className="table-content" style={{ width: '5.9rem' }}>
        <div className="table-content-title">
          添加应用服务修复版本
        </div>
        {versionCreateDataSet.current.get('appServiceDetailsVOS').map((record) => (
          <FixVersionAdder dataSet={versionCreateDataSet} record={record} projectId={projectId} applicationId={applicationId} />
        ))}
      </div>
    );
  }

  return (
    <React.Fragment>
      <div style={{ padding: '0 .2rem' }} className="form-content"> {getForm()} </div>
      {(status === 'unpublished' || status === 'create') && renderService()}
      {status === 'published' && renderAddFixVersion()}
    </React.Fragment>
  );
}
