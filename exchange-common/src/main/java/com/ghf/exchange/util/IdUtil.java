package com.ghf.exchange.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.util.ObjectUtils;

/**
 * @author jiangyuanlin@163.com
 */
@Slf4j
public class IdUtil {

    private static final int MIN_SERVICE_SEQUENCE = 0;
    private static final int MAX_SERVICE_SEQUENCE = 4096;
    private static IDGenerator idGenerator;

    static {
        long serviceSequence;
        //从环境变量中读取0到4095的机器编号，否则随机生成一个
        String serviceSequenceString = System.getenv("serviceSequence");
        if (ObjectUtils.isEmpty(serviceSequenceString)) {
            serviceSequence = RandomUtils.nextInt(MIN_SERVICE_SEQUENCE, MAX_SERVICE_SEQUENCE);
            log.warn("未找到大于等于{}}小于{}的serviceSequence环境变量，随机生成一个，当前机器编号为{}", MIN_SERVICE_SEQUENCE, MAX_SERVICE_SEQUENCE, serviceSequence);
        } else {
            serviceSequence = new Long(serviceSequenceString);
            if (serviceSequence < MIN_SERVICE_SEQUENCE || serviceSequence > MAX_SERVICE_SEQUENCE) {
                serviceSequence = RandomUtils.nextInt(MIN_SERVICE_SEQUENCE, MAX_SERVICE_SEQUENCE);
                log.warn("未找到大于等于{}小于{}的serviceSequence环境变量，随机生成一个，当前机器编号为{}", MIN_SERVICE_SEQUENCE, MAX_SERVICE_SEQUENCE, serviceSequence);
            } else {
                log.info("", "当前机器编号为{}", serviceSequence);
            }
        }

        idGenerator = new IDGenerator(serviceSequence);
    }

    private IdUtil() {
    }

    /**
     * 生成16位长整型id，确保前端JavaScript精度不丢失
     *
     * @return
     */
    public static synchronized long generateLongId() {
        return idGenerator.nextId();
    }

    //也可使用以下方式产生16位雪花算法，https://www.gushiciku.cn/pl/gWGH

    /**
     * 0000000000 0 - 0000000000 0000000000 0000000000 0 - 00 0000000000 - 0000000000
     * 11位标识 -              31位时间戳 -                 12位服务序列号 -  10位顺序位
     * 注：
     * 1.生成ID缩短至16位
     * 2.31位精确到秒时间戳可使用68年，生成16位ID时保证使用60.5年
     * 3.12位服务序列号，可以支持部署4096个节点
     * 4.10位顺序位，每秒生成1024个ID
     * <p>
     * 16位极值               对应时间戳（1365367696）（2013-04-08 04:48:16）   对应数据中心ID    对应顺序位
     * 最大值：9007199254740991         2147483647（2081-04-26 08:02:23）             4095          1023
     * 16位值：1000000000000000          238418579（2020-10-27 16:11:15）              416             0
     * 最小值：         4195329                  1（2013-04-08 04:48:17）               1             1
     * <p>
     * 1111111111111111111111111111111 111111111111 1111111111
     * 0001110001101011111101010010011 000110100000 0000000000
     * 0000000000000000000000000000001 000000000001 0000000001
     *
     * @author canyang3
     */
    static class IDGenerator {

        /**
         * 服务序列号
         */
        private long serviceSequence = 1L;

        /**
         * 时间戳开始计时时间 2013-04-08 04:48:16
         */
        private static final long START_TIMESTAMP = 1365367696L;

        /**
         * 默认顺序位的初始值
         */
        private long sequence = 1L;
        /**
         * 顺序位2进制长度
         */
        private static final long SEQUENCE_BITS = 10L;

        /**
         * 服务序列号2进制长度
         */
        private static final long SERVICE_SEQUENCE_BITS = 12L;

        /**
         * 服务序列号右移位数
         */
        private final long serviceSequenceShift = SEQUENCE_BITS;

        /**
         * 时间戳右移位数
         */
        private final long timestampShift = SEQUENCE_BITS + SERVICE_SEQUENCE_BITS;

        /**
         * 生成序列的掩码(10位所对应的最大整数值)，这里为1023 (0b1111111111=1023)
         */
        private final long sequenceMask = -1L ^ (-1L << SEQUENCE_BITS);

        private long lastTimestamp = -1L;

        public IDGenerator(Long serviceSequence) {
            this.serviceSequence = serviceSequence;
        }

        /**
         * 生成唯一的趋势递增的id
         */
        public synchronized long nextId() {
            long timestamp = timeGen();
            if (timestamp < lastTimestamp) {
                log.error("clock is moving backwards.  Rejecting requests until {}.", lastTimestamp);
            }
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1L) & sequenceMask;
                if (sequence == 0L) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }
            lastTimestamp = timestamp;
            return ((timestamp - START_TIMESTAMP) << timestampShift) | (serviceSequence << serviceSequenceShift) | sequence;
        }

        private long tilNextMillis(long lastTimestamp) {
            long timestamp = timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        private long timeGen() {
            return System.currentTimeMillis() / 1000L;
        }
    }
}
