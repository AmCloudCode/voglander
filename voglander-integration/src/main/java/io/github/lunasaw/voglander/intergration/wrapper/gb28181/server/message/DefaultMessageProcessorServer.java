package io.github.lunasaw.voglander.intergration.wrapper.gb28181.server.message;

import com.alibaba.fastjson.JSON;
import io.github.lunasaw.gb28181.common.entity.notify.DeviceAlarmNotify;
import io.github.lunasaw.gb28181.common.entity.notify.DeviceKeepLiveNotify;
import io.github.lunasaw.gb28181.common.entity.notify.MediaStatusNotify;
import io.github.lunasaw.gb28181.common.entity.notify.MobilePositionNotify;
import io.github.lunasaw.gb28181.common.entity.response.DeviceItem;
import io.github.lunasaw.gb28181.common.entity.response.DeviceRecord;
import io.github.lunasaw.gb28181.common.entity.response.DeviceResponse;
import io.github.lunasaw.gbproxy.server.transimit.request.message.MessageProcessorServer;
import io.github.lunasaw.sip.common.entity.RemoteAddressInfo;
import io.github.lunasaw.voglander.client.domain.qo.DeviceChannelReq;
import io.github.lunasaw.voglander.client.service.DeviceRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/10/17
 */
@Slf4j
@Component
public class DefaultMessageProcessorServer implements MessageProcessorServer {

    @Autowired
    private DeviceRegisterService deviceRegisterService;

    @Override
    public void updateDeviceResponse(String userId, DeviceResponse deviceResponse) {
        log.info("接收到设备通道信息 updateDeviceResponse::userId = {}, deviceResponse = {}", userId, JSON.toJSONString(deviceResponse));

        DeviceChannelReq req = new DeviceChannelReq();
        req.setDeviceId(userId);
        for (DeviceItem deviceItem : deviceResponse.getDeviceItemList()) {
            req.setChannelId(deviceItem.getDeviceId());
            req.setChannelnfo(JSON.toJSONString(deviceItem));
            deviceRegisterService.addChannel(req);
        }

    }

    @Override
    public void keepLiveDevice(DeviceKeepLiveNotify deviceKeepLiveNotify) {
        log.info("接收到设备的心跳 keepLiveDevice::deviceKeepLiveNotify = {}", JSON.toJSONString(deviceKeepLiveNotify));
        deviceRegisterService.keepalive(deviceKeepLiveNotify.getDeviceId());
    }

    @Override
    public void updateRemoteAddress(String userId, RemoteAddressInfo remoteAddressInfo) {
        log.info("接收到设备的地址信息 updateRemoteAddress::remoteAddressInfo = {}", remoteAddressInfo);
        deviceRegisterService.updateRemoteAddress(userId, remoteAddressInfo.getIp(), remoteAddressInfo.getPort());
    }

    @Override
    public void updateDeviceAlarm(DeviceAlarmNotify deviceAlarmNotify) {
        log.info("接收到设备的告警信息 updateDeviceAlarm::deviceAlarmNotify = {}", deviceAlarmNotify);
    }

    @Override
    public void updateMobilePosition(MobilePositionNotify mobilePositionNotify) {
        log.info("接收到设备的位置信息 updateMobilePosition::mobilePositionNotify = {}", mobilePositionNotify);
    }

    @Override
    public void updateMediaStatus(MediaStatusNotify mediaStatusNotify) {
        log.info("接收到设备的媒体状态信息 updateMediaStatus::mediaStatusNotify = {}", mediaStatusNotify);
    }

    @Override
    public void updateDeviceRecord(String userId, DeviceRecord deviceRecord) {
        log.info("接收到设备的录像信息 updateDeviceRecord::userId = {}, deviceRecord = {}", userId, JSON.toJSONString(deviceRecord));
    }
}
