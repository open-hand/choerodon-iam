# choerodon-iam

IAM of Choerodon.

## Installing the Chart

To install the chart with the release name `choerodon-iam`:

```console
$ helm repo add c7n https://openchart.choerodon.com.cn/choerodon/c7n
$ helm repo update
$ helm install choerodon-iam c7n/choerodon-iam
```

Specify each parameter using the `--set key=value[,key=value]` argument to `helm install`.

## Uninstalling the Chart

```bash
$ helm delete choerodon-iam
```

## Requirements

| Repository | Name | Version |
|------------|------|---------|
| https://openchart.choerodon.com.cn/choerodon/c7n | common | 1.x.x |

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| affinity | object | `{}` | Affinity for pod assignment. Evaluated as a template. Note: podAffinityPreset, podAntiAffinityPreset, and nodeAffinityPreset will be ignored when it's set |
| args | list | `[]` | Args for running the server container (set to default if not set). Use array form |
| automountServiceAccountToken | bool | `false` | AutomountServiceAccountToken indicates whether a service account token should be automatically mounted. |
| base.pullPolicy | string | `"IfNotPresent"` | Specify a imagePullPolicy |
| base.pullSecrets | list | `[]` | Optionally specify an array of imagePullSecrets. |
| base.registry | string | `"registry.cn-shanghai.aliyuncs.com"` | Java base image registry |
| base.repository | string | `"c7n/javabase"` | Java base image repository |
| base.tag | string | `"jdk8u282-b08"` | Java base image tag |
| command | list | `[]` | Command for running the server container (set to default if not set). Use array form |
| commonAnnotations | object | `{}` | Add annotations to all the deployed resources |
| commonLabels | object | `{}` | Add labels to all the deployed resources |
| containerPort.actuatorPort | int | `8031` | server management port |
| containerPort.serverPort | int | `8030` | server port |
| customLivenessProbe | object | `{}` | Custom Liveness |
| customReadinessProbe | object | `{}` | Custom Readiness |
| customStartupProbe | object | `{}` | Custom Startup probes |
| enableServiceLinks | bool | `false` | EnableServiceLinks indicates whether information about services should be injected into pod's environment variables,  matching the syntax of Docker links. Optional: Defaults to false. |
| extraEnv.BUY_SAAS_URL | string | `"http://192.168.17.180:81/market-home"` |  |
| extraEnv.CHOERODON_FIX_DATA_FLAG | bool | `true` |  |
| extraEnv.CHOERODON_GATEWAY_URL | string | `"http://api.example.com"` |  |
| extraEnv.CHOERODON_GUIDE_DOC | string | `"http://open.app.example.com/"` |  |
| extraEnv.CHOERODON_HELP_DOC | string | `"http://open.app.example.com/"` |  |
| extraEnv.CHOERODON_OPEN_PLATFORM_OAUTH_CLIENTID | string | `"choerodon"` |  |
| extraEnv.CHOERODON_OPEN_PLATFORM_OAUTH_PUBLICKEY | string | `"XXXX"` |  |
| extraEnv.CHOERODON_OPEN_PLATFORM_OAUTH_SECRET | string | `"XXXX"` |  |
| extraEnv.CHOERODON_OPEN_PLATFORM_OAUTH_URL | string | `"https://gateway.open.hand-china.com"` |  |
| extraEnv.CHOERODON_ORGANIZATION_LINK_COMPLETE | string | `"http://app.example.com/#/iam/invite-user"` |  |
| extraEnv.CHOERODON_PROJECT_INVITE_URL | string | `"http://app.example.com/#/iam/invite-user"` |  |
| extraEnv.CHOERODON_REGISTER_LOGIN_URL | string | `"http://open.app.example.com/"` |  |
| extraEnv.CHOERODON_REGISTER_URL | string | `"http://open.app.example.com/#/iam/register-saas?crowdId="` |  |
| extraEnv.CHOERODON_YQCLOUD_CLIENT | string | `"client"` |  |
| extraEnv.CHOERODON_YQCLOUD_EMAIL | string | `"choerodonadmin@default.yqcloud.com"` |  |
| extraEnv.CHOERODON_YQCLOUD_HOST | string | `"http://api.example.yqcloud.com"` |  |
| extraEnv.CHOERODON_YQCLOUD_PASSWORD | string | `"password"` |  |
| extraEnv.CHOERODON_YQCLOUD_SECRET | string | `"secret"` |  |
| extraEnv.CHOERODON_YQCLOUD_SKIP | bool | `false` |  |
| extraEnv.CHOERODON_YQCLOUD_SYNC_ALL | bool | `false` |  |
| extraEnv.CHOERODON_YQCLOUD_TENANTID | string | `"258196267824443392"` |  |
| extraEnv.CHOERODON_YQCLOUD_USERNAME | string | `"username"` |  |
| extraEnv.EUREKA_CLIENT_SERVICEURL_DEFAULTZONE | string | `"http://register-server:8000/eureka/"` |  |
| extraEnv.EUREKA_INSTANCE_PREFER_IP_ADDRESS | bool | `true` |  |
| extraEnv.FEIGN_CLIENT_CONFIG_DEFAULT_CONNECT_TIMEOUT | int | `2000` |  |
| extraEnv.FEIGN_CLIENT_CONFIG_DEFAULT_READ_TIMEOUT | int | `5000` |  |
| extraEnv.HUAWEI_ACCESS_KEY | string | `"769001d5-a6b8-442a-ba42-ae6eacdf0fsfs"` |  |
| extraEnv.HUAWEI_LOGIN_URL | string | `"http://app.example.com/"` |  |
| extraEnv.HUAWEI_REGISTER_URL | string | `"http://app.example.com/#/saas/register-saas?instanceId="` |  |
| extraEnv.HZERO_CAPTCHA_TEST_DISABLE | bool | `false` |  |
| extraEnv.HZERO_ENABLE_DATA_PERMISSION | bool | `true` |  |
| extraEnv.HZERO_IAM_USERNAME_REGEX | string | `nil` |  |
| extraEnv.HZERO_IMPORT_INIT_TABLE | bool | `false` |  |
| extraEnv.HZERO_PERMISSION_CLEAN_PERMISSION | bool | `false` |  |
| extraEnv.HZERO_PERMISSION_PARSE_PERMISSION | bool | `true` |  |
| extraEnv.HZERO_PERMISSION_SKIP_PARSE_SERVICES | string | `"register,gateway,oauth,swagger"` |  |
| extraEnv.HZERO_RESOURCE_PATTERN | string | `"/v1/*,/hzero/*,/iam/v1/*,/iam/hzero/*,/choerodon/v1/*"` |  |
| extraEnv.HZERO_START_INIT_CLIENT | bool | `true` |  |
| extraEnv.HZERO_START_INIT_FIELD_PERMISSION | bool | `true` |  |
| extraEnv.HZERO_START_INIT_LDAP | bool | `true` |  |
| extraEnv.HZERO_START_INIT_OPEN_LOGIN_WAY | bool | `true` |  |
| extraEnv.HZERO_START_INIT_PASSWORD_POPLICY | bool | `true` |  |
| extraEnv.HZERO_START_INIT_USER | bool | `true` |  |
| extraEnv.LOG_LEVEL | string | `"info"` |  |
| extraEnv.ORDER_SDK_CLIENT_ID | string | `"client"` |  |
| extraEnv.ORDER_SDK_MY_PRODUCT | string | `"http://open.com/my-product/my-authorize"` |  |
| extraEnv.ORDER_SDK_PRIVATE_KEY | string | `"MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCPMC7J03sCye9nkkhOyeuZDZVHqcHm40OvN5QB9ibpzR-OjMDVewws1y4qUZjZpNXI0eO9Sk1I--kqG5OTcfQVeRrnLwhAgLRsMXR0aOTXFcxla3lm5IlIDWXZX2umvwCzhlM0XtbK92JBToF1XLPuvnxkn8o1jbZhjbqml3h-3oz8MS8vk8bMMaS3tfMk_05OPdhM0iog3Nxb4ftMo-Fgk4e3o7NCm7U2zxA4MXQHshCYpE1m2GTvH4wACJL9xmu95vwOF-Wh1km3BF3VQas758bPXD4EUqH6KDv0Aw1GLe3TlD75s_NlbDJyMV9g7BnFYg9hUtLX3kj2XGJ2xwRRAgMBAAECggEAT-0PUO1RTr81eVS4M_KWJfrlBvmFGMJm95lIbsfd9MfhA4sh4IgsxaLfTBP8BBD0nII8Y17kQQcMocp-1-DV5F6TW9RvAMJW88WvQYODqWOrkjkprPuIfcWswmd3Cb64P32NWEBQGT95gwa1ULOMZInDmc9v6fDOiym3tGH8iqQLZaGAPwGoNaQ4Et2fLPAjgYKvr2spf9yTJ0YPEz051P21XyipBK9FF7H7gIRfy7l7nfK3JF2HHsIXYqxTlZwCA0BaGautpJQWX_6SlvVM3jcLPMUD83Rh8U09agqmfMJUBSC731yryxFLKX6BMnyI1pJN_4F-NPP3l-5Em9F-uQKBgQDeLCiJXjzBBBif9YeWcrDXIW8TlN4l-6RlozrTEGskzeA7r-8CrMmDpyyjg9UDbhhVna2XiNgeHDbNIHz6c5M3L5WmMhHbTKBoibvJ2u-23_5jrRpfM8aZ-f0KuIN4wKLXrPyra_7XrRNdl8QeoA7so3_BczJLj9xXL5Cl9aRXgwKBgQCk_V_X59wvgivC26RR2IGaiAEFAFIBsF4paKEtr0CUsnJIgtP4MXCoSDEMjIBhF3_JWBq-DBlcaukmhZv7ushnoeGqjIAIrfXwfOZdRVpsXR2uEchEutnWx3LnDxMixER4szfZ3lksvbMo5LdUJROuZ75qMHiw41yJU1DyMTtYmwKBgA6bjqrBhHMYZtoVla2hqtjqPNhnwFd0_TElk1ePS8wmsSibf7aIT9OVKU0y_M117qmqbWB6QeNw7XV9PlplQPeu7EL6JcxqdF2LOMqj14EJpzs8hXBfAh7bnfSXfyVwXQSbPh2zQxVBLvXHZcCxu2l5souwnEz1j4I4AWW4Ce8rAoGAItJ3UBJXno9SvSWuYLfZYswhqvSOWlznAsZaflHmx3uCM1upcVC9_kZ-U6-zyMo05kH6IvJax-6olsDMUyzw7y5zepN80lgk-ETkOftRQl3EzeBi7F6ja3lc_vdniobyDKeGuiz7lT3UemULbG8G4GB1B-SDFJJoDXPQOc26QNECgYEA3RB_QsSeHJ1nFfOAimQJkBJ1Hc6RXix1Kvdvr7hHRMNGZ_AJZz88PIxozwR5NTBo9z5ne_SjeKgmZDKorZFX3WAusgY8PNXiwU4x2K_FWBVTNxe3T__tvqGaSDXaS-gP2qN6BQ-NA-CsjMoJQnHbBLrH3_BzFrZ74ukjveNNJtI"` |  |
| extraEnv.ORDER_SDK_PRODUCT_NUM | string | `"test0401"` |  |
| extraEnv.ORDER_SDK_REMOTE_HOST | string | `"http://example.open.com"` |  |
| extraEnv.ORDER_SDK_REMOTE_PUBLIC_KEY | string | `"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsZo4hqbtu20WotJVixSIH7z4bfFhmcstQPrsW9wH-h2N-GHJrO3bDvKEtieZrcN9LuA05J0NtgcORedEWM7ZwMg9OCNh0LJkliItyhXoS-OS73szd3G7GfAGxoGtPffuAgK2TT1hhaZuDcqUBr4NXogJxxu9_FJyU7X_Ui7FyFmDXXi9u8IkR0Ev78oNZPE7a8g6urQup_3cXSoYrUOS4xWB07ObA7VKrEYXZ7kT-CWSc-uGMaztdTIpCD_TPETGJ3LcJsi54n0njFIe2BpVJOf_HLBhh4axP-2z6DqGa3SmtvnDBYLVZMOmKaoG9DwRflmB_J7tSfD3nb7C7-ddlwIDAQAB"` |  |
| extraEnv.ORDER_SDK_SECRET | string | `"secret"` |  |
| extraEnv.SERVICES_FRONT_URL | string | `"http://c7n.app.example.com/"` |  |
| extraEnv.SPRING_DATASOURCE_CONNECTION_TIMEOUT | int | `30000` |  |
| extraEnv.SPRING_DATASOURCE_MAXIMUM_POOL_SIZE | int | `200` |  |
| extraEnv.SPRING_DATASOURCE_MINIMUM_IDLE | int | `20` |  |
| extraEnv.SPRING_DATASOURCE_PASSWORD | string | `"password"` |  |
| extraEnv.SPRING_DATASOURCE_URL | string | `"jdbc:mysql://localhost:3306/hzero_platform?useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true&serverTimezone=Asia/Shanghai"` |  |
| extraEnv.SPRING_DATASOURCE_USERNAME | string | `"choerodon"` |  |
| extraEnv.SPRING_REDIS_DATABASE | int | `1` |  |
| extraEnv.SPRING_REDIS_HOST | string | `"localhost"` |  |
| extraEnv.SPRING_REDIS_PORT | int | `6379` |  |
| extraEnv.TENANT_INIT_ASPECT | bool | `false` |  |
| extraEnvVarsCM | string | `""` | ConfigMap with extra environment variables |
| extraEnvVarsSecret | string | `""` | Secret with extra environment variables |
| extraVolumeMounts | list | `[]` | Extra volume mounts to add to server containers |
| extraVolumes | list | `[]` | Extra volumes to add to the server statefulset |
| fullnameOverride | string | `nil` | String to fully override common.names.fullname template |
| global.imagePullSecrets | list | `[]` | Global Docker registry secret names as an array |
| global.imageRegistry | string | `nil` | Global Docker image registry |
| global.storageClass | string | `nil` | Global StorageClass for Persistent Volume(s) |
| hostAliases | list | `[]` | server pod host aliases |
| image.pullPolicy | string | `"IfNotPresent"` | Specify a imagePullPolicy. Defaults to 'Always' if image tag is 'latest', else set to 'IfNotPresent' |
| image.pullSecrets | list | `[]` | Optionally specify an array of imagePullSecrets. Secrets must be manually created in the namespace. |
| image.registry | string | `"registry.cn-shanghai.aliyuncs.com"` | service image registry |
| image.repository | string | `"c7n/choerodon-iam-service-hand"` | service image repository |
| image.tag | string | `nil` | service image tag. Default Chart.AppVersion |
| ingress.annotations | object | `{}` | Additional annotations for the Ingress resource. To enable certificate autogeneration, place here your cert-manager annotations. |
| ingress.apiVersion | string | `""` | Force Ingress API version (automatically detected if not set) |
| ingress.enabled | bool | `false` | Enable ingress record generation for Discourse |
| ingress.extraHosts | list | `[]` | An array with additional hostname(s) to be covered with the ingress record |
| ingress.extraPaths | list | `[]` | An array with additional arbitrary paths that may need to be added to the ingress under the main host |
| ingress.extraTls | list | `[]` | TLS configuration for additional hostname(s) to be covered with this ingress record |
| ingress.hostname | string | `"server.local"` | Default host for the ingress record |
| ingress.ingressClassName | string | `""` | IngressClass that will be be used to implement the Ingress (Kubernetes 1.18+) |
| ingress.path | string | `"/"` | Default path for the ingress record |
| ingress.pathType | string | `"ImplementationSpecific"` | Ingress path type |
| ingress.secrets | list | `[]` | Custom TLS certificates as secrets |
| ingress.selfSigned | bool | `false` | Create a TLS secret for this ingress record using self-signed certificates generated by Helm |
| ingress.tls | bool | `false` | Enable TLS configuration for the host defined at `ingress.hostname` parameter |
| initContainers | object | `{}` | Add init containers to the server pods. |
| initDatabases.affinity | object | `{}` | Affinity for pod assignment. Evaluated as a template. Note: podAffinityPreset, podAntiAffinityPreset, and nodeAffinityPreset will be ignored when it's set |
| initDatabases.datasource.driver | string | `"com.mysql.jdbc.Driver"` |  |
| initDatabases.datasource.password | string | `"password"` |  |
| initDatabases.datasource.url | string | `"jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true&serverTimezone=Asia/Shanghai"` |  |
| initDatabases.datasource.username | string | `"username"` |  |
| initDatabases.enabled | bool | `true` |  |
| initDatabases.exclusion | string | `""` | Excluding update certain tables or fields: table1,table2.column1 |
| initDatabases.nodeSelector | object | `{}` | Node labels for pod assignment. Evaluated as a template. |
| initDatabases.pullPolicy | string | `"IfNotPresent"` | Specify a imagePullPolicy. Defaults to 'Always' if image tag is 'latest', else set to 'IfNotPresent' |
| initDatabases.pullSecrets | list | `[]` | Optionally specify an array of imagePullSecrets. Secrets must be manually created in the namespace. |
| initDatabases.registry | string | `"registry.cn-shanghai.aliyuncs.com"` | DB tool image registry |
| initDatabases.repository | string | `"c7n/dbtool"` | DB tool image repository |
| initDatabases.tag | string | `"0.9.2"` | DB tool image tag. Default Chart.AppVersion |
| initDatabases.timeout | int | `1800` |  |
| initDatabases.tolerations | list | `[]` | Tolerations for pod assignment. Evaluated as a template. |
| kubeVersion | string | `nil` | Force target Kubernetes version (using Helm capabilites if not set) |
| livenessProbe.enabled | bool | `true` | Enable livenessProbe |
| livenessProbe.failureThreshold | int | `5` | Failure threshold for livenessProbe |
| livenessProbe.initialDelaySeconds | int | `480` | Initial delay seconds for livenessProbe |
| livenessProbe.periodSeconds | int | `5` | Period seconds for livenessProbe |
| livenessProbe.successThreshold | int | `1` | Success threshold for livenessProbe |
| livenessProbe.timeoutSeconds | int | `3` | Timeout seconds for livenessProbe |
| nameOverride | string | `nil` | String to partially override common.names.fullname template (will maintain the release name) |
| nodeAffinityPreset.key | string | `""` | Node label key to match |
| nodeAffinityPreset.type | string | `""` | Node affinity type. Allowed values: soft, hard |
| nodeAffinityPreset.values | list | `[]` | Node label values to match |
| nodeSelector | object | `{}` | Node labels for pod assignment. Evaluated as a template. |
| persistence.accessModes | list | `["ReadWriteOnce"]` | Persistent Volume Access Mode |
| persistence.annotations | object | `{}` | Persistent Volume Claim annotations |
| persistence.enabled | bool | `false` | If true, use a Persistent Volume Claim, If false, use emptyDir |
| persistence.existingClaim | string | `nil` | Enable persistence using an existing PVC |
| persistence.mountPath | string | `"/data"` | Data volume mount path |
| persistence.size | string | `"8Gi"` | Persistent Volume size |
| persistence.storageClass | string | `nil` | Persistent Volume Storage Class |
| podAffinityPreset | string | `""` | Pod affinity preset. Allowed values: soft, hard |
| podAnnotations | object | `{}` | Pod annotations |
| podAntiAffinityPreset | string | `"soft"` | Pod anti-affinity preset. Allowed values: soft, hard |
| podLabels | object | `{}` | Pod labels |
| readinessProbe.enabled | bool | `true` | Enable readinessProbe |
| readinessProbe.failureThreshold | int | `5` | Failure threshold for readinessProbe |
| readinessProbe.initialDelaySeconds | int | `30` | Initial delay seconds for readinessProbe |
| readinessProbe.periodSeconds | int | `5` | Period seconds for readinessProbe |
| readinessProbe.successThreshold | int | `1` | Success threshold for readinessProbe |
| readinessProbe.timeoutSeconds | int | `3` | Timeout seconds for readinessProbe |
| replicaCount | int | `1` | Number of deployment replicas |
| resources.limits | object | `{"memory":"3Gi"}` | The resources limits for the init container |
| resources.requests | object | `{"memory":"3Gi"}` | The requested resources for the init container |
| schedulerName | string | `nil` | Scheduler name |
| securityContext | object | `{"enabled":true,"fsGroup":33,"runAsUser":33}` | Security Context |
| service.annotations | object | `{}` | Provide any additional annotations which may be required. This can be used to set the LoadBalancer service type to internal only. |
| service.enabled | bool | `true` | Set to true to enable service record generation |
| service.externalTrafficPolicy | string | `"Cluster"` | Enable client source IP preservation |
| service.loadBalancerIP | string | `nil` | loadBalancerIP for the server Service (optional, cloud specific) |
| service.loadBalancerSourceRanges | list | `[]` | Load Balancer sources |
| service.nodePort | object | `{"actuator":30103,"server":30102}` | Specify the nodePort value for the LoadBalancer and NodePort service types. |
| service.port | object | `{"actuator":8031,"server":8030}` | server Service port |
| service.type | string | `"ClusterIP"` | server Service type |
| serviceAccount.create | bool | `false` | Set to true to create serviceAccount |
| serviceAccount.name | string | `""` | The name of the ServiceAccount to use. |
| sidecars | object | `{}` | Add sidecars to the server pods. |
| skywalking.collectorService | string | `"oap.skywalking:11800"` | Collector SkyWalking trace receiver service addresses. |
| skywalking.commandOverride | string | `nil` | String to fully override Skywalking Agent Configuration template |
| skywalking.enabled | bool | `false` | Enable skywalking |
| skywalking.pullPolicy | string | `"IfNotPresent"` | Specify a imagePullPolicy Defaults to 'Always' if image tag is 'latest', else set to 'IfNotPresent' |
| skywalking.pullSecrets | list | `[]` | Optionally specify an array of imagePullSecrets. Secrets must be manually created in the namespace. |
| skywalking.registry | string | `"registry.cn-shanghai.aliyuncs.com"` | Skywalking image registry |
| skywalking.repository | string | `"c7n/skywalking-agent"` | Skywalking image repository |
| skywalking.sampleNPer3Secs | int | `9` | Negative or zero means off, by default. sampleNPer3Secs means sampling N TraceSegment in 3 seconds tops. |
| skywalking.serviceName | string | `nil` | The serviceName (Default .Chart.Name) to represent a logic group providing the same capabilities/logic.  Suggestion: set a unique name for every logic service group, service instance nodes share the same code,Max length is 50(UTF-8 char). |
| skywalking.tag | string | `"8.10.0"` | Skywalking image tag |
| startupProbe.enabled | bool | `false` | Enable startupProbe |
| startupProbe.failureThreshold | int | `60` | Failure threshold for startupProbe |
| startupProbe.initialDelaySeconds | int | `0` | Initial delay seconds for startupProbe |
| startupProbe.periodSeconds | int | `5` | Period seconds for startupProbe |
| startupProbe.successThreshold | int | `1` | Success threshold for startupProbe |
| startupProbe.timeoutSeconds | int | `3` | Timeout seconds for startupProbe |
| tolerations | list | `[]` | Tolerations for pod assignment. Evaluated as a template. |
| updateStrategy.rollingUpdate | object | `{"maxSurge":"100%","maxUnavailable":0}` | Rolling update config params. Present only if DeploymentStrategyType = RollingUpdate. |
| updateStrategy.type | string | `"RollingUpdate"` | Type of deployment. Can be "Recreate" or "RollingUpdate". Default is RollingUpdate. |
| volumePermissionsEnabled | bool | `false` | Change the owner and group of the persistent volume mountpoint to runAsUser:fsGroup values from the securityContext section. |
| workingDir | string | `"/opt/choerodon"` | Container's working directory(Default mountPath). |

## Maintainers

| Name | Email | Url |
| ---- | ------ | --- |
| choerodon | zhuchiyu@vip.hand-china.com | https://choerodon.io |
