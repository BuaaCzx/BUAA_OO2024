import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BalancedRandom {
    private final List<Integer> numbers;
    private final Random random;

    public BalancedRandom(long seed) {
        numbers = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            numbers.add(i);
        }
        random = new Random(seed);
    }

    public int getNextNumber() {
        if (numbers.isEmpty()) {
            // 如果数组为空，说明1到6都已经被选择了一次，重新填充数组
            for (int i = 1; i <= 6; i++) {
                numbers.add(i);
            }
        }
        // 随机选择一个数
        int index = random.nextInt(numbers.size());
        int number = numbers.remove(index);
        return number;
    }
}
