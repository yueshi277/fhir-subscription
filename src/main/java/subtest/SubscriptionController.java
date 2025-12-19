package subtest;

import com.google.gson.*;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


@Controller
public class SubscriptionController {
    private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(SubscriptionController.class);
    private final AtomicLong ourCount = new AtomicLong();

    Mutil mutil = new Mutil();

    @PutMapping
    public ResponseEntity gotResource(@RequestBody String resource) {
        JsonParser parser = new JsonParser();
        JsonElement jsonTree = parser.parse(resource);
        JsonObject jsonObject = jsonTree.getAsJsonObject();
        String resourceType = jsonObject.get("resourceType").getAsString();
        String id = jsonObject.get("id").getAsString();

        long count = ourCount.get();
        ourLog.info("{} {}:{}", count, resourceType, id);
        ourCount.incrementAndGet();

        Document doc = Document.parse(resource);
        mutil.writeDocument(doc);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/r5")
    public ResponseEntity gotPost(@RequestBody String resource) {
        Document doc = Document.parse(resource);
        mutil.writeDocument(doc);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/resource/{id}")
    public @ResponseBody ResponseEntity<String> get(@PathVariable("id") String id) {
        String subscription = mutil.findById(id);
        if (!subscription.isEmpty()) {
            return new ResponseEntity<String>(subscription, HttpStatus.OK);
        }
        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND, "subscription not found"
        );
    }

    @GetMapping("/{resourceType}/total")
    public @ResponseBody ResponseEntity<Result> getTotal(@PathVariable("resourceType") String resourceType) {
        ArrayList<String> output = mutil.findAll(resourceType);
        Result result = new Result();
        result.setResourceType(resourceType);
        result.setTotal(output.size());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete")
    public String delete() {
        mutil.deleteAll();
        return "redirect:/list";
    }

    @RequestMapping("/")
    String index() {
        return "index";
    }

    @GetMapping("/list")
    String list(Map<String, Object> model) {
        ArrayList<String> output = mutil.findAll("");
        model.put("total", output.size());
        model.put("records", output);
        return "list";
    }

    @GetMapping(value = "/delete/{resource}")
    public void deleteResource(@PathVariable("resource") String resource) {
        mutil.deleteCdrResource(resource);
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "not deleted"
        );
    }

}
