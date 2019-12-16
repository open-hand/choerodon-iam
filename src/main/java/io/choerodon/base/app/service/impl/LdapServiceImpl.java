package io.choerodon.base.app.service.impl;

import static io.choerodon.base.infra.asserts.LdapAssertHelper.WhichColumn;
import static io.choerodon.base.infra.utils.SagaTopic.Organization.CREATE_LDAP_AUTO;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.InvalidNameException;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.asgard.schedule.QuartzDefinition;
import io.choerodon.base.api.dto.LdapAccountDTO;
import io.choerodon.base.api.dto.LdapConnectionDTO;
import io.choerodon.base.api.dto.payload.LdapAutoTaskEventPayload;
import io.choerodon.base.api.validator.LdapValidator;
import io.choerodon.base.app.service.LdapService;
import io.choerodon.base.infra.asserts.LdapAssertHelper;
import io.choerodon.base.infra.asserts.OrganizationAssertHelper;
import io.choerodon.base.infra.dto.*;
import io.choerodon.base.infra.dto.asgard.QuartzTask;
import io.choerodon.base.infra.dto.asgard.ScheduleMethodDTO;
import io.choerodon.base.infra.dto.asgard.ScheduleTaskDTO;
import io.choerodon.base.infra.dto.asgard.ScheduleTaskDetail;
import io.choerodon.base.infra.enums.LdapAutoFrequencyType;
import io.choerodon.base.infra.enums.LdapSyncType;
import io.choerodon.base.infra.factory.MessageSourceFactory;
import io.choerodon.base.infra.feign.AsgardFeignClient;
import io.choerodon.base.infra.mapper.*;
import io.choerodon.base.infra.utils.LocaleUtils;
import io.choerodon.base.infra.utils.ldap.LdapSyncUserTask;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.FeignException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.ldap.DirectoryType;

/**
 * @author wuguokai
 */
@Service
public class LdapServiceImpl implements LdapService {
    private static final String LDAP_ERROR_USER_MESSAGE_DIR = "classpath:messages/messages";
    private static final String REGEX = "\\(.*\\)";
    private static final String CRON_FORMAT = "%s %s %s %s %s %s";
    private static final String TASK_FORMAT = "%s组织LDAP自动同步用户任务";
    private static final String TASK_DESCRIPTION = "组织下自动同步LDAP用户任务";
    private static final String CRON_TRIGGER = "cron-trigger";
    private static final String STOP = "STOP";
    private static final String ORGANIZATION_CODE = "organizationCode";
    private static final String EXECUTE_METHOD = "syncLdapUserOrganization";

    public static final String LDAP_CONNECTION_DTO = "ldapConnectionDTO";

    public static final String LDAP_TEMPLATE = "ldapTemplate";

    private static final String OBJECT_CLASS = "objectclass";

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapServiceImpl.class);

    private LdapSyncUserTask ldapSyncUserTask;
    private LdapSyncUserTask.FinishFallback finishFallback;
    private LdapErrorUserMapper ldapErrorUserMapper;


    private OrganizationAssertHelper organizationAssertHelper;

    private LdapAssertHelper ldapAssertHelper;

    private LdapMapper ldapMapper;

    private LdapHistoryMapper ldapHistoryMapper;

    private LdapAutoMapper ldapAutoMapper;

    private TransactionalProducer producer;

    private AsgardFeignClient asgardFeignClient;

    private OrganizationMapper organizationMapper;

    public LdapServiceImpl(OrganizationAssertHelper organizationAssertHelper,
                           LdapAssertHelper ldapAssertHelper,
                           LdapMapper ldapMapper,
                           LdapSyncUserTask ldapSyncUserTask,
                           LdapSyncUserTask.FinishFallback finishFallback,
                           LdapErrorUserMapper ldapErrorUserMapper,
                           LdapHistoryMapper ldapHistoryMapper,
                           TransactionalProducer producer,
                           AsgardFeignClient asgardFeignClient,
                           OrganizationMapper organizationMapper,
                           LdapAutoMapper ldapAutoMapper) {
        this.ldapSyncUserTask = ldapSyncUserTask;
        this.finishFallback = finishFallback;
        this.ldapErrorUserMapper = ldapErrorUserMapper;
        this.organizationAssertHelper = organizationAssertHelper;
        this.ldapMapper = ldapMapper;
        this.ldapAssertHelper = ldapAssertHelper;
        this.ldapHistoryMapper = ldapHistoryMapper;
        this.ldapAutoMapper = ldapAutoMapper;
        this.producer = producer;
        this.organizationMapper = organizationMapper;
        this.asgardFeignClient = asgardFeignClient;
    }

    @Override
    public LdapDTO create(Long orgId, LdapDTO ldapDTO) {
        organizationAssertHelper.notExisted(orgId);
        ldapDTO.setOrganizationId(orgId);
        validateLdap(ldapDTO);
        if (ldapMapper.insertSelective(ldapDTO) != 1) {
            throw new InsertException("error.ldap.insert");
        }
        return ldapMapper.selectByPrimaryKey(ldapDTO);
    }

    private void validateLdap(LdapDTO ldapDTO) {
        String customFilter = ldapDTO.getCustomFilter();
        if (!StringUtils.isEmpty(customFilter) && !Pattern.matches(REGEX, customFilter)) {
            throw new CommonException("error.ldap.customFilter");
        }
        if (ldapDTO.getSagaBatchSize() < 1) {
            ldapDTO.setSagaBatchSize(1);
        }
        if (ldapDTO.getConnectionTimeout() < 1) {
            throw new CommonException("error.ldap.connectionTimeout");
        }
    }

    @Override
    public LdapDTO update(Long organizationId, LdapDTO ldapDTO) {
        LdapDTO oldLdapDTO = queryByOrganizationId(organizationId);
        ldapDTO.setId(oldLdapDTO.getId());
        validateLdap(ldapDTO);
        organizationAssertHelper.notExisted(organizationId);
        ldapAssertHelper.ldapNotExisted(WhichColumn.ID, oldLdapDTO.getId());
        return doUpdate(ldapDTO);
    }

    @Override
    public LdapDTO queryByOrganizationId(Long orgId) {
        organizationAssertHelper.notExisted(orgId);
        return ldapAssertHelper.ldapNotExisted(WhichColumn.ORGANIZATION_ID, orgId);
    }

    @Override
    public void delete(Long orgId) {
        LdapDTO oldLdapDTO = queryByOrganizationId(orgId);
        organizationAssertHelper.notExisted(orgId);
        ldapAssertHelper.ldapNotExisted(WhichColumn.ID, oldLdapDTO.getId());
        ldapMapper.deleteByPrimaryKey(oldLdapDTO.getId());
    }

    @Override
    public LdapConnectionDTO testConnect(Long organizationId, LdapAccountDTO ldapAccount) {
        organizationAssertHelper.notExisted(organizationId);
        LdapDTO oldLdapDTO = queryByOrganizationId(organizationId);
        LdapDTO ldap = ldapAssertHelper.ldapNotExisted(WhichColumn.ID, oldLdapDTO.getId());
        if (!organizationId.equals(ldap.getOrganizationId())) {
            throw new CommonException("error.organization.not.has.ldap", organizationId, oldLdapDTO.getId());
        }
        ldap.setAccount(ldapAccount.getAccount());
        ldap.setPassword(ldapAccount.getPassword());
        return (LdapConnectionDTO) testConnect(ldap).get(LDAP_CONNECTION_DTO);
    }

    @Override
    public Map<String, Object> testConnect(LdapDTO ldapDTO) {
        LdapValidator.validate(ldapDTO);
        boolean anonymous = StringUtils.isEmpty(ldapDTO.getAccount()) || StringUtils.isEmpty(ldapDTO.getPassword());
        LdapConnectionDTO ldapConnectionDTO = new LdapConnectionDTO();
        Map<String, Object> returnMap = new HashMap<>(2);

        LdapTemplate ldapTemplate = initLdapTemplate(ldapDTO, anonymous);
        returnMap.put(LDAP_TEMPLATE, ldapTemplate);
        //默认将account当作userDn,如果无法登陆，则去ldap服务器抓取ldapDO.getLoginNameField()==account的userDn，然后使用返回的userDn登陆
        accountAsUserDn(ldapDTO, ldapConnectionDTO, ldapTemplate);
        //输入的账户无法登陆，去ldap服务器抓取userDn(例外hand ldap)
        if (!anonymous && ldapConnectionDTO.getCanConnectServer() && !ldapConnectionDTO.getCanLogin()) {
            returnMap.put(LDAP_TEMPLATE, fetchUserDn2Authenticate(ldapDTO, ldapConnectionDTO));
        }
        returnMap.put(LDAP_CONNECTION_DTO, ldapConnectionDTO);
        return returnMap;
    }

    @Override
    public void syncLdapUser(Long organizationId) {
        LdapDTO oldLdapDTO = queryByOrganizationId(organizationId);
        LdapDTO ldap = validateLdap(organizationId, oldLdapDTO.getId());
        Map<String, Object> map = testConnect(ldap);
        LdapConnectionDTO ldapConnectionDTO =
                (LdapConnectionDTO) map.get(LDAP_CONNECTION_DTO);
        if (!ldapConnectionDTO.getCanConnectServer()) {
            throw new CommonException("error.ldap.connect");
        }
        if (!ldapConnectionDTO.getCanLogin()) {
            throw new CommonException("error.ldap.authenticate");
        }
        if (!ldapConnectionDTO.getMatchAttribute()) {
            throw new CommonException("error.ldap.attribute.match");
        }
        LdapTemplate ldapTemplate = (LdapTemplate) map.get(LdapServiceImpl.LDAP_TEMPLATE);
        ldapSyncUserTask.syncLDAPUser(ldapTemplate, ldap, LdapSyncType.SYNC.value(), finishFallback);
    }

    @Override
    public LdapDTO validateLdap(Long organizationId, Long id) {
        organizationAssertHelper.notExisted(organizationId);
        LdapDTO ldap = ldapAssertHelper.ldapNotExisted(WhichColumn.ID, id);
        LdapValidator.validate(ldap);
        return ldap;
    }

    @Override
    public LdapHistoryDTO queryLatestHistory(Long organizationId) {
        LdapDTO oldLdapDTO = queryByOrganizationId(organizationId);
        LdapHistoryDTO example = new LdapHistoryDTO();
        example.setLdapId(oldLdapDTO.getId());
        List<LdapHistoryDTO> ldapHistoryList = ldapHistoryMapper.select(example);
        if (ldapHistoryList.isEmpty()) {
            return null;
        } else {
            ldapHistoryList.sort(Comparator.comparing(LdapHistoryDTO::getId).reversed());
            return ldapHistoryList.get(0);
        }
    }

    @Override
    public LdapDTO enableLdap(Long organizationId) {
        LdapDTO oldLdapDTO = queryByOrganizationId(organizationId);
        return updateEnabled(organizationId, oldLdapDTO.getId(), true);
    }

    private LdapDTO updateEnabled(Long organizationId, Long id, Boolean enabled) {
        LdapDTO dto = ldapAssertHelper.ldapNotExisted(WhichColumn.ID, id);
        if (!dto.getOrganizationId().equals(organizationId)) {
            throw new CommonException("error.ldap.organizationId.not.match");
        }
        dto.setEnabled(enabled);
        return doUpdate(dto);
    }

    @Override
    public LdapDTO disableLdap(Long organizationId) {
        LdapDTO oldLdapDTO = queryByOrganizationId(organizationId);
        return updateEnabled(organizationId, oldLdapDTO.getId(), false);
    }

    @Override
    public LdapHistoryDTO stop(Long organizationId) {
        LdapHistoryDTO ldapHistoryDTO = queryLatestHistory(organizationId);
        if (ldapHistoryDTO == null) {
            throw new NotExistedException("error.ldapHistory.not.exist");
        }
        ldapHistoryDTO.setSyncEndTime(new Date(System.currentTimeMillis()));
        if (ldapHistoryMapper.updateByPrimaryKeySelective(ldapHistoryDTO) != 1) {
            throw new UpdateException("error.ldapHistory.update");
        }
        return ldapHistoryMapper.selectByPrimaryKey(ldapHistoryDTO.getId());
    }

    @Override
    public PageInfo<LdapHistoryDTO> pagingQueryHistories(Pageable pageable, Long organizationId) {
        LdapHistoryDTO ldapHistoryDTO = queryLatestHistory(organizationId);
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                .doSelectPageInfo(() -> ldapHistoryMapper.selectAllEnd(ldapHistoryDTO.getLdapId()));
    }

    @Override
    public PageInfo<LdapErrorUserDTO> pagingQueryErrorUsers(Pageable pageable, Long ldapHistoryId, LdapErrorUserDTO ldapErrorUserDTO) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        PageInfo<LdapErrorUserDTO> result =
                PageMethod.startPage(page, size)
                        .doSelectPageInfo(() -> ldapErrorUserMapper.fuzzyQuery(ldapHistoryId, ldapErrorUserDTO));
        //cause国际化处理
        List<LdapErrorUserDTO> errorUsers = result.getList();
        MessageSource messageSource = MessageSourceFactory.create(LDAP_ERROR_USER_MESSAGE_DIR);
        Locale locale = LocaleUtils.locale();
        errorUsers.forEach(errorUser -> {
            String cause = errorUser.getCause();
            errorUser.setCause(messageSource.getMessage(cause, null, locale));
        });
        return result;
    }

    @Override
    @Saga(code = CREATE_LDAP_AUTO,
            description = "base创建ldap自动同步任务", inputSchema = "{}")
    @Transactional
    public LdapAutoDTO createLdapAuto(Long organizationId, LdapAutoDTO ldapAutoDTO) {
        if (queryLdapAutoByOrgId(organizationId) != null) {
            throw new CommonException("organization.already.exist.ldap.auto");
        }
        ldapAutoDTO.setOrganizationId(organizationId);
        if (ldapAutoMapper.insertSelective(ldapAutoDTO) != 1) {
            throw new CommonException("error.insert.ldap.auto");
        }
        LdapAutoTaskEventPayload taskEventPayload = new LdapAutoTaskEventPayload();
        taskEventPayload.setLdapAutoId(ldapAutoDTO.getId());
        taskEventPayload.setOrganizationId(organizationId);
        sendProducerQuartzTaskEvent(taskEventPayload, organizationId, ResourceLevel.ORGANIZATION, CREATE_LDAP_AUTO);
        return ldapAutoDTO;
    }

    @Override
    public void handleLdapAutoTask(LdapAutoTaskEventPayload ldapAutoTaskEventPayload) {
        if (ldapAutoTaskEventPayload.getDeleteQuartzTaskId() != null) {
            asgardFeignClient.deleteOrgTask(ldapAutoTaskEventPayload.getOrganizationId(), ldapAutoTaskEventPayload.getDeleteQuartzTaskId());
        }
        OrganizationDTO organizationDTO = organizationMapper.selectByPrimaryKey(ldapAutoTaskEventPayload.getOrganizationId());
        LdapAutoDTO ldapAutoDTO = ldapAutoMapper.selectByPrimaryKey(ldapAutoTaskEventPayload.getLdapAutoId());
        ScheduleTaskDTO scheduleTaskDTO = new ScheduleTaskDTO();
        scheduleTaskDTO.setName(String.format(TASK_FORMAT, organizationDTO.getName()));
        scheduleTaskDTO.setDescription(TASK_DESCRIPTION);
        scheduleTaskDTO.setStartTime(ldapAutoDTO.getStartTime());
        scheduleTaskDTO.setCronExpression(getAutoLdapCron(ldapAutoDTO));
        scheduleTaskDTO.setTriggerType(CRON_TRIGGER);
        scheduleTaskDTO.setExecuteStrategy(STOP);
        if (ldapAutoTaskEventPayload.getActive() != null && !ldapAutoTaskEventPayload.getActive()) {
            scheduleTaskDTO.setStatus(QuartzDefinition.TaskStatus.DISABLE.name());
        }
        ScheduleTaskDTO.NotifyUser notifyUser = new ScheduleTaskDTO.NotifyUser();
        notifyUser.setAdministrator(true);
        notifyUser.setAssigner(false);
        notifyUser.setCreator(false);
        scheduleTaskDTO.setNotifyUser(notifyUser);

        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put(ORGANIZATION_CODE, organizationDTO.getCode());
        scheduleTaskDTO.setParams(mapParams);

        scheduleTaskDTO.setMethodId(getMethodDTO(ldapAutoTaskEventPayload.getOrganizationId()).getId());

        ldapAutoDTO.setQuartzTaskId(createQuartzTask(ldapAutoTaskEventPayload.getOrganizationId(), scheduleTaskDTO).getId());
        if (ldapAutoMapper.updateByPrimaryKeySelective(ldapAutoDTO) != 1) {
            throw new CommonException("error.update.ldap.auto");
        }
    }

    @Override
    @Transactional
    public LdapAutoDTO updateLdapAuto(Long organizationId, LdapAutoDTO ldapAutoDTO) {
        LdapAutoDTO oldLdapAutoDTO = ldapAutoMapper.selectByPrimaryKey(ldapAutoDTO.getId());
        Boolean isNotChange = false;
        if (ldapAutoDTO.getFrequency().equals(oldLdapAutoDTO.getFrequency()) && ldapAutoDTO.getStartTime().compareTo(oldLdapAutoDTO.getStartTime()) == 0) {
            isNotChange = true;
        }
        if (ldapAutoMapper.updateByPrimaryKeySelective(ldapAutoDTO) != 1) {
            throw new CommonException("error.update.ldap.auto");
        }
        if (!isNotChange) {
            LdapAutoTaskEventPayload taskEventPayload = new LdapAutoTaskEventPayload();
            taskEventPayload.setLdapAutoId(ldapAutoDTO.getId());
            taskEventPayload.setDeleteQuartzTaskId(oldLdapAutoDTO.getQuartzTaskId());
            taskEventPayload.setOrganizationId(organizationId);
            taskEventPayload.setActive(ldapAutoDTO.getActive());
            sendProducerQuartzTaskEvent(taskEventPayload, organizationId, ResourceLevel.ORGANIZATION, CREATE_LDAP_AUTO);
        } else if (ldapAutoDTO.getActive().compareTo(oldLdapAutoDTO.getActive()) != 0) {
            ScheduleTaskDetail taskDetailDTO = getQuartzTaskDetail(organizationId, ldapAutoDTO.getQuartzTaskId());
            if (ldapAutoDTO.getActive()) {
                asgardFeignClient.enableOrgTask(organizationId, ldapAutoDTO.getQuartzTaskId(), taskDetailDTO.getObjectVersionNumber());
            } else {
                asgardFeignClient.disableOrgTask(organizationId, ldapAutoDTO.getQuartzTaskId(), taskDetailDTO.getObjectVersionNumber());
            }
        }
        return ldapAutoDTO;
    }

    @Override
    public LdapAutoDTO queryLdapAutoDTO(Long organizationId) {
        LdapAutoDTO queryLdapAutoDTO = new LdapAutoDTO();
        queryLdapAutoDTO.setOrganizationId(organizationId);
        return ldapAutoMapper.selectOne(queryLdapAutoDTO);
    }

    private ScheduleMethodDTO getMethodDTO(Long organizationId) {
        ResponseEntity<List<ScheduleMethodDTO>> methodsResponseEntity = null;
        try {
            methodsResponseEntity = asgardFeignClient.getMethodByService(organizationId, "base-service");
        } catch (FeignException e) {
            throw new CommonException(e);
        }
        List<ScheduleMethodDTO> methodDTOList = methodsResponseEntity.getBody();
        if (methodDTOList == null || methodDTOList.size() == 0) {
            throw new CommonException("error.list.methods");
        }
        Optional<ScheduleMethodDTO> methodDTO = methodDTOList.stream().filter(t -> t.getCode().equals(EXECUTE_METHOD)).findFirst();
        if (!methodDTO.isPresent()) {
            throw new CommonException("error.ldap.sync.method.get");
        }

        return methodDTO.get();
    }

    private ScheduleTaskDetail getQuartzTaskDetail(Long organizationId, Long quartzTaskId) {
        ResponseEntity<ScheduleTaskDetail> quartzTaskResponseEntity = null;
        try {
            quartzTaskResponseEntity = asgardFeignClient.getTaskDetail(organizationId, quartzTaskId);
        } catch (FeignException e) {
            throw new CommonException(e);
        }
        ScheduleTaskDetail result = quartzTaskResponseEntity.getBody();
        if (result == null || result.getId() == null) {
            throw new CommonException("error.query.quartz.task");
        }
        return result;
    }

    private QuartzTask createQuartzTask(Long organizationId, ScheduleTaskDTO scheduleTaskDTO) {
        ResponseEntity<QuartzTask> quartzTaskResponseEntity = null;
        try {
            quartzTaskResponseEntity = asgardFeignClient.createOrgTask(organizationId, scheduleTaskDTO);
        } catch (FeignException e) {
            throw new CommonException(e);
        }
        QuartzTask result = quartzTaskResponseEntity.getBody();
        if (result == null || result.getId() == null) {
            throw new CommonException("error.create.quartz.task");
        }
        return result;
    }

    private String getAutoLdapCron(LdapAutoDTO ldapAutoDTO) {
        String[] currTime = new SimpleDateFormat("HH:mm:ss").format(ldapAutoDTO.getStartTime()).split(":");
        String cron;
        switch (LdapAutoFrequencyType.valueOf(ldapAutoDTO.getFrequency())) {
            case DAY:
                cron = String.format(CRON_FORMAT, currTime[2], currTime[1], currTime[0], "*", "*", "?");
                break;
            case WEEK:
                SimpleDateFormat dateFm = new SimpleDateFormat("E", Locale.ENGLISH);
                String currWeek = dateFm.format(ldapAutoDTO.getStartTime());
                cron = String.format(CRON_FORMAT, currTime[2], currTime[1], currTime[0], "?", "*", currWeek);
                break;
            case MONTH:
                String[] currDay = new SimpleDateFormat("yyyy-MM-dd").format(ldapAutoDTO.getStartTime()).split("-");
                String date = Integer.parseInt(currDay[2]) > 28 ? "L" : currDay[2];
                cron = String.format(CRON_FORMAT, currTime[2], currTime[1], currTime[0], date, "*", "?");
                break;
            default:
                throw new CommonException("error.frequency.type");
        }
        return cron;
    }

    private void sendProducerQuartzTaskEvent(LdapAutoTaskEventPayload payload, Long sourceId, ResourceLevel resourceLevel, String sagaCode) {
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(resourceLevel)
                        .withRefType("ldapAutoTaskEvent")
                        .withSagaCode(sagaCode),
                builder -> {
                    builder
                            .withPayloadAndSerialize(payload)
                            .withRefId(payload.getLdapAutoId().toString())
                            .withSourceId(sourceId);
                    return payload;
                });
    }

    private LdapAutoDTO queryLdapAutoByOrgId(Long orgId) {
        LdapAutoDTO ldapAutoDTO = new LdapAutoDTO();
        ldapAutoDTO.setOrganizationId(orgId);
        return ldapAutoMapper.selectOne(ldapAutoDTO);
    }

    private LdapDTO doUpdate(LdapDTO ldapDTO) {
        if (ldapMapper.updateByPrimaryKey(ldapDTO) != 1) {
            throw new UpdateException("error.ldap.update");
        }
        return ldapMapper.selectByPrimaryKey(ldapDTO.getId());
    }

    private LdapTemplate fetchUserDn2Authenticate(LdapDTO ldapDTO, LdapConnectionDTO ldapConnectionDTO) {
        LdapContextSource contextSource = new LdapContextSource();
        String url = ldapDTO.getServerAddress() + ":" + ldapDTO.getPort();
        int connectionTimeout = ldapDTO.getConnectionTimeout();
        contextSource.setUrl(url);
        contextSource.setBase(ldapDTO.getBaseDn());
        contextSource.setAnonymousReadOnly(true);
        setConnectionTimeout(contextSource, connectionTimeout);
        contextSource.afterPropertiesSet();
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        if (DirectoryType.MICROSOFT_ACTIVE_DIRECTORY.value().equals(ldapDTO.getDirectoryType())) {
            ldapTemplate.setIgnorePartialResultException(true);
        }

        String userDn = null;
        Filter filter = getFilterByObjectClassAndAttribute(ldapDTO);
        try {
            List<String> names =
                    ldapTemplate.search(
                            query()
                                    .searchScope(SearchScope.SUBTREE)
                                    .filter(filter),
                            new AbstractContextMapper() {
                                @Override
                                protected Object doMapFromContext(DirContextOperations ctx) {
                                    return ctx.getNameInNamespace();
                                }
                            });
            if (names.size() == 1) {
                userDn = names.get(0);
            }
        } catch (UncategorizedLdapException e) {
            if (e.getRootCause() instanceof NamingException) {
                LOGGER.warn("baseDn or userDn may be wrong!");
            }
            LOGGER.warn("uncategorized ldap exception {}", e);
        } catch (Exception e) {
            LOGGER.warn("can not find anything while filter is {}, exception {}", filter, e);
        }

        if (userDn == null) {
            LOGGER.error("can not find anything or find more than one userDn while filter is {}, login failed", filter);
            return null;
        } else {
            contextSource.setAnonymousReadOnly(false);
            contextSource.setUserDn(userDn);
            contextSource.setPassword(ldapDTO.getPassword());

            ldapConnectionDTO.setCanLogin(false);
            ldapConnectionDTO.setMatchAttribute(false);
            try {
                LdapTemplate newLdapTemplate = new LdapTemplate(contextSource);
                matchAttributes(ldapDTO, ldapConnectionDTO, newLdapTemplate);
                ldapConnectionDTO.setCanLogin(true);
                return newLdapTemplate;
            } catch (InvalidNameException | AuthenticationException e) {
                LOGGER.error("userDn = {} or password is invalid, login failed, exception: {}", userDn, e);
                return null;
            } catch (Exception e) {
                LOGGER.error("unexpected exception: {} ", e);
                return null;
            }
        }
    }

    private Filter getFilterByObjectClassAndAttribute(LdapDTO ldapDTO) {
        String account = ldapDTO.getAccount();
        AndFilter andFilter = getAndFilterByObjectClass(ldapDTO);
        andFilter.and(new EqualsFilter(ldapDTO.getLoginNameField(), account));
        return andFilter;
    }

    private AndFilter getAndFilterByObjectClass(LdapDTO ldapDO) {
        String objectClass = ldapDO.getObjectClass();
        String[] arr = objectClass.split(",");
        AndFilter andFilter = new AndFilter();
        for (String str : arr) {
            andFilter.and(new EqualsFilter(OBJECT_CLASS, str));
        }
        return andFilter;
    }

    private void setConnectionTimeout(LdapContextSource contextSource, int connectionTimeout) {
        Map<String, Object> environment = new HashMap<>(1);
        //设置ldap服务器连接超时时间为10s
        environment.put("com.sun.jndi.ldap.connect.timeout", String.valueOf(connectionTimeout * 1000));
        contextSource.setBaseEnvironmentProperties(environment);
    }

    private LdapTemplate initLdapTemplate(LdapDTO ldapDTO, boolean anonymous) {
        LdapContextSource contextSource = new LdapContextSource();
        String url = ldapDTO.getServerAddress() + ":" + ldapDTO.getPort();
        int connectionTimeout = ldapDTO.getConnectionTimeout();
        contextSource.setUrl(url);
        contextSource.setBase(ldapDTO.getBaseDn());
        setConnectionTimeout(contextSource, connectionTimeout);

        if (!anonymous) {
            contextSource.setUserDn(ldapDTO.getAccount());
            contextSource.setPassword(ldapDTO.getPassword());
        } else {
            contextSource.setAnonymousReadOnly(true);
        }
        contextSource.afterPropertiesSet();
        return new LdapTemplate(contextSource);
    }

    private void accountAsUserDn(LdapDTO ldapDTO, LdapConnectionDTO ldapConnectionDTO, LdapTemplate ldapTemplate) {
        try {
            if (DirectoryType.MICROSOFT_ACTIVE_DIRECTORY.value().equals(ldapDTO.getDirectoryType())) {
                ldapTemplate.setIgnorePartialResultException(true);
            }
            ldapConnectionDTO.setCanConnectServer(false);
            ldapConnectionDTO.setCanLogin(false);
            ldapConnectionDTO.setMatchAttribute(false);
            //使用管理员登陆，查询一个objectclass=ldapDO.getObjectClass的对象去匹配属性
            matchAttributes(ldapDTO, ldapConnectionDTO, ldapTemplate);
            ldapConnectionDTO.setCanConnectServer(true);
            ldapConnectionDTO.setCanLogin(true);
        } catch (InvalidNameException | AuthenticationException e) {
            if (e.getRootCause() instanceof javax.naming.InvalidNameException
                    || e.getRootCause() instanceof javax.naming.AuthenticationException) {
                ldapConnectionDTO.setCanConnectServer(true);
                ldapConnectionDTO.setCanLogin(false);
            }
            LOGGER.warn("can not login when using account as userDn, so fetch userDn from ldap server, exception {}", e);
        } catch (UncategorizedLdapException | CommunicationException e) {
            if (e.getRootCause() instanceof MalformedURLException
                    || e.getRootCause() instanceof UnknownHostException) {
                //ldap连接失败
                ldapConnectionDTO.setCanConnectServer(false);
                ldapConnectionDTO.setCanLogin(false);
            }
            LOGGER.error("connect to ldap server failed, exception: {}", e);
        } catch (Exception e) {
            ldapConnectionDTO.setCanConnectServer(false);
            ldapConnectionDTO.setCanLogin(false);
            LOGGER.error("connect to ldap server failed, exception: {}", e);
        }
    }

    private void matchAttributes(LdapDTO ldapDTO, LdapConnectionDTO ldapConnectionDTO, LdapTemplate ldapTemplate) {
        Map<String, String> attributeMap = initAttributeMap(ldapDTO);
        Filter filter = getAndFilterByObjectClass(ldapDTO);
        List<Attributes> attributesList =
                ldapTemplate.search(
                        query()
                                .searchScope(SearchScope.SUBTREE)
                                .countLimit(100).filter(filter),
                        new AttributesMapper<Attributes>() {
                            @Override
                            public Attributes mapFromAttributes(Attributes attributes) throws NamingException {
                                return attributes;
                            }
                        });
        if (attributesList.isEmpty()) {
            LOGGER.warn("can not get any attributes while the filter is {}", filter);
            ldapConnectionDTO.setLoginNameField(ldapDTO.getLoginNameField());
            ldapConnectionDTO.setRealNameField(ldapDTO.getRealNameField());
            ldapConnectionDTO.setPhoneField(ldapDTO.getPhoneField());
            ldapConnectionDTO.setEmailField(ldapDTO.getEmailField());
        } else {
            Set<String> keySet = new HashSet<>();
            for (Attributes attributes : attributesList) {
                NamingEnumeration<String> attributesIDs = attributes.getIDs();
                while (attributesIDs != null && attributesIDs.hasMoreElements()) {
                    keySet.add(attributesIDs.nextElement());
                }
            }
            fullMathAttribute(ldapConnectionDTO, attributeMap, keySet);
        }
    }

    private Map<String, String> initAttributeMap(LdapDTO ldap) {
        Map<String, String> attributeMap = new HashMap<>(10);
        attributeMap.put(LdapDTO.GET_LOGIN_NAME_FIELD, ldap.getLoginNameField());
        attributeMap.put(LdapDTO.GET_REAL_NAME_FIELD, ldap.getRealNameField());
        attributeMap.put(LdapDTO.GET_EMAIL_FIELD, ldap.getEmailField());
        attributeMap.put(LdapDTO.GET_PHONE_FIELD, ldap.getPhoneField());
        attributeMap.put(LdapDTO.GET_UUID_FIELD, ldap.getUuidField());
        return attributeMap;
    }

    private void fullMathAttribute(LdapConnectionDTO ldapConnectionDTO, Map<String, String> attributeMap, Set<String> keySet) {
        boolean match = true;
        for (Map.Entry<String, String> entry : attributeMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null && !keySet.contains(value)) {
                match = false;
                ldapConnectionDTO.fullFields(key, value);
            }
        }
        ldapConnectionDTO.setMatchAttribute(match);
    }
}
