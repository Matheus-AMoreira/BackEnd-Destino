using System.Text.Json;
using BackEnd_Destino.DTOs;
using BackEnd_Destino.Models;

namespace BackEnd_Destino.Tests;

public class DataIntegrityTests
{
    public static void Run()
    {
        Console.WriteLine("--- Verificando Integridade de Dados ---");

        // 1. Verificar Formato de Resposta de Autenticação
        var authResponse = new AuthResponse("test-token", "test-refresh-token", new UserResponse(Guid.NewGuid(), "Teste", "User", "test@email.com", "USUARIO", "teste-user"));
        var authJson = JsonSerializer.Serialize(authResponse);
        Console.WriteLine($"AuthResponse JSON: {authJson}");

        // 2. Verificar Formato de Pacote (conforme esperado pelo frontend)
        var pacote = new Package
        {
            Id = 1,
            Name = "Paris",
            Description = "Viagem incrível"
        };
        var pacoteJson = JsonSerializer.Serialize(pacote);
        Console.WriteLine($"Pacote JSON: {pacoteJson}");

        Console.WriteLine("--- Verificação Concluída ---");
    }
}
