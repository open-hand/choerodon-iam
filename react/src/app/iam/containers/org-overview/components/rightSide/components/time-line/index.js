import React from 'react';
import { observer } from 'mobx-react-lite';
import './index.less';
import { Icon } from 'choerodon-ui';

// const data =


const TimeLine = observer(() => (
  <div className="c7ncd-timeLine">
    <div className="c7ncd-timeLine-body">
      <ul>
        <li>
          <div className="c7ncd-timeLine-date">
            <span>22</span>
            <span>Feb</span>
          </div>
          <div className="c7ncd-timeLine-content">
            <div className="c7ncd-timeLine-content-header">
              <Icon type="project_line" />
              <span>创建项目</span>
            </div>
            <p>Leon 创建了项目【Choerodon</p>
          </div>
        </li>
        <li>
          <div className="c7ncd-timeLine-date">
            <span>22</span>
            <span>Feb</span>
          </div>
          <div className="c7ncd-timeLine-content">
            <div className="c7ncd-timeLine-content-header">
              <Icon type="project_line" className="stop" />
              <span>停用项目</span>
            </div>
            <p>Leon 停用了项目【Choerodon</p>
          </div>
        </li>
        <li>
          <div className="c7ncd-timeLine-date">
            <span>22</span>
            <span>Feb</span>
          </div>
          <div className="c7ncd-timeLine-content">
            <div className="c7ncd-timeLine-content-header">
              <Icon type="blur_linear" />
              <span>权限分配</span>
            </div>
            <p>Will 为 Leon分配了【组织成员】</p>
          </div>
        </li>
        <li>
          <div className="c7ncd-timeLine-date">
            <span>22</span>
            <span>Feb</span>
          </div>
          <div className="c7ncd-timeLine-content">
            <div className="c7ncd-timeLine-content-header">
              <Icon type="blur_linear" className="stop" />
              <span>权限分配</span>
            </div>
            <p>用户XXX（用户名）被 XXX（用户名）</p>
          </div>
        </li>
        <li>
          <div className="c7ncd-timeLine-date">
            <span>22</span>
            <span>Feb</span>
          </div>
          <div className="c7ncd-timeLine-content">
            <div className="c7ncd-timeLine-content-header">
              <Icon type="settings" />
              <span>基础设置修改</span>
            </div>
            <p>Leon 创建了项目【Choerodon</p>
          </div>
        </li>
      </ul>
    </div>
  </div>
));

export default TimeLine;
