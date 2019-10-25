const config = {
  // server: 'http://localhost:8080',
  server: 'http://api.staging.saas.hand-china.com',
  // server: 'http://api.c7nf.choerodon.staging.saas.hand-china.com',
  master: './node_modules/@choerodon/master/lib/master.js',
  projectType: 'choerodon',
  buildType: 'single',
  dashboard: {},
  modules: [
    '.',
  ],
  resourcesLevel: ['site', 'organization', 'project', 'user'],
  outward: '#/knowledge/share,#/knowledge/organizations/create,#/knowledge/project/create,#/iam/register-organization',
};

module.exports = config;
