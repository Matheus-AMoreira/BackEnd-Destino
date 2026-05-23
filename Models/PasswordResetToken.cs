using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BackEnd_Destino.Models;

[Table("password_reset_tokens")]
public class PasswordResetToken
{
    [Key]
    [Column("email")]
    public string Email { get; set; } = string.Empty;

    [Required]
    [Column("token")]
    public string Token { get; set; } = string.Empty;

    [Column("created_at")]
    public DateTime? CreatedAt { get; set; }
}
