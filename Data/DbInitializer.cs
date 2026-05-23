namespace BackEnd_Destino.Data;

public static class DbInitializer
{
    private static readonly Random _random = new();
    private static readonly HashSet<long> _usedIds = new();

    public static async Task Seed(AppDbContext context)
    {
        Console.WriteLine("🚀 Iniciando Seeders...");
        
        // Carregar IDs existentes para evitar colisões
        await LoadExistingIds(context);
        
        await SeedPermissions(context);
        await SeedRoles(context);
        await SeedGeography(context);

        await context.SaveChangesAsync();
        Console.WriteLine("✅ Seeders concluídos com sucesso!");
    }

    private static async Task SeedPermissions(AppDbContext context)
    {
        if (await context.Permissions.AnyAsync()) return;

        var permissions = new List<Permission>
        {
            new() { Slug = "dashboard:read", Description = "Acessar painel administrativo", IsStaff = true },
            new() { Slug = "user:read", Description = "Visualizar usuários", IsStaff = true },
            new() { Slug = "user:create", Description = "Cadastrar novos funcionários", IsStaff = true },
            new() { Slug = "hotel:read", Description = "Visualizar hotéis", IsStaff = true },
            new() { Slug = "hotel:create", Description = "Criar hotéis", IsStaff = true },
            new() { Slug = "hotel:update", Description = "Editar hotéis", IsStaff = true },
            new() { Slug = "hotel:delete", Description = "Deletar hotéis", IsStaff = true },
            new() { Slug = "package:read", Description = "Visualizar pacotes", IsStaff = true },
            new() { Slug = "package:create", Description = "Criar pacotes", IsStaff = true },
            new() { Slug = "package:update", Description = "Editar pacotes", IsStaff = true },
            new() { Slug = "offer:create", Description = "Criar ofertas", IsStaff = true },
            new() { Slug = "offer:update", Description = "Editar ofertas", IsStaff = true },
            new() { Slug = "offer:delete", Description = "Deletar ofertas", IsStaff = true },
            new() { Slug = "profile:update", Description = "Atualizar próprio perfil", IsStaff = false },
            new() { Slug = "profile:delete", Description = "Solicitar exclusão da própria conta", IsStaff = false },
            new() { Slug = "purchase:create", Description = "Realizar compras", IsStaff = false },
            new() { Slug = "purchase:read", Description = "Visualizar próprias compras", IsStaff = false },
        };

        context.Permissions.AddRange(permissions);
        await context.SaveChangesAsync();
    }

    private static async Task SeedRoles(AppDbContext context)
    {
        if (await context.Roles.AnyAsync()) return;

        var adminRole = new Role { Name = "ADMINISTRADOR", Description = "Acesso total", IsStaff = true };
        var employeeRole = new Role { Name = "FUNCIONARIO", Description = "Auxiliar", IsStaff = true };
        var userRole = new Role { Name = "USUARIO", Description = "Cliente", IsStaff = false };

        context.Roles.AddRange(adminRole, employeeRole, userRole);
        await context.SaveChangesAsync();

        var allPermissions = await context.Permissions.ToListAsync();

        // Admin
        context.RolePermissions.AddRange(allPermissions.Select(p => new RolePermission { RoleId = adminRole.Id, PermissionId = p.Id }));

        // User
        var userPermissions = allPermissions.Where(p => !p.IsStaff).ToList();
        context.RolePermissions.AddRange(userPermissions.Select(p => new RolePermission { RoleId = userRole.Id, PermissionId = p.Id }));
    }

    private static async Task SeedGeography(AppDbContext context)
    {
        using var httpClient = new HttpClient();
        Console.WriteLine("📥 Buscando regiões do IBGE...");
        
        var regioesIbge = await httpClient.GetFromJsonAsync<List<IbgeRegiao>>("https://servicodados.ibge.gov.br/api/v1/localidades/regioes");
        if (regioesIbge == null) return;

        foreach (var rDto in regioesIbge)
        {
            var regiao = await context.Regions.FirstOrDefaultAsync(r => r.Code == rDto.Sigla);
            if (regiao == null)
            {
                regiao = new Region { Id = GenerateUniqueId(), Name = rDto.Nome, Code = rDto.Sigla };
                context.Regions.Add(regiao);
                await context.SaveChangesAsync();
            }

            Console.WriteLine($"📍 Processando Região: {regiao.Name}");

            var estadosIbge = await httpClient.GetFromJsonAsync<List<IbgeEstado>>($"https://servicodados.ibge.gov.br/api/v1/localidades/regioes/{rDto.Id}/estados");
            if (estadosIbge == null) continue;

            foreach (var eDto in estadosIbge)
            {
                var estado = await context.States.FirstOrDefaultAsync(e => e.Code == eDto.Sigla);
                if (estado == null)
                {
                    estado = new State { Id = GenerateUniqueId(), Name = eDto.Nome, Code = eDto.Sigla, RegionId = regiao.Id };
                    context.States.Add(estado);
                    await context.SaveChangesAsync();
                }

                Console.WriteLine($"  -> Estado: {estado.Name}");

                var cidadesIbge = await httpClient.GetFromJsonAsync<List<IbgeCidade>>($"https://servicodados.ibge.gov.br/api/v1/localidades/estados/{eDto.Sigla}/municipios");
                if (cidadesIbge == null) continue;

                var existingCitiesNames = await context.Cities
                    .Where(c => c.StateId == estado.Id)
                    .Select(c => c.Name)
                    .ToListAsync();

                var newCities = cidadesIbge
                    .Where(cDto => !existingCitiesNames.Contains(cDto.Nome))
                    .Select(cDto => new City 
                    { 
                        Id = GenerateUniqueId(), 
                        Name = cDto.Nome, 
                        StateId = estado.Id 
                    })
                    .ToList();

                if (newCities.Any())
                {
                    context.Cities.AddRange(newCities);
                    await context.SaveChangesAsync();
                }
            }
        }
    }

    private static async Task LoadExistingIds(AppDbContext context)
    {
        var cityIds = await context.Cities.Select(c => c.Id).ToListAsync();
        var stateIds = await context.States.Select(s => s.Id).ToListAsync();
        var regionIds = await context.Regions.Select(r => r.Id).ToListAsync();

        foreach (var id in cityIds) _usedIds.Add(id);
        foreach (var id in stateIds) _usedIds.Add(id);
        foreach (var id in regionIds) _usedIds.Add(id);
    }

    private class IbgeRegiao
    {
        [JsonPropertyName("id")] public int Id { get; set; }
        [JsonPropertyName("nome")] public string Nome { get; set; } = "";
        [JsonPropertyName("sigla")] public string Sigla { get; set; } = "";
    }

    private class IbgeEstado
    {
        [JsonPropertyName("id")] public int Id { get; set; }
        [JsonPropertyName("nome")] public string Nome { get; set; } = "";
        [JsonPropertyName("sigla")] public string Sigla { get; set; } = "";
    }

    private class IbgeCidade
    {
        [JsonPropertyName("id")] public int Id { get; set; }
        [JsonPropertyName("nome")] public string Nome { get; set; } = "";
    }

    private static long GenerateUniqueId()
    {
        long newId;
        do
        {
            // Gera um número de 10 dígitos (entre 1.000.000.000 e 9.999.999.999)
            newId = (long)(_random.NextDouble() * (9999999999 - 1000000000) + 1000000000);
        } while (_usedIds.Contains(newId));

        _usedIds.Add(newId);
        return newId;
    }
}
