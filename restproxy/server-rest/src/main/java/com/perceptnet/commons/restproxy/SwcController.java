package com.perceptnet.commons.restproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 02.11.2019
 */
@RestController
@RequestMapping("/swc")
public class SwcController {
    @Autowired
    protected HttpServletRequest httpServletRequest;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private SwcService swcService;

    @RequestMapping(value = "/{serviceName}/{methodName}", method = RequestMethod.POST)
    @ResponseBody
    public Object handleRequest(@PathVariable("serviceName") String serviceName,
                                @PathVariable("methodName") String methodName,
                                @RequestBody String requestBody) {
        return swcService.callService(serviceName + "/" + methodName, requestBody);
    }

    @RequestMapping(value = "/binary/{serviceName}/{methodName}", method = RequestMethod.POST,
            produces = "application/octet-stream")
    @ResponseBody
    public HttpEntity<byte[]> handleRequestForBytes(@PathVariable("serviceName") String serviceName,
                                                    @PathVariable("methodName") String methodName,
                                                    @RequestBody String requestBody) {
        Object rawResponse = swcService.callService(serviceName + "/" + methodName, requestBody);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return new HttpEntity<byte[]>((byte[])rawResponse, headers);
    }

    @GetMapping(value = "/**")
    @ResponseBody
    public Object handleRequest(HttpServletRequest request) {
        String path = request.getServletPath();
        path = path.substring("/swc/".length());
        Object result = swcService.callService(path, null);
        return result;
    }

}
