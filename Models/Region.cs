using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BackEnd_Destino.Models;

[Table("regions")]
public class Region
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    [Column("id")]
    public long Id { get; set; }

    [Column("sigla")]
    [MaxLength(2)]
    public string Code { get; set; } = string.Empty;

    [Column("name")]
    [MaxLength(12)]
    public string Name { get; set; } = string.Empty;

    public virtual ICollection<State> States { get; set; } = new List<State>();
}
