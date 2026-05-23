using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BackEnd_Destino.Models;

[Table("permissions")]
public class Permission
{
    [Key]
    [Column("id")]
    public Guid Id { get; set; } = Guid.NewGuid();

    [Required]
    [Column("slug")]
    public string Slug { get; set; } = string.Empty;

    [Column("description")]
    public string? Description { get; set; }

    [Column("is_staff")]
    public bool IsStaff { get; set; } = false;

    public virtual ICollection<RolePermission> RolePermissions { get; set; } = new List<RolePermission>();
    public virtual ICollection<UserPermission> UserPermissions { get; set; } = new List<UserPermission>();
}
