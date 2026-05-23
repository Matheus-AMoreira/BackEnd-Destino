namespace BackEnd_Destino.Authorization;

public static class AuthorizationExtensions
{
    public static TBuilder RequirePermission<TBuilder>(this TBuilder builder, string permission) 
        where TBuilder : IEndpointConventionBuilder
    {
        return builder.RequireAuthorization(new AuthorizeAttribute { Policy = $"Permission:{permission}" });
    }
}

public class PermissionPolicyProvider : IAuthorizationPolicyProvider
{
    public Task<AuthorizationPolicy> GetDefaultPolicyAsync() => Task.FromResult(new AuthorizationPolicyBuilder().RequireAuthenticatedUser().Build());
    public static Task<AuthorizationPolicy?> GetFallbackPolicyAsync() => Task.FromResult<AuthorizationPolicy?>(null);

    public Task<AuthorizationPolicy?> GetPolicyAsync(string policyName)
    {
        if (policyName.StartsWith("Permission:", StringComparison.OrdinalIgnoreCase))
        {
            var permission = policyName["Permission:".Length..];
            var policy = new AuthorizationPolicyBuilder();
            policy.AddRequirements(new PermissionRequirement(permission));
            return Task.FromResult<AuthorizationPolicy?>(policy.Build());
        }

        return Task.FromResult<AuthorizationPolicy?>(null);
    }
}
