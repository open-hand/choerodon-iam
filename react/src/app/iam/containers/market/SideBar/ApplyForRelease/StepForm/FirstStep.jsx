import React, { useContext, useState } from 'react';
import { observer, Observer } from 'mobx-react-lite';
import { DataSet, Table, SelectBox, Select, Form, TextField } from 'choerodon-ui/pro';
import _ from 'lodash';
import './FirstStep.less';
import { Collapse } from 'choerodon-ui';
import { axios } from '@choerodon/boot';
import Store from '../Store';

const { Column } = Table;
const { Panel } = Collapse;
const { Option } = SelectBox;

const cssPrefix = 'c7n-market-firstStep';

const existVersionContainer = ({ value, record }) => (value ? value.version : null);

const FirstStep = observer((props) => {
  const {
    existPlatformAppTableDataSet,
    newPlatformAppTableDataSet,
    versionOptionDataSet,
    marketAppDataSet,
  } = useContext(Store);
  // const [versionSelectType, setVersionSelectType] = useState(true);
  return (
    <div className={`${cssPrefix}`}>
      <Form className={`${cssPrefix}-form`} dataSet={versionOptionDataSet.queryDataSet}>
        <Select
          name="refAppId"
          clearButton={false}
          optionRenderer={({ record, text, value }) => <div>{text}</div>}
          onChange={async () => {
            marketAppDataSet.current.set('name', versionOptionDataSet.queryDataSet.current.get('applicationName'));
            marketAppDataSet.current.set('description', versionOptionDataSet.queryDataSet.current.get('applicationDescription'));
            // versionOptionDataSet.queryDataSet.current.set('refAppId', value);
            await versionOptionDataSet.query();
            if (versionOptionDataSet.length) {
              existPlatformAppTableDataSet.queryDataSet.current.set('versionObj', versionOptionDataSet.get(0).toData());
              await existPlatformAppTableDataSet.query();
            } else {
              existPlatformAppTableDataSet.queryDataSet.current.reset();
              existPlatformAppTableDataSet.loadData([]);
            }
            newPlatformAppTableDataSet.queryDataSet.current.set('applicationId', versionOptionDataSet.queryDataSet.current.get('applicationId'));
            await newPlatformAppTableDataSet.query();
          }}
        />
      </Form>
      <ToggleCreateType />
      <ExistCreateType />
      <NewCreateType />
    </div>
  );
});

const ToggleCreateType = observer(() => {
  const { mobxStore } = useContext(Store);
  return (
    <Form className={`${cssPrefix}-form`}>
      <SelectBox value={mobxStore.createType} onChange={mobxStore.setCreateType} label="应用版本选择方式">
        <Option value="exist">已有应用版本</Option>
        <Option value="new">新建应用版本</Option>
      </SelectBox>
    </Form>
  );
});

const ExistCreateType = observer(() => {
  const { mobxStore, existPlatformAppTableDataSet } = useContext(Store);
  return mobxStore.createType === 'exist' && (
    <div className={`${cssPrefix}-form`}>
      <Form dataSet={existPlatformAppTableDataSet.queryDataSet}>
        <Select
          name="versionObj"
          clearButton={false}
          onChange={() => {
            existPlatformAppTableDataSet.query();
          }}
        />
      </Form>
      <hr className={`${cssPrefix}-hr`} />
      <Collapse bordered={false} defaultActiveKey={['1']}>
        <Panel header={(<span style={{ marginLeft: '0.1rem' }}>包含的应用服务</span>)} key="1">
          <Table dataSet={existPlatformAppTableDataSet} queryBar="none" className={`${cssPrefix}-table`}>
            <Column name="name" />
            <Column name="appServiceVersions" renderer={existVersionContainer} minWidth={200} header="应用服务版本" />
          </Table>
        </Panel>
      </Collapse>
      <hr className={`${cssPrefix}-hr`} />
    </div>
  );
});

const NewCreateType = observer(() => {
  const { mobxStore, versionNameDataSet, versionOptionDataSet, newPlatformAppTableDataSet, projectId } = useContext(Store);

  const queryAppServiceVersions = _.debounce(async (e, record) => {
    // if (e.target.value !== '') {
    const res = await axios.get(`/iam/choerodon/v1/projects/${projectId}/applications/${versionOptionDataSet.queryDataSet.current.get('applicationId')}/services/${record.get('id')}/with_all_version?version=${e.target.value}`);
    record.set('allAppServiceVersions', res);
    // }
  }, 500);

  function debounceQuery(e, record) {
    e.persist();
    queryAppServiceVersions(e, record);
  }

  return mobxStore.createType === 'new' && (
    <div className={`${cssPrefix}-form`}>
      <Form dataSet={versionNameDataSet}>
        <TextField name="versionName" />
      </Form>
      <hr className={`${cssPrefix}-hr`} />
      <Collapse bordered={false} defaultActiveKey={['1']}>
        <Panel header={(<span style={{ marginLeft: '0.1rem' }}>包含的应用服务</span>)} key="1">
          <Table className={`${cssPrefix}-table-create`} dataSet={newPlatformAppTableDataSet} queryBar="none" highLightRow={false}>
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
        </Panel>
      </Collapse>
      <hr className={`${cssPrefix}-hr`} />
    </div>
  );
});

export default FirstStep;
