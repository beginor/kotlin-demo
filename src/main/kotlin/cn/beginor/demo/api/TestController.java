package cn.beginor.demo.api;

import cn.beginor.demo.models.LoginModel;
import cn.beginor.demo.security.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final JwtUtil util;

    public TestController(JwtUtil util) {
        this.util = util;
    }

    @GetMapping("/")
    public String test() {
        var token = util.parseToken("", "");
        var name = token.getName();
        return String.valueOf(util.getSharedKey().toString());
    }
}
