package com.kiyoung.msa.gateway.APIGateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ZuulLoggingFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public String filterType() {
        return "pre";//I want to intercept all the requests before execution...
    }

    @Override
    public Object run() throws ZuulException {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        logger.info("request -> {} request uri -> {}",request,request.getRequestURI());

        return null;
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}
