import React, { Component, useMemo } from 'react';
import { Icon } from 'choerodon-ui';
import PropTypes from 'prop-types';
import './StatusTag.less';

const Color = {
  RUNNING: '#4d90fe',
  FAILED: '#f44336',
  COMPLETED: '#00BFA5',
  NON_CONSUMER: '#00BFA5',
  DEFAULT: '#b8b8b8',
  QUEUE: '#ffb100',
  ENABLE: '#00bfa5',
  DISABLE: '#d3d3d3',
  FINISHED: '#ffb100',
  NO_APPROVAL: '#ffb100',
  APPROVED: '#00bfa5',
  REJECTED: '#f44336',
};

const IconType = {
  COMPLETED: 'check_circle',
  NON_CONSUMER: 'check_circle',
  FAILED: 'cancel',
  ENABLE: 'check_circle',
  DISABLE: 'remove_circle',
  FINISHED: 'state_over',
  RUNNING: 'timelapse',
  PREDEFINE: 'settings',
  CUSTOM: 'av_timer',
  UN_START: 'timer',
  QUEUE: 'watch_later',
};
const propTypes = {
  name: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.bool,
  ]),
  color: PropTypes.string,
  colorCode: PropTypes.string,
  iconType: PropTypes.string,
};
const defaultProps = {
  colorCode: 'DEFAULT',
};

function StatusTag(props) {
  const { name, colorCode = defaultProps.colorCode, color, iconType, ...otherProps } = props;
  function renderIconMode() {
    return (
      <span
        {...otherProps}
        className="c7n-iam-status-tag-with-icon"
        style={{
          ...props.style,
        }}
      >
        <Icon type={iconType || [IconType[colorCode]]} />
        <span>{name || ''}</span>
      </span>
    );
  }

  function renderDefaultMode() {
    const additionalStyles = {};
    const offsetStyle = {};
    if (name.length > 3) {
      additionalStyles.width = 'auto';
      offsetStyle.marginLeft = '-0.02rem';
    }
    return (
      <div
        {...otherProps}
        className="c7n-iam-status-tag"
        style={{
          background: color || Color[colorCode],
          ...additionalStyles,
          ...props.style,
        }}
      >
        <div style={offsetStyle}>{name}</div>
      </div>
    );
  }

  function render() {
    const { mode } = props;
    switch (mode) {
      case 'icon':
        return useMemo(() => renderIconMode(), [name, color, colorCode]);
      default:
        // return renderDefaultMode();
        return useMemo(() => renderDefaultMode(), [name, color, colorCode]);
    }
  }
  return render();
}
StatusTag.propTypes = propTypes;
export default StatusTag;
