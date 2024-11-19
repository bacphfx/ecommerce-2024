package com.ecommerce.setting;

import com.ecommerce.common.Constants;
import com.ecommerce.common.entity.setting.Setting;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SettingFilter implements Filter {

    @Autowired
    private SettingService settingService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String url = httpServletRequest.getRequestURL().toString();
        if (url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        List<Setting> generalSettings =  settingService.getGeneralSettings();
        generalSettings.forEach(setting -> {
            servletRequest.setAttribute(setting.getKey(), setting.getValue());
        });
        servletRequest.setAttribute("S3_BASE_URI", Constants.S3_BASE_URI);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
