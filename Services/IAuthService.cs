namespace BackEnd_Destino.Services;

public interface IAuthService
{
    string GenerateToken(User user);
    string HashPassword(string password);
    bool VerifyPassword(string password, string hashedPassword);
    RefreshToken GenerateRefreshToken(Guid userId);
}
