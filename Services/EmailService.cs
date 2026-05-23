namespace BackEnd_Destino.Services;


public class EmailService : IEmailService
{
    private readonly IConfiguration _configuration;

    public EmailService(IConfiguration configuration)
    {
        _configuration = configuration;
    }

    public async Task SendEmailAsync(string to, string subject, string body)
    {
        var smtpHost = _configuration["Smtp:Host"]!;
        var smtpPort = _configuration["Smtp:Port"]!;
        var smtpUsername = _configuration["Smtp:Username"]!;
        var smtpPassword = _configuration["Smtp:Password"]!;
        var smtpFromEmail = _configuration["Smtp:FromEmail"]!;
        var smtpFromName = _configuration["Smtp:FromName"]!;
        
        var message = new MimeMessage();
        message.From.Add(new MailboxAddress(smtpFromName, smtpFromEmail));
        message.To.Add(new MailboxAddress("", to));
        message.Subject = subject;

        var bodyBuilder = new BodyBuilder { HtmlBody = body };
        message.Body = bodyBuilder.ToMessageBody();

        using var client = new SmtpClient();
        try
        {
            await client.ConnectAsync(smtpHost, int.Parse(smtpPort), SecureSocketOptions.StartTls);
            await client.AuthenticateAsync(smtpUsername, smtpPassword);
            await client.SendAsync(message);
            await client.DisconnectAsync(true);
        }
        catch (Exception ex)
        {
            // Log error or handle it
            Console.WriteLine($"Erro ao enviar e-mail: {ex.Message}");
            throw;
        }
    }
}
