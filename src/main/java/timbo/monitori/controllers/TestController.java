package timbo.monitori.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ping")
public class TestController {

    @GetMapping
    public String echo() {
        return "pong";
    }

}
