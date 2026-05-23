namespace BackEnd_Destino.Endpoints;

public static class AdminEndpoints
{
    public static void MapAdminEndpoints(this IEndpointRouteBuilder routes)
    {
        var group = routes.MapGroup("/admin").RequireAuthorization();

        group.MapGet("/stats", async (AppDbContext db) =>
        {
            var stats = new
            {
                pacotes = await db.Packages.CountAsync(),
                hoteis = await db.Hotels.CountAsync(),
                transportes = await db.Transports.CountAsync(),
                ofertas = await db.Offers.CountAsync(),
                usuarios = await db.Users.CountAsync()
            };
            return Results.Ok(stats);
        }).RequirePermission("dashboard:read");

        group.MapGet("/pacotes", async (AppDbContext db) =>
        {
            var pacotes = await db.Packages
                .Include(p => p.Staff)
                .Include(p => p.PackagePhotos)
                .OrderByDescending(p => p.Id)
                .ToListAsync();
            return Results.Ok(pacotes);
        }).RequirePermission("package:read");

        group.MapGet("/pacotes/{id}/compras", async (long id, AppDbContext db) =>
        {
            var compras = await db.Purchases
                .Include(c => c.User)
                .Include(c => c.Offer)
                    .ThenInclude(o => o!.Hotel)
                        .ThenInclude(h => h!.City)
                            .ThenInclude(c => c!.State)
                .Where(c => c.Offer!.PackageId == id)
                .ToListAsync();
            return Results.Ok(compras);
        }).RequirePermission("purchase:read"); 

        group.MapGet("/hoteis", async (AppDbContext db) =>
        {
            var hoteis = await db.Hotels.Include(h => h.City).ToListAsync();
            return Results.Ok(hoteis);
        }).RequirePermission("hotel:read");

        group.MapGet("/roles", async (AppDbContext db) =>
        {
            var roles = await db.Roles.ToListAsync();
            return Results.Ok(roles);
        });

        group.MapGet("/transportes", async (AppDbContext db) =>
        {
            var transportes = await db.Transports.ToListAsync();
            return Results.Ok(transportes);
        });

        group.MapGet("/ofertas", async (AppDbContext db) =>
        {
            var ofertas = await db.Offers
                .Include(o => o.Package)
                .Include(o => o.Hotel)
                .Include(o => o.Transport)
                .ToListAsync();
            return Results.Ok(ofertas);
        });

        group.MapGet("/pacote-fotos", async (AppDbContext db) =>
        {
            var fotos = await db.PackagePhotos.Include(f => f.Items).ToListAsync();
            return Results.Ok(fotos);
        });

        group.MapGet("/estatisticas", async ([FromQuery] int? ano, [FromQuery] long? estadoId, AppDbContext db) =>
        {
            int year = ano ?? DateTime.UtcNow.Year;

            var dados = await db.Purchases
                .Where(c => c.PurchaseDate.Year == year)
                .GroupBy(c => new { Month = c.PurchaseDate.Month, c.Status })
                .Select(g => new { mes = g.Key.Month, status = g.Key.Status, total = g.Count() })
                .ToListAsync();

            var anosVendas = await db.Purchases.Select(c => c.PurchaseDate.Year).Distinct().ToListAsync();
            var anosUsers = await db.Users.Where(u => u.CreatedAt.HasValue).Select(u => u.CreatedAt!.Value.Year).Distinct().ToListAsync();
            
            var anosDisponiveis = anosVendas.Union(anosUsers).OrderByDescending(y => y).ToList();
            if (!anosDisponiveis.Contains(year)) anosDisponiveis.Add(year);

            var crescimentoUsuarios = await db.Users
                .Where(u => u.CreatedAt.HasValue)
                .GroupBy(u => u.CreatedAt!.Value.Year)
                .Select(g => new { ano = g.Key, total = g.Count() })
                .OrderBy(g => g.ano)
                .ToListAsync();

            var queryDestinos = db.Purchases
                .Include(c => c.Offer)
                    .ThenInclude(o => o!.Hotel)
                        .ThenInclude(h => h!.City)
                            .ThenInclude(c => c!.State)
                .Where(c => c.Status == PurchaseStatus.Accepted && c.Offer!.StartDate.Year == year);

            if (estadoId.HasValue)
            {
                queryDestinos = queryDestinos.Where(c => c.Offer!.Hotel!.City!.StateId == estadoId.Value);
            }

            var destinosPopulares = await queryDestinos
                .GroupBy(c => new { 
                    City = c.Offer!.Hotel!.City!.Name, 
                    State = c.Offer!.Hotel!.City!.State!.Code 
                })
                .Select(g => new { 
                    cidade = g.Key.City, 
                    estado = g.Key.State, 
                    total = g.Count() 
                })
                .OrderByDescending(g => g.total)
                .Take(10)
                .ToListAsync();

            var estados = await db.States.OrderBy(e => e.Name).ToListAsync();

            return Results.Ok(new
            {
                dados,
                destinosPopulares,
                crescimentoUsuarios,
                ano = year,
                anosDisponiveis,
                estados,
                filtros = new {
                    estado_id = estadoId
                }
            });
        });

        group.MapGet("/usuarios", async ([FromQuery] string? termo, [FromQuery] int page, AppDbContext db) =>
        {
            int pageSize = 10;
            var query = db.Users.AsQueryable();

            if (!string.IsNullOrEmpty(termo))
            {
                query = query.Where(u => u.FirstName.Contains(termo) || u.Email.Contains(termo) || u.Cpf.Contains(termo));
            }

            var total = await query.CountAsync();
            var users = await query
                .OrderByDescending(u => u.CreatedAt)
                .Skip((page - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();

            var lastPage = (int)Math.Ceiling((double)total / pageSize);

            var links = new List<object>();
            for (int i = 1; i <= lastPage; i++)
            {
                links.Add(new { url = $"?page={i}", label = i.ToString(), active = i == page });
            }

            return Results.Ok(new
            {
                data = users,
                current_page = page,
                last_page = lastPage,
                total = total,
                links = links
            });
        }).RequirePermission("user:read");

        group.MapPost("/usuario/registrar", async (RegisterStaffRequest request, AppDbContext db, IEmailService emailService) => {
            if (await db.Users.AnyAsync(u => u.Email == request.Email))
            {
                return Results.BadRequest(new { message = "Este e-mail já está em uso." });
            }

            var user = new User
            {
                FirstName = request.FirstName,
                LastName = request.LastName,
                Email = request.Email,
                Cpf = request.Cpf,
                Phone = request.Phone ?? "",
                RoleId = request.RoleId,
                Password = Guid.NewGuid().ToString(), // Senha temporária aleatória
                CreatedAt = DateTime.UtcNow
            };
            user.UpdateSlug();

            db.Users.Add(user);
            await db.SaveChangesAsync();

            // Simular envio de convite
            var inviteUrl = $"http://localhost:3000/ativar-conta?email={user.Email}";
            await emailService.SendEmailAsync(user.Email, "Bem-vindo ao Time - Paula Viagens", 
                $"Olá {user.FirstName},<br><br>Você foi cadastrado em nosso sistema. Clique no link abaixo para configurar sua senha:<br><a href='{inviteUrl}'>{inviteUrl}</a>");

            return Results.Created($"/admin/usuario/{user.Id}", user);
        });

        group.MapGet("/usuario/{id}", async (Guid id, AppDbContext db) => {
            var user = await db.Users
                .Include(u => u.Purchases)
                    .ThenInclude(c => c.Offer)
                .FirstOrDefaultAsync(u => u.Id == id);
            return user != null ? Results.Ok(user) : Results.NotFound();
        });

        group.MapPost("/usuario/{id}/aprovar", async (Guid id, AppDbContext db) => {
            var user = await db.Users.FindAsync(id);
            if (user == null) return Results.NotFound();
            user.EmailVerifiedAt = DateTime.UtcNow;
            await db.SaveChangesAsync();
            return Results.NoContent();
        });

        group.MapPost("/usuario/{id}/toggle-block", async (Guid id, AppDbContext db) => {
            var user = await db.Users.FindAsync(id);
            if (user == null) return Results.NotFound();
            user.IsValid = !user.IsValid;
            await db.SaveChangesAsync();
            return Results.NoContent();
        });

        group.MapPost("/usuario/{id}/resend-invitation", async (Guid id, AppDbContext db, IEmailService emailService) => {
            var user = await db.Users.FindAsync(id);
            if (user == null) return Results.NotFound();

            // Simular reenvio de convite (URL fictícia de ativação)
            var inviteUrl = $"http://localhost:3000/ativar-conta?email={user.Email}";
            await emailService.SendEmailAsync(user.Email, "Convite para Acessar o Sistema - Paula Viagens", 
                $"Olá {user.FirstName},<br><br>Você foi convidado para acessar nosso sistema. Clique no link abaixo para configurar sua senha:<br><a href='{inviteUrl}'>{inviteUrl}</a>");

            return Results.Ok(new { message = "Convite reenviado com sucesso." });
        });

        group.MapPut("/usuario/{id}/access", async (Guid id, UpdateUserAccessRequest request, AppDbContext db) => {
            var user = await db.Users.FindAsync(id);
            if (user == null) return Results.NotFound();

            user.RoleId = request.RoleId;
            await db.SaveChangesAsync();
            return Results.NoContent();
        });

        group.MapPut("/usuario/{id}/perfil", async (Guid id, AdminUpdateProfileRequest request, AppDbContext db) => {
            var user = await db.Users.FindAsync(id);
            if (user == null) return Results.NotFound();

            user.FirstName = request.FirstName;
            user.LastName = request.LastName;
            user.Email = request.Email;
            user.Phone = request.Phone;
            user.Cpf = request.Cpf ?? user.Cpf;
            
            user.UpdateSlug();
            await db.SaveChangesAsync();
            return Results.Ok(user);
        });

        // --- CIDADES & FUNCIONARIOS HELPER ---
        group.MapGet("/cidades", async (AppDbContext db) => {
            return Results.Ok(await db.Cities.Include(c => c.State).OrderBy(c => c.Name).ToListAsync());
        });

        group.MapGet("/funcionarios", async (AppDbContext db) => {
            return Results.Ok(await db.Users
                .Include(u => u.Role)
                .Where(u => u.Role != null && (u.Role.Name == "ADMINISTRADOR" || u.Role.Name == "FUNCIONARIO"))
                .OrderBy(u => u.FirstName)
                .ToListAsync());
        });

        // --- HOTEL CRUD ---
        group.MapPost("/hoteis", async (Hotel hotel, AppDbContext db) => {
            db.Hotels.Add(hotel);
            await db.SaveChangesAsync();
            return Results.Created($"/admin/hoteis/{hotel.Id}", hotel);
        });

        group.MapGet("/hoteis/{id}", async (long id, AppDbContext db) => {
            var hotel = await db.Hotels.FindAsync(id);
            return hotel != null ? Results.Ok(hotel) : Results.NotFound();
        });

        group.MapPut("/hoteis/{id}", async (long id, Hotel input, AppDbContext db) => {
            var hotel = await db.Hotels.FindAsync(id);
            if (hotel == null) return Results.NotFound();
            
            hotel.Name = input.Name;
            hotel.DailyRate = input.DailyRate;
            hotel.CityId = input.CityId;
            hotel.Address = input.Address;
            
            await db.SaveChangesAsync();
            return Results.NoContent();
        });

        group.MapDelete("/hoteis/{id}", async (long id, AppDbContext db) => {
            var hotel = await db.Hotels.FindAsync(id);
            if (hotel == null) return Results.NotFound();
            db.Hotels.Remove(hotel);
            await db.SaveChangesAsync();
            return Results.NoContent();
        });

        // --- TRANSPORTE CRUD ---
        group.MapPost("/transportes", async (Transport transporte, AppDbContext db) => {
            db.Transports.Add(transporte);
            await db.SaveChangesAsync();
            return Results.Created($"/admin/transportes/{transporte.Id}", transporte);
        });

        group.MapGet("/transportes/{id}", async (long id, AppDbContext db) => {
            var transporte = await db.Transports.FindAsync(id);
            return transporte != null ? Results.Ok(transporte) : Results.NotFound();
        });

        group.MapPut("/transportes/{id}", async (long id, Transport input, AppDbContext db) => {
            var transporte = await db.Transports.FindAsync(id);
            if (transporte == null) return Results.NotFound();
            
            transporte.Company = input.Company;
            transporte.Type = input.Type;
            transporte.Price = input.Price;
            
            await db.SaveChangesAsync();
            return Results.NoContent();
        });

        group.MapDelete("/transportes/{id}", async (long id, AppDbContext db) => {
            var transporte = await db.Transports.FindAsync(id);
            if (transporte == null) return Results.NotFound();
            db.Transports.Remove(transporte);
            await db.SaveChangesAsync();
            return Results.NoContent();
        });

        // --- OFERTA CRUD ---
        group.MapPost("/ofertas", async (Offer oferta, AppDbContext db) => {
            db.Offers.Add(oferta);
            await db.SaveChangesAsync();
            return Results.Created($"/admin/ofertas/{oferta.Id}", oferta);
        });

        group.MapGet("/ofertas/{id}", async (long id, AppDbContext db) => {
            var oferta = await db.Offers.FindAsync(id);
            return oferta != null ? Results.Ok(oferta) : Results.NotFound();
        });

        group.MapPut("/ofertas/{id}", async (long id, Offer input, AppDbContext db) => {
            var oferta = await db.Offers.FindAsync(id);
            if (oferta == null) return Results.NotFound();
            
            oferta.StartDate = input.StartDate;
            oferta.EndDate = input.EndDate;
            oferta.Price = input.Price;
            oferta.Availability = input.Availability;
            oferta.Status = input.Status;
            oferta.PackageId = input.PackageId;
            oferta.HotelId = input.HotelId;
            oferta.TransportId = input.TransportId;
            
            
            await db.SaveChangesAsync();
            return Results.NoContent();
        });

        group.MapDelete("/ofertas/{id}", async (long id, AppDbContext db) => {
            var oferta = await db.Offers.FindAsync(id);
            if (oferta == null) return Results.NotFound();
            db.Offers.Remove(oferta);
            await db.SaveChangesAsync();
            return Results.NoContent();
        });

        // --- PACOTE CRUD ---
        group.MapPost("/pacotes", async (Package pacote, AppDbContext db) => {
            db.Packages.Add(pacote);
            await db.SaveChangesAsync();
            return Results.Created($"/admin/pacotes/{pacote.Id}", pacote);
        });

        group.MapGet("/pacotes/{id}", async (long id, AppDbContext db) => {
            var pacote = await db.Packages.FindAsync(id);
            return pacote != null ? Results.Ok(pacote) : Results.NotFound();
        });

        group.MapPut("/pacotes/{id}", async (long id, Package input, AppDbContext db) => {
            var pacote = await db.Packages.FindAsync(id);
            if (pacote == null) return Results.NotFound();
            
            pacote.Name = input.Name;
            pacote.Description = input.Description;
            pacote.TagIds = input.TagIds;
            pacote.StaffId = input.StaffId;
            pacote.PackagePhotoId = input.PackagePhotoId;
            
            
            await db.SaveChangesAsync();
            return Results.NoContent();
        });

        group.MapDelete("/pacotes/{id}", async (long id, AppDbContext db) => {
            var pacote = await db.Packages.FindAsync(id);
            if (pacote == null) return Results.NotFound();
            db.Packages.Remove(pacote);
            await db.SaveChangesAsync();
            return Results.NoContent();
        });

        // --- PACOTE FOTO CRUD ---
        group.MapPost("/pacotedefotos", async (PackagePhoto foto, AppDbContext db) => {
            db.PackagePhotos.Add(foto);
            await db.SaveChangesAsync();
            return Results.Created($"/admin/pacotedefotos/{foto.Id}", foto);
        });

        group.MapGet("/pacotedefotos/{id}", async (long id, AppDbContext db) => {
            var foto = await db.PackagePhotos.Include(f => f.Items).FirstOrDefaultAsync(f => f.Id == id);
            return foto != null ? Results.Ok(foto) : Results.NotFound();
        });

        group.MapPut("/pacotedefotos/{id}", async (long id, PackagePhoto input, AppDbContext db) => {
            var foto = await db.PackagePhotos.FindAsync(id);
            if (foto == null) return Results.NotFound();
            
            foto.Name = input.Name;
            foto.StorageType = input.StorageType;
            
            
            await db.SaveChangesAsync();
            return Results.NoContent();
        });

        group.MapDelete("/pacotedefotos/{id}", async (long id, AppDbContext db) => {
            var foto = await db.PackagePhotos.FindAsync(id);
            if (foto == null) return Results.NotFound();
            db.PackagePhotos.Remove(foto);
            await db.SaveChangesAsync();
            return Results.NoContent();
        });
    }
}
