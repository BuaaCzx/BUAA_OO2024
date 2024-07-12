import com.oocourse.elevator3.TimableOutput;

import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();  // 初始化时间戳
        ScheduleRequestQueue waitQueue = new ScheduleRequestQueue();  // 创建请求队列

        ArrayList<ElevatorRequestQueue> waitQueues = new ArrayList<>();  // 创建处理队列，注意这个队列下标从 0 开始
        ArrayList<Elevator> elevators = new ArrayList<>();  // 创建电梯列表

        Schedule schedule = new Schedule(waitQueue, waitQueues, elevators);  // 创建调度器对象
        schedule.start();

        InputThread inputThread = new InputThread(waitQueue, waitQueues);  // 创建输入线程
        inputThread.start();

        for (int i = 1; i <= 6; i++) {
            ElevatorRequestQueue elevatorWaitQueue = new ElevatorRequestQueue();  // 创建处理队列
            waitQueues.add(elevatorWaitQueue);  // 添加处理队列
            Elevator elevator = new Elevator(elevatorWaitQueue, schedule, i);  // 创建电梯对象
            elevator.start();
            elevators.add(elevator);
        }
    }
}

/*

*/