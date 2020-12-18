package methods;

import org.apache.commons.codec.digest.DigestUtils;

public class KeyGenerator {
    private long next;

    public String generateKey(long seconds) {
        next = seconds;

        long s = magicFunction();

        return DigestUtils.md5Hex(String.valueOf(s));
    }

    private long rand() {
        next = next * 1103515245 + 12345;

        return (next / 65536) % 32768;
    }

    private long magicFunction() {
        long rand1 = rand();
        long rand2 = rand();

        return (rand1 << 16) | rand2;
    }
}
