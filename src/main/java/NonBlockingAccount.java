import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class NonBlockingAccount {

    private AtomicInteger availableSum = new AtomicInteger(0);
    private Queue<Entry> history = new ConcurrentLinkedQueue<>();

    private static class Entry {
        private enum Mode {DEPOSIT, WITHDRAW};

        private final Mode mode;
        private final int value;
        private final int afterValue;
        private final boolean success;
        private final long time;

        Entry(Mode mode, int value, int afterValue, boolean success, long time) {
            this.mode = mode;
            this.value = value;
            this.afterValue = afterValue;
            this.success = success;
            this.time = time;
        }

        public int getAfterValue() {
            return afterValue;
        }

        public int getValue() {
            return value;
        }

        public long getTime() {
            return time;
        }

        public Mode getMode() {
            return mode;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public void deposit(int value) {
        availableSum.addAndGet(value);
    }

    public boolean withdraw(int value) {
        if (value > availableSum.get()) {
            return false;
        }
        return false;
    }

    public String getDetails() {
        return null;
    }

}
