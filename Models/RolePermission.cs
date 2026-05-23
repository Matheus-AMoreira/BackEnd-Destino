using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BackEnd_Destino.Models;

[Table("role_permissions")]
public class RolePermission
{
    [Key]
    [Column("id")]
    public Guid Id { get; set; } = Guid.NewGuid();

    [Column("role_id")]
    public Guid RoleId { get; set; }
    public virtual Role? Role { get; set; }

    [Column("permission_id")]
    public Guid PermissionId { get; set; }
    public virtual Permission? Permission { get; set; }
}
