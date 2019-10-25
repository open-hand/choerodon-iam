import React, { useState, useEffect, useRef } from 'react';
import classnames from 'classnames';
import { useObserver } from 'mobx-react-lite';

import jsonFormat from '../../../../common/json-format';
import SagaStore from '../store/SagaImgStore';
import './style/index.less';

const prefixCls = 'c7n-saga';

const SagaImg = function SagaImg() {
  const [showDetail, setShowDetail] = useState(false);
  const [jsonTitle, setJsonTitle] = useState(false);
  const [activeCode, setActiveCode] = useState('');
  const [json, setJson] = useState('');
  const [task, setTask] = useState([]);
  const taskDetail = useRef(null);
  const taskImg = useRef(null);
  
  /**
   * 1. taskImg 超出 detail未超出屏幕高度
   * 2. taskImg 未超出 detail超出屏幕高度 (不做处理)
   * 3. taskImg 未超出 detail也没有超出 (不会有滚动)
   * 4. taskImg 超出 detail也超出  taskImg没有detail超出的多
   * 5. taskImg 超出 detail也超出  taskImg比detail超出的多
   */
  function handleScroll(container) {
    const imgDom = taskImg.current;
    const detail = taskDetail.current;
    if (!imgDom) {
      return;
    }
    const imgHeight = imgDom.scrollHeight;
    const top = imgDom.offsetTop;
    if (detail && imgHeight + top > container.clientHeight && imgHeight > detail.scrollHeight) {
      const detailHeight = detail.scrollHeight;
      let detailTop = container.scrollTop;
      if (detailTop > top) {
        if (detailHeight > container.clientHeight) {
          detailTop = Math.min((imgHeight - detailHeight) + top, detailTop);
        }
        detail.style.cssText = `top: ${detailTop}px`;
        detail.classList.add('autoscroll');
      } else {
        detail.classList.remove('autoscroll');
        detail.style.cssText = '';
      }
    } else if (detail) {
      detail.classList.remove('autoscroll');
      detail.style.cssText = '';
    }
  }

  // 获取详情框
  function getSidebarContainer() {
    const content = document.body.getElementsByClassName(`${prefixCls}-sidebar`)[0];
    return content.getElementsByClassName('c7n-pro-modal-body')[0];
  }

  // 添加滚动监听
  function addScrollEventListener() {
    const container = getSidebarContainer();
    container.addEventListener('scroll', handleScroll.bind(this, container));
  }

  useEffect(() => {
    addScrollEventListener();
  });

  // 连接线 
  function lines() {
    return (
      <div className={`${prefixCls}-img-line`} />
    );
  }

  
  // 是否渲染任务详情
  function showDetails(code) {
    if (code === 'output') {
      return;
    }
    if (code === 'input' || code === 'output') {
      setActiveCode(code);
      setJsonTitle(code === 'input' ? '输入数据' : '输出数据');
      setShowDetail(false);
      setJson(SagaStore.getData[code]);
      (() => {
        const container = getSidebarContainer();
        handleScroll(container);
      })();
      return;
    }
    setActiveCode(code);
    setJsonTitle(false);
    setShowDetail(true);
    (() => {
      const container = getSidebarContainer();
      handleScroll(container);
    })();
  }

  // 获取点击的是第几个任务
  function getClickIndex(code) {
    const tasks = SagaStore.getTasks;
    tasks.forEach((items) => {
      items.forEach((item) => {
        if (item.code === code) {
          setTask(item);
        }
      });
    });
  }
  
  // 任务框
  function squareWrapper(node) {
    if (typeof node === 'string') {
      const clsNames = classnames(`${prefixCls}-img-square`, 
        node === activeCode ? `${prefixCls}-task-active` : '');
      return (
        <div
          className={clsNames}
          key={node}
          onClick={() => {
            showDetails(node);
            getClickIndex(node);
          }}
        >
          <span>{node}</span>
        </div>
      );
    }
    return (
      <div className={`${prefixCls}-img-squares`}>
        {node}
      </div>
    );
  }
  
  // 任务内容
  function renderContent() {
    const tasks = SagaStore.getTasks;
    const line = lines();
    const content = [];
    if (tasks.length) {
      content.push(line);
      tasks.forEach((items) => {
        const node = items.map((
          { code },
        ) => squareWrapper(code));
        if (node.length === 1) {
          content.push(node);
        } else {
          content.push(squareWrapper(node));
        }
        content.push(line);
      });
      return content;
    }
    return line;
  }

  // 输入输出圆形框
  function circleWrapper(code) {
    const clsNames = classnames(`${prefixCls}-img-circle`, 
      code.toLowerCase() === activeCode ? `${prefixCls}-task-active` : '',
      code === 'Output' ? 'output' : '');
    return (
      <div
        className={clsNames}
        key={code}
        onClick={showDetails.bind(this, code.toLowerCase())}
      >
        {code}
      </div>
    );
  }

  // json字符串转换
  function handleTransObj(str) {
    let obj = null;
    if (!str) {
      return obj;
    }
    obj = JSON.parse(str);
    if (typeof obj === 'string') {
      /* eslint-disable-next-line */
      obj = eval(obj);
    }
    return obj;
  }

  // 是否渲染输入、输出数据
  function renderJson() {
    return (
      <div className={`${prefixCls}-task-detail`}>
        <div className={`${prefixCls}-task-detail-title`}>
          {jsonTitle}
        </div>
        <div className={`${prefixCls}-task-detail-content`}>
          <div className={`${prefixCls}-detail-json`}>
            <pre style={{ maxHeight: '350px' }}>
              <code id="json">
                {jsonFormat(handleTransObj(json))}
              </code>
            </pre>
          </div>
        </div>
      </div>
    );
  }

  // 任务详情内容
  function renderTaskDetail() {
    const {
      code,
      description,
      seq,
      maxRetryCount,
      timeoutSeconds,
      timeoutPolicy,
      service,
      concurrentLimitPolicy,
      concurrentLimitNum,
      inputSchema,
    } = task;
    const list = [{
      key: '任务编码',
      value: code,
    }, {
      key: '任务描述',
      value: description,
    }, {
      key: '序列',
      value: seq,
    }, {
      key: '并发限制模式',
      value: concurrentLimitPolicy,
    }, {
      key: '最大并发数',
      value: concurrentLimitNum,
    }, {
      key: '最大重试次数',
      value: maxRetryCount,
    }, {
      key: '超时时间',
      value: timeoutSeconds,
    }, {
      key: '超时策略',
      value: timeoutPolicy,
    }, {
      key: '所属微服务',
      value: service,
    }];
    const input = {
      key: '输入数据示例',
      value: inputSchema ? jsonFormat(JSON.parse(inputSchema)) : '暂无数据',
    };
    return (
      <div className={`${prefixCls}-task-detail`}>
        <div className={`${prefixCls}-task-detail-content`}>
          {list.map(({ key, value }) => <div key={`task-detail-${key}`}>{key}: {value}</div>)}
          <div>{input.key}:
            <div className={`${prefixCls}-detail-json`}>
              <pre style={{ maxHeight: '350px' }}><code>{input.value}</code></pre>
            </div>
          </div>
        </div>
      </div>
    );
  }

  const input = circleWrapper('Input');
  const output = circleWrapper('Output');
  return useObserver(() => (
    <div className={`${prefixCls}-img-detail-wrapper`}>
      <div className={`${prefixCls}-img`} ref={taskImg}>
        {input}
        {renderContent()}
        {output}
      </div>
      {showDetail && (
      <div className={`${prefixCls}-img-detail`} ref={taskDetail}>
        <div>
          <div className={`${prefixCls}-task-detail-title`}>
            任务详情
          </div>
          {renderTaskDetail()}
        </div>
      </div>
      )}
      {jsonTitle && (
      <div className={`${prefixCls}-img-detail`} ref={taskDetail}>
        {renderJson()}
      </div>
      )}
    </div>
  ));
};

export default SagaImg;
