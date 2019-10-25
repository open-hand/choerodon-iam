import React, { useContext, Fragment } from 'react';
import { Table, Modal, DataSet, Button } from 'choerodon-ui/pro';
import { Breadcrumb as Bread } from 'choerodon-ui/';
import { Breadcrumb, Content, TabPage } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import { Link } from 'react-router-dom';
import errorUserDataSet from '../stores/errorUserDataSet';
import SyncErrorForm from './SyncErrorForm';
import Store from './store';
import './index.less';

const modalKey = Modal.key();
const { Column } = Table;
const { Item } = Bread;
let modal;
const SyncRecordForm = observer(() => {
  const { getLdapDataSet, history, orgName } = useContext(Store);
  const dataSet = getLdapDataSet();
  if (!dataSet) return null;
  function renderNewsNum({ record }) {
    let num = record.get('updateUserCount');
    let num2 = record.get('errorUserCount');
    if (num == null || num2 == null) {
      num = 0;
      num2 = 0;
    }
    return <span>{`${num}/${num + num2}`}</span>;
  }
  function renderErrorUserCount({ record }) {
    let num = record.get('errorUserCount');
    if (num == null) {
      num = 0;
    }
    return <span>{num}</span>;
  }
  // 计算时间差
  function handleLoadTime({ record }) {
    const startTime = record.get('syncBeginTime');
    const endTime = record.get('syncEndTime');
    const formatDate1 = startTime.replace(/ /g, '/');
    const formatData2 = endTime.replace(/ /g, '/');
    const date1 = new Date(formatDate1);// 开始时间
    const date2 = new Date(formatData2);// 结束时间
    const date3 = date2.getTime() - date1.getTime();// 时间差的毫秒数
    // 计算出相差天数
    const days = Math.floor(date3 / (24 * 3600 * 1000));
    // 计算出小时数

    const leave1 = date3 % (24 * 3600 * 1000);// 计算天数后剩余的毫秒数
    const hours = Math.floor(leave1 / (3600 * 1000));
    // 计算相差分钟数
    const leave2 = leave1 % (3600 * 1000);// 计算小时数后剩余的毫秒数
    const minutes = Math.floor(leave2 / (60 * 1000));

    // 计算相差秒数

    const leave3 = leave2 % (60 * 1000);// 计算分钟数后剩余的毫秒数
    const seconds = Math.round(leave3 / 1000);
    const setDay = days !== 0 ? `${days}天` : '';
    const setHours = hours !== 0 ? `${hours}小时` : '';
    const setMinutes = minutes !== 0 ? `${minutes} 分钟` : '';
    const setSeconds = `${seconds} 秒`;
    const time = setDay + setHours + setMinutes + setSeconds;
    return time;
  }
  function closeModal() {
    modal.close();
  }
  // 打开模态框
  function openModal(record) {
    // console.log(record);
    const id = record.get('id');
    const dsConfig = errorUserDataSet(id);
    dsConfig.selection = false;
    const dataSet2 = new DataSet(dsConfig);
    modal = Modal.open({
      key: modalKey,
      drawer: true,
      // destoryOnClose: true,
      title: (
        '失败详情'
      ),
      style: {
        width: 'calc(100% - 3.5rem)',
      },
      children: (
        <SyncErrorForm dataSet2={dataSet2} />
      ),
      fullScreen: true,
      footer: (
        <Button funcType="raised" color="blue" onClick={closeModal}>关闭</Button>
      ),
    });
  }

  function renderClickableColumn({ record, text }) {
    return (
      <span className="link" onClick={() => openModal(record)}>{text}</span>
    );
  }

  return (
    <TabPage>
      <Breadcrumb custom> 
        <Item>{orgName}</Item>
        <Item>
          <Link to={`/base/organization-setting/ldap${history.location.search}`}>通用</Link>
        </Item>
        <Item>同步记录</Item>
      </Breadcrumb>
      <Content className="sync-record">
        <Table
          dataSet={dataSet}
        >
          <Column renderer={renderClickableColumn} name="syncBeginTime" width={200} />
          <Column className="text-gray" name="updateUserCount" renderer={renderNewsNum} />
          <Column className="text-gray" name="errorUserCount" renderer={renderErrorUserCount} />
          <Column className="text-gray" name="syncEndTime" renderer={handleLoadTime} />
        </Table>
      </Content>

    </TabPage>
  );
});

export default SyncRecordForm;
