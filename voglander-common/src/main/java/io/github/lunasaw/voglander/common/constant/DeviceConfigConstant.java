package io.github.lunasaw.voglander.common.constant;

/**
 * @author luna
 * @date 2023/12/28
 */
public interface DeviceConfigConstant {


    /**
     * 字符集, 支持 UTF-8 与 GB2312
     */

    String CHARSET = "charset";

    /**
     * 目录订阅周期，0为不订阅
     */
    String SUBSCRIBE_CYCLE_FOR_CATALOG = "subscribeCycleForCatalog";

    /**
     * 移动设备位置订阅周期，0为不订阅
     */
    String SUBSCRIBE_CYCLE_FOR_MOBILE_POSITION = "subscribeCycleForMobilePosition";

    /**
     * 移动设备位置信息上报时间间隔,单位:秒,默认值5
     */
    String MOBILE_POSITION_SUBMISSION_INTERVAL = "mobilePositionSubmissionInterval";

    /**
     * 报警订阅周期，0为不订阅
     */
    String SUBSCRIBE_CYCLE_FOR_ALARM = "subscribeCycleForAlarm";

    /**
     * 是否开启ssrc校验，默认关闭，开启可以防止串流
     */
    String SSRC_CHECK = "ssrcCheck";

    /**
     * 地理坐标系， 目前支持 WGS84,GCJ02
     */
    String GEO_COORD_SYS = "geoCoordSys";

    /**
     * 设备使用的媒体id, 默认为null
     */
    String MEDIA_SERVER_ID = "mediaServerId";

}
