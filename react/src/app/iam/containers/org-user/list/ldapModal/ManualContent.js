import React, { useEffect, useState } from 'react';
import { observer } from 'mobx-react-lite';
import { message, Progress, Spin } from 'choerodon-ui/pro';
import moment from 'moment';
import { useLdapStore } from './stores';
import { useInterval } from '../../../../components/costomHooks';

// 计算时间差
function handleLoadTime(startTime, endTime) {
  if (startTime && endTime) {
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
  } else {
    return '正在计算时间...';
  }
}

const manualContent = observer((props) => {
  const {
    syncRecordDs,
    prefixCls,
    modal,
    ldapStore,
    AppState: { currentMenuType: { organizationId: orgId } },
  } = useLdapStore();

  const record = syncRecordDs.current;
  const id = record && record.get('id');
  const syncEndTime = record && record.get('syncEndTime');

  const [delay, setDelay] = useState(false);
  const [isStop, changeIsStop] = useState(false);

  let intervalId;

  async function pollHistory() {
    try {
      await syncRecordDs.query();
      if (ldapStore.getTabKey === 'auto') {
        setDelay(false);
        clearInterval(intervalId);
        return;
      }
      if (!syncRecordDs.current || (syncRecordDs.current && syncRecordDs.current.get('syncEndTime'))) {
        setDelay(false);
        modal.update({ okProps: { color: 'primary', loading: false }, okText: '手动同步' });
        changeIsStop(false);
      } else {
        setDelay(3000);
        if (new Date() - new Date(syncRecordDs.current.get('syncBeginTime')) > 3600000) {
          modal.update({ okProps: { color: 'red' }, okText: '终止同步' });
          changeIsStop(true);
        } else {
          modal.update({ okProps: { color: 'primary', loading: true }, okText: '手动同步' });
          changeIsStop(false);
        }
      }
    } catch (e) {
      return false;
    }
  }

  useEffect(() => {
    setDelay(false);
    if (ldapStore.getTabKey === 'manual') {
      pollHistory();
      modal.handleOk(async () => {
        if (!isStop) {
          const result = await ldapStore.syncUsers(orgId);
          if (!result.failed) {
            pollHistory();
            setDelay(3000);
          } else {
            message.error(result.message);
          }
          return false;
        } else {
          clearInterval(intervalId);
          setDelay(false);
          const result = await ldapStore.stopSyncUsers(orgId);
          if (!result.failed) {
            syncRecordDs.query();
          } else {
            message.error(result.message);
          }
          modal.update({ okProps: { color: 'primary', loading: false }, okText: '手动同步' });
          return false;
        }
      });
    } else {
      clearInterval(intervalId);
    }
  }, [ldapStore.getTabKey]);

  function getSyncRecord() {
    if (syncRecordDs.status === 'loading') {
      return <Spin />;
    }
    if (!id) {
      return <span className={`${prefixCls}-manual-record`}>暂无同步记录</span>;
    } else {
      const syncBeginTime = record.get('syncBeginTime');
      const newUserCount = record.get('newUserCount');
      const errorUserCount = record.get('errorUserCount');
      const updateUserCount = record.get('updateUserCount');
      return (
        <div className={`${prefixCls}-manual-record`} style={{ display: syncEndTime ? 'block' : 'none' }}>
          <div className={`${prefixCls}-manual-record-text`}>
            上次同步完成时间
            <span className={`${prefixCls}-manual-record-time`}>{syncEndTime}</span>
            &nbsp; (耗时{handleLoadTime(syncBeginTime, syncEndTime)})
          </div>
          <div>
            共同步
            <span className={`${prefixCls}-manual-record-number-success`}>{newUserCount + updateUserCount || 0}</span>
            个用户成功，
            <span className={`${prefixCls}-manual-record-number-failed`}>{errorUserCount || 0}</span>
            个用户失败
          </div>
        </div>
      );
    }
  }

  useInterval(pollHistory, delay);

  return (
    <div className={`${prefixCls}-manual-content`}>
      {getSyncRecord()}
      <div className={`${prefixCls}-manual-sync`} style={{ display: !syncEndTime && id ? 'block' : 'none' }}>
        <Progress type="loading" size="large" />
        <div className={`${prefixCls}-manual-sync-loading`}>正在导入中</div>
        <div>(本次导入耗时较长，您可先返回进行其他操作)</div>
      </div>
    </div>
  );
});

export default manualContent;
