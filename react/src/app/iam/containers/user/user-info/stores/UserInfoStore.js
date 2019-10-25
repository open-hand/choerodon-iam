import { action, computed, observable } from 'mobx';
import { axios } from '@choerodon/boot';

class UserInfoStore {
  @observable userInfo = {};

  @observable avatar;

  @computed
  get getUserInfo() {
    return this.userInfo;
  }

  @action
  setUserInfo(data) {
    this.userInfo = data;
    this.avatar = data.imageUrl;
  }

  @action
  setAvatar(avatar) {
    this.avatar = avatar;
  }

  @computed
  get getAvatar() {
    return this.avatar;
  }

  updateUserInfo = user => axios.put(`/base/v1/users/${user.id}/info`, JSON.stringify(user));

  updatePassword = (id, body) => axios.put(`/base/v1/users/${id}/password`, JSON.stringify(body));

  checkEmailAddress = email => (
    axios.post('/base/v1/users/check', JSON.stringify({ id: this.userInfo.id, email }))
  );

  checkPhoneExist = phone => (
    axios.post('/base/v1/users/check', JSON.stringify({ id: this.userInfo.id, phone }))
  );
}

export default UserInfoStore;
