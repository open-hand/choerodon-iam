import React, {
  useState, useEffect, useContext, Fragment, useImperativeHandle,
} from 'react';
import { FormattedMessage } from 'react-intl';
import { observer } from 'mobx-react-lite';
import {
  Button, Tabs, Icon, Form, Input, Checkbox, Select,
} from 'choerodon-ui';
import { Permission, Choerodon } from '@choerodon/boot';
import AvatarUploader from './AvatarUploader';

const { Option } = Select;
const FormItem = Form.Item;
function EditUserInfo(props) {
  const [selecteds, setSelecteds] = useState([]);
  const { form, UserInfoStore, intl, intlPrefix } = props;
  const { getFieldDecorator, getFieldValue } = form;
  const [visible, setVisible] = useState(false);
  const [user, setUser] = useState({});
  const [avatar, setAvatar] = useState(UserInfoStore.getAvatar);

  const handleChange = (value, e, current) => {
    const newSelecteds = selecteds;
    newSelecteds[current] = Number(value);
    setSelecteds(newSelecteds);
  };
  const handleDelteItem = (current) => {
    const keys = form.getFieldValue('keys');
    if (keys.length === 1) {
      return;
    }
    const newSelecteds = selecteds;
    newSelecteds[current] = 0;
    setSelecteds(newSelecteds);
    form.setFieldsValue({
      keys: keys.filter(key => key !== current),
    });
  };
  const handleSubmit = () => {
    const { resetAvatar } = props;
    props.form.validateFields((err, values) => {
      if (!err) {
        const newUser = {
          ...user,
          email: values.email,
          language: values.language,
          phone: values.phone,
          timeZone: values.timeZone,
          realName: values.realName,
          imageUrl: avatar,
        };
        // return false;
        // console.log(' UserInfoStore.getAvatar', UserInfoStore.getAvatar);
        // console.log(avatar);
        // resetAvatar(info.imageUrl);

        UserInfoStore.updateUserInfo(newUser).then((data) => {
          if (data.failed) {
            Choerodon.prompt(data.message);
          } else {
            form.resetFields();
            UserInfoStore.setUserInfo(data);
            // 父页面头像更新  可以通过Store 进行更新
            resetAvatar(avatar);
            Choerodon.prompt(intl.formatMessage({ id: 'modify.success' }));
            // setSubmitting(false);
            // props.loadUserInfo();
            props.modal.close();
          }
        }).catch(() => {
          Choerodon.prompt(intl.formatMessage({ id: 'modify.error' }));
          // setSubmitting(false);
        });
      }
    });
  };
  /**
 * 临时方案 解决点击取消 不保存上传的头像
  const handleClose = () => {
    if (UserInfoStore.getAvatar !== avatar) {
      console.log('save after',user);
      const newUser = {
        ...user,
        imageUrl: avatar,
      };
      UserInfoStore.updateUserInfo(newUser).then((data) => {
        if (data.failed) {
          Choerodon.prompt(data.message);
        } else {
          form.resetFields();
          UserInfoStore.setUserInfo(data);
          Choerodon.prompt(intl.formatMessage({ id: 'modify.success' }));
          // setSubmitting(false);
          // props.loadUserInfo();
        }
      }).catch(() => {
        Choerodon.prompt(intl.formatMessage({ id: 'modify.error' }));
        // setSubmitting(false);
      });
    }
  };
 * 
 */
  useImperativeHandle(props.forwardref, () => (
    {
      handleSubmit,
    }));


  const renderLanguageOptions = () => {
    let language;
    if (language) {
      return language.content.map(({ code, name }) => (<Option key={code} value={code}>{name}</Option>));
    } else {
      return [
        <Option key="zh_CN" value="zh_CN"><FormattedMessage id={`${intlPrefix}.language.zhcn`} /></Option>,
        // <Option key="en_US" value="en_US"><FormattedMessage id={`${intlPrefix}.language.enus`}/></Option>,
      ];
    }
  };
  const renderTimeZoneOptions = () => {
    const timeZone = [];
    if (timeZone.length > 0) {
      return timeZone.map(({ code, description }) => (<Option key={code} value={code}>{description}</Option>));
    } else {
      return [
        <Option key="CTT" value="CTT"><FormattedMessage id={`${intlPrefix}.timezone.ctt`} /></Option>,
        // <Option key="EST" value="EST"><FormattedMessage id={`${intlPrefix}.timezone.est`}/></Option>,
      ];
    }
  };
  const openAvatorUploader = () => {
    setVisible(true);
  };

  const handleVisibleChange = (newVisible) => {
    setVisible(newVisible);
  };

  const renderAvatar = ({ id, realName }) => {
    const { AppState, resetAvatar } = props;
    return (
      <div
        className="user-info-avatar user-info-avatar-modal-edit"
        style={
          avatar && {
            backgroundImage: `url('${Choerodon.fileServer(avatar)}')`,
          }
        }
      >
        {!avatar && realName && realName.charAt(0)}
        <Permission
          service={[]}
          type="site"
        >
          <Button className="user-info-avatar-button" onClick={openAvatorUploader}>
            <div className="user-info-avatar-button-icon">
              <Icon type="photo_camera" />
            </div>
          </Button>

          <AvatarUploader id={id} visible={visible} onVisibleChange={handleVisibleChange} setAvatar={setAvatar} />
        </Permission>
      </div>
    );
  };


  const checkEmailAddress = (rule, value, callback) => {
    if (value !== user.email && value !== '') {
      UserInfoStore.checkEmailAddress(value).then(({ failed }) => {
        if (failed) {
          callback(intl.formatMessage({ id: `${intlPrefix}.email.used.msg` }));
        } else {
          callback();
        }
      }).catch(Choerodon.handleResponseError);
    } else {
      callback();
    }
  };

  const checkPhone = (rule, value, callback) => {
    const { formatMessage } = intl;
    const code = getFieldValue('internationalTelCode') || '86';
    let pattern = /^[0-9]*$/;
    if (value) {
      if (pattern.test(value)) {
        if (code === '86') {
          pattern = /^1[3-9]\d{9}$/;
          if (pattern.test(value)) {
            UserInfoStore.checkPhoneExist(value).then((res) => {
              if (res) {
                callback(intl.formatMessage({ id: `${intlPrefix}.phone.used.msg` }));
              } else {
                callback();
              }
            }).catch(Choerodon.handleResponseError);
          } else {
            callback(formatMessage({ id: `${intlPrefix}.phone.district.rule` }));
          }
        } else {
          callback();
        }
      } else {
        callback(formatMessage({ id: `${intlPrefix}.num.required` }));
      }
    } else {
      callback();
    }
  };

  useEffect(() => {
    // 初始化
    setUser(UserInfoStore.getUserInfo);
  }, []);
  const renderForm = () => {
    const { organizationName, realName, organizationCode, email, loginName, phone, language, timeZone } = user;
    return (
      <Form layout="vertical">
        <FormItem>
          {renderAvatar(user)}
          <span className="user-info-avatar-modal-edit-text">头像</span>
        </FormItem>
        <FormItem>
          {getFieldDecorator('realName', {
            rules: [{
              required: true,
              whitespace: true,
              message: intl.formatMessage({ id: `${intlPrefix}.name.require.msg` }),
            }],
            initialValue: realName,
          })(
            <Input
              placeholder={intl.formatMessage({ id: `${intlPrefix}.name` })}
              autoComplete="off"
              label={intl.formatMessage({ id: `${intlPrefix}.name` })}

            />,
          )}
        </FormItem>
        <FormItem>
          {getFieldDecorator('loginName', {
            rules: [{
              required: true,
            }],
            initialValue: loginName,
          })(
            <Input
              defaultValue={loginName}
              disabled
              placeholder={intl.formatMessage({ id: `${intlPrefix}.loginname` })}
              label={intl.formatMessage({ id: `${intlPrefix}.loginname` })}
            />,
          )}

        </FormItem>
        <FormItem>
          {
            getFieldDecorator('email', {
              rules: [
                {
                  // required: true,
                  // whitespace: true,
                  message: intl.formatMessage({ id: `${intlPrefix}.email.require.msg` }),
                },
                {
                  type: 'email',
                  message: intl.formatMessage({ id: `${intlPrefix}.email.pattern.msg` }),
                },
                {
                  validator: checkEmailAddress,
                },
              ],
              initialValue: email,
            })(
              <Input
                placeholder={intl.formatMessage({ id: `${intlPrefix}.email` })}
                label={intl.formatMessage({ id: `${intlPrefix}.email` })}

              />,
            )
          }

        </FormItem>

        <FormItem>
          {getFieldDecorator('phone', {
            rules: [{
              validator: checkPhone,

            },

            ],
            initialValue: phone,
          })(
            <Input
              placeholder={intl.formatMessage({ id: `${intlPrefix}.phone` })}
              label={intl.formatMessage({ id: `${intlPrefix}.phone` })}
            />,
          )}
        </FormItem>
        <FormItem>
          {getFieldDecorator('language', {
            initialValue: language,
          })(
            <Select
              placeholder={intl.formatMessage({ id: `${intlPrefix}.language` })}
              label={intl.formatMessage({ id: `${intlPrefix}.language` })}
            // getPopupContainer={() => document.getElementsByClassName('page-content')[0]}
            >
              {renderLanguageOptions()}
            </Select>,
          )}
        </FormItem>
        <FormItem>
          {getFieldDecorator('timeZone', {
            initialValue: timeZone || 'CTT',
          })(
            <Select
              placeholder={intl.formatMessage({ id: `${intlPrefix}.timezone` })}
              label={intl.formatMessage({ id: `${intlPrefix}.timezone` })}
            // getPopupContainer={() => document.getElementsByClassName('page-content')[0]}
            >
              {renderTimeZoneOptions()}
            </Select>,
          )}
        </FormItem>
        <FormItem>
          {getFieldDecorator('organizationName', {
            rules: [
              {
                required: true,
              },
            ],
            initialValue: organizationName || '',
          })(
            <Input
              disabled
              placeholder={intl.formatMessage({ id: `${intlPrefix}.org.name` })}
              label={intl.formatMessage({ id: `${intlPrefix}.org.name` })}
            />,
          )}
        </FormItem>
        <FormItem>
          {getFieldDecorator('organizationCode', {
            rules: [
              {
                required: true,
              },
            ],
            initialValue: organizationCode || '',
          })(
            <Input
              disabled
              placeholder={intl.formatMessage({ id: `${intlPrefix}.org.code` })}
              label={intl.formatMessage({ id: `${intlPrefix}.org.code` })}
            />,
          )}
        </FormItem>
      </Form>

    );
  };
  return renderForm();
}

export default Form.create()(observer(EditUserInfo));
