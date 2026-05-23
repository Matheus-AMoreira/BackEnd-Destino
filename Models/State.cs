using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BackEnd_Destino.Models;

[Table("states")]
public class State
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    [Column("id")]
    public long Id { get; set; }

    [Column("sigla")]
    [MaxLength(2)]
    public string Code { get; set; } = string.Empty;

    [Column("name")]
    [MaxLength(100)]
    public string Name { get; set; } = string.Empty;

    [Column("region_id")]
    public long? RegionId { get; set; }

    [ForeignKey("RegionId")]
    public virtual Region? Region { get; set; }

    public virtual ICollection<City> Cities { get; set; } = new List<City>();
}
