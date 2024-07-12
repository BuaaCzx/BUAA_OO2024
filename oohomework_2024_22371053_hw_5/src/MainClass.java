import com.oocourse.elevator1.TimableOutput;

import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();  // 初始化时间戳
        RequestQueue waitQueue = new RequestQueue();  // 创建请求队列

        ArrayList<RequestQueue> processQueues = new ArrayList<>();  // 创建处理队列，注意这个队列下标从 0 开始

        for (int i = 1; i <= 6; i++) {
            RequestQueue processQueue = new RequestQueue();  // 创建处理队列
            processQueues.add(processQueue);  // 添加处理队列
            Elevator elevator = new Elevator(processQueue, i);  // 创建电梯对象
            elevator.start();
        }

        Schedule schedule = new Schedule(waitQueue, processQueues);  // 创建调度器对象
        schedule.start();

        InputThread inputThread = new InputThread(waitQueue);  // 创建输入线程
        inputThread.start();
    }
}

/*

*/