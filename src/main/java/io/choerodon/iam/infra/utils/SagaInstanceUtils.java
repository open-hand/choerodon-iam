package io.choerodon.iam.infra.utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import io.choerodon.iam.api.vo.SagaInstanceDetails;
import io.choerodon.iam.infra.dto.asgard.SagaTaskInstanceDTO;
import io.choerodon.iam.infra.enums.InstanceStatusEnum;
import io.choerodon.iam.infra.enums.ProjectStatusEnum;

/**
 * Created by wangxiang on 2020/9/24
 */
public class SagaInstanceUtils {

    private static final String FAILED = "FAILED";
    private static final Logger LOGGER = LoggerFactory.getLogger(SagaInstanceUtils.class);

    public static Long fillFailedInstanceId(Map<String, SagaInstanceDetails> map, String refId) {
        if (!MapUtils.isEmpty(map) && !Objects.isNull(map.get(refId)) && FAILED.equalsIgnoreCase(map.get(refId).getStatus().trim())) {
            return map.get(refId).getId();
        } else {
            return null;
        }
    }

    public static Map<String, SagaInstanceDetails> listToMap(List<SagaInstanceDetails> sagaInstanceDetails) {
        Map<String, SagaInstanceDetails> sagaInstanceDetailsMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(sagaInstanceDetails)) {
            sagaInstanceDetailsMap = sagaInstanceDetails.stream().collect(Collectors.toMap(SagaInstanceDetails::getRefId, Function.identity()));
        }
        return sagaInstanceDetailsMap;
    }

    /**
     * 获取事务的状态
     *
     * @param sagaInstanceDetails 事务实例
     * @return
     */
    public static String getSagaStatus(List<SagaInstanceDetails> sagaInstanceDetails) {
        //事务实例可能被清除了 清除了之后默认状态为完成
        if (CollectionUtils.isEmpty(sagaInstanceDetails)) {
            return InstanceStatusEnum.COMPLETED.getValue();
        }
        Integer allTask = 0;
        Integer completedTask = getCompletedCount(sagaInstanceDetails);
        allTask = getAllTaskCount(sagaInstanceDetails);
        //所有任务都成功,事务的状态就是完成
        if (completedTask.intValue() == allTask.intValue()) {
            return InstanceStatusEnum.COMPLETED.getValue();
        }
        //要是包含一个失败的任务 ，整个状态失败
        if (sagaInstanceDetails.stream().map(SagaInstanceDetails::getStatus).collect(Collectors.toSet()).contains(FAILED)) {
            return InstanceStatusEnum.FAILED.getValue();
        } else {
            // 其他情况都是运行中 包括等待被拉取排队之类的。
            return InstanceStatusEnum.RUNNING.getValue();
        }
    }

    public static Integer getCompletedCount(List<SagaInstanceDetails> sagaInstanceDetails) {
        Integer completeCount = sagaInstanceDetails.stream().map(SagaInstanceDetails::getCompletedCount).reduce((integer, integer2) -> integer + integer2).orElseGet(() -> 0);
        if (Objects.isNull(completeCount)) {
            return 0;
        } else {
            return completeCount;
        }
    }

    public static Integer getAllTaskCount(List<SagaInstanceDetails> sagaInstanceDetails) {
        Integer allTask = 0;
        if (CollectionUtils.isEmpty(sagaInstanceDetails)) {
            return allTask;
        }
        allTask = sagaInstanceDetails.stream().map(instanceDetails -> OptionalBean.ofNullable(instanceDetails.getCompletedCount()).orElseGet(() -> 0) +
                OptionalBean.ofNullable(instanceDetails.getFailedCount()).orElseGet(() -> 0) +
                OptionalBean.ofNullable(instanceDetails.getQueueCount()).orElseGet(() -> 0) +
                OptionalBean.ofNullable(instanceDetails.getRollbackCount()).orElseGet(() -> 0) +
                OptionalBean.ofNullable(instanceDetails.getRunningCount()).orElseGet(() -> 0) +
                OptionalBean.ofNullable(instanceDetails.getWaitToBePulledCount()).orElseGet(() -> 0)).reduce((integer, integer2) -> integer + integer2).orElseGet(() -> 0);
        return allTask;
    }


    /**
     * 获取失败的实例id
     *
     * @param sagaInstanceDetails 事务实例
     * @return 集合
     */
    public static List<Long> getSagaIds(List<SagaInstanceDetails> sagaInstanceDetails) {
        List<SagaTaskInstanceDTO> sagaTaskInstanceDTOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(sagaInstanceDetails)) {
            return new ArrayList<>();
        }
        sagaInstanceDetails.forEach(instanceDetails -> {
            if (!CollectionUtils.isEmpty(instanceDetails.getSagaTaskInstanceDTOS())) {
                instanceDetails.getSagaTaskInstanceDTOS().stream()
                        .filter(sagaTaskInstanceDTO -> StringUtils.equalsIgnoreCase(sagaTaskInstanceDTO.getStatus(), InstanceStatusEnum.FAILED.getValue()))
                        .forEach(sagaTaskInstanceDTO -> {
                            sagaTaskInstanceDTOS.add(sagaTaskInstanceDTO);
                        });
            }
        });
        if (CollectionUtils.isEmpty(sagaTaskInstanceDTOS)) {
            return new ArrayList<>();
        }
        return sagaTaskInstanceDTOS.stream().map(SagaTaskInstanceDTO::getId).collect(Collectors.toList());

    }

}
