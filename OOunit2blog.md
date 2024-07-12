# 第二单元博客

## 同步块的设置

在三次作业中，我都只使用了 ``synchronize`` 关键字来设置同步块、以及定义 ``synchronize`` 方法来进行多线程临界区的管理。

事实上，尽管经历了三次迭代，身边有的同学对代码架构进行了大刀阔斧的改动；但是我从头到尾都是以第一次作业建立起的生产者消费者模式为整体架构，以及多线程之间的实现。并没有在其它方面引入多线程，自然而然的，也就只有临界区的两个“托盘”，也就是代码中的两个 `RequestQueue` 需要注意线程安全问题。我的处理方法是，把临界类的 **所有方法** 都定义成 `synchronize` 的，并且在外部对临界类进行相关的访问与更改时都使用 `synchronize` 关键字框起来。

下面逐一列出我的代码中同步块的设置：

```java
synchronized (waitQueue) {
    waitQueue.addRequest(r);
    waitQueue.notifyAll();
}

synchronized (elevatorRequestQueues.get(r.getElevatorId() - 1)) {
    elevatorRequestQueues.get(r.getElevatorId() - 1).addRequest(r);
    elevatorRequestQueues.get(r.getElevatorId() - 1).notifyAll();
}
```

这一部分代码出现在 `InputThread` 类中，向“托盘”增加指令的时候，需要对“托盘”进行加锁，以保证线程安全。

```java
synchronized (elevatorWaitingQueues.get(elevatorId - 1)) {
    elevatorWaitingQueues.get(elevatorId - 1).addRequest(
            personRequest
    );
    Elevator elevator = elevators.get(elevatorId - 1);
    if (!elevator.isDoubleCar()) {
        TimableOutput.println("RECEIVE-"
                + personRequest.getPersonId()
                + "-" + elevatorId);
    } else {
        TimableOutput.println("RECEIVE-"
                + personRequest.getPersonId()
                + "-" + elevatorId
                + "-" + elevator.getCharCarId(
                        personRequest.getDoubleCarId(
                                elevator.getTransferFloor())));
    }
}
```

这一部分代码出现在 ``Schedule`` 类中，与上面的 `InputThread` 类中的同步块相似，也是向托盘中添加指令，但这里需要注意，在输出 ``RECEIVE`` 之前，一直都要获取托盘的锁，否则可能会使电梯提前移动，出现错误。

实际上，在我的架构中，是通过给托盘队列上锁，进而达到控制电梯的效果：因为电梯会卡在调用托盘队列的 ``synchronized`` 方法中，因此，直到托盘队列被解锁，电梯才会继续移动。我在后续控制电梯不在重置期间被分配任务并 `RECEIVE` 乘客时，也利用了这一特性。

```java
synchronized (waitingQueue) { // 加锁下人
    if (!temp.isEmpty()) { // 电梯上有人
        doorOpen();
        for (MyPersonRequest request : temp) {
            removeRequest(request);
            request.setBeginning(currentFloor);
            waitingQueue.addRequest(request);
        }
        doorClose();
    }
    doorClose(); // 先关门
    // [时间戳]RESET_BEGIN-电梯ID
    status = Status.RESETTING;
    waitingQueue.normalReset(this, resetRequest);
    status = Status.WAITING;
    for (MyRequest request : waitingQueue.getRequests()) {
        MyPersonRequest r = (MyPersonRequest) request;
        TimableOutput.println("RECEIVE-" + r.getPersonId() + "-" + id);
    }
}
```

这一部分的代码片段出现在 `Elevator` 类中，在电梯重置时，首先获取等待队列的锁，然后再执行对电梯的重置操作，整个重置操作结束后，再释放锁。上文也有提到，获取了等待队列的锁之后，实际上就控制了其他类中对等待队列中的方法的调用，因此，直到重置结束，`InputThread` 类和 `Schedule` 类才能成功的将指令添加到等待队列中，并输出 `RECEIVE`。

## 调度器设置

第一次作业的调度器是指定电梯编号的，直接按编号分配给相应的电梯等待队列即可，与电梯线程的交互通过托盘————`waitingQueue` 来实现，是一个典型的生产者消费者模式。

此后的两次迭代，电梯编号不给定，需要自己按照相应的算法来分配电梯。实际上，可以完全沿用第一次作业中实现的架构，只是电梯 id 从直接从 `Request` 中读取，变成了实现一个自己的调度方法来计算。

而我的调度方法策略一直选择的都是“均摊随机”。相较于纯随机，可能会由于种子的不好，导致分配不均，出现潜在的 `RTLE`，“均摊随机”实际上保证了随机性，同时，在每 6 个随机数中，必然出现 ``1-6`` 中的所有数字，这样，既达到了随机的效果，也在一定程度上保证了分配的均匀性。

选择随机作为我的调度策略，实际上是考虑到了“性能”与“正确性”之间的平衡。在思考自己的调度策略的同时，看到往届的学长学姐和身边的同学，实现了各种各样的调度方法，比较经典的有函数调参，和影子电梯。我也曾认为影子电梯会是一个不错的选择，但与完成这一设计的同学交流，以及考虑到自己的架构进行深入思考后，发现影子电梯的实现比较繁琐，且并不像自己所想的那样，不需要考虑线程安全问题。因此，综合考虑，我决定牺牲一部分的性能分，来确保自己代码的绝对正确性，使用随机的方式来进行调度分配。

下面以 UML 类图（以电梯类为中心）的形式来给出我三次作业的架构，并在此基础上对三次迭代进行分析。

![hw5](https://cdn.luogu.com.cn/upload/image_hosting/ffm939e9.png)

![hw6](https://cdn.luogu.com.cn/upload/image_hosting/44vkhny3.png)

![hw7](https://cdn.luogu.com.cn/upload/image_hosting/zd8sdcp2.png)

第一次作业中，电梯类和策略类、等待队列、请求类进行交互，架构比较简单。

第二次作业中，`Request` 类分为了两种，分别给出了不同的类来实现；同时，也把 `RequestQueue` 类分成了两种，分别是电梯和调度器之间的托盘和调度器和输入线程之间的托盘，来实现两个生产者消费者模式。并且，调度器类将请求分配给电梯。

第三次作业，整体架构没有发生很大的变化，新增了一种 `ResetRequest` 类，其余的交互都与第二次作业相同。这是因为我的双轿厢电梯的实现和多数同学不同，我直接在原来的电梯类中实现了双轿厢电梯的运行模式，没有新增其他的类。

至于线程之间的协同，实际上把握住总体架构的两个生产者消费者模型（输入线程和调度器之间，调度器和电梯之间），掌握了经典的模型之后，便可以很快的分析出线程之间的关系。

通过三次的迭代和最后的分析，不难发现，总体架构上，两个生产者消费者模型的基本架构是不变的，而单个电梯的运行策略在第一次作业时完成后，也不会再做改动。而容易改变的地方主要是调度器和底层的 `Request` 类。

## 双轿厢电梯

在第三次作业中，引入了双轿厢电梯的概念。普遍的做法是把两个轿厢分别开两个线程实现，然后这两个线程相互独立，但又需要通过线程间的交互来保证不同时出现在换乘楼层上。

然而，这种常规做法很容易出现线程不安全的问题，因此，在综合考虑过后，我仍然决定采用 **损失性能保证绝对正确性** 的思路，即使用一个线程来同时运行两个轿厢。

对于双轿厢电梯，我仍然把它看作是一个类中的产物，但在这个类中，我同时维护两个轿厢的信息，在运行时同时只会有一个轿厢运动。并且仅有在一台电梯中没有人的时候，才可能切换轿厢。即在一个轿厢完成它的所有任务后，才会让第二个轿厢运行，完成属于它的任务。

这样，防止两台电梯相撞便变得非常简单了：我只需要在切换电梯的时候看一下它当前是否在换乘楼层，如果是的话，就让它离开换乘楼层，上移/下移一层楼。

## bug

三次作业，强测和互测都没有出现 bug，整个代码收获了极致的正确性，但相应的后果是性能分损失惨重，不过这也是预料之内。

课下自己做测试的时候，除了一些小的 bug，例如临界区类的某个方法忘记加锁了，输入输出等问题，这些小 bug 可以通过简单的测试数据发现并定位。

在这里我们主要关注我自己测试以及互测中出现的一个很典的 bug。由于电梯正在 reset 时，调度器不能马上把指令分配给电梯，因为此时电梯等待队列的锁被占用了，因此调度器也会被阻塞在调用等待队列的方法上。此时调度器会卡在那里不动，因此，如果有新的 reset 指令传到了调度器中，调度器便不能及时的处理，从而造成 reset 指令不能被及时处理。

这个 bug 可以通过大量测试来发现，初看觉得很不可思议，不知道为什么会出现 reset 响应过慢的错误。不过仔细分析整体架构，并单独分析 reset 指令的整个流转实现过程，便可以很快定位问题。解决问题的方法也很简单，把 reset 指令直接从 input 线程传给电梯即可。

此外在互测中，在同一时段放入大量请求是一种较强的数据，容易使线程不安全的程序出现问题。另外，有的同学的代码会被“一条简单的 reset 指令” hack，导致出现 RTLE，这应该是没有正确的处理好电梯结束的条件导致的。

## 心得体会

在多线程环境下，对共享资源的访问需要加锁，否则会出现线程安全问题。我通过在临界类的方法前加 `synchronized` 关键字，以及对共享资源加锁的方式来保证线程安全。这让我认识到，在多线程编程中，一定要注意对共享资源的保护，避免出现数据不一致的问题。

我在三次作业中基本沿用了第一次作业的生产者消费者模式架构，只是根据作业要求进行了一些微调。这让我认识到，良好的初始设计非常重要，可以让我们在后续的开发迭代中事半功倍。同时，层次化设计可以让整个系统模块清晰、易于维护。

我为了确保代码的正确性，牺牲了一些性能。这让我认识到，在实际开发中，我们需要在性能和正确性之间进行权衡。在某些情况下，保证代码的正确性更加重要。

在完成电梯作业的过程中，我经常和同学们进行交流，并在交流中相互探讨彼此架构和实现的优劣，并相互提出建议。每次和同学们交流，我都获益匪浅，有助于帮我打开作业的完成思路，并防止一些潜在 bug 的出现。
