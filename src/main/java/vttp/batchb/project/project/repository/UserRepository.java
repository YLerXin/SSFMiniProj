package vttp.batchb.project.project.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public UserRepository(@Qualifier("redis-string") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveUser(String email, String password) {
        redisTemplate.opsForHash().put("LOGIN", email, password);
    }

    public String getPassword(String email) {
        Object pwd = redisTemplate.opsForHash().get("LOGIN", email);
        return (pwd != null) ? pwd.toString() : null;
    }

    public boolean userExists(String email) {
        return redisTemplate.opsForHash().hasKey("LOGIN", email);
    }
}
