using System.Text;
using BackEnd_Destino.Data;
using BackEnd_Destino.Endpoints;
using BackEnd_Destino.Services;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using BackEnd_Destino.Authorization;
using Microsoft.AspNetCore.Authorization;

DotNetEnv.Env.Load();

var builder = WebApplication.CreateBuilder(args);

var config = builder.Configuration;
if (Environment.GetEnvironmentVariable("DB_CONNECTION_STRING") is string dbConn)
    config["ConnectionStrings:DefaultConnection"] = dbConn;
if (Environment.GetEnvironmentVariable("JWT_KEY") is string jwtKeyEnv)
    config["Jwt:Key"] = jwtKeyEnv;
if (Environment.GetEnvironmentVariable("JWT_ISSUER") is string jwtIssuerEnv)
    config["Jwt:Issuer"] = jwtIssuerEnv;
if (Environment.GetEnvironmentVariable("JWT_AUDIENCE") is string jwtAudienceEnv)
    config["Jwt:Audience"] = jwtAudienceEnv;
if (Environment.GetEnvironmentVariable("SMTP_HOST") is string smtpHostEnv)
    config["Smtp:Host"] = smtpHostEnv;
if (Environment.GetEnvironmentVariable("SMTP_PORT") is string smtpPortEnv)
    config["Smtp:Port"] = smtpPortEnv;
if (Environment.GetEnvironmentVariable("SMTP_USERNAME") is string smtpUsernameEnv)
    config["Smtp:Username"] = smtpUsernameEnv;
if (Environment.GetEnvironmentVariable("SMTP_PASSWORD") is string smtpPasswordEnv)
    config["Smtp:Password"] = smtpPasswordEnv;
if (Environment.GetEnvironmentVariable("SMTP_FROM_EMAIL") is string smtpFromEmailEnv)
    config["Smtp:FromEmail"] = smtpFromEmailEnv;
if (Environment.GetEnvironmentVariable("SMTP_FROM_NAME") is string smtpFromNameEnv)
    config["Smtp:FromName"] = smtpFromNameEnv;

// 1. Database Configuration
var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(connectionString));

// 1.1 JSON Configuration
builder.Services.ConfigureHttpJsonOptions(options => {
    options.SerializerOptions.PropertyNamingPolicy = System.Text.Json.JsonNamingPolicy.SnakeCaseLower;
    options.SerializerOptions.ReferenceHandler = System.Text.Json.Serialization.ReferenceHandler.IgnoreCycles;
    options.SerializerOptions.DefaultIgnoreCondition = System.Text.Json.Serialization.JsonIgnoreCondition.WhenWritingNull;
});

// 2. Services
builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddScoped<IEmailService, EmailService>();
builder.Services.AddHttpContextAccessor();

// 3. Authentication & Authorization
var jwtKey = builder.Configuration["Jwt:Key"]!;
var jwtIssuer = builder.Configuration["Jwt:Issuer"];
var jwtAudience = builder.Configuration["Jwt:Audience"];
var key = Encoding.UTF8.GetBytes(jwtKey);

builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = true,
        ValidateAudience = true,
        ValidateLifetime = true,
        ValidateIssuerSigningKey = true,
        ValidIssuer = jwtIssuer,
        ValidAudience = jwtAudience,
        IssuerSigningKey = new SymmetricSecurityKey(key),
        ClockSkew = TimeSpan.Zero // Precisão para os 10 minutos
    };

    options.Events = new JwtBearerEvents
    {
        OnMessageReceived = context =>
        {
            var token = context.Request.Cookies["access_token"];
            
            if (string.IsNullOrEmpty(token))
            {
                var authHeader = context.Request.Headers["Authorization"].ToString();
                if (authHeader.StartsWith("Bearer ", StringComparison.OrdinalIgnoreCase))
                {
                    token = authHeader.Substring("Bearer ".Length).Trim();
                }
            }

            context.Token = token;
            return Task.CompletedTask;
        }
    };
});

builder.Services.AddSingleton<IAuthorizationPolicyProvider, PermissionPolicyProvider>();
builder.Services.AddScoped<IAuthorizationHandler, PermissionHandler>();
builder.Services.AddAuthorization();

// 4. CORS
builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(policy =>
    {
        policy.WithOrigins("http://localhost:5173")
              .AllowAnyMethod()
              .AllowAnyHeader()
              .AllowCredentials();
    });
});

var app = builder.Build();

if (args.Length > 0 && args[0].ToLower() == "seed")
{
    using var scope = app.Services.CreateScope();
    var context = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    await DbInitializer.Seed(context);
    return;
}

if (app.Environment.IsDevelopment())
{
    app.UseDeveloperExceptionPage();
}

app.UseCors();
app.UseMiddleware<BackEnd_Destino.Middleware.CookieRefreshMiddleware>();
app.UseAuthentication();
app.UseAuthorization();

// 5. Register Endpoints
var api = app.MapGroup("/api");

api.MapAuthEndpoints();
api.MapPublicEndpoints();
api.MapUserEndpoints();
api.MapAdminEndpoints();

app.Run();
