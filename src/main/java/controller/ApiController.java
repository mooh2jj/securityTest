package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApiController {

    @GetMapping("")
    public String index(){
        return "index";
    }

    @GetMapping("/user")
    public @ResponseBody String user() {

        return "user";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {

        return "manager";
    }

    @GetMapping("/login")	// 시큐리티 낚아채지 않게 설정!
    public @ResponseBody String login() {

        return "login";
    }

    @GetMapping("/join")
    public @ResponseBody String join() {

        return "join";
    }

    @GetMapping("/joinProc")
    public @ResponseBody String joinProc() {

        return "회원가입이 완료됨";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {

        return "admin";
    }
}
