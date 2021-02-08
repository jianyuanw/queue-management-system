package io.azuremicroservices.qme.qme.controllers;

import io.azuremicroservices.qme.qme.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/OperateQueue")
public class OperateQueueController {

    private final List<User.Role> permittedPerspectiveToOperate = Arrays.asList(User.Role.BRANCH_OPERATOR);

    public String redirectToNoOperatePermissionPage() {
        return "branch-operator/friendlyNotifyNoOperatePermission";
    }

    public String redirectTo404Page() {
        return "branch-operator/operate404Page";
    }

    @GetMapping("/test")
    public String test() {
        return redirectTo404Page();
    }
}
