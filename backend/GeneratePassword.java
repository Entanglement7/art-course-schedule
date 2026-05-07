import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String encoded = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("Encoded: " + encoded);

        // 测试现有的哈希
        String existingHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyU4SVZJ.";
        System.out.println("\nTesting existing hash:");
        System.out.println("admin123 matches: " + encoder.matches("admin123", existingHash));
        System.out.println("123456 matches: " + encoder.matches("123456", existingHash));
        System.out.println("password matches: " + encoder.matches("password", existingHash));
        System.out.println("admin matches: " + encoder.matches("admin", existingHash));
    }
}
