package cn.chenlijian.little.starter.log.publisher;

import cn.chenlijian.little.common.entity.log.ApiLogDTO;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

/**
 * 默认日志发布者实现类
 * 该类实现了LogPublisher接口，用于发布操作日志
 * 主要功能是将操作日志记录异步转换为JSON字符串并打印出来
 * 可以扩展为将日志存储到数据库或其他处理方式
 *
 * @author chenlijian xdmdcp@163.com
 */
@Slf4j
public class DefaultLogPublisher implements LogPublisher {

    /**
     * 异步发布日志
     * 该方法接收一个ApiLogDTO对象作为参数，代表操作日志记录
     * 使用JSONUtil将日志记录对象转换为JSON字符串，并通过日志框架打印出来
     * 此方法使用了异步注解@Async，意味着它将在一个单独的线程中执行，不会阻塞主调用线程
     *
     * @param logDTO 操作日志记录，包含操作日志的相关信息
     */
    @Async
    @Override
    public void publish(ApiLogDTO logDTO) {
        // 默认实现：打印日志，可扩展为存储数据库
        log.info("[ApiLogDTO] {}", JSONUtil.toJsonStr(logDTO));
    }
}
