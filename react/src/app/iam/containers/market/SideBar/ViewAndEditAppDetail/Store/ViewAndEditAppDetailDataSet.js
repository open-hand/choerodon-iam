import { axios } from '@choerodon/boot';
import { DataSet, Icon, Tooltip } from 'choerodon-ui/pro';
import { message } from 'choerodon-ui';
import React from 'react';

const toggleCategory = (data) => (data.categoryOption.type === 'custom' ? ({
  categoryName: data.categoryEditName,
  categoryCode: undefined,
}) : ({
  categoryName: data.categoryOption.name,
  categoryCode: data.categoryOption.code,
}));

export default function (projectId, appId, mobxStore, status, editReleased, categoryOption) {
  const validateName = async (value, name, record) => {
    if (value === record.getPristineValue(name) || !value) return;
    if (value.length > 30) {
      return '文本内容限制 30 字符，请重新输入';
    }
    if (!/^[\u4e00-\u9fa5a-zA-Z0-9\s\-_.]+$/.test(value)) {
      return '应用名称只能由汉字、字母、数字、"_" 、"."、"-"、空格、组成';
    }
    if (/^\s|\s$/.test(value)) {
      return '不能以空格开头或结束';
    }
    const nameValidator = await axios.get(`base/v1/projects/${projectId}/publish_applications/check_name`, {
      params: {
        name: value,
      },
    });
    if (nameValidator.failed) {
      message.error(nameValidator.message);
      return '接口请求错误';
    }
    if (!nameValidator) {
      return '应用名称重复';
    }
    return true;
  };
  const validateCategoryName = async (value, name, record) => {
    if (value === record.getPristineValue(name) || !value) return;
    if (!/^[\u4e00-\u9fa5a-zA-Z0-9\s]+$/.test(value)) {
      return '只能由汉字、字母（大小写）、数字、空格构成';
    }
    if (value.length > 30) {
      return '文本内容限制 30 字符，请重新输入';
    }
    if (/^\s|\s$/.test(value)) {
      return '不能以空格开头或结束';
    }
    try {
      const res = await axios.get(`base/v1/projects/${projectId}/publish_applications/app_categories/check`, {
        params: {
          category_name: value,
        },
      });
      if (res.failed) {
        return res.message;
      }
      if (!res) {
        return '应用类型重复';
      }
    } catch (err) {
      return err;
    }
    return true;
  };
  const validateContributor = (value) => {
    if (value.length > 30) {
      return '文本内容限制 30 字符，请重新输入';
    }
    if (/^\s|\s$/.test(value)) {
      return '不能以空格开头或结束';
    }
    return true;
  };
  const validateDescription = (value) => {
    if (value.length > 250) {
      return '文本内容限制 250 字符，请重新输入';
    }
    return true;
  };
  const emailValidator = (value) => {
    if (!/^[a-zA-Z0-9_.-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(value)) {
      return '邮箱不符合规范';
    }
    return true;
  };
  const remarkValidator = (value) => {
    if (value && value.length > 250) {
      return '文本内容限制 250 字符，请重新输入';
    }
    return true;
  };
  const editorValidate = async (value, name, record) => {
    // 匹配html界面为空白的正则。
    const patternHTMLEmpty = /^(((<[^i>]+>)*\s*)|&nbsp;|\s)*$/g;
    if (!value || patternHTMLEmpty.test(value)) {
      return `请输入${record.getField(name).get('label')}`;
    }
    return true;
  };
  const toggleOverview = ({ record }) => ({
    validator: status === 'published' ? editorValidate : null,
  });
  const dynamicNameProps = ({ record }) => ({
    required: record.get('categoryOption').type === 'custom',
    validator: !record.get('categoryDefault') ? validateCategoryName : null,
  });
  return {
    autoQuery: true,
    autoCreate: true,
    paging: false,
    dataKey: null,
    fields: [
      { name: 'id', type: 'string' },
      { name: 'refAppId', type: 'string' },
      { name: 'refAppName', type: 'string' },
      { name: 'imageUrl', type: 'string', label: '应用图标' },
      { name: 'name', type: 'string', label: '应用名称', required: true, validator: validateName },
      { name: 'contributor', type: 'string', label: '贡献者', required: true, validator: validateContributor },
      { name: 'categoryOption', type: 'object', label: '应用类型', options: categoryOption, textField: 'name', valueField: 'type', ignore: 'always' },
      { name: 'categoryEditName', type: 'string', label: '类型名称', dynamicProps: dynamicNameProps },
      { name: 'categoryName', type: 'string', label: '应用类型', ignore: 'always' },
      { name: 'description', type: 'string', label: '应用描述', required: true, validator: validateDescription },
      { name: 'publishType',
        type: 'string',
        label: (
          <span style={{ pointerEvents: 'auto' }} className="labelHelp">
            发布类型
            <Tooltip
              title={(
                <div>
                  <p style={{ marginBottom: 0 }}>源代码：该应用下的服务会开放源码，可用于二次开发。</p>
                  <p style={{ marginBottom: 0 }}>部署包：该应用无源码库，只可进行部署</p>
                </div>
              )}
            >
              <Icon type="help" style={{ marginLeft: '0.1rem' }} />
            </Tooltip>
          </span>
        ),
        multiple: true,
        required: true },
      { name: 'free', type: 'boolean', label: '是否免费', required: true },
      { name: 'sourceApplicationName', type: 'string', label: '应用来源' },
      { name: 'notificationEmail', type: 'string', label: '通知邮箱', required: true, validator: emailValidator },
      { name: 'overview', type: 'string', label: '应用介绍', dynamicProps: toggleOverview },
      { name: 'approveMessage', type: 'string', label: '驳回信息' },
    ],
    transport: {
      read: () => ({
        url: `base/v1/projects/${projectId}/publish_applications/${appId}/detail`,
        method: 'get',
        transformResponse: (data) => ({
          ...JSON.parse(data),
          publishType: JSON.parse(data).publishType === 'mkt_code_deploy' ? ['mkt_code_only', 'mkt_deploy_only'] : JSON.parse(data).publishType,
          categoryOption: {
            name: JSON.parse(data).categoryDefault ? JSON.parse(data).categoryName : '新建应用类型',
            type: JSON.parse(data).categoryDefault ? JSON.parse(data).categoryName : 'custom',
          },
          categoryEditName: JSON.parse(data).categoryDefault ? undefined : JSON.parse(data).categoryName,
        }),
      }),
      update: ({ data, dataSet }) => ({
        url: dataSet.submitUrl,
        method: 'put',
        data: {
          ...data[0],
          publishType: data[0].publishType.includes('mkt_code_only', 'mkt_deploy_only') && data[0].publishType.length === 2 ? 'mkt_code_deploy' : data[0].publishType[0],
          ...toggleCategory(dataSet.toData()[0]),
        },
      }),
    },
    events: {
      load: ({ dataSet }) => {
        dataSet.forEach((item) => {
          item.status = 'update';
          mobxStore.setOverview(item.get('overview') || '');
        });
      },
      update: ({ name, value, oldValue, record }) => {
        if (name === 'categoryOption' && oldValue && oldValue.type === 'custom') {
          record.set('categoryName', null);
        }
      },
    },
  };
}
