package com.piliepeng.service;

/**
 * @Author piliepeng
 * @Date 2024/11/1 9:05
 * @PackageName:com.piliepeng.service
 * @ClassName: CounterService
 * @Description: TODO
 * @Version 1.0
 */

import org.springframework.stereotype.Service;

@Service
public class CounterService {
    private int count = 0;

    public void increment() {
        count++;  // 非线程安全的操作
    }

    public int getCount() {
        return count;
    }

    public void reset() {
        count = 0;
    }
}