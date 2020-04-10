package io.choerodon.iam.infra.asserts;

import org.springframework.stereotype.Component;

import io.choerodon.base.infra.dto.BookMarkDTO;
import io.choerodon.base.infra.mapper.BookMarkMapper;
import io.choerodon.core.exception.CommonException;

/**
 * @author superlee
 * @since 2019-05-13
 */
@Component
public class BookMarkAssertHelper extends AssertHelper {

    private BookMarkMapper bookMarkMapper;

    public BookMarkAssertHelper(BookMarkMapper bookMarkMapper) {
        this.bookMarkMapper = bookMarkMapper;
    }

    public BookMarkDTO bookMarkNotExisted(Long id) {
        return bookMarkNotExisted(id, "error.bookMark.notExist");
    }

    public BookMarkDTO bookMarkNotExisted(Long id, String message) {
        BookMarkDTO dto = bookMarkMapper.selectByPrimaryKey(id);
        if (dto == null) {
            throw new CommonException(message);
        }
        return dto;
    }
}
