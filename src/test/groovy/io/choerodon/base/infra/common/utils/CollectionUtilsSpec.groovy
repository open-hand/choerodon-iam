package io.choerodon.base.infra.common.utils

import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.infra.utils.CollectionUtils
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 * */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class CollectionUtilsSpec extends Specification {
    private int count = 100

    def "SubList"() {
        given: "构造请求参数"
        List<Integer> list = new ArrayList<>()
        for (int i = 0; i < count; i++) {
            list.add(i)
        }

        when: "调用方法"
        List<List<Integer>> result = CollectionUtils.subList(list, 10)

        then: "校验结果"
        result.size() == 10
    }

    def "SubSet"() {
        given: "构造请求参数"
        Set<Integer> set = new HashSet<>()
        for (int i = 0; i < count; i++) {
            set.add(i)
        }

        when: "调用方法"
        Set<Set<Integer>> result = CollectionUtils.subSet(set, 10)

        then: "校验结果"
        result.size() == 10
    }
}
