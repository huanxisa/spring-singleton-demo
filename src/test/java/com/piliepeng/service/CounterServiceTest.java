package com.piliepeng.service;

/**
 * @Author piliepeng
 * @Date 2024/11/1 9:08
 * @PackageName:com.piliepeng.service
 * @ClassName: CounterServiceTest
 * @Description: TODO
 * @Version 1.0
 */

import com.piliepeng.config.AppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class CounterServiceTest {

    @Autowired
    private CounterService counterService;

    @Test
    public void testConcurrentIncrement() throws InterruptedException {
        // 重置计数器
        counterService.reset();

        // 线程数量
        int threadCount = 1000;

        // 使用CountDownLatch确保所有线程同时开始
        CountDownLatch startLatch = new CountDownLatch(1);
        // 使用CountDownLatch等待所有线程完成
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(50);

        // 启动1000个线程
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 等待开始信号
                    startLatch.await();
                    // 执行递增操作
                    counterService.increment();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // 标记当前线程完成
                    endLatch.countDown();
                }
            });
        }

        // 发出开始信号
        startLatch.countDown();

        // 等待所有线程完成
        endLatch.await(10, TimeUnit.SECONDS);

        // 关闭线程池
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // 获取最终计数值
        int finalCount = counterService.getCount();
        System.out.println("Expected count: " + threadCount);
        System.out.println("Actual count: " + finalCount);

        // 验证计数值是否小于预期，证明存在线程安全问题
        assertNotEquals("Count should be less than expected due to race condition",
                threadCount, finalCount);
    }
}