using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BackEnd_Destino.Models;

[Table("refresh_tokens")]
public class RefreshToken
{
    [Key]
    [Column("id")]
    public Guid Id { get; set; } = Guid.NewGuid();

    [Column("token")]
    public string Token { get; set; } = string.Empty;

    [Column("user_id")]
    public Guid UserId { get; set; }

    [ForeignKey("UserId")]
    public virtual User? User { get; set; }

    [Column("expires_at")]
    public DateTime ExpiresAt { get; set; }

    [Column("created_at")]
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    [Column("revoked_at")]
    public DateTime? RevokedAt { get; set; }

    public bool IsExpired => DateTime.UtcNow >= ExpiresAt;
    public bool IsActive => RevokedAt == null && !IsExpired;
}
