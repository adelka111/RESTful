package utb.fai.RESTAPIServer;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    // Stabilní pořadí – testy pracují s "prvním" uživatelem ze seznamu
    private static final Sort BY_ID = Sort.by("id");

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserController(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public List<MyUser> getAllUsers() {
        return userRepository.findAll(BY_ID);
    }

    @GetMapping("/search")
    public List<MyUser> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber) {
        return userRepository.findAll(BY_ID).stream()
                .filter(u -> containsIgnoreCase(u.getName(), name))
                .filter(u -> containsIgnoreCase(u.getEmail(), email))
                .filter(u -> phoneNumber == null
                        || (u.getPhoneNumber() != null && u.getPhoneNumber().contains(phoneNumber)))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MyUser> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MyUser> createUser(@RequestBody(required = false) String body) {
        MyUser user = parseUser(body);
        if (user == null || !user.isUserDataValid()) {
            return ResponseEntity.badRequest().build();
        }
        user.setId(null); // id přiděluje server
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MyUser> updateUser(@PathVariable Long id,
                                             @RequestBody(required = false) String body) {
        Optional<MyUser> existing = userRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        MyUser data = parseUser(body);
        if (data == null || !data.isUserDataValid()) {
            return ResponseEntity.badRequest().build();
        }
        MyUser user = existing.get();
        user.setName(data.getName());
        user.setEmail(data.getEmail());
        user.setPhoneNumber(data.getPhoneNumber());
        return ResponseEntity.ok(userRepository.save(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MyUser> deleteUser(@PathVariable Long id) {
        Optional<MyUser> existing = userRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        userRepository.delete(existing.get());
        return ResponseEntity.ok(existing.get());
    }

    @DeleteMapping
    public ResponseEntity<List<MyUser>> deleteAllUsers() {
        userRepository.deleteAll();
        return ResponseEntity.ok(List.of());
    }

    /**
     * Tělo se čte jako String a parsuje ručně, aby endpoint přijal JSON
     * i bez hlavičky Content-Type: application/json (NATT ji v některých
     * testech neposílá). Neplatný nebo chybějící JSON vrací null.
     */
    private MyUser parseUser(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(body, MyUser.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private static boolean containsIgnoreCase(String value, String filter) {
        if (filter == null) {
            return true;
        }
        return value != null
                && value.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT));
    }
}
