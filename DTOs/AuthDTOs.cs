using System.Text.Json.Serialization;

namespace BackEnd_Destino.DTOs;

public record LoginRequest(string Email, string Password);
public record RegisterRequest(string FirstName, string LastName, string Cpf, string Email, string Phone, string Password);
public record AuthResponse(string Token, string RefreshToken, UserResponse User);
public record RefreshRequest(string RefreshToken);
public record UserResponse(Guid Id, string FirstName, string LastName, string Email, string Role, string Slug);
public record UpdateProfileRequest(string FirstName, string LastName, string Email);
public record UpdatePasswordRequest(string CurrentPassword, string Password);
public record ForgotPasswordRequest(string Email);
public record ResetPasswordRequest(string Token, string Email, string Password);
public record ContactRequest(string Name, string Email, string Subject, string Message);
public record UpdateUserAccessRequest(Guid? RoleId);
public record AdminUpdateProfileRequest(string FirstName, string LastName, string Email, string? Phone, string? Cpf);
public record RegisterStaffRequest(string FirstName, string LastName, string Email, string Cpf, string? Phone, Guid RoleId);

public record PaginatedPackagesResponse(
    [property: JsonPropertyName("packages")] System.Collections.Generic.IEnumerable<BackEnd_Destino.Models.Package> Packages,
    [property: JsonPropertyName("total_pages")] int TotalPages,
    [property: JsonPropertyName("current_page")] int CurrentPage
);

public record SearchFilters(string Term, decimal MaxPrice, int Page, int Size);
public record PaginationInfo(int Page, int TotalPages, long TotalElements);

public record SearchPackagesResponse(
    [property: JsonPropertyName("packages")] System.Collections.Generic.IEnumerable<BackEnd_Destino.Models.Package> Packages,
    [property: JsonPropertyName("filters")] SearchFilters Filters,
    [property: JsonPropertyName("pagination")] PaginationInfo Pagination
);
