namespace BackEnd_Destino.Endpoints;

public static class PublicEndpoints
{
    public static void MapPublicEndpoints(this IEndpointRouteBuilder routes)
    {
        routes.MapGet("/", async (AppDbContext db) =>
        {
            var pacotes = await db.Packages
                .Include(p => p.PackagePhotos)
                .OrderByDescending(p => p.Id)
                .Take(6)
                .ToListAsync();
            return Results.Ok(pacotes);
        });

        routes.MapGet("/pacotes", async (AppDbContext db, [FromQuery] int page = 0) =>
        {
            int pageSize = 12;
            var query = db.Packages.AsQueryable();
            
            var total = await query.CountAsync();
            var pacotes = await query
                .Include(p => p.PackagePhotos)
                .OrderByDescending(p => p.Id)
                .Skip(page * pageSize)
                .Take(pageSize)
                .ToListAsync();

            return Results.Ok(new PaginatedPackagesResponse(
                Packages: pacotes,
                TotalPages: (int)Math.Ceiling((double)total / pageSize),
                CurrentPage: page
            ));
        });

        routes.MapGet("/buscar", async (
            [FromQuery] string? termo, 
            [FromQuery] decimal? precoMax, 
            [FromQuery] int page, 
            [FromQuery] int size, 
            AppDbContext db) =>
        {
            page = page < 0 ? 0 : page;
            size = size <= 0 ? 12 : size;
            var query = db.Packages
                .Include(p => p.PackagePhotos)
                .Include(p => p.Offers)
                .AsQueryable();

            if (!string.IsNullOrEmpty(termo))
            {
                query = query.Where(p => p.Name.Contains(termo) || p.Description.Contains(termo));
            }

            if (precoMax > 0)
            {
                query = query.Where(p => p.Offers.Any(o => o.Price <= precoMax));
            }

            var totalElements = await query.LongCountAsync();
            var pacotes = await query
                .OrderByDescending(p => p.Id)
                .Skip(page * size)
                .Take(size)
                .ToListAsync();

            var totalPages = (int)Math.Ceiling((double)totalElements / size);

            return Results.Ok(new SearchPackagesResponse(
                Packages: pacotes,
                Filters: new SearchFilters(termo ?? "", precoMax ?? 0, page, size),
                Pagination: new PaginationInfo(page, totalPages, totalElements)
            ));
        });

        routes.MapGet("/pacotes/{name}", async (string name, AppDbContext db) =>
        {
            var pacote = await db.Packages
                .Include(p => p.PackagePhotos)
                    .ThenInclude(pf => pf != null ? pf.Items : null)
                .Include(p => p.Offers)
                    .ThenInclude(o => o.Hotel)
                        .ThenInclude(h => h!.City)
                            .ThenInclude(c => c!.State)
                .Include(p => p.Offers)
                    .ThenInclude(o => o.Transport)
                .FirstOrDefaultAsync(p => p.Name == name);

            if (pacote == null) return Results.NotFound();
            return Results.Ok(pacote);
        });

        routes.MapGet("/ofertas/{id}", async (long id, AppDbContext db) =>
        {
            var oferta = await db.Offers
                .Include(o => o.Package)
                .Include(o => o.Hotel)
                    .ThenInclude(h => h!.City)
                        .ThenInclude(c => c!.State)
                .Include(o => o.Transport)
                .FirstOrDefaultAsync(o => o.Id == id);

            if (oferta == null) return Results.NotFound();
            return Results.Ok(oferta);
        });

        routes.MapPost("/contato", async (ContactRequest request, IEmailService emailService, IConfiguration config) =>
        {
            var adminEmail = config["Smtp:FromEmail"];
            if (string.IsNullOrEmpty(adminEmail))
            {
                return Results.Problem("O e-mail de destino (SMTP_FROM_EMAIL) não está configurado.");
            }
            await emailService.SendEmailAsync(adminEmail, $"Nova Mensagem de Contato - {request.Subject}", 
                $"<b>Nome:</b> {request.Name}<br><b>E-mail:</b> {request.Email}<br><br><b>Mensagem:</b><br>{request.Message}");
            
            return Results.Ok(new { message = "Mensagem enviada com sucesso!" });
        });
    }
}

