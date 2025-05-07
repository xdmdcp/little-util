package cn.chenlijian.little.starter.log.props;

import lombok.Getter;

/**
 * 日志存储类型
 *
 * @author chenlijian xdmdcp@163.com
 */
@Getter
public enum OptLogStorageType {
    /**
     * 通过logger记录日志到本地
     */
    CONSOLE,
    /**
     * 记录日志到数据库
     */
    DB,
    ;
}
