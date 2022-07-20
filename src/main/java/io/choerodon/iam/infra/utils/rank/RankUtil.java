package io.choerodon.iam.infra.utils.rank;

/**
 * Created by jian_zhang02@163.com on 2018/5/28.
 */
public class RankUtil {

    private RankUtil(){}

    public static String mid(){
        AgileRank minRank = AgileRank.min();
        AgileRank maxRank = AgileRank.max();
        return minRank.between(maxRank).format();
    }

    /**
     * 获取比rank大的排序值
     *
     * @param rank
     * @return
     */
    public static String genNext(String rank){
        return AgileRank.parse(rank).genNext().format();
    }

    /**
     * 获取比minRank小的排序值
     *
     * @param minRank
     * @return
     */
    public static String genPre(String minRank) {
        return AgileRank.parse(minRank).genPrev().format();
    }

    public static String between(String leftRank, String rightRank) {
        AgileRank left = AgileRank.parse(leftRank);
        AgileRank right = AgileRank.parse(rightRank);
        return left.between(right).format();
    }
}
