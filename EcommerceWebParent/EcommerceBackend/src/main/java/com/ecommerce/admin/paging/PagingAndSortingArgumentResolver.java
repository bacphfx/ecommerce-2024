package com.ecommerce.admin.paging;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PagingAndSortingArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(PagingAndSortingParam.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer model,
                                  NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {
        PagingAndSortingParam annotation = parameter.getParameterAnnotation(PagingAndSortingParam.class);

        String sortBy = request.getParameter("sortBy") != null ? request.getParameter("sortBy") : "id";
        String sortOrder = request.getParameter("sortOrder") != null ? request.getParameter("sortOrder") : "asc";
        String reverseSortOrder = sortOrder.equals("asc") ? "desc" : "asc";
        String keyword = request.getParameter("keyword");

        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("reverseSortOrder", reverseSortOrder);
        model.addAttribute("moduleURL", annotation.moduleURL());
        if (keyword != null) {
            model.addAttribute("keyword", keyword);
        }
        return new PagingAndSortingHelper(model, annotation.listName(), sortBy, sortOrder, keyword);
    }
}
