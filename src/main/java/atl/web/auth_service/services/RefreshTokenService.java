package atl.web.auth_service.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import atl.web.auth_service.dto.RefreshTokenRequestDto;
import atl.web.auth_service.dto.RefreshTokenResponseDto;
import atl.web.auth_service.exceptions.RefreshTokenNotFoundException;
import atl.web.auth_service.jwt.JwtUtils;
import atl.web.auth_service.model.Credential;
import atl.web.auth_service.model.RefreshToken;
import atl.web.auth_service.repositories.RefreshTokenRepository;
import lombok.AllArgsConstructor;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class RefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;
    private JwtUtils jwtUtils;

    public String getAccessToken(Credential credential) {
        return jwtUtils.generateAccessToken(credential);
    }

    @Transactional
    public String getRefreshToken(Credential credential) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByCredential(credential);

        if (refreshToken.isPresent()) {
            String token = refreshToken.get().getRefreshToken();

            if (jwtUtils.validateToken(token) && refreshToken.get().isActive()) {
                return token;
            }

            String rToken = jwtUtils.generateRefreshToken(credential);
            refreshToken.get().setRefreshToken(rToken);
            refreshToken.get().setActive(true);
            refreshTokenRepository.save(refreshToken.get());

            return rToken;
        } else {
            String token = jwtUtils.generateRefreshToken(credential);
            refreshTokenRepository.save(new RefreshToken(null, credential, token, true));

            return token;
        }

    }

    @Transactional
    public void deactiveRefreshToken(Credential credential) {
        refreshTokenRepository.deactiveRefreshToken(credential);
    }

    @Transactional
    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto request) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new RefreshTokenNotFoundException(request.getRefreshToken()));

        if (!refreshToken.isActive()) {
            throw new RefreshTokenNotFoundException(request.getRefreshToken());
        }

        Credential credential = refreshToken.getCredential();

        String aToken = getAccessToken(credential);
        String rToken = getRefreshToken(credential);

        refreshToken.setRefreshToken(rToken);

        refreshTokenRepository.save(refreshToken);

        return new RefreshTokenResponseDto(aToken, rToken);
    }

    public Boolean validateToken(String token) {
        return jwtUtils.validateToken(token);
    }

    public String extractRole(String token){
        return jwtUtils.getRoleFromToken(token);
    }

    public String extractUsername(String token){
        return jwtUtils.getUsernameFromToken(token);
    }
}
