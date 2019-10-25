/* eslint-disable */
import React, {useContext} from 'react';
import {Select, Form, TextArea, TextField, SelectBox, Tooltip, Output, Table} from 'choerodon-ui/pro';
import Store from '../Store';
import './ThirdStep.less';

const cssPrefix = 'c7n-market-thirdStep';

const { Column } = Table;

const imgContainer = ({ value }) => <img className={`${cssPrefix}-imgContainer`} src={value} alt="appIcon"/>;

const FreeContainer = ({ value }) => <span>{value ? '是' : '否'}</span>;

const categoryTypeContainer = ({ value, record }) => (value.type === 'custom' ? record.get('categoryName') : value.name);

const PublishTypeContainer = ({ value }) => {
  const publishTypeMap = {
    mkt_code_only: '源代码',
    mkt_deploy_only: '部署包',
    all: '源代码、部署包',
  };
  return <span>{publishTypeMap[value]}</span>;
};



const ThirdStep = (props) => {
  const { mobxStore, marketAppDataSet, versionOptionDataSet, newPlatformAppTableDataSet, existPlatformAppTableDataSet, versionNameDataSet } = useContext(Store);
  const AppContainer = () => versionOptionDataSet.queryDataSet.current.get('applicationName');
  const VersionContainer = () => mobxStore.createType === 'exist' ? existPlatformAppTableDataSet.queryDataSet.current.get('versionName') : versionNameDataSet.current.get('versionName');
  const TableVersionContainer = ({ value, record }) => record.get('appServiceVersions').version;
  const TableContainer = () => mobxStore.createType === 'exist' ? (
    <Table className={`${cssPrefix}-table`} dataSet={existPlatformAppTableDataSet} queryBar="none">
      <Column name="name" />
      <Column name="appServiceVersions" renderer={TableVersionContainer} minWidth={200} header="应用服务版本" />
    </Table>
  ) : (
    <Table className={`${cssPrefix}-table`} dataSet={newPlatformAppTableDataSet} queryBar="none" selectionMode={false} filter={(record) => record.isSelected}>
      <Column name="name" />
      <Column name="allAppServiceVersions" renderer={TableVersionContainer} minWidth={200} header="应用服务版本" />
    </Table>
  );
  return (
    <React.Fragment>
      <Form header="应用服务信息" className={`${cssPrefix}-viewForm`} labelLayout="horizontal" labelAlign="left" labelWidth={150}>
        <Output label="需要发布的应用" style={{ width: '5.12rem' }} renderer={AppContainer}/>
        <Output label="应用版本" style={{ width: '5.12rem' }} renderer={VersionContainer} />
        <Output label="包含的应用服务" renderer={() => <TableContainer />} />
      </Form>
      <Form header="申请信息" className={`${cssPrefix}-viewForm`} dataSet={marketAppDataSet} labelLayout="horizontal" labelAlign="left" labelWidth={150}>
        <Output name="imageUrl" renderer={imgContainer} />
        <Output name="name" />
        <Output name="categoryOption" renderer={categoryTypeContainer}/>
        <Output name="description" />
        <Output name="free" renderer={FreeContainer} />
        <Output name="publishType" renderer={PublishTypeContainer} />
        <Output name="notificationEmail" />
        <Output name="remark" />
      </Form>
    </React.Fragment>
  );
};

export default ThirdStep;
