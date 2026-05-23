namespace BackEnd_Destino.Endpoints;

public static class AuthEndpoints
{
    public static void MapAuthEndpoints(this IEndpointRouteBuilder routes)
    {
        var group = routes.MapGroup("/auth");

        group.MapPost("/login", async (LoginRequest request, AppDbContext db, IAuthService authService, HttpContext context) =>
        {
            var user = await db.Users
                .Include(u => u.Role)
                    .ThenInclude(r => r!.RolePermissions)
                        .ThenInclude(rp => rp.Permission)
                .Include(u => u.UserPermissions)
                    .ThenInclude(up => up.Permission)
                .FirstOrDefaultAsync(u => u.Email == request.Email);

            if (user == null || !authService.VerifyPassword(request.Password, user.Password))
            {
                return Results.BadRequest(new { message = "E-mail ou senha inválidos." });
            }

            if (!user.IsValid)
            {
                return Results.Json(new { message = "Sua conta está bloqueada. Entre em contato com o suporte." }, statusCode: 403);
            }

            var token = authService.GenerateToken(user);
            var refreshToken = authService.GenerateRefreshToken(user.Id);
            
            db.RefreshTokens.Add(refreshToken);
            await db.SaveChangesAsync();

            // Configurar Cookies
            var cookieOptions = new CookieOptions
            {
                HttpOnly = true,
                Secure = true, // No ambiente de produção deve ser true
                SameSite = SameSiteMode.Strict,
                Expires = DateTime.UtcNow.AddDays(7)
            };

            context.Response.Cookies.Append("access_token", token, new CookieOptions { 
                HttpOnly = true, 
                Secure = true, 
                SameSite = SameSiteMode.Strict, 
                Expires = DateTime.UtcNow.AddMinutes(10) 
            });
            context.Response.Cookies.Append("refresh_token", refreshToken.Token, cookieOptions);

            return Results.Ok(new { 
                message = "Login realizado com sucesso",
                user = new UserResponse(user.Id, user.FirstName, user.LastName, user.Email, user.Role?.Name ?? "USUARIO", user.Slug)
            });
        });

        group.MapPost("/register", async (RegisterRequest request, AppDbContext db, IAuthService authService, HttpContext context) =>
        {
            if (await db.Users.AnyAsync(u => u.Email == request.Email))
                return Results.BadRequest(new { message = "E-mail já cadastrado." });

            if (await db.Users.AnyAsync(u => u.Cpf == request.Cpf))
                return Results.BadRequest(new { message = "CPF já cadastrado." });

            var userRole = await db.Roles.FirstOrDefaultAsync(r => r.Name == "USUARIO");

            var user = new User
            {
                FirstName = request.FirstName,
                LastName = request.LastName,
                Cpf = request.Cpf,
                Email = request.Email,
                Phone = request.Phone,
                Password = authService.HashPassword(request.Password),
                RoleId = userRole?.Id,
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow
            };
            user.UpdateSlug(); // Usa o método de slug do modelo

            db.Users.Add(user);
            await db.SaveChangesAsync();

            var token = authService.GenerateToken(user);
            var refreshToken = authService.GenerateRefreshToken(user.Id);
            
            db.RefreshTokens.Add(refreshToken);
            await db.SaveChangesAsync();

            // Configurar Cookies
            context.Response.Cookies.Append("access_token", token, new CookieOptions { 
                HttpOnly = true, 
                Secure = true, 
                SameSite = SameSiteMode.Strict, 
                Expires = DateTime.UtcNow.AddMinutes(10) 
            });
            context.Response.Cookies.Append("refresh_token", refreshToken.Token, new CookieOptions { 
                HttpOnly = true, 
                Secure = true, 
                SameSite = SameSiteMode.Strict, 
                Expires = DateTime.UtcNow.AddDays(7) 
            });

            return Results.Ok(new { 
                message = "Cadastro realizado com sucesso",
                user = new UserResponse(user.Id, user.FirstName, user.LastName, user.Email, userRole?.Name ?? "USUARIO", user.Slug)
            });
        });

        group.MapPost("/refresh", async (AppDbContext db, IAuthService authService, HttpContext context) =>
        {
            var refreshTokenValue = context.Request.Cookies["refresh_token"];
            if (string.IsNullOrEmpty(refreshTokenValue)) return Results.Unauthorized();

            var storedToken = await db.RefreshTokens
                .Include(t => t.User)
                    .ThenInclude(u => u.Role)
                        .ThenInclude(r => r!.RolePermissions)
                            .ThenInclude(rp => rp.Permission)
                .Include(t => t.User)
                    .ThenInclude(u => u.UserPermissions)
                        .ThenInclude(up => up.Permission)
                .FirstOrDefaultAsync(t => t.Token == refreshTokenValue);

            if (storedToken == null || !storedToken.IsActive)
                return Results.Unauthorized();

            // Revogar o token antigo e gerar um novo
            storedToken.RevokedAt = DateTime.UtcNow;
            
            var newToken = authService.GenerateToken(storedToken.User!);
            var newRefreshToken = authService.GenerateRefreshToken(storedToken.UserId);
            
            db.RefreshTokens.Add(newRefreshToken);
            await db.SaveChangesAsync();

            // Atualizar Cookies
            context.Response.Cookies.Append("access_token", newToken, new CookieOptions { 
                HttpOnly = true, 
                Secure = true, 
                SameSite = SameSiteMode.Strict, 
                Expires = DateTime.UtcNow.AddMinutes(10) 
            });
            context.Response.Cookies.Append("refresh_token", newRefreshToken.Token, new CookieOptions { 
                HttpOnly = true, 
                Secure = true, 
                SameSite = SameSiteMode.Strict, 
                Expires = DateTime.UtcNow.AddDays(7) 
            });

            return Results.Ok(new { 
                user = new UserResponse(storedToken.User.Id, storedToken.User.FirstName, storedToken.User.LastName, storedToken.User.Email, storedToken.User.Role?.Name ?? "USUARIO", storedToken.User.Slug)
            });
        });

        group.MapGet("/me", async (ClaimsPrincipal userClaims, AppDbContext db) =>
        {
            var userIdClaim = userClaims.FindFirst(ClaimTypes.NameIdentifier);
            if (userIdClaim == null) return Results.Unauthorized();

            var userId = Guid.Parse(userIdClaim.Value);
            var user = await db.Users
                .Include(u => u.Role)
                .FirstOrDefaultAsync(u => u.Id == userId);

            if (user == null) return Results.NotFound();

            return Results.Ok(new UserResponse(user.Id, user.FirstName, user.LastName, user.Email, user.Role?.Name ?? "USUARIO", user.Slug));
        }).RequireAuthorization();

        group.MapPost("/logout", (HttpContext context) =>
        {
            context.Response.Cookies.Delete("access_token");
            context.Response.Cookies.Delete("refresh_token");
            return Results.Ok(new { message = "Logout realizado com sucesso" });
        });

        group.MapGet("/verify-email/{id}/{hash}", async (Guid id, string hash, AppDbContext db) =>
        {
            var user = await db.Users.FindAsync(id);
            if (user == null) return Results.NotFound(new { message = "Usuário não encontrado." });

            // Simplificação: no futuro podemos validar o hash propriamente
            if (user.EmailVerifiedAt != null) return Results.BadRequest(new { message = "E-mail já verificado." });

            user.EmailVerifiedAt = DateTime.UtcNow;
            await db.SaveChangesAsync();

            return Results.Ok(new { message = "E-mail verificado com sucesso!" });
        });

        group.MapPost("/verification-notification", async (ClaimsPrincipal userClaims, AppDbContext db, IEmailService emailService) =>
        {
            var userIdClaim = userClaims.FindFirst(ClaimTypes.NameIdentifier);
            if (userIdClaim == null) return Results.Unauthorized();

            var userId = Guid.Parse(userIdClaim.Value);
            var user = await db.Users.FindAsync(userId);
            if (user == null) return Results.NotFound();

            if (user.EmailVerifiedAt != null) return Results.BadRequest(new { message = "E-mail já verificado." });

            // Simular envio de e-mail de verificação
            var verifyUrl = $"http://localhost:3000/verificar-email/{user.Id}/hash_ficticio";
            await emailService.SendEmailAsync(user.Email, "Verifique seu E-mail - Paula Viagens", 
                $"Olá {user.FirstName},<br><br>Clique no link abaixo para verificar seu e-mail:<br><a href='{verifyUrl}'>{verifyUrl}</a>");

            return Results.Ok(new { message = "Link de verificação enviado." });
        }).RequireAuthorization();

        group.MapPost("/forgot-password", async (ForgotPasswordRequest request, AppDbContext db, IEmailService emailService) =>
        {
            var user = await db.Users.FirstOrDefaultAsync(u => u.Email == request.Email);
            if (user == null) return Results.Ok(new { message = "Se o e-mail existir, um link de recuperação será enviado." });

            var token = Guid.NewGuid().ToString();
            var resetToken = await db.PasswordResetTokens.FindAsync(request.Email);
            
            if (resetToken != null)
            {
                resetToken.Token = token;
                resetToken.CreatedAt = DateTime.UtcNow;
            }
            else
            {
                db.PasswordResetTokens.Add(new PasswordResetToken { Email = request.Email, Token = token, CreatedAt = DateTime.UtcNow });
            }

            await db.SaveChangesAsync();

            // Enviar e-mail (URL fictícia por enquanto, ajustar conforme o frontend)
            var resetUrl = $"http://localhost:3000/redefinir-senha?token={token}&email={request.Email}";
            await emailService.SendEmailAsync(user.Email, "Recuperação de Senha - Paula Viagens", 
                $"Olá {user.FirstName},<br><br>Clique no link abaixo para redefinir sua senha:<br><a href='{resetUrl}'>{resetUrl}</a>");

            return Results.Ok(new { message = "E-mail de recuperação enviado." });
        });

        group.MapPost("/reset-password", async (ResetPasswordRequest request, AppDbContext db, IAuthService authService) =>
        {
            var resetToken = await db.PasswordResetTokens.FirstOrDefaultAsync(t => t.Email == request.Email && t.Token == request.Token);
            if (resetToken == null || resetToken.CreatedAt < DateTime.UtcNow.AddHours(-2))
                return Results.BadRequest(new { message = "Token inválido ou expirado." });

            var user = await db.Users.FirstOrDefaultAsync(u => u.Email == request.Email);
            if (user == null) return Results.NotFound();

            user.Password = authService.HashPassword(request.Password);
            user.UpdatedAt = DateTime.UtcNow;

            db.PasswordResetTokens.Remove(resetToken);
            await db.SaveChangesAsync();

            return Results.Ok(new { message = "Senha redefinida com sucesso." });
        });
    }
}
