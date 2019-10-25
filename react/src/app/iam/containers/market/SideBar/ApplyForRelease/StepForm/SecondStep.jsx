import React, { useContext } from 'react';
import { Select, Form, TextArea, TextField, SelectBox, Tooltip } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import Store from '../Store';

import UploadLogo from '../../../../../components/UploadLogo';

const { Option } = Select;

const SecondStep = observer((props) => {
  const { marketAppDataSet, projectId } = useContext(Store);

  const imgUrlField = marketAppDataSet.current.getField('imageUrl');
  const validationMessage = imgUrlField.isValid() ? '' : imgUrlField.getValidationMessage();

  const setUpload = (value) => {
    marketAppDataSet.current.set('imageUrl', value);
  };
  return (
    <React.Fragment>
      <UploadLogo setUpload={setUpload} img={marketAppDataSet.current.get('imageUrl')} validationMessage={validationMessage} projectId={projectId} />
      <Form labelLayout="float" dataSet={marketAppDataSet} columns={2} style={{ width: 512 }}>
        <TextField name="name" colSpan={2} />
        <TextField name="contributor" colSpan={2} disabled />
        <Select name="categoryOption" clearButton={false} />
        <TextField name="categoryName" disabled={marketAppDataSet.current.get('categoryType') !== 'custom'} />
        <TextArea name="description" resize="vertical" colSpan={2} />
        <SelectBox name="free" colSpan={2}>
          <Option value>是</Option>
          <Option value={false}>否</Option>
        </SelectBox>
        <SelectBox name="publishType" colSpan={2}>
          <Option value="mkt_code_only">源代码</Option>
          <Option value="mkt_deploy_only">部署包</Option>
        </SelectBox>
        <TextField name="notificationEmail" colSpan={2} help="该邮箱用于接收消息通知" showHelp="tooltip" />
        <TextArea name="remark" resize="vertical" colSpan={2} />
      </Form>
    </React.Fragment>
  );
});

export default SecondStep;
