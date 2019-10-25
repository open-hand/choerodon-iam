import React, { Component } from 'react';
import { Popover } from 'choerodon-ui';
import { Icon } from 'choerodon-ui/pro';
import { strLength } from '../../common/util';
import './index.less';

export default function renderRoles({ value, record, text, ...restProps }) {
  const maxLength = 300;
  let currentLen = 0;
  let maxTagCount = 0;
  function getTag(role) {
    if (!role.enabled) {
      return <span className="c7n-pro-output-multiple-block disable">{role.name}</span>;
    } else {
      return <span className="c7n-pro-output-multiple-block">{role.name}</span>;
    }
  }
  record.getPristineValue('roles').forEach(role => {
    currentLen += 20 + strLength(role.name);
    if (currentLen < maxLength) {
      maxTagCount += 1;
    }
  });
  const restRoles = record.getPristineValue('roles').slice(maxTagCount);
  return record.getPristineValue('roles').map((role, index) => {
    if (index < maxTagCount) {
      return getTag(role);
    } else if (index === maxTagCount) {
      return (
        <Popover
          trigger="hover"
          popupContainer={that => that}
          placement="bottom"
          content={(
            <div className="expand-more-tags">
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
