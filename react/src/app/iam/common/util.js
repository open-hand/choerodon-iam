import _ from 'lodash';

/**
 * 判断params和filters中是否含有特殊字符
 * @param params 表格params
 * @param filters 表格filters
 * @returns {*}
 *
 * 如果加了其他的方法就把下面这条eslint删了
 */
/* eslint-disable-next-line */
export function handleFiltersParams(params, filters) {
  /* eslint-disable-next-line */
  const pattern = new RegExp(/[\{\}\|\^\`\\]/g);
  const targetParams = params.find(item => pattern.test(item));
  const targetfilters = Object.values(filters).find(item => !_.isEmpty(item) && pattern.test(item[0]));
  return targetParams || targetfilters;
}

export function strLength(str) {
  const len = { cL: 0, nL: 0, uL: 0, lL: 0, ol: 0, dL: 0, xL: 0, gL: 0 };
  if (!str) {
    return 0;
  }
  for (let i = 0; i < str.length; i += 1) {
    if (str.charCodeAt(i) >= 19968) {
      len.cL += 1; // 中文
    } else if (str.charCodeAt(i) >= 48 && str.charCodeAt(i) <= 57) {
      len.nL += 1; // 0-9
    } else if (str.charCodeAt(i) >= 65 && str.charCodeAt(i) <= 90) {
      len.uL += 1; // A-Z
    } else if (str.charCodeAt(i) >= 97 && str.charCodeAt(i) <= 122) {
      len.lL += 1; // a-z
    } else if (str.charCodeAt(i) === 46) {
      len.dL += 1; // .
    } else if (str.charCodeAt(i) === 45) {
      len.gL += 1; // -
    } else if (str.charCodeAt(i) === 47 || str.charCodeAt(i) === 92) {
      len.xL += 1; // / \
    } else {
      len.ol += 1;
    }
  }
  return len.cL * 13 + len.nL * 7.09 + len.uL * 8.7 + len.lL * 6.8 + len.ol * 8 + len.dL * 3.78 + len.gL * 6.05 + len.xL * 4.58;
}
