import React, { useContext, Fragment } from 'react';
import { Table, Modal, DataSet, Button } from 'choerodon-ui/pro';
import { Breadcrumb as Bread } from 'choerodon-ui/';
import { Breadcrumb, Content, TabPage } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import { Link } from 'react-router-dom';
import moment from 'moment';
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
    let num3 = record.get('newUserCount');
    if (num == null || num2 == null || num3 == null) {
      num = 0;
      num2 = 0;
      num3 = 0;
    }
    return <span>{`${num + num3}/${num + num2 + num3}`}</span>;
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

    const releaseDate = moment(endTime);
    const currentDate = moment(startTime);

    const diff = releaseDate.diff(currentDate);
    const diffDuration = moment.duration(diff);

    const diffYears = diffDuration.years();
    const diffMonths = diffDuration.months();
    const diffDays = diffDuration.days();
    const diffHours = diffDuration.hours();
    const diffMinutes = diffDuration.minutes();
    const diffSeconds = diffDuration.seconds();

    return `${diffYears ? `${diffYears}年` : ''}${diffMonths ? `${diffMonths}月` : ''}${diffDays ? `${diffDays}日` : ''}${diffHours ? `${diffHours}小时` : ''}${diffMinutes ? `${diffMinutes}分钟` : ''}${diffSeconds ? `${diffSeconds}秒` : ''}`;
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
          <Link to={`/iam/organization-setting/ldap${history.location.search}`}>通用</Link>
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
