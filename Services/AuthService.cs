namespace BackEnd_Destino.Services;


public class AuthService : IAuthService
{
    private readonly IConfiguration _configuration;

    public AuthService(IConfiguration configuration)
    {
        _configuration = configuration;
    }

    public string GenerateToken(User user)
    {
        var jwtKey = _configuration["Jwt:Key"]!;
        var jwtIssuer = _configuration["Jwt:Issuer"];
        var jwtAudience = _configuration["Jwt:Audience"];
        
        var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtKey));
        var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

        var permissions = new List<string>();
        
        // Permissões via Role
        if (user.Role?.RolePermissions != null)
        {
            permissions.AddRange(user.Role.RolePermissions
                .Where(rp => rp.Permission != null)
                .Select(rp => rp.Permission!.Slug));
        }

        // Permissões diretas do usuário
        if (user.UserPermissions != null)
        {
            permissions.AddRange(user.UserPermissions
                .Where(up => up.Permission != null)
                .Select(up => up.Permission!.Slug));
        }

        var claims = new List<Claim>
        {
            new Claim(ClaimTypes.NameIdentifier, user.Id.ToString()),
            new Claim(ClaimTypes.Email, user.Email),
            new Claim(ClaimTypes.Role, user.Role?.Name ?? "USUARIO"),
            new Claim("firstName", user.FirstName),
            new Claim("slug", user.Slug)
        };

        foreach (var permission in permissions.Distinct())
        {
            claims.Add(new Claim("permission", permission));
        }

        var token = new JwtSecurityToken(
            issuer: jwtIssuer,
            audience: jwtAudience,
            claims: claims,
            expires: DateTime.UtcNow.AddMinutes(10),
            signingCredentials: creds
        );

        return new JwtSecurityTokenHandler().WriteToken(token);
    }

    public RefreshToken GenerateRefreshToken(Guid userId)
    {
        var randomNumber = new byte[32];
        using var rng = RandomNumberGenerator.Create();
        rng.GetBytes(randomNumber);
        
        return new RefreshToken
        {
            Token = Convert.ToBase64String(randomNumber),
            UserId = userId,
            ExpiresAt = DateTime.UtcNow.AddDays(7),
            CreatedAt = DateTime.UtcNow
        };
    }

    public string HashPassword(string password)
    {
        // Parâmetros Argon2id padrão do Laravel (ajustados via .env se necessário, mas aqui fixos nos padrões informados)
        var salt = new byte[16];
        using (var rng = RandomNumberGenerator.Create())
        {
            rng.GetBytes(salt);
        }

        using var argon2 = new Argon2id(Encoding.UTF8.GetBytes(password))
        {
            Salt = salt,
            DegreeOfParallelism = 1, // threads
            MemorySize = 65536,      // memory in KB
            Iterations = 4           // time
        };

        var hash = argon2.GetBytes(32);

        // Formato compatível com Laravel: $argon2id$v=19$m=65536,t=4,p=1$SALT$HASH
        // Laravel usa Base64 sem padding para salt e hash
        var saltBase64 = Convert.ToBase64String(salt).Replace("=", "");
        var hashBase64 = Convert.ToBase64String(hash).Replace("=", "");

        return $"$argon2id$v=19$m=65536,t=4,p=1${saltBase64}${hashBase64}";
    }

    public bool VerifyPassword(string password, string hashedPassword)
    {
        if (string.IsNullOrEmpty(hashedPassword) || !hashedPassword.StartsWith("$argon2id$"))
            return false;

        try
        {
            var parts = hashedPassword.Split('$');
            if (parts.Length != 6) return false;

            var config = parts[3]; // m=65536,t=4,p=1
            var saltBase64 = parts[4];
            var hashBase64 = parts[5];

            // Extrair parâmetros
            var memory = 65536;
            var iterations = 4;
            var parallelism = 1;

            foreach (var p in config.Split(','))
            {
                var kv = p.Split('=');
                if (kv[0] == "m") memory = int.Parse(kv[1]);
                if (kv[0] == "t") iterations = int.Parse(kv[1]);
                if (kv[0] == "p") parallelism = int.Parse(kv[1]);
            }

            // Adicionar padding se necessário para Convert.FromBase64String
            var salt = Convert.FromBase64String(PadBase64(saltBase64));
            var storedHash = Convert.FromBase64String(PadBase64(hashBase64));

            using var argon2 = new Argon2id(Encoding.UTF8.GetBytes(password))
            {
                Salt = salt,
                DegreeOfParallelism = parallelism,
                MemorySize = memory,
                Iterations = iterations
            };

            var computedHash = argon2.GetBytes(32);

            return CryptographicOperations.FixedTimeEquals(computedHash, storedHash);
        }
        catch
        {
            return false;
        }
    }

    private string PadBase64(string base64)
    {
        return base64.Length % 4 == 0 ? base64 : base64.PadRight(base64.Length + (4 - base64.Length % 4), '=');
    }
}
