package com.basaki.agent.spring;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by indra.basak on 6/15/17.
 */
@Controller("/hello")
public class HelloWorldSpringService {

    @RequestMapping(method = RequestMethod.GET, value = "/{msg}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String getMessage(@PathVariable("msg") String msg) {
        return "Spring say : " + msg;
    }
}
