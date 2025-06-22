package io.github.lunasaw.voglander.manager.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.github.lunasaw.voglander.manager.assembler.DeviceAssembler;
import io.github.lunasaw.voglander.manager.domaon.dto.DeviceDTO;
import io.github.lunasaw.voglander.manager.service.DeviceService;
import io.github.lunasaw.voglander.repository.entity.DeviceDO;

/**
 * @author luna
 * @date 2023/12/30
 */
@Component
public class DeviceManager {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceAssembler deviceAssembler;

    /**
     * 删除缓存，在方法之后执行
     *
     * @param dto
     * @return
     */
    @CacheEvict(cacheNames = "device", key = "#dto.deviceId")
    public Long saveOrUpdate(DeviceDTO dto) {
        Assert.notNull(dto, "dto can not be null");
        Assert.notNull(dto.getDeviceId(), "deviceId can not be null");
        DeviceDO deviceDO = deviceAssembler.toDeviceDO(dto);

        DeviceDO byDeviceId = getByDeviceId(dto.getDeviceId());
        if (byDeviceId != null) {
            deviceDO.setId(byDeviceId.getId());
            deviceService.updateById(deviceDO);
            return byDeviceId.getId();
        }
        deviceService.save(deviceDO);
        return deviceDO.getId();
    }

    @CacheEvict(value = "device", key = "#deviceId")
    public void updateStatus(String deviceId, int status) {
        DeviceDO deviceDO = getByDeviceId(deviceId);
        if (deviceDO == null) {
            return;
        }
        deviceDO.setStatus(status);
        deviceService.updateById(deviceDO);
    }

    /**
     * 删除缓存
     * 默认在方法执行之后进行缓存删除
     * 属性：
     * allEntries=true 时表示删除cacheNames标识的缓存下的所有缓存，默认是false
     * beforeInvocation=true 时表示在目标方法执行之前删除缓存，默认false
     */
    @CacheEvict(cacheNames = "device", key = "#deviceId")
    public Boolean deleteDevice(String deviceId) {
        Assert.notNull(deviceId, "userId can not be null");
        QueryWrapper<DeviceDO> queryWrapper = new QueryWrapper<DeviceDO>().eq("device_id", deviceId);
        return deviceService.remove(queryWrapper);
    }

    public DeviceDO getByDeviceId(String deviceId) {
        Assert.notNull(deviceId, "userId can not be null");
        QueryWrapper<DeviceDO> queryWrapper = new QueryWrapper<DeviceDO>().eq("device_id", deviceId).last("limit 1");
        return deviceService.getOne(queryWrapper);
    }

    @Cacheable(value = "device", key = "#deviceId")
    public DeviceDTO getDtoByDeviceId(String deviceId) {
        DeviceDO byDeviceId = getByDeviceId(deviceId);
        return deviceAssembler.toDeviceDTO(byDeviceId);
    }

    /**
     * 分页查询设备列表，返回DTO模型并解析扩展字段
     *
     * @param page 当前页
     * @param size 页大小
     * @param queryWrapper 查询条件
     * @return 分页结果
     */
    public Page<DeviceDTO> pageQuery(int page, int size, QueryWrapper<DeviceDO> queryWrapper) {
        Page<DeviceDO> queryPage = new Page<>(page, size);
        Page<DeviceDO> pageInfo = deviceService.page(queryPage, queryWrapper);

        // 使用 Assembler 进行数据转换
        Page<DeviceDTO> resultPage = new Page<>(page, size);
        resultPage.setRecords(deviceAssembler.toDeviceDTOList(pageInfo.getRecords()));
        resultPage.setTotal(pageInfo.getTotal());
        resultPage.setCurrent(pageInfo.getCurrent());
        resultPage.setSize(pageInfo.getSize());
        resultPage.setPages(pageInfo.getPages());

        return resultPage;
    }
}
