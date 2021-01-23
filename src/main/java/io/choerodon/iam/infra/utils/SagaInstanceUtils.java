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
        LOGGER.info("》》》》》》》》》》》事务实例集合的大小{}", sagaInstanceDetails.size());
        if (CollectionUtils.isEmpty(sagaInstanceDetails)) {
            return InstanceStatusEnum.COMPLETED.getValue();
        }
        //所有任务都成功,事务的状态就是完成 这里不能通过完成的实例任务数量来判断是否整个事务的状态，因为事务的定义可以更改
        //事务实例的状态有失败就是失败
        List<String> instanceStatus = sagaInstanceDetails.stream().map(SagaInstanceDetails::getStatus).collect(Collectors.toList());
        if (instanceStatus.contains(InstanceStatusEnum.FAILED.getValue())) {
            return InstanceStatusEnum.FAILED.getValue();
        }
        //除开失败的实例不包含成功的集合
        List<String> runningCodes = instanceStatus.stream().filter(s -> !StringUtils.equalsIgnoreCase(s, InstanceStatusEnum.COMPLETED.getValue())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(runningCodes)) {
            return InstanceStatusEnum.RUNNING.getValue();
        }
        return InstanceStatusEnum.COMPLETED.getValue();
    }

    public static Integer getCompletedCount(List<SagaInstanceDetails> sagaInstanceDetails) {

        sagaInstanceDetails.forEach(instanceDetails -> {
            int size = instanceDetails.getSagaTaskInstanceDTOS().stream().filter(sagaTaskInstanceDTO -> StringUtils.equalsIgnoreCase(sagaTaskInstanceDTO.getStatus(), InstanceStatusEnum.COMPLETED.getValue())).collect(Collectors.toList()).size();
        });
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
        allTask = sagaInstanceDetails.stream().map(instanceDetails -> instanceDetails.getAllTask()).reduce((integer, integer2) -> integer + integer2).orElseGet(() -> 0);
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
