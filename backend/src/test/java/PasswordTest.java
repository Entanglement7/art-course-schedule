import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Generate hash for admin123
        String hash = encoder.encode("admin123");
        System.out.println("Hash for admin123: " + hash);

        // Test existing hash
        String existingHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyU4SVZJ.";
        System.out.println("\nTesting passwords against existing hash:");

        String[] passwords = {"admin123", "123456", "password", "admin", "teacher123", "student123"};
        for (String pwd : passwords) {
            boolean matches = encoder.matches(pwd, existingHash);
            System.out.println(pwd + ": " + matches);
        }
    }
}
