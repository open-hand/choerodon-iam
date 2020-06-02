/**
 * Created by jinqin.ma on 2017/6/27.
 */
import { action, computed, observable } from 'mobx';
import { axios, store, Choerodon } from '@choerodon/boot';
import { message } from 'choerodon-ui/pro';
import queryString from 'query-string';
import { handleFiltersParams } from '../../../common/util';

@store('OrganizationStore')
class OrganizationStore {
  @observable orgData = [];

  @observable loading = false;

  @observable submitting = false;

  @observable show;

  @observable sidebarVisible = false;

  @observable pagination = {
    current: 1,
    pageSize: 10,
    total: 0,
  };

  @observable filters = {};

  @observable sort = {};

  @observable params = [];

  @observable editData = {};

  @observable myOrg = {};

  @observable myRoles = [];

  @observable partDetail = {};

  @observable usersData = [];

  @action
  setFilters() {
    this.filters = {};
  }

  @action
  setParams() {
    this.params = [];
  }

  @action
  setPartDetail(data) {
    this.partDetail = data;
  }

  @action
  setEditData(data) {
    this.editData = data;
  }

  @action
  showSideBar() {
    this.sidebarVisible = true;
  }

  @action
  hideSideBar() {
    this.sidebarVisible = false;
  }

  @action setUsersData(data) {
    this.usersData = data;
  }

  @computed get getUsersData() {
    return this.usersData;
  }

  checkCode = value => axios.post('/iam/choerodon/v1/organizations/check', JSON.stringify({ code: value }));

  @action
  createOrUpdateOrg({ code, name, address, userId, homePage }, modify, imgUrl = null, HeaderStore) {
    const { show, editData: { id, code: originCode, objectVersionNumber } } = this;
    const isCreate = show === 'create';
    if (!modify && !isCreate) {
      this.sidebarVisible = false;
      return Promise.resolve('modify.success');
    } else {
      let url;
      let body;
      let method;
      if (isCreate) {
        url = '/iam/choerodon/v1/organizations';
        body = {
          name,
          code,
        };

        if (address) {
          body.address = address;
        }

        if (userId) {
          body.userId = userId;
        }

        if (homePage) {
          body.homePage = homePage;
        }

        method = 'post';
      } else {
        url = `/iam/choerodon/v1/organizations/${id}`;
        body = {
          name,
          homePage,
          objectVersionNumber,
          code: originCode,
          address: address || null,
        };
        method = 'put';
      }

      if (imgUrl) {
        body.imageUrl = imgUrl;
      }
      this.submitting = true;
      return axios[method](url, JSON.stringify(body))
        .then(action((data) => {
          this.submitting = false;
          if (data.failed) {
            throw data.message;
          }
          if (isCreate) {
            message.info('创建成功');
            this.sidebarVisible = false;
            // HeaderStore.addOrg(data);
          } else {
            message.info('保存成功');
            this.sidebarVisible = false;
            // HeaderStore.updateOrg(data);
          }
        }));
    }
  }

  getOrgById = organizationId => axios.get(`/iam/choerodon/v1/organizations/${organizationId}`);

  getOrgByIdOrgLevel = organizationId => axios.get(`/iam/choerodon/v1/organizations/${organizationId}/org_level`);

  getRolesById = (organizationId, userId) => axios.get(`/iam/choerodon/v1/organizations/${organizationId}/role_members/users/${userId}`);

  loadMyData(organizationId, userId) {
    axios.all([
      this.getOrgByIdOrgLevel(organizationId),
      this.getRolesById(organizationId, userId),
    ])
      .then(action(([org, roles]) => {
        this.myOrg = org;
        this.myRoles = roles;
      }))
      .catch(Choerodon.handleResponseError);
  }

  loadOrgDetail = id => axios.get(`/iam/choerodon/v1/organizations/${id}`).then((data) => {
    if (data.failed) {
      return data.message;
    } else {
      this.setPartDetail(data);
      this.showSideBar();
    }
  }).catch(Choerodon.handleResponseError);

  toggleDisable(id, enabled) {
    return axios.put(`/iam/choerodon/v1/organizations/${id}/${enabled ? 'disable' : 'enable'}`);
  }

  loadUsers = (queryObj = { sort: 'id' }) => axios.get(`/iam/choerodon/v1/all/users?${queryString.stringify(queryObj)}`);
}

export default new OrganizationStore();
