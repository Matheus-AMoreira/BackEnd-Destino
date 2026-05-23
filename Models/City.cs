using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BackEnd_Destino.Models;

[Table("cities")]
public class City
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    [Column("id")]
    public long Id { get; set; }

    [Column("name")]
    [MaxLength(40)]
    public string Name { get; set; } = string.Empty;

    [Column("state_id")]
    public long StateId { get; set; }

    [ForeignKey("StateId")]
    public virtual State? State { get; set; }

}
