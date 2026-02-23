package com.elias.attendancecontrol.service;
import com.elias.attendancecontrol.model.entity.QRToken;
import com.elias.attendancecontrol.model.entity.SessionToken;
import java.util.Map;
public interface TokenService {
    boolean validateSessionToken(String token);
    void revokeToken(String token);
    QRToken regenerateQR(Long sessionId);
    boolean validateQR(String token);
    QRToken getQRTokenWithSessionAndOrganization(String token, String orgSlug);
    void invalidateQR(String token);
    void invalidateUserSessionTokens(String username);
    void invalidateUserSessionTokensById(Long userId);
    Map<String, Object> generateQRWithFullData(Long sessionId, String baseUrl);
    void autoRegenerateActiveSessionQRs();
}
