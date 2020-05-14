import React, { Component, useEffect, useState, useImperativeHandle } from 'react';
import { Button, Col, Form, Input, Row } from 'choerodon-ui';
import { FormattedMessage, injectIntl } from 'react-intl';
import { withRouter } from 'react-router-dom';
import { observer } from 'mobx-react-lite';
import { axios, Permission, Choerodon } from '@choerodon/boot';

const FormItem = Form.Item;
const intlPrefix = 'user.changepwd';
const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 100 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 9 },
  },
};

let editFocusInput = React.createRef();
function EditPassword(props) {
  const [enablePwd, setEnablePwd] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [confirmDirty, setConfirmDirty] = useState(undefined);
  const { UserInfoStore } = props;
  // state = {
  //     submitting: false,
  //     confirmDirty: null,
  //     res: {},
  // };


  const loadEnablePwd = () => {
    axios.get('/iam/choerodon/v1/system/setting/enable_resetPassword')
      .then((response) => {
        setEnablePwd(response);
      });
  };


  const compareToFirstPassword = (rule, value, callback) => {
    const { intl, form } = props;
    if (value && value !== form.getFieldValue('password')) {
      callback(intl.formatMessage({ id: `${intlPrefix}.twopwd.pattern.msg` }));
    } else {
      callback();
    }
  };

  const validateToNextPassword = (rule, value, callback) => {
    const { form } = props;
    if (value && confirmDirty) {
      form.validateFields(['confirm'], { force: true });
    }
    if (value.indexOf(' ') !== -1) {
      callback('密码不能包含空格');
    }
    callback();
  };

  const handleConfirmBlur = (e) => {
    const { value } = e.target;
    setConfirmDirty(confirmDirty || !!value);
  };

  const handleSubmit = () => {
    const { getFieldValue } = props.form;
    const user = UserInfoStore.getUserInfo;
    const body = {
      originalPassword: getFieldValue('oldpassword'),
      password: getFieldValue('confirm'),
    };
    props.form.validateFields((err, values) => {
      if (!err) {
        setSubmitting(true);
        UserInfoStore.updatePassword(user.id, body)
          .then(({ failed, message }) => {
            setSubmitting(false);
            if (failed) {
              Choerodon.prompt(message);
            } else {
              Choerodon.logout();
            }
          })
          .catch((error) => {
            setSubmitting(false);
            Choerodon.handleResponseError(error);
          });
      }
    });
  };
  useImperativeHandle(props.forwardref, () => (
    {
      handleSubmit,
    }));

  const reload = () => {
    const { resetFields } = props.form;
    resetFields();
  };
  /** 仓库密码修改
      const showModal = () => {
          setState({
              visible: true,
          });
          Modal.confirm({
              className: 'c7n-iam-confirm-modal',
              title: '修改仓库密码',
              content: '确定要修改您的gitlab仓库密码吗？点击确定后，您将跳转至GitLab仓库克隆密码的修改页面。',
              okText: '修改',
              width: 560,
              onOk: () => {
                  const { res: { enable_reset, resetGitlabPasswordUrl } } = this.state;
                  if (enable_reset) {
                      window.open(resetGitlabPasswordUrl);
                  }
              },
          });
      };
   */
  useEffect(() => {
    loadEnablePwd();
  }, []);

  const render = () => {
    const { intl, form } = props;
    const { getFieldDecorator } = form;

    const user = UserInfoStore.getUserInfo;
    return (
      //   <Page
      //     service={[
      //       'base-service.user.selfUpdatePassword',
      //     ]}
      //   >
      <div className="ldapContainer">
        <Form layout="vertical">
          <FormItem
            {...formItemLayout}
          >
            {getFieldDecorator('oldpassword', {
              rules: [{
                required: true,
                message: intl.formatMessage({ id: `${intlPrefix}.oldpassword.require.msg` }),
              }, {
                validator: validateToNextPassword,
              }],
              validateTrigger: 'onBlur',
            })(
              <Input
                autoComplete="off"
                label={<FormattedMessage id={`${intlPrefix}.oldpassword`} />}
                type="password"
                ref={(e) => {
                  editFocusInput = e;
                }}
                showPasswordEye
                disabled={user.ldap}
              />,
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
          >
            {getFieldDecorator('password', {
              rules: [{
                required: true,
                message: intl.formatMessage({ id: `${intlPrefix}.newpassword.require.msg` }),
              }, {
                validator: validateToNextPassword,
              }],
              validateTrigger: 'onBlur',
              validateFirst: true,
            })(
              <Input
                autoComplete="off"
                label={<FormattedMessage id={`${intlPrefix}.newpassword`} />}
                type="password"
                showPasswordEye
                disabled={user.ldap}
              />,
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
          >
            {getFieldDecorator('confirm', {
              rules: [{
                required: true,
                message: intl.formatMessage({ id: `${intlPrefix}.confirmpassword.require.msg` }),
              }, {
                validator: compareToFirstPassword,
              }],
              validateTrigger: 'onBlur',
              validateFirst: true,
            })(
              <Input
                autoComplete="off"
                label={<FormattedMessage id={`${intlPrefix}.confirmpassword`} />}
                type="password"
                onBlur={handleConfirmBlur}
                showPasswordEye
                disabled={user.ldap}
              />,
            )}
          </FormItem>
        </Form>
      </div>
    );
  };
  return render();
}
export default Form.create({})(observer(EditPassword));
