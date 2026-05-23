using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BackEnd_Destino.Models;

[Table("user_permissions")]
public class UserPermission
{
    [Key]
    [Column("id")]
    public Guid Id { get; set; } = Guid.NewGuid();

    [Column("user_id")]
    public Guid UserId { get; set; }
    public virtual User? User { get; set; }

    [Column("permission_id")]
    public Guid PermissionId { get; set; }
    public virtual Permission? Permission { get; set; }
}
