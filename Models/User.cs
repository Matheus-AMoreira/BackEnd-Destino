using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BackEnd_Destino.Models;

[Table("users")]
public class User
{
    [Key]
    [Column("id")]
    public Guid Id { get; set; } = Guid.NewGuid();

    [Column("nome")]
    [MaxLength(20)]
    public string FirstName { get; set; } = string.Empty;

    [Column("sobre_nome")]
    [MaxLength(20)]
    public string LastName { get; set; } = string.Empty;

    [Column("slug")]
    [MaxLength(100)]
    public string Slug { get; set; } = string.Empty;

    [Column("cpf")]
    [MaxLength(255)]
    public string Cpf { get; set; } = string.Empty;

    [Column("email")]
    [MaxLength(50)]
    public string Email { get; set; } = string.Empty;

    [Column("telefone")]
    [MaxLength(11)]
    public string Phone { get; set; } = string.Empty;

    [Column("password")]
    public string Password { get; set; } = string.Empty;

    [Column("is_valid")]
    public bool IsValid { get; set; } = true;

    [Column("role_id")]
    public Guid? RoleId { get; set; }
    public virtual Role? Role { get; set; }

    [Column("email_verified_at")]
    public DateTime? EmailVerifiedAt { get; set; }

    [Column("created_at")]
    public DateTime? CreatedAt { get; set; }

    [Column("updated_at")]
    public DateTime? UpdatedAt { get; set; }

    public virtual ICollection<Purchase> Purchases { get; set; } = new List<Purchase>();
    public virtual ICollection<RefreshToken> RefreshTokens { get; set; } = new List<RefreshToken>();
    public virtual ICollection<UserPermission> UserPermissions { get; set; } = new List<UserPermission>();

    public void UpdateSlug()
    {
        Slug = $"{FirstName}-{LastName}".ToLower()
            .Replace(" ", "-")
            .Normalize(System.Text.NormalizationForm.FormD)
            .Where(c => char.GetUnicodeCategory(c) != System.Globalization.UnicodeCategory.NonSpacingMark)
            .Aggregate("", (s, c) => s + c);
    }
}
