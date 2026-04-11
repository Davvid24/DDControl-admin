package com.ddcontrol.ddcontroladmin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;
import io.jsonwebtoken.io.SerializationException;
import io.jsonwebtoken.io.Serializer;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails,
                                Integer idUsuario,
                                Integer idEmpresa,
                                String rol) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("idUsuario", idUsuario)
                .claim("idEmpresa", idEmpresa)
                .claim("rol", rol)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiration))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public Integer extractIdUsuario(String token) {
        Object val = extractClaims(token).get("idUsuario");
        return val != null ? ((Number) val).intValue() : null;
    }

    public Integer extractIdEmpresa(String token) {
        Object val = extractClaims(token).get("idEmpresa");
        return val != null ? ((Number) val).intValue() : null;
    }

    public String extractRol(String token) {
        return (String) extractClaims(token).get("rol");
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ── SERIALIZER ────────────────────────────────────────────
    private static class JsonSerializer implements Serializer<Map<String, ?>> {

        @Override
        public byte[] serialize(Map<String, ?> map) {
            return toJson(map).getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public void serialize(Map<String, ?> stringMap, OutputStream outputStream) throws SerializationException {

        }

        private String toJson(Object obj) {
            if (obj == null)              return "null";
            if (obj instanceof String s)  return "\"" + escape(s) + "\"";
            if (obj instanceof Number)    return obj.toString();
            if (obj instanceof Boolean)   return obj.toString();
            if (obj instanceof Date d)    return String.valueOf(d.getTime() / 1000);
            if (obj instanceof Map<?,?> m) {
                StringBuilder sb = new StringBuilder("{");
                boolean first = true;
                for (Map.Entry<?, ?> e : m.entrySet()) {
                    if (!first) sb.append(",");
                    sb.append("\"").append(escape(e.getKey().toString())).append("\":");
                    sb.append(toJson(e.getValue()));
                    first = false;
                }
                return sb.append("}").toString();
            }
            return "\"" + escape(obj.toString()) + "\"";
        }

        private String escape(String s) {
            return s.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
        }
    }

    // ── DESERIALIZER ──────────────────────────────────────────
    private static class JsonDeserializer implements Deserializer<Map<String, ?>> {

        @Override
        public Map<String, ?> deserialize(byte[] bytes) {
            String json = new String(bytes, StandardCharsets.UTF_8).trim();
            return parseObject(json);
        }

        @Override
        public Map<String, ?> deserialize(Reader reader) throws DeserializationException {
            return Map.of();
        }

        @SuppressWarnings("unchecked")
        private Map<String, Object> parseObject(String json) {
            Map<String, Object> map = new LinkedHashMap<>();
            json = json.trim();
            if (json.startsWith("{")) json = json.substring(1);
            if (json.endsWith("}"))   json = json.substring(0, json.length() - 1);
            json = json.trim();
            if (json.isEmpty()) return map;

            List<String> tokens = splitTopLevel(json);
            for (String token : tokens) {
                int colon = token.indexOf(':');
                if (colon < 0) continue;
                String key   = token.substring(0, colon).trim();
                String value = token.substring(colon + 1).trim();
                if (key.startsWith("\"") && key.endsWith("\""))
                    key = unescape(key.substring(1, key.length() - 1));
                map.put(key, parseValue(value));
            }
            return map;
        }

        private Object parseValue(String v) {
            v = v.trim();
            if (v.equals("null"))          return null;
            if (v.equals("true"))          return true;
            if (v.equals("false"))         return false;
            if (v.startsWith("\"") && v.endsWith("\""))
                return unescape(v.substring(1, v.length() - 1));
            if (v.startsWith("{"))         return parseObject(v);
            // número
            try {
                if (v.contains(".")) return Double.parseDouble(v);
                long l = Long.parseLong(v);
                if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) return (int) l;
                return l;
            } catch (NumberFormatException e) {
                return v;
            }
        }

        private List<String> splitTopLevel(String s) {
            List<String> parts = new ArrayList<>();
            int depth = 0;
            boolean inStr = false;
            StringBuilder cur = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '\\' && inStr) { cur.append(c); cur.append(s.charAt(++i)); continue; }
                if (c == '"')  inStr = !inStr;
                if (!inStr && (c == '{' || c == '[')) depth++;
                if (!inStr && (c == '}' || c == ']')) depth--;
                if (!inStr && depth == 0 && c == ',') {
                    parts.add(cur.toString().trim());
                    cur.setLength(0);
                } else {
                    cur.append(c);
                }
            }
            if (cur.length() > 0) parts.add(cur.toString().trim());
            return parts;
        }

        private String unescape(String s) {
            return s.replace("\\\"", "\"")
                    .replace("\\\\", "\\")
                    .replace("\\n",  "\n")
                    .replace("\\r",  "\r")
                    .replace("\\t",  "\t");
        }
    }
}