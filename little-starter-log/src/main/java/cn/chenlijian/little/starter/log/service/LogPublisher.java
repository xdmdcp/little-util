package cn.chenlijian.little.starter.log.service;

import cn.chenlijian.little.common.entity.log.ApiLogDTO;

/**
 * 日志发布者接口
 * 提供了一个方法用于发布操作日志记录
 *
 * @author chenlijian xdmdcp@163.com
 */
public interface LogPublisher {
    /**
     * 发布日志记录
     *
     * @param record 操作日志记录对象，包含日志的相关信息
     */
    void publish(ApiLogDTO record);
}
