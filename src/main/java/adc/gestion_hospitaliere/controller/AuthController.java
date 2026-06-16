package adc.gestion_hospitaliere.controller;
import adc.gestion_hospitaliere.Entity.User;
import adc.gestion_hospitaliere.dto.ResponseApi.ApiResponse;
import adc.gestion_hospitaliere.dto.auth.AuthResponse;
import adc.gestion_hospitaliere.dto.auth.LoginRequest;
import adc.gestion_hospitaliere.dto.auth.RegisterRequest;
import adc.gestion_hospitaliere.dto.auth.UserUpdateDto;
import adc.gestion_hospitaliere.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor



public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .message("Inscription réussie")
                .data(response).build());
    }



    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .message("Connexion réussie")
                .data(response).build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        // à implémenter si besoin
        return ResponseEntity.ok("Utilisateur connecté");
    }
}