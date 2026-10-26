import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder(10);
        String raw = "123456";
        String hash1 = encoder.encode(raw);
        String hash2 = encoder.encode(raw);
        System.out.println("raw      : " + raw);
        System.out.println("hash1    : " + hash1 + "  len=" + hash1.length());
        System.out.println("hash2    : " + hash2 + "  len=" + hash2.length());
        System.out.println("salt内嵌随机，两次密文不同 = " + !hash1.equals(hash2));
        System.out.println("matches(正确) = " + encoder.matches(raw, hash1));
        System.out.println("matches(错误) = " + encoder.matches("654321", hash1));
        long t0 = System.currentTimeMillis();
        encoder.matches(raw, hash1);
        System.out.println("单次校验耗时约 " + (System.currentTimeMillis() - t0) + " ms");
    }
}
