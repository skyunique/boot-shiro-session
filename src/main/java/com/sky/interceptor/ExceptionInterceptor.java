package com.sky.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.sky.common.response.BaseResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class ExceptionInterceptor implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        String xmlhttpRequest = httpServletRequest.getHeader("X-Requested-With");
        e.printStackTrace();
        if(xmlhttpRequest != null){ //ajax请求
            System.out.println("MyExceptionResolver--ajax异常");
            System.out.println(e);
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json");
            try {
                PrintWriter writer = httpServletResponse.getWriter();
                BaseResponse res = new BaseResponse<>(1, e.getMessage());
                writer.write(JSONObject.toJSONString(res));
                writer.flush();


            } catch (IOException ex){
                System.out.println("获取输出流出错");
            }
            return new ModelAndView();
        }else{
            System.out.println("MyExceptionResolver--URL异常");
            System.out.println(e);
            //如果是文件异常，Ajax异步上传，要返回JSON
            if(e instanceof MaxUploadSizeExceededException){
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.setContentType("application/json");
                try {
                    PrintWriter writer = httpServletResponse.getWriter();
                    BaseResponse res = new BaseResponse<>(1, "文件超过限制大小");
                    writer.write(JSONObject.toJSONString(res));
                    writer.flush();
                } catch (IOException ex){
                    System.out.println("获取输出流出错");
                }
                return new ModelAndView();
            }
        }

        ModelAndView mv = new ModelAndView("error");
        mv.addObject("exception",e);

        return mv;
    }
}
