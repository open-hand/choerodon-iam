import { axios } from '@choerodon/boot';
import React from 'react';
import { Icon, Tooltip, DataSet } from 'choerodon-ui/pro';
import { message } from 'choerodon-ui';

export default function (projectId, organizationId, categoryTypeOption) {
  const validateName = async (value, name, record) => {
    if (value === record.getPristineValue(name) || !value) return;
    if (value.length > 30) {
      return '文本内容限制 30 字符，请重新输入';
    }
    if (!/^[\u4e00-\u9fa5a-zA-Z0-9_\-.\s]+$/.test(value)) {
      return '应用名称只能由汉字、字母、数字、"_" 、"."、"-"、空格、组成';
    }
    if (/^\s|\s$/.test(value)) {
      return '不能以空格开头或结束';
    }
    try {
      const res = await axios.get(`base/v1/projects/${projectId}/publish_applications/check_name`, {
        params: {
          name: value,
        },
      });
      if (res.failed) {
        return res.message;
      }
      if (!res) {
        return '应用名称重复';
      }
    } catch (err) {
      return err;
    }
    return true;
  };
  const validateCategoryName = async (value, name, record) => {
    if (value === record.getPristineValue(name) || !value) return;
    // "^[A-Za-z0-9\\u4e00-\\u9fa5\\s]{1,30}$"
    // \u4e00-\u9fa5a-zA-Z0-9\s
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
  const validateImg = async (value) => {
    if (!value) {
      return '请上传应用图标';
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
  const dynamicCategoryName = ({ record }) => ({
    required: record.get('categoryType') === 'custom',
    validator: record.get('categoryType') === 'custom' ? validateCategoryName : null,
  });
  return {
    autoQuery: false,
    autoCreate: true,
    dataKey: null,
    paging: false,
    fields: [
      { name: 'imageUrl', type: 'string', label: '应用图标', validator: validateImg },
      { name: 'name', type: 'string', label: '应用名称', required: true, validator: validateName },
      { name: 'contributor', type: 'string', label: '贡献者', required: true, validator: validateContributor },
      { name: 'categoryOption', type: 'object', label: '应用类型', textField: 'name', valueField: 'type', options: categoryTypeOption, required: true, ignore: 'always', defaultValue: categoryTypeOption.get(0) },
      { name: 'categoryType', type: 'string', bind: 'categoryOption.type', ignore: 'always' },
      { name: 'categoryName', type: 'string', label: '类型名称', dynamicProps: dynamicCategoryName, ignore: 'always' },
      { name: 'description', type: 'string', label: '应用描述', required: true, validator: validateDescription },
      { name: 'free', type: 'boolean', label: '是否免费', required: true, defaultValue: true },
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
        defaultValue: 'mkt_deploy_only',
        required: true },
      { name: 'notificationEmail', type: 'string', label: '通知邮箱', validator: emailValidator, required: true },
      { name: 'remark', type: 'string', label: '备注', validator: remarkValidator },
    ],
    events: {
      update: ({ name, value, oldValue, record }) => {
        if (name === 'categoryOption' && oldValue && oldValue.type === 'custom') {
          record.set('categoryName', null);
        }
      },
    },
  };
}
