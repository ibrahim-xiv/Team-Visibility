package com.TeamVisibility.App.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * - Redirects "/" to login page
 * - Auth guard: redirects unauthenticated users to login.html when they
 *   try to access protected HTML pages directly via URL.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final java.util.Set<String> PROTECTED_PAGES = java.util.Set.of(
        "home.html", "profile.html", "profile-edit.html", "profile-view.html",
        "meeting-form.html", "event-details.html", "chat.html",
        "saved.html", "settings.html", "map.html"
    );

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/login.html");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
                String path = req.getRequestURI();
                String page = path.substring(path.lastIndexOf('/') + 1);
                if (PROTECTED_PAGES.contains(page)) {
                    Object userId = req.getSession(false) != null
                        ? req.getSession(false).getAttribute("userId")
                        : null;
                    if (userId == null) {
                        res.sendRedirect(req.getContextPath() + "/login.html");
                        return false;
                    }
                }
                return true;
            }
        });
    }
}
