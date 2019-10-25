import React, { Component } from 'react';
import { Badge, Popover } from 'choerodon-ui';
import { Icon } from 'choerodon-ui/pro';
import { strLength } from '../../../../common/util';
import './index.less';

export default function renderRoles({ value, record, name, maxLength, nameField, badgeConfig, showBadge = () => false }) {
  // const maxLength = 700;
  let currentLen = 0;
  let maxTagCount = 0;
  function getTag(service, index, fromPopOver) {
    if (showBadge(service, index, fromPopOver)) {
      return (
        <Badge {...badgeConfig(service, index)}>
          <span className="c7n-pro-output-multiple-block disable">{service[nameField]}</span>
        </Badge>
      );
    }
    return <span className="c7n-pro-output-multiple-block">{service[nameField]}</span>;
  }
  record.getPristineValue(name).forEach(service => {
    currentLen += 20 + strLength(service[nameField]);
    if (currentLen < maxLength) {
      maxTagCount += 1;
    }
  });
  const restRoles = record.getPristineValue(name).slice(maxTagCount);
  return record.getPristineValue(name).map((role, index) => {
    if (index < maxTagCount) {
      return getTag(role, index, false);
    } else if (index === maxTagCount) {
      return (
        <Popover
          trigger="hover"
          popupContainer={that => that}
          placement="bottom"
          content={(
            <div className="expand-more-tags">
              {restRoles.map((restRole, tagIndex) => getTag(restRole, tagIndex, true))}
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
