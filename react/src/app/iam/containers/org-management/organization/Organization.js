import React, { Component } from 'react';
import { runInAction } from 'mobx';
import { inject, observer } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Button, Form, Input, Modal, Tooltip, Row, Col, Select, Icon } from 'choerodon-ui';
import { Table, message } from 'choerodon-ui/pro';
import { Content, Header, TabPage, Permission, axios, Breadcrumb, Action, Choerodon } from '@choerodon/boot';
import { FormattedMessage, injectIntl } from 'react-intl';
import classnames from 'classnames';
import MouseOverWrapper from '../../../components/mouseOverWrapper/index';
import StatusTag from '../../../components/statusTag/index';
import './Organization.less';
import AvatarUploader from '../../../components/avatarUploader';

const prefixCls = 'c7n-iam-organization';
const { Sidebar } = Modal;
const { Option } = Select;
const { Column } = Table;
const FormItem = Form.Item;
const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 8 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 16 },
  },
};
const inputWidth = 340;
const intlPrefix = 'global.organization';
let timer;

@Form.create()
@withRouter
@observer
export default class Organization extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectLoading: true,
      isShowAvatar: false,
      imgUrl: null,
    };
  }

  handleRefresh = () => {
    const { organizationDataSet } = this.props;
    organizationDataSet.query();
  };

  // 创建组织侧边
  createOrg = () => {
    const { form, OrganizationStore } = this.props;
    form.resetFields();
    this.setState({
      imgUrl: null,
    });
    runInAction(() => {
      OrganizationStore.setEditData({});
      OrganizationStore.show = 'create';
      OrganizationStore.showSideBar();
    });
  };

  handleEdit = (data) => {
    const { form, OrganizationStore } = this.props;
    form.resetFields();
    this.setState({
      imgUrl: data.imageUrl,
    });
    runInAction(() => {
      OrganizationStore.show = 'edit';
      OrganizationStore.setEditData(data);
      OrganizationStore.showSideBar();
    });
  };

  showDetail = (data) => {
    const { OrganizationStore } = this.props;
    runInAction(() => {
      OrganizationStore.setEditData(data);
      OrganizationStore.loadOrgDetail(data.id).then((mes) => {
        if (mes) {
          Choerodon.prompt(mes);
        }
      });
      OrganizationStore.show = 'detail';
    });
  }

  handleSubmit = (e) => {
    e.preventDefault();
    const { form, intl, OrganizationStore, HeaderStore, AppState, organizationDataSet } = this.props;
    if (OrganizationStore.show !== 'detail') {
      form.validateFields((err, values, modify) => {
        Object.keys(values).forEach((key) => {
          // 去除form提交的数据中的全部前后空格
          if (typeof values[key] === 'string') values[key] = values[key].trim();
        });
        const { realName, id } = AppState.getUserInfo;
        if (values.userId === `${realName}`) values.userId = id;
        if (values.category === '默认组织') values.category = 'DEFAULT';
        if (OrganizationStore.editData.imageUrl !== this.state.imgUrl) modify = true;
        if (!err) {
          OrganizationStore.createOrUpdateOrg(values, modify, this.state.imgUrl, HeaderStore)
            .catch(mes => {
              message.error(mes);
            }).finally(() => {
              organizationDataSet.query();
            });
        }
      });
    } else {
      OrganizationStore.hideSideBar();
    }
  };

  handleCancelFun = () => {
    const { OrganizationStore } = this.props;
    OrganizationStore.hideSideBar();
  };

  handleDisable = ({ enabled, id }) => {
    const { intl, OrganizationStore, HeaderStore, AppState, organizationDataSet } = this.props;
    OrganizationStore.toggleDisable(id, enabled)
      .then(() => {
        Choerodon.prompt(intl.formatMessage({ id: enabled ? 'disable.success' : 'enable.success' }));
        organizationDataSet.query();
      }).catch(Choerodon.handleResponseError);
  };

  checkHomepage = async (rule, homepage, callback) => {
    if (!homepage) {
      callback();
      return;
    }
    try {
      const res = await axios.post('/iam/choerodon/v1/domain/check', { url: homepage });
      if (res) {
        callback();
      } else {
        callback('请输入可访问的官网地址！');
      }
    } catch (err) {
      callback('网络异常，请稍后再试！');
    }
  }

  /**
   * 组织编码校验
   * @param rule 表单校验规则
   * @param value 组织编码
   * @param callback 回调函数
   */
  checkCode = (rule, value, callback) => {
    const { intl, OrganizationStore } = this.props;
    OrganizationStore.checkCode(value)
      .then(({ failed }) => {
        if (failed) {
          callback(intl.formatMessage({ id: 'global.organization.onlymsg' }));
        } else {
          callback();
        }
      });
  };

  handleSelectFilter = (value) => {
    this.setState({
      selectLoading: true,
    });

    const queryObj = {
      param: value,
      sort: 'id',
      organization_id: 0,
    };

    if (timer) {
      clearTimeout(timer);
    }

    if (value) {
      timer = setTimeout(() => this.loadUsers(queryObj), 300);
    } else {
      return this.loadUsers(queryObj);
    }
  }

  handleCategorySelectFilter = (value) => {
    this.setState({
      selectLoading: true,
    });

    const queryObj = {
      param: value,
    };

    if (timer) {
      clearTimeout(timer);
    }

    if (value) {
      timer = setTimeout(() => this.loadOrganizationCategories(queryObj), 300);
    } else {
      return this.loadOrganizationCategories(queryObj);
    }
  }

  // 加载全平台用户信息
  loadUsers = (queryObj) => {
    const { OrganizationStore } = this.props;
    OrganizationStore.loadUsers(queryObj).then((data) => {
      OrganizationStore.setUsersData((data.list || []).slice());
      this.setState({
        selectLoading: false,
      });
    });
  }

  // 加载全部组织类别
  loadOrganizationCategories = (queryObj) => {
    const { OrganizationStore } = this.props;
    OrganizationStore.loadOrganizationCategories(queryObj).then((data) => {
      OrganizationStore.setOrgCategories((data.list || []).slice());
      this.setState({
        selectLoading: false,
      });
    });
  }

  /**
   * 获取组织所有者下拉选项
   * @returns {any[]}
   */
  getOption() {
    const { OrganizationStore } = this.props;
    const usersData = OrganizationStore.getUsersData;
    return usersData && usersData.length > 0 ? (
      usersData.map(({ id, realName, email, imageUrl }) => (
        <Option key={id} value={id}>
          <Tooltip placement="left" title={email} realName={realName}>
            <div className={`${prefixCls}-option`}>
              <div className={`${prefixCls}-option-avatar`}>
                {
                  imageUrl ? <img src={imageUrl} alt="userAvatar" style={{ width: '100%' }} />
                    : <span className={`${prefixCls}-option-avatar-noavatar`}>{realName && realName.split('')[0]}</span>
                }
              </div>
              <span>{realName}</span>
            </div>
          </Tooltip>
        </Option>
      ))
    ) : null;
  }


  /**
   * 获取组织类型下拉选项
   * @returns {any[]}
   */
  getCategories() {
    const { OrganizationStore } = this.props;
    const orgCategories = OrganizationStore.getOrgCategories;
    return orgCategories && orgCategories.length > 0 ? (
      orgCategories.map(({ code, name }) => (
        <Option key={code} value={code}>{name}</Option>
      ))
    ) : null;
  }

  renderSidebarTitle() {
    const { show } = this.props.OrganizationStore;
    switch (show) {
      case 'create':
        return 'global.organization.create';
      case 'edit':
        return 'global.organization.modify';
      case 'detail':
        return 'global.organization.detail';
      default:
        return '';
    }
  }

  // 渲染侧边栏成功按钮文字
  renderSidebarOkText() {
    const { OrganizationStore: { show } } = this.props;
    if (show === 'create') {
      return <FormattedMessage id="create" />;
    } else if (show === 'edit') {
      return <FormattedMessage id="save" />;
    } else {
      return <FormattedMessage id="close" />;
    }
  }

  renderSidebarDetail() {
    const { intl: { formatMessage }, OrganizationStore: { editData, partDetail } } = this.props;
    const infoList = [{
      key: formatMessage({ id: `${intlPrefix}.avatar` }),
      value: {
        imgUrl: editData.imageUrl,
        name: editData.name.charAt(0),
      },
    }, {
      key: formatMessage({ id: `${intlPrefix}.code` }),
      value: editData.code,
    }, {
      key: formatMessage({ id: `${intlPrefix}.name` }),
      value: editData.name,
    }, {
      key: formatMessage({ id: `${intlPrefix}.region` }),
      value: editData.address ? editData.address : '无',
    }, {
      key: formatMessage({ id: `${intlPrefix}.home.page` }),
      value: partDetail.homePage,
    }, {
      key: formatMessage({ id: `${intlPrefix}.project.creationDate` }),
      value: editData.creationDate,
    }, {
      key: '组织所有者',
      value: partDetail.ownerRealName,
    }, {
      key: formatMessage({ id: `${intlPrefix}.mailbox` }),
      value: partDetail.ownerEmail,
    }, {
      key: formatMessage({ id: `${intlPrefix}.phone` }),
      value: partDetail.ownerPhone ? partDetail.ownerPhone : '无',
    }];
    return (
      <Content className="sidebar-content">
        {
          infoList.map(({ key, value }) => (
            <Row
              key={key}
              className={classnames('c7n-organization-detail-row', { 'c7n-organization-detail-row-hide': value === null })}
            >
              <Col span={10}>{key}</Col>
              {
                key === formatMessage({ id: `${intlPrefix}.avatar` }) ? (
                  <div style={{ margin: 0 }} className="c7n-iam-organization-avatar">
                    <div
                      className="c7n-iam-organization-avatar-wrap"
                      style={{
                        backgroundColor: '#c5cbe8',
                        backgroundImage: value.imgUrl ? `url(${Choerodon.fileServer(value.imgUrl)})` : '',
                      }}
                    >
                      {!value.imgUrl && value.name}
                    </div>
                  </div>
                ) : (
                  <Col span={12}>{value}</Col>
                )
              }
            </Row>
          ))
        }
      </Content>
    );
  }

  renderSidebarContent() {
    const { intl, form: { getFieldDecorator }, OrganizationStore: { show, editData }, AppState } = this.props;
    const { realName } = AppState.getUserInfo;
    return (
      <Content className="sidebar-content">
        <div>
          {this.getAvatar(editData)}
          <span className="c7n-iam-organization-avatar-label">{intl.formatMessage({ id: `${intlPrefix}.avatar` })}</span>
        </div>
        <Form>
          {
            show === 'create' && (
              <FormItem
                {...formItemLayout}
              >
                {getFieldDecorator('code', {
                  rules: [{
                    required: true,
                    whitespace: true,
                    message: intl.formatMessage({ id: 'global.organization.coderequiredmsg' }),
                  }, {
                    max: 15,
                    message: intl.formatMessage({ id: 'global.organization.codemaxmsg' }),
                  }, {
                    pattern: /^[a-z](([a-z0-9]|-(?!-))*[a-z0-9])*$/,
                    message: intl.formatMessage({ id: 'global.organization.codepatternmsg' }),
                  }, {
                    validator: this.checkCode,
                  }],
                  validateTrigger: 'onBlur',
                  validateFirst: true,
                })(
                  <Input
                    ref={(e) => {
                      this.creatOrgFocusInput = e;
                    }}
                    label={<FormattedMessage id="global.organization.code" />}
                    autoComplete="off"
                    style={{ width: inputWidth }}
                    maxLength={15}
                    showLengthInfo={false}
                  />,
                )}
              </FormItem>
            )
          }
          <FormItem
            {...formItemLayout}
          >
            {getFieldDecorator('name', {
              rules: [{
                required: true,
                message: intl.formatMessage({ id: 'global.organization.namerequiredmsg' }),
                whitespace: true,
              }, {
                // eslint-disable-next-line no-useless-escape
                pattern: /^[-—\.\w\s\u4e00-\u9fa5]{1,32}$/,
                message: intl.formatMessage({ id: 'organization.info.name.pattern.msg' }),
              }],
              validateTrigger: 'onBlur',
              initialValue: show === 'create' ? undefined : editData.name,
            })(
              <Input
                ref={(e) => {
                  this.editOrgFocusInput = e;
                }}
                label={<FormattedMessage id="global.organization.name" />}
                autoComplete="off"
                style={{ width: inputWidth }}
                maxLength={32}
                showLengthInfo={false}
              />,
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
          >
            {
              getFieldDecorator('address', {
                rules: [],
                initialValue: show === 'create' ? undefined : editData.address,
              })(
                <Input
                  label={<FormattedMessage id="global.organization.region" />}
                  autoComplete="off"
                  style={{ width: inputWidth }}
                />,
              )
            }
          </FormItem>
          <FormItem
            {...formItemLayout}
          >
            {
              getFieldDecorator('homePage', {
                rules: [
                  {
                    pattern: /(https?:\/\/)+(www\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\.[a-z]{2,4}\b([-a-zA-Z0-9@:%_+.~#?&//=]*)/,
                    message: intl.formatMessage({ id: `${intlPrefix}.homepage.pattern.msg` }),
                  },
                  {
                    validator: this.checkHomepage,
                  },
                ],
                validateTrigger: 'onBlur',
                initialValue: show === 'create' ? undefined : editData.homePage,
              })(
                <Input
                  label={<FormattedMessage id="global.organization.home.page" />}
                  autoComplete="off"
                  style={{ width: inputWidth }}
                />,
              )
            }
          </FormItem>
          {
            show === 'create' && (
              <FormItem
                {...formItemLayout}
              >
                {getFieldDecorator('userId', {
                  initialValue: `${realName}`,
                })(
                  <Select
                    style={{ width: inputWidth }}
                    label={<FormattedMessage id={`${intlPrefix}.owner`} />}
                    notFoundContent={intl.formatMessage({ id: 'memberrole.notfound.msg' })}
                    onFilterChange={this.handleSelectFilter}
                    getPopupContainer={that => that}
                    filterOption={false}
                    optionFilterProp="children"
                    loading={this.state.selectLoading}
                    choiceRender={r => (r.props ? r.props.realName : r)}
                    filter
                  >
                    {this.getOption()}
                  </Select>,
                )}
              </FormItem>
            )
          }
        </Form>
      </Content>
    );
  }

  getAvatar(data = {}) {
    const { isShowAvatar, imgUrl } = this.state;
    return (
      <div className="c7n-iam-organization-avatar">
        <div
          className="c7n-iam-organization-avatar-wrap"
          style={{
            backgroundColor: data.name ? ' #c5cbe8' : '#ccc',
            backgroundImage: imgUrl ? `url(${Choerodon.fileServer(imgUrl)})` : '',
          }}
        >
          {!imgUrl && data.name && data.name.charAt(0)}
          <Button className={classnames('c7n-iam-organization-avatar-button', { 'c7n-iam-organization-avatar-button-create': !data.name, 'c7n-iam-organization-avatar-button-edit': data.name })} onClick={this.openAvatarUploader}>
            <div className="c7n-iam-organization-avatar-button-icon">
              <Icon type="photo_camera" />
            </div>
          </Button>
          <AvatarUploader intl={this.props.intl} visible={isShowAvatar} intlPrefix="global.organization.avatar.edit" onVisibleChange={this.closeAvatarUploader} onUploadOk={this.handleUploadOk} />
        </div>
      </div>
    );
  }

  /**
   * 打开上传图片模态框
   */
  openAvatarUploader = () => {
    this.setState({
      isShowAvatar: true,
    });
  }

  closeAvatarUploader = (visible) => {
    this.setState({
      isShowAvatar: visible,
    });
  };

  handleUploadOk = (res) => {
    this.setState({
      imgUrl: res,
      isShowAvatar: false,
    });
  }

  getAction = ({ text, record }) => {
    record = record.toData();
    const actionDatas = [{
      service: ['base-service.organization.update'],
      text: <FormattedMessage id="modify" />,
      action: this.handleEdit.bind(this, record),
    }, {
      service: ['base-service.organization.disableOrganization', 'base-service.organization.enableOrganization'],
      text: <FormattedMessage id={record.enabled ? 'disable' : 'enable'} />,
      action: () => this.handleDisable(record),
    }, {
      service: ['base-service.organization.query'],
      text: <FormattedMessage id="detail" />,
      action: this.showDetail.bind(this, record),
    }];
    return <Action data={actionDatas} />;
  }

  handleNavigateToOrganization = (record) => {
    const { history, AppState, HeaderStore } = this.props;
    const { category, id, name } = record;
    history.push(`/buzz/cooperate?category=${category}&id=${id}&name=${encodeURIComponent(name)}&orgId=${id}&organizationId=${id}&type=organization`);
  }

  getName = ({ text, record }) => {
    record = record.toData();
    const { HeaderStore, AppState: { userInfo: { admin } } } = this.props;
    const hasRole = HeaderStore.getOrgData.find((v) => v.id === record.id);
    const clickable = (hasRole || admin);
    return (
      <span
        className={clickable ? 'link' : 'text-gray'}
        onClick={() => clickable && this.handleNavigateToOrganization(record)}
      >
        {text}
      </span>
    );
  }

  render() {
    const {
      intl, OrganizationStore: {
        params, loading, pagination, sidebarVisible, submitting, show, orgData,
      },
      organizationDataSet,
    } = this.props;

    return (
      <TabPage
        service={[
          'base-service.organization.list',
          'base-service.organization.query',
          'base-service.organization.create',
          'base-service.organization.update',
          'base-service.organization.disableOrganization',
          'base-service.organization.enableOrganization',
          'base-service.role-member.queryAllUsers',
        ]}
      >
        <Header title={<FormattedMessage id="global.organization.header.title" />}>
          <Permission service={['base-service.organization.create']}>
            <Button
              onClick={this.createOrg}
              icon="playlist_add"
            >
              <FormattedMessage id="global.organization.create" />
            </Button>
          </Permission>
        </Header>
        <Breadcrumb />
        <Content
          className="c7n-iam-organization"
        >
          <Table
            dataSet={organizationDataSet}
          >
            <Column renderer={this.getName} name="name" />
            <Column renderer={this.getAction} width={50} align="right" />
            <Column
              name="homePage"
              className="link"
              renderer={({ text }) => (
                <MouseOverWrapper text={text} width={0.24}>
                  <a href={text} target="_blank" rel="noopener noreferrer">{text}</a>
                </MouseOverWrapper>
              )}
            />
            <Column className="text-gray" name="code" />
            <Column className="text-gray" name="projectCount" width={80} />
            <Column
              className="text-gray"
              name="enabled"
              align="left"
              width={80}
              renderer={({ value }) => (<StatusTag name={intl.formatMessage({ id: value ? 'enable' : 'disable' })} colorCode={value ? 'COMPLETED' : 'DEFAULT'} />)}
            />
            <Column className="text-gray" name="ownerRealName" width={80} />
            <Column
              className="text-gray"
              name="creationDate"
              renderer={({ text }) => (
                <Tooltip title={text}>
                  <span>{text ? text.split(' ')[0] : ''}</span>
                </Tooltip>
              )}
            />
          </Table>
          <Sidebar
            title={<FormattedMessage id={this.renderSidebarTitle()} />}
            visible={sidebarVisible}
            onOk={this.handleSubmit}
            onCancel={this.handleCancelFun}
            okCancel={show !== 'detail'}
            okText={this.renderSidebarOkText()}
            cancelText={<FormattedMessage id="cancel" />}
            confirmLoading={submitting}
            className={classnames('c7n-iam-organization-sidebar', { 'c7n-iam-organization-sidebar-create': show === 'create' })}
          >
            {show !== 'detail' ? this.renderSidebarContent() : this.renderSidebarDetail()}
          </Sidebar>
        </Content>
      </TabPage>
    );
  }
}
