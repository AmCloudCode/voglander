package io.github.lunasaw.voglander.repository.manager;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.luna.common.check.Assert;

import io.github.lunasaw.voglander.repository.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务幂等管理器; 目前只通过缓存做幂等
 *
 * @author luna
 **/
@Slf4j
@Component
public class BizUniqueManager {

    /**
     * 默认的缓存过期时间; 一天
     */
    private static final int DEFAULT_EXPIRED_TIME_SEC = 86400;

    @Autowired
    private RedisCache redisManager;

    /**
     * 校验是否唯一
     *
     * @param bizType 业务类型
     * @param bizNo 业务单号
     * @return true/false 唯一/未处理过
     */
    public void check(String bizType, String bizNo) {
        Assert.notNull(bizType, "bizType不能为空");
        Assert.notNull(bizNo, "bizNo不能为空");

        log.debug("唯一性校验, bizType:{}, bizNo: {}", bizType, bizNo);

        String exist = redisManager.getCacheObject(buildCacheUniqueKey(bizType, bizNo));

        Assert.isTrue(exist == null, "重复请求");
    }

    /**
     * 生成唯一性记录
     *
     * @param bizType 业务类型
     * @param bizNo 业务单号
     */
    public void createUniqueRecord(String bizType, String bizNo) {
        Assert.notNull(bizType, "bizType不能为空");
        Assert.notNull(bizNo, "bizNo不能为空");

        log.debug("插入唯一性记录, bizType:{}, bizNo: {}", bizType, bizNo);

        redisManager.setCacheObjectIfAbsent(buildCacheUniqueKey(bizType, bizNo), true, DEFAULT_EXPIRED_TIME_SEC, TimeUnit.SECONDS);

        log.debug("插入唯一性记录成功");
    }

    public void deleteUniqueRecord(String bizType, String bizNo) {
        Assert.notNull(bizType, "bizType不能为空");
        Assert.notNull(bizNo, "bizNo不能为空");

        log.debug("删除唯一性记录，bizType:{}, bizNo: {}", bizType, bizNo);

        redisManager.deleteKey(buildCacheUniqueKey(bizType, bizNo));

        log.debug("删除唯一性记录成功");
    }

    /**
     * 构建缓存唯一键
     *
     * @param bizType
     * @param bizNo
     * @return
     */
    private String buildCacheUniqueKey(String bizType, String bizNo) {
        return bizType + "_" + bizNo;
    }
}
