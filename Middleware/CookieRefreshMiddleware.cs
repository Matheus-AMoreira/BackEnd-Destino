using BackEnd_Destino.Data;
using BackEnd_Destino.Services;
using Microsoft.EntityFrameworkCore;
using System.IdentityModel.Tokens.Jwt;

namespace BackEnd_Destino.Middleware;

public class CookieRefreshMiddleware
{
    private readonly RequestDelegate _next;

    public CookieRefreshMiddleware(RequestDelegate next)
    {
        _next = next;
    }

    public async Task InvokeAsync(HttpContext context, IServiceProvider serviceProvider)
    {
        var accessToken = context.Request.Cookies["access_token"];
        var refreshToken = context.Request.Cookies["refresh_token"];

        if (string.IsNullOrEmpty(accessToken) && !string.IsNullOrEmpty(refreshToken))
        {
            using var scope = serviceProvider.CreateScope();
            var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
            var authService = scope.ServiceProvider.GetRequiredService<IAuthService>();

            var storedToken = await db.RefreshTokens
                .Include(t => t.User)
                .FirstOrDefaultAsync(t => t.Token == refreshToken);

            if (storedToken != null && storedToken.IsActive)
            {
                // Renovar o token
                storedToken.RevokedAt = DateTime.UtcNow;
                
                var newToken = authService.GenerateToken(storedToken.User);
                var newRefreshToken = authService.GenerateRefreshToken(storedToken.UserId);
                
                db.RefreshTokens.Add(newRefreshToken);
                await db.SaveChangesAsync();

                // Definir novos cookies
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

                // Injetar o novo token no contexto para que o AuthenticationMiddleware o veja nesta mesma requisição
                context.Request.Headers["Authorization"] = $"Bearer {newToken}";
                // No caso do nosso setup de cookies no Program.cs, o OnMessageReceived lê do cookie.
                // Mas o cookie acabou de ser adicionado à Resposta, não à Requisição.
                // Então precisamos "fingir" que ele veio na requisição para o JwtBearerEvents.OnMessageReceived
                // Ou simplesmente setar o token manualmente se possível.
                // Como OnMessageReceived lê context.Request.Cookies["access_token"], vamos tentar adicionar à requisição.
                // Mas context.Request.Cookies é somente leitura em muitas implementações.
            }
        }

        await _next(context);
    }
}
