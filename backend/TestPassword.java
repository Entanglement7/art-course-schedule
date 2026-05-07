import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = "$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq";
        boolean matches = encoder.matches("123456", hash);
        System.out.println("密码 '123456' 匹配结果: " + matches);
    }
}
