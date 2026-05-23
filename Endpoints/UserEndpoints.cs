using System.Security.Claims;

namespace BackEnd_Destino.Endpoints;

public static class UserEndpoints
{
    public static void MapUserEndpoints(this IEndpointRouteBuilder routes)
    {
        var group = routes.MapGroup("/usuario").RequireAuthorization();

        group.MapGet("/perfil", async (ClaimsPrincipal user, AppDbContext db) =>
        {
            var userId = Guid.Parse(user.FindFirstValue(ClaimTypes.NameIdentifier)!);
            var userData = await db.Users.FindAsync(userId);
            return userData != null ? Results.Ok(userData) : Results.NotFound();
        });

        group.MapPut("/perfil", async (UpdateProfileRequest request, ClaimsPrincipal user, AppDbContext db) =>
        {
            var userId = Guid.Parse(user.FindFirstValue(ClaimTypes.NameIdentifier)!);
            var userData = await db.Users.FindAsync(userId);
            if (userData == null) return Results.NotFound();

            userData.FirstName = request.FirstName;
            userData.LastName = request.LastName;
            userData.Email = request.Email;
            userData.UpdatedAt = DateTime.UtcNow;

            await db.SaveChangesAsync();
            return Results.Ok(userData);
        }).RequirePermission("profile:update");

        group.MapPut("/password", async (UpdatePasswordRequest request, ClaimsPrincipal user, AppDbContext db, IAuthService authService) =>
        {
            var userId = Guid.Parse(user.FindFirstValue(ClaimTypes.NameIdentifier)!);
            var userData = await db.Users.FindAsync(userId);
            if (userData == null) return Results.NotFound();

            if (!authService.VerifyPassword(request.CurrentPassword, userData.Password))
            {
                return Results.BadRequest(new { message = "Senha atual incorreta." });
            }

            userData.Password = authService.HashPassword(request.Password);
            userData.UpdatedAt = DateTime.UtcNow;

            await db.SaveChangesAsync();
            return Results.Ok(new { message = "Senha atualizada com sucesso." });
        });

        group.MapDelete("/perfil", async (ClaimsPrincipal user, AppDbContext db) =>
        {
            var userId = Guid.Parse(user.FindFirstValue(ClaimTypes.NameIdentifier)!);
            var userData = await db.Users
                .Include(u => u.Role)
                .FirstOrDefaultAsync(u => u.Id == userId);

            if (userData == null) return Results.NotFound();

            // Proteção contra deleção de Staff (Regra do Laravel)
            if (userData.Role != null && userData.Role.IsStaff)
            {
                return Results.Json(new { message = "Funcionários não podem ser deletados para preservar o histórico. Use o bloqueio." }, statusCode: 403);
            }

            db.Users.Remove(userData);
            await db.SaveChangesAsync();

            return Results.Ok(new { message = "Conta excluída com sucesso." });
        }).RequirePermission("profile:delete");

        group.MapGet("/viagens", async (ClaimsPrincipal user, AppDbContext db) =>
        {
            var userId = Guid.Parse(user.FindFirstValue(ClaimTypes.NameIdentifier)!);
            var viagens = await db.Purchases
                .Include(c => c.Offer)
                    .ThenInclude(o => o!.Package)
                .Include(c => c.Offer)
                    .ThenInclude(o => o!.Hotel)
                        .ThenInclude(h => h!.City)
                            .ThenInclude(c => c!.State)
                .Where(c => c.UserId == userId)
                .OrderByDescending(c => c.PurchaseDate)
                .ToListAsync();
            return Results.Ok(viagens);
        }).RequirePermission("purchase:read");

        group.MapGet("/viagens/{id}", async (Guid id, ClaimsPrincipal user, AppDbContext db) =>
        {
            var userId = Guid.Parse(user.FindFirstValue(ClaimTypes.NameIdentifier)!);
            var viagem = await db.Purchases
                .Include(c => c.Offer)
                    .ThenInclude(o => o!.Package)
                        .ThenInclude(p => p!.PackagePhotos)
                .Include(c => c.Offer)
                    .ThenInclude(o => o!.Hotel)
                        .ThenInclude(h => h!.City)
                            .ThenInclude(c => c!.State)
                .FirstOrDefaultAsync(c => c.Id == id && c.UserId == userId);

            return viagem != null ? Results.Ok(viagem) : Results.NotFound();
        });

        group.MapPost("/checkout/processar", async (Purchase request, ClaimsPrincipal user, AppDbContext db) =>
        {
            var userId = Guid.Parse(user.FindFirstValue(ClaimTypes.NameIdentifier)!);
            var oferta = await db.Offers.FindAsync(request.OfferId);
            if (oferta == null) return Results.NotFound(new { message = "Oferta não encontrada." });

            var compra = new Purchase
            {
                Id = Guid.NewGuid(),
                PurchaseDate = DateTime.UtcNow,
                Status = PurchaseStatus.Accepted,
                Method = request.Method,
                PaymentProcessor = request.PaymentProcessor,
                Installments = request.Installments,
                FinalValue = request.Method == "PIX" ? oferta.Price * 0.95m : oferta.Price,
                UserId = userId,
                OfferId = request.OfferId
            };

            db.Purchases.Add(compra);
            await db.SaveChangesAsync();

            return Results.Ok(compra);
        }).RequirePermission("purchase:create");

        group.MapGet("/checkout/confirmation/{id}", async (Guid id, ClaimsPrincipal user, AppDbContext db) =>
        {
            var userId = Guid.Parse(user.FindFirstValue(ClaimTypes.NameIdentifier)!);
            var compra = await db.Purchases
                .Include(c => c.Offer)
                    .ThenInclude(o => o!.Package)
                .Include(c => c.Offer)
                    .ThenInclude(o => o!.Hotel)
                        .ThenInclude(h => h!.City)
                .FirstOrDefaultAsync(c => c.Id == id && c.UserId == userId);

            if (compra == null) return Results.NotFound();
            return Results.Ok(compra);
        }).RequirePermission("purchase:read");
    }
}
