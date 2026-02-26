package com.backoffice.filter;

import java.io.IOException;

import com.backoffice.util.TokenService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtre de protection par token pour les endpoints API
 * Vérifie le header X-API-Token pour les requêtes vers /api/*
 */
public class TokenFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        
        // Vérifier seulement les endpoints API
        if (requestURI.contains("/api/")) {
            
            // Permettre les requêtes OPTIONS (preflight CORS)
            if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                chain.doFilter(request, response);
                return;
            }
            
            // Récupérer le token du header
            String token = httpRequest.getHeader("X-API-Token");
            
            // Valider le token
            if (token == null || token.trim().isEmpty()) {
                sendError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Token manquant");
                return;
            }
            
            if (!TokenService.isTokenValide(token)) {
                sendError(httpResponse, HttpServletResponse.SC_FORBIDDEN, "Token invalide ou expiré");
                return;
            }
        }
        
        // Token valide ou non-API endpoint, continuer
        chain.doFilter(request, response);
    }
    
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":" + status + ",\"message\":\"" + message + "\"}");
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}
