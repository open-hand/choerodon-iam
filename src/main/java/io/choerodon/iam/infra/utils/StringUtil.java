package io.choerodon.iam.infra.utils;

import java.lang.reflect.Field;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.core.exception.CommonException;

/**
 * String工具类
 *
 * @author inghuang123@gmail.com
 */
public class StringUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtil.class);

    private StringUtil() {

    }

    private static final String ERROR_GET_TO_STRING = "error.stringUtil.getToString";

    /**
     * 根据对象获得对象toString重写后的字符串
     *
     * @param object 对象
     * @return toString
     */
    public static String getToString(Object object) {
        StringBuilder sb = new StringBuilder("{");
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                sb.append(field.getName()).append("=").append(field.get(object)).append(",");
            } catch (Exception e) {
                throw new CommonException(ERROR_GET_TO_STRING, e);
            }
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 对象转换警告捕获
     *
     * @param obj obj
     * @param <T> T
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        if (obj == null) {
            return null;
        } else {
            return (T) obj;
        }
    }

    public static String replaceChar(String str) {
        if (str != null && !str.equals("")) {
            for (int i = 9; i < 14; i++) {
                //替换tab、换行
                str = str.replaceAll(String.valueOf((char) i), "\\\\n");
            }
            //替换双引号为转义字符的双引号
            str = str.replaceAll(String.valueOf("\""), "\\\\\"");
        }
        return str;
    }

    /**
     * 汉字转为拼音
     * @param chinese
     * @return
     */
    public static String toPinyin(String chinese) {
        StringBuilder pinyinStr = new StringBuilder();
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char c : newChar) {
            if (c > 128) {
                try {
                    pinyinStr.append(PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat)[0]);
                } catch (Exception e) {
                    LOGGER.error("error.str.to.pinyin:{}", c);
                }
            } else {
                pinyinStr.append(c);
            }
        }
        return pinyinStr.toString();
    }

    /**
     * 汉字转拼音首字母大写
     * @param str
     * @return
     */
    public static String getPinYinHeadChar(String str) {
        StringBuilder convert = new StringBuilder();
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert.append(pinyinArray[0].charAt(0));
            } else {
                convert.append(word);
            }
        }
        return convert.toString().toUpperCase();
    }

}
