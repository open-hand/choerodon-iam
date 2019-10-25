import React, { useContext, useState } from 'react';
import _ from 'lodash';
import { Popover } from 'choerodon-ui';
import { Table, DataSet, Icon, Tooltip } from 'choerodon-ui/pro';
import { strLength } from '../../../common/util';
import InRowDataSet from './stores/InRowDataSet';


const { Column } = Table;
const serviceTypeMap = {
  normal: '普通应用',
  test: '测试应用',
};
const serviceVersionStatusMap = {
  unpublished: { text: '未发布', icon: 'error', color: '#F44336' },
  processing: { text: '发布中', icon: 'timelapse', color: 'rgb(77, 144, 254)' },
  done: { text: '已完成', icon: false, color: false },
  failure: { text: '失败', icon: 'error', color: '#F44336' },
};

export default function renderExpandRow({ record }) {
  const dataSet = new DataSet(InRowDataSet(record.get('appServiceDetailsVOS')));
  
  function getVersionTag({ value: { status, version } }) {
    if (!serviceVersionStatusMap[status]) {
      return <span>{version}</span>;
    }
    const { color } = serviceVersionStatusMap[status];
    const { icon } = serviceVersionStatusMap[status];
    const { text } = serviceVersionStatusMap[status];
    if (icon) {
      return (
        <span style={{ display: 'flex', alignItems: 'center' }}>
          <Tooltip placement="top" title={text}>
            <Icon type={icon} style={{ color, marginRight: '.02rem' }} />
          </Tooltip>
          <span>{version}</span>
        </span>
      );
    } else {
      return <span>{version}</span>;
    }
  }
  function renderVersion({ value, record: versionRecord, text, ...restProps }) {
    const maxLength = 320;
    let currentLen = 0;
    let maxTagCount = 0;
    function getTag(item) {
      return <span className="c7n-pro-output-multiple-block">{getVersionTag({ value: item })}</span>;
    }
    versionRecord.getPristineValue('appServiceVersions').forEach(item => {
      currentLen += 20 + strLength(item.version);
      if (currentLen < maxLength) {
        maxTagCount += 1;
      }
    });
    const restRoles = versionRecord.getPristineValue('appServiceVersions').slice(maxTagCount);
    return versionRecord.getPristineValue('appServiceVersions').map((item, index) => {
      if (index < maxTagCount) {
        return getTag(item);
      } else if (index === maxTagCount) {
        return (
          <Popover
            trigger="hover"
            popupContainer={that => that}
            placement="bottom"
            content={(
              <div className="expand-more-tags app-versions">
                {restRoles.map(restRole => (<div style={{ margin: '0.08rem 0', textAlign: 'center' }}>{getTag(restRole)}</div>))}
              </div>
            )}
          >
            <Icon color="primary" type="expand_more" />
          </Popover>
        );
      }
      return null;
    });
  }

  function renderType({ value }) {
    return serviceTypeMap[value];
  }
  
  return (
    <div className="inrow-table">
      <Table pagination={false} pristine queryBar="none" dataSet={dataSet}>
        <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="name" />
        <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="code" />
        <Column renderer={renderType} style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="type" />
        <Column renderer={renderVersion} width={343} name="appServiceVersions" className="app-versions" />
      </Table>
    </div>
  );
}
