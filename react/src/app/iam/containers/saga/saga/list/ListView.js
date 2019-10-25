import React, { useContext } from 'react';
import { Content, Header, Page } from '@choerodon/boot';
import { Table, Modal } from 'choerodon-ui/pro';
import Store from '../store';
import SagaDetails from '../detail/SagaDetails';

const { Column } = Table;

const modalKey = Modal.key();
const modalStyle = {
  width: 'calc(100vw - 350px)',
};

const ListView = () => {
  const { dataSet, prefixCls } = useContext(Store);

  function openModal(id) {
    Modal.open({
      key: modalKey,
      title: '事务定义详情',
      drawer: true,
      style: modalStyle,
      children: <SagaDetails id={id} />,
      okText: '关闭',
      okCancel: false,
      className: `${prefixCls}-sidebar`,
      destroyOnClose: true,
    });
  }

  function renderName({ text, record }) {
    return (
      <span
        onClick={() => openModal(record.get('id'))}
        style={{ cursor: 'pointer' }}
      >
        {text}
      </span>
    );
  }

  return (
    <Page className={prefixCls}>
      <Header title="表格" />
      <Content>
        <Table dataSet={dataSet}>
          <Column renderer={renderName} name="code" />
          <Column name="service" />
          <Column name="description" />
        </Table>
      </Content>
    </Page>
  );
};

export default ListView;
