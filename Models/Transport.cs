using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BackEnd_Destino.Models;

[Table("transports")]
public class Transport
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    [Column("id")]
    public long Id { get; set; }

    [Column("company")]
    [MaxLength(100)]
    public string Company { get; set; } = string.Empty;

    [Column("type")]
    public string Type { get; set; } = string.Empty;

    [Column("price")]
    public decimal Price { get; set; }
    
}
